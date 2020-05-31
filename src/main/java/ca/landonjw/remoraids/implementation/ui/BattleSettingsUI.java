package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class BattleSettingsUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public BattleSettingsUI(@Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
        super(player, bossEntity);
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                .onClick(() -> {
                    EditorUI editorUI = new EditorUI(player, bossEntity);
                    editorUI.open();
                })
                .build();

        Button restraints = Button.builder()
                .item(new ItemStack(Blocks.IRON_BARS))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Restraints")
                .onClick(() -> {

                })
                .build();

        Button rewards = Button.builder()
                .item(new ItemStack(PixelmonItemsValuables.nugget))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Rewards")
                .onClick(() -> {
                    RewardsUI rewardsUI = new RewardsUI(player, bossEntity);
                    rewardsUI.open();
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(0, 4, getBossButton())
                .set(2, 2, restraints)
                .set(2, 6, rewards)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Respawn Settings")
                .build();

        page.forceOpenPage(player);
    }

}