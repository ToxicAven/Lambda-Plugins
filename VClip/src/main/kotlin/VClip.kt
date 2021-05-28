import com.lambda.client.command.ClientCommand

object VClip: ClientCommand(
    name = "vclip",
    description = "Attempts to teleport through blocks vertically"
) {
    init {
        int("y") { yArg ->
            execute {
                mc.player.setPositionAndUpdate(mc.player.posX, mc.player.posY + yArg.value, mc.player.posZ)
            }
        }
    }
}