import com.lambda.client.plugin.api.Plugin

internal object BackpackLoader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(Backpack)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}