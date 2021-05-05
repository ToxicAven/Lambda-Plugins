import com.lambda.client.plugin.api.Plugin

internal object AntiToastLoader: Plugin() {

    override fun onLoad() {
        modules.add(AntiToast)
    }

    override fun onUnload() {}
}