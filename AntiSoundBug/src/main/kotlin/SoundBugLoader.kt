import com.lambda.client.plugin.api.Plugin

internal object SoundBugLoader: Plugin() {

    override fun onLoad() {
        modules.add(AntiSoundBug)
    }

    override fun onUnload() {}
}