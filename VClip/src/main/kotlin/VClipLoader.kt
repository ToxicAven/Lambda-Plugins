import com.lambda.client.plugin.api.Plugin

internal object VClipLoader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        commands.add(VClip)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}