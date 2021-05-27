import com.lambda.client.plugin.api.Plugin

internal object SelfWebLoader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(SelfWeb)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}