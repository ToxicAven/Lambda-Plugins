import com.lambda.client.module.Category
import com.lambda.client.plugin.api.PluginModule
import com.lambda.event.listener.listener
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.InventoryBasic
import net.minecraftforge.fml.common.gameevent.TickEvent

internal object Backpack: PluginModule(
    name = "Backpack",
    category = Category.MISC,
    description = "Holds the Enderchest GUI",
    pluginMain = BackpackLoader
) {

    private var echestScreen: GuiScreen? = null
    init {

        listener<TickEvent.ClientTickEvent> {
            if (mc.currentScreen is GuiContainer) {
                val container: Container = (mc.currentScreen as GuiContainer).inventorySlots
                if (container is ContainerChest && container.lowerChestInventory is InventoryBasic) {
                    val basic = container.lowerChestInventory as InventoryBasic
                    if (basic.name.equals("Ender Chest", ignoreCase = true)) {
                        this.echestScreen = mc.currentScreen
                        mc.currentScreen = null
                    }
                }
            }
        }

        onDisable {
            if(this.echestScreen != null && mc.player != null && mc.world != null) {
                mc.displayGuiScreen(this.echestScreen)
            }
            this.echestScreen = null
        }
    }
}