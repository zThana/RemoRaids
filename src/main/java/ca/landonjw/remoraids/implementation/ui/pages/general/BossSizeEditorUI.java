package ca.landonjw.remoraids.implementation.ui.pages.general;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A user interface to edit the size of a boss.
 * Can be viewed by: Registry > Options > Edit > General Settings > Edit Size
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossSizeEditorUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public BossSizeEditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        super(source, player, bossEntity);
    }

    /** {@inheritDoc} */
    public void open() {
        Config config = RemoRaids.getMessageConfig();

        Button decrementSize = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrease Size")
                .onClick(() -> {
                    bossEntity.getBoss().setSize(bossEntity.getBoss().getSize() - 0.25f);
                    open();
                })
                .build();

        Button incrementSize = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeHolderRight))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increase Size")
                .onClick(() -> {
                    bossEntity.getBoss().setSize(bossEntity.getBoss().getSize() + 0.25f);
                    open();
                })
                .build();

        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(config.get(MessageConfig.UI_COMMON_BACK))
                .onClick(() -> {
                    source.open();
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(2, 3, decrementSize)
                .set(2, 4, getBossButton())
                .set(2, 5, incrementSize)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Size (" + (bossEntity.getBoss().getSize() * 100) + "%" + ")")
                .build();

        page.forceOpenPage(player);
    }

}
