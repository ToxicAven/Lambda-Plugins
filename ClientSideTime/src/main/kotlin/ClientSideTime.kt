import com.lambda.client.event.events.PacketEvent
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.threads.safeListener
import com.lambda.event.listener.listener
import net.minecraft.network.play.server.SPacketTimeUpdate
import net.minecraftforge.fml.common.gameevent.TickEvent

internal object ClientSideTime: PluginModule(
    name = "ClientSideTime",
    category = Category.RENDER,
    description = "Change time of day client side",
    pluginMain = CSTLoader
) {
    private val time by setting("Time", 6000, 0..24000, 20)

    init {

        listener<PacketEvent.Receive> {
            if (it.packet is SPacketTimeUpdate) it.cancel()
        }

        safeListener<TickEvent.ClientTickEvent> {
            mc.world.worldTime = time.toLong()
        }
    }
}