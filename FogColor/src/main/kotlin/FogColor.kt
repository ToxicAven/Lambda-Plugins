import net.minecraftforge.client.event.EntityViewRenderEvent
import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.color.ColorHolder
import com.lambda.event.listener.listener

internal object FogColor: PluginModule(
    name = "FogColor",
    category = Category.RENDER,
    description = "Change the color of render fog",
    pluginMain = FogColorLoader
) {
    private val color by setting("Color", ColorHolder(111, 166, 222), false)

    init {
        listener<EntityViewRenderEvent.FogColors> {
            it.red = color.r.toFloat() / 255f
            it.green = color.g.toFloat() / 255f
            it.blue = color.b.toFloat() / 255f
        }
    }
}