import com.lambda.client.plugin.api.Plugin

internal object CSTLoader: Plugin() {

    override fun onLoad() {
        modules.add(ClientSideTime)
    }

    override fun onUnload() {}
}