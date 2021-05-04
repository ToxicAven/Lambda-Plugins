import com.lambda.client.plugin.api.Plugin

internal object FogColorLoader: Plugin() {

    override fun onLoad() {
        modules.add(FogColor)
    }

    override fun onUnload() {}
}