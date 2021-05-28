import com.lambda.client.plugin.api.Plugin

internal object SSSLoader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(SuperSecretSettings)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}