package ca.landonjw.remoraids.implementation.ui.pages.battle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItemsValuables;

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
import net.minecraft.item.ItemStack;

public class BattleSettingsUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public BattleSettingsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		Config config = RemoRaids.getMessageConfig();

		Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
			source.open();
		}).build();

		Button restraints = Button.builder().item(new ItemStack(Blocks.IRON_BARS)).displayName(config.get(MessageConfig.UI_BATTLES_BATTLE_SETTINGS_EDIT_RESTRAINTS)).onClick(() -> {
			RestraintsUI restraintsUI = new RestraintsUI(this, player, bossEntity);
			restraintsUI.open();
		}).build();

		Button rewards = Button.builder().item(new ItemStack(PixelmonItemsValuables.nugget)).displayName(config.get(MessageConfig.UI_BATTLES_BATTLE_SETTINGS_EDIT_REWARDS)).onClick(() -> {
			RewardsUI rewardsUI = new RewardsUI(this, player, bossEntity);
			rewardsUI.open();
		}).build();

		Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 2, restraints).set(2, 6, rewards).set(3, 4, back).build();

		Page page = Page.builder().template(template).title(config.get(MessageConfig.UI_BATTLES_BATTLE_SETTINGS_TITLE)).build();

		page.forceOpenPage(player);
	}

}
