import com.lambda.client.plugin.api.Plugin

internal object F3Loader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(F3Spoof)
    }

    override fun onUnload() {}
}