package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

/**
 * A user interface that displays general settings of a boss.
 * Can be viewed by: Registry > Options > Edit > General Settings
 *
 * @author landonjw
 * @since  1.0.0
 */
public class GeneralSettingsUI extends BaseBossUI {

    /**
     * Constructor for the user interface.
     *
     * @param player     the player using the user interface
     * @param bossEntity the boss being edited
     */
    public GeneralSettingsUI(@Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        super(player, bossEntity);
    }

    /** {@inheritDoc} */
    public void open() {
        Button setStats = Button.builder()
                .item(new ItemStack(Items.PAPER))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Stats")
                .onClick(() -> {
                    BattleStatsSelectionUI statsSelection = new BattleStatsSelectionUI(player, bossEntity);
                    statsSelection.open();
                })
                .build();

        Button setSize = Button.builder()
                .item(new ItemStack(PixelmonItems.rareCandy))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Size")
                .onClick(() -> {
                    BossSizeEditorUI sizeEditor = new BossSizeEditorUI(player, bossEntity);
                    sizeEditor.open();
                })
                .build();

        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                .onClick(() -> {
                    EditorUI editor = new EditorUI(player, bossEntity);
                    editor.open();
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(0, 4, getBossButton())
                .set(2, 3, setSize)
                .set(2, 5, setStats)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "General Settings")
                .build();

        page.forceOpenPage(player);
    }

}
