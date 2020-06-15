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
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * A user interface to select a stat to modify within the boss ui.
 * Can be viewed by: Registry > Options > Edit > General Settings > Edit Stats
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BattleStatsSelectionUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public BattleStatsSelectionUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        super(source, player, bossEntity);
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
        Config config = RemoRaids.getMessageConfig();

        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(config.get(MessageConfig.UI_COMMON_BACK))
                .onClick(() -> {
                    source.open();
                })
                .build();

        Button health;

        if(RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get().getPlayersInBattle().size() == 0){
            health = Button.builder()
                    .item(new ItemStack(PixelmonItemsHeld.powerWeight))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Health")
                    .onClick(() -> {
                        BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, StatsType.HP);
                        statEditor.open();
                    })
                    .build();
        }
        else{
            health = Button.builder()
                    .item(new ItemStack(PixelmonItemsHeld.powerWeight))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Edit Health")
                    .lore(Arrays.asList(TextFormatting.WHITE + "You cannot modify this stat while boss is in battle!"))
                    .build();
        }

        Button attack = Button.builder()
                .item(new ItemStack(PixelmonItemsHeld.powerBracer))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Attack")
                .onClick(() -> {
                    BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, StatsType.Attack);
                    statEditor.open();
                })
                .build();

        Button defence = Button.builder()
                .item(new ItemStack(PixelmonItemsHeld.powerBelt))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Defence")
                .onClick(() -> {
                    BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, StatsType.Defence);
                    statEditor.open();
                })
                .build();

        Button specialAttack = Button.builder()
                .item(new ItemStack(PixelmonItemsHeld.powerLens))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Special Attack")
                .onClick(() -> {
                    BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, StatsType.SpecialAttack);
                    statEditor.open();
                })
                .build();

        Button specialDefence = Button.builder()
                .item(new ItemStack(PixelmonItemsHeld.powerBand))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Special Defence")
                .onClick(() -> {
                    BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, StatsType.SpecialDefence);
                    statEditor.open();
                })
                .build();

        Button speed = Button.builder()
                .item(new ItemStack(PixelmonItemsHeld.powerAnklet))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Edit Speed")
                .onClick(() -> {
                    BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, StatsType.Speed);
                    statEditor.open();
                })
                .build();

        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(0, 4, getBossButton())
                .set(2, 1, health)
                .set(2, 2, attack)
                .set(2, 3, defence)
                .set(2, 5, specialAttack)
                .set(2, 6, specialDefence)
                .set(2, 7, speed)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Stat Selection")
                .build();

        page.forceOpenPage(player);
    }

}
