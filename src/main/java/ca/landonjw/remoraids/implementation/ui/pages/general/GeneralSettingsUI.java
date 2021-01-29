package ca.landonjw.remoraids.implementation.ui.pages.general;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;

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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * A user interface that displays general settings of a boss.
 * Can be viewed by: Registry > Options > Edit > General Settings
 *
 * @author landonjw
 * @since 1.0.0
 */
public class GeneralSettingsUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public GeneralSettingsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	public void open() {
		if (bossNotInBattle()) {
			Config config = RemoRaids.getMessageConfig();

			Button setStats = Button.builder().item(new ItemStack(Items.PAPER)).displayName(config.get(MessageConfig.UI_GENERAL_EDIT_STATS)).onClick(() -> {
				BattleStatsSelectionUI statsSelection = new BattleStatsSelectionUI(this, player, bossEntity);
				statsSelection.open();
			}).build();

			Button setSize = Button.builder().item(new ItemStack(PixelmonItems.rareCandy)).displayName(config.get(MessageConfig.UI_GENERAL_EDIT_SIZE)).onClick(() -> {
				BossSizeEditorUI sizeEditor = new BossSizeEditorUI(this, player, bossEntity);
				sizeEditor.open();
			}).build();

			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
				source.open();
			}).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 2, setSize).set(2, 6, setStats).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(config.get(MessageConfig.UI_GENERAL_SETTINGS_TITLE)).build();

			page.forceOpenPage(player);
		}
	}

}
