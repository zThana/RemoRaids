package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

/**
 * A user interface to open the main page of the editor.
 * Can be viewed by: Registry > Options > Edit
 *
 * @author landonjw
 * @since  1.0.0
 */
public class EditorUI extends BaseBossUI {

    /**
     * Constructor for the user interface.
     *
     * @param player     the player using the user interface
     * @param bossEntity the boss being edited
     */
    public EditorUI(@Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        super(player, bossEntity);
    }

    /** {@inheritDoc} */
    public void open() {
        Button spawningSettings = Button.builder()
                .item(new ItemStack(PixelmonItemsHeld.luckyEgg))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Spawning Settings")
                .onClick(() -> {
                    SpawnSettingsUI spawnSettings = new SpawnSettingsUI(player, bossEntity);
                    spawnSettings.open();
                })
                .build();

        Button generalSettings = Button.builder()
                .item(new ItemStack(PixelmonItems.porygonPieces))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "General Settings")
                .onClick(() -> {
                    GeneralSettingsUI generalUI = new GeneralSettingsUI(player, bossEntity);
                    generalUI.open();
                })
                .build();

        Button battleSettings = Button.builder()
                .item(new ItemStack(PixelmonItemsPokeballs.pokeBall))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Battle Settings")
                .onClick(() -> {})
                .build();

        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                .onClick(() -> {
                    BossOptionsUI bossOptions = new BossOptionsUI(player, bossEntity);
                    bossOptions.open();
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(0, 4, getBossButton())
                .set(2, 2, spawningSettings)
                .set(2, 4, generalSettings)
                .set(2, 6, battleSettings)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Editor")
                .build();

        page.forceOpenPage(player);
    }

}
