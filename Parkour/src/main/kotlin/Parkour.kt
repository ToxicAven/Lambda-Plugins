import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.threads.safeListener
import net.minecraftforge.fml.common.gameevent.TickEvent

internal object Parkour: PluginModule(
    name = "Parkour",
    category = Category.MOVEMENT,
    description = "Jumps when you are at the edge of a block",
    pluginMain = ParkourLoader
) {
    init {
        safeListener<TickEvent.ClientTickEvent> {
            if (
                player.onGround && !player.isSneaking &&
                world.getCollisionBoxes(player, player.entityBoundingBox
                    .offset(0.0, -0.5, 0.0)
                    .expand(-0.001, 0.0, -0.001)).isEmpty()
            ) {
                player.jump()
            }
        }
    }
}