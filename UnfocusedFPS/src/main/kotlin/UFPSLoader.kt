import com.lambda.client.plugin.api.Plugin

internal object UFPSLoader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(UnfocusedFPS)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}