import com.lambda.client.plugin.api.Plugin

internal object ParkourLoader: Plugin() {

    override fun onLoad() {
        // Load any modules, commands, or HUD elements here
        modules.add(Parkour)
    }

    override fun onUnload() {
        // Here you can unregister threads etc...
    }
}