import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.client.util.threads.safeListener
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.GameSettings
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.Display

internal object UnfocusedFPS: PluginModule(
    name = "UnfocusedFPS",
    description = "Reduces framerate when the game is minimized",
    category = Category.RENDER,
    pluginMain = UFPSLoader
) {
private val FpsCap by setting("Frame Cap", 20, 5..120, 5)

private var Settings: GameSettings = Minecraft.getMinecraft().gameSettings

init {
    safeListener<TickEvent.ClientTickEvent> {
        if (!Display.isActive()) Settings.limitFramerate = FpsCap
        else Settings.limitFramerate = 260
    }
}
}