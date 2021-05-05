import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.event.listener.listener
import net.minecraftforge.fml.common.gameevent.TickEvent

internal object AntiToast: PluginModule(
    name = "ModuleExample",
    category = Category.MISC,
    description = "Hides Minecraft tutorial toasts",
    pluginMain = AntiToastLoader
) {
    private val constant by setting("Constant", false, description = "Forces the module to remain on, instead of instantly toggling")

    init {
        listener<TickEvent.ClientTickEvent> {
            mc.toastGui.clear()
            if (!constant) disable()
        }
    }
}