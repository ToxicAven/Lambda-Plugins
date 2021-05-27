import com.lambda.client.event.SafeClientEvent
import com.lambda.client.mixin.extension.rightClickDelayTimer
import com.lambda.client.module.Category
import com.lambda.client.module.modules.player.Freecam
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.items.blockBlacklist
import com.lambda.client.util.items.shulkerList
import com.lambda.client.util.threads.safeListener
import net.minecraft.block.Block
import net.minecraft.block.BlockAir
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockWeb
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.GameType
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.math.atan2
import kotlin.math.sqrt

internal object SelfWeb: PluginModule(
    name = "SelfWeb",
    category = Category.COMBAT,
    description = "Places a web in your feet",
    pluginMain = SelfWebLoader
) {
    private val triggerable by setting("Triggerable", true)
    private val autoCenter by setting("Center", true)
    private val timeoutTicks by setting("Timeout Ticks", 1, 1..100, 1, { triggerable })
    private val blocksPerTick by setting("Blocks Per Tick", 10, 1..20, 1)
    private val tickDelay by setting("Tick Delay", 0, 0..10, 1)
    private val rotate by setting("Rotate", true)
    private val noGlitchBlocks by setting("No Glitch Blocks", true)

    private var playerHotbarSlot = -1
    private var lastHotbarSlot = -1
    private var offsetStep = 0
    private var delayStep = 0
    private var totalTicksRunning = 0
    private var firstRun = false
    private var isSneaking = false
    private var playerPos: Vec3d? = null

    init {
        onEnable {
            if (mc.player == null) {
                disable()
            }
            val centerPos: BlockPos = mc.player.position
            playerPos = mc.player.positionVector
            val y = centerPos.y.toDouble()
            var x = centerPos.x.toDouble()
            var z = centerPos.z.toDouble()
            val plusPlus = Vec3d(x + 0.5, y, z + 0.5)
            val plusMinus = Vec3d(x + 0.5, y, z - 0.5)
            val minusMinus = Vec3d(x - 0.5, y, z - 0.5)
            val minusPlus = Vec3d(x - 0.5, y, z + 0.5)
            if (autoCenter) {
                if (distanceTo(plusPlus) < distanceTo(plusMinus) && distanceTo(plusPlus) < distanceTo(minusMinus) && distanceTo(plusPlus) < distanceTo(minusPlus)) {
                    x = centerPos.x.toDouble() + 0.5
                    z = centerPos.z.toDouble() + 0.5
                    centerPlayer(x, y, z)
                }
                if (distanceTo(plusMinus) < distanceTo(plusPlus) && distanceTo(plusMinus) < distanceTo(minusMinus) && distanceTo(plusMinus) < distanceTo(minusPlus)) {
                    x = centerPos.x.toDouble() + 0.5
                    z = centerPos.z.toDouble() - 0.5
                    centerPlayer(x, y, z)
                }
                if (distanceTo(minusMinus) < distanceTo(plusPlus) && distanceTo(minusMinus) < distanceTo(plusMinus) && distanceTo(minusMinus) < distanceTo(minusPlus)) {
                    x = centerPos.x.toDouble() - 0.5
                    z = centerPos.z.toDouble() - 0.5
                    centerPlayer(x, y, z)
                }
                if (distanceTo(minusPlus) < distanceTo(plusPlus) && distanceTo(minusPlus) < distanceTo(plusMinus) && distanceTo(minusPlus) < distanceTo(minusMinus)) {
                    x = centerPos.x.toDouble() - 0.5
                    z = centerPos.z.toDouble() + 0.5
                    centerPlayer(x, y, z)
                }
            }
            firstRun = true
            playerHotbarSlot = mc.player.inventory.currentItem
            lastHotbarSlot = -1
        }

        onDisable {
            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot
            }
            if (isSneaking) {
                mc.player.connection.sendPacket(CPacketEntityAction(mc.player as Entity, CPacketEntityAction.Action.STOP_SNEAKING) as Packet<*>)
                isSneaking = false
            }
            playerHotbarSlot = -1
            lastHotbarSlot = -1
        }

        safeListener<TickEvent.ClientTickEvent> {
            if (mc.player == null || Freecam.isEnabled) {
                disable()
            }
            if (triggerable && totalTicksRunning >= timeoutTicks) {
                totalTicksRunning = 0
                disable()
            }
            if (!firstRun) {
                if (delayStep < tickDelay) {
                    ++delayStep
                    return@safeListener
                }
                delayStep = 0
            }
            if (firstRun) {
                firstRun = false
            }
            var blocksPlaced = 0
            while (blocksPlaced < blocksPerTick) {
                val offsetPattern: Array<Vec3d> = heheFeet
                val maxSteps: Int = heheFeet.size
                if (offsetStep >= maxSteps) {
                    offsetStep = 0
                    break
                }
                val offsetPos = BlockPos(offsetPattern[offsetStep])
                val targetPos: BlockPos = BlockPos(mc.player.positionVector).add(offsetPos.x, offsetPos.y, offsetPos.z)
                if (placeBlock(targetPos)) {
                    ++blocksPlaced
                }
                ++offsetStep
            }
            if (blocksPlaced > 0) {
                if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                    mc.player.inventory.currentItem = playerHotbarSlot
                    lastHotbarSlot = playerHotbarSlot
                }
                if (isSneaking) {
                    mc.player.connection.sendPacket(CPacketEntityAction(mc.player as Entity, CPacketEntityAction.Action.STOP_SNEAKING) as Packet<*>)
                    isSneaking = false
                }
            }
            ++totalTicksRunning
        }
    }

    private fun centerPlayer(x: Double, y: Double, z: Double) {
        mc.player.connection.sendPacket(CPacketPlayer.Position(x, y, z, true) as Packet<*>)
        mc.player.setPosition(x, y, z)
    }

    private fun distanceTo(vec: Vec3d): Double {
        return playerPos!!.distanceTo(vec)
    }

    private fun SafeClientEvent.placeBlock(pos: BlockPos): Boolean {
        val block: Block = mc.world.getBlockState(pos).block
        if (block !is BlockAir && block !is BlockLiquid) {
            return false
        }
        val side: EnumFacing = getPlaceableSide(pos) ?: return false
        val neighbour = pos.offset(side)
        val opposite = side.opposite
        if (!canBeClicked(neighbour)) {
            return false
        }
        val hitVec = Vec3d(neighbour as Vec3i).add(0.5, 0.5, 0.5).add(Vec3d(opposite.directionVec).scale(0.5))
        val neighbourBlock: Block = mc.world.getBlockState(neighbour).block
        val obiSlot = findWeb()
        if (obiSlot == -1) {
            disable()
        }
        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot
            lastHotbarSlot = obiSlot
        }
        if (!isSneaking && blockBlacklist.contains(neighbourBlock) || shulkerList.contains(neighbourBlock)) {
            mc.player.connection.sendPacket(CPacketEntityAction(mc.player as
                Entity, CPacketEntityAction.Action.START_SNEAKING) as Packet<*>)
            isSneaking = true
        }
        if (rotate) {
            faceVectorPacketInstant(hitVec)
        }
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND)
        mc.player.swingArm(EnumHand.MAIN_HAND)
        mc.rightClickDelayTimer = 4
        if (noGlitchBlocks && mc.playerController.currentGameType != GameType.CREATIVE) {
            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, neighbour, opposite) as Packet<*>)
        }
        return true
    }

    private fun findWeb(): Int {
        var slot = -1
        for (i in 0..8) {
            val stack: ItemStack = mc.player.inventory.getStackInSlot(i)
            if (stack == ItemStack.EMPTY || stack.item !is ItemBlock || (stack.item as ItemBlock).block !is BlockWeb) continue
            slot = i
            break
        }
        return slot
    }

    private fun SafeClientEvent.getPlaceableSide(pos: BlockPos): EnumFacing? {
        for (side in EnumFacing.values()) {
            val neighbour = pos.offset(side)
            if (!mc.world.getBlockState(neighbour).block.canCollideCheck(mc.world.getBlockState(neighbour), false) || mc.world.getBlockState(neighbour).material.isReplaceable) continue
            return side
        }
        return null
    }

    private fun canBeClicked(pos: BlockPos): Boolean {
        return getBlock(pos).canCollideCheck(getState(pos), false)
    }

    private fun getBlock(pos: BlockPos): Block {
        return getState(pos).block
    }

    private fun getState(pos: BlockPos): IBlockState {
        return mc.world.getBlockState(pos)
    }

    private fun faceVectorPacketInstant(vec: Vec3d) {
        val rotations: FloatArray = getLegitRotations(vec)
        mc.player.connection.sendPacket(CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround) as Packet<*>)
    }

    private fun getLegitRotations(vec: Vec3d): FloatArray {
        val eyesPos: Vec3d = getEyesPos()
        val diffX = vec.x - eyesPos.x
        val diffY = vec.y - eyesPos.y
        val diffZ = vec.z - eyesPos.z
        val diffXZ = sqrt(diffX * diffX + diffZ * diffZ)
        val yaw = Math.toDegrees(atan2(diffZ, diffX)).toFloat() - 90.0f
        val pitch = (-Math.toDegrees(atan2(diffY, diffXZ))).toFloat()
        return floatArrayOf(mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch))
    }

    private fun getEyesPos(): Vec3d {
        return Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight().toDouble(), mc.player.posZ)
    }

    private val heheFeet: Array<Vec3d> = arrayOf(Vec3d(0.0, 0.0, 0.0))
}