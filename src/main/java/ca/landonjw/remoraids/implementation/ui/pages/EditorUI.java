package ca.landonjw.remoraids.implementation.ui.pages;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.battle.BattleSettingsUI;
import ca.landonjw.remoraids.implementation.ui.pages.general.GeneralSettingsUI;
import ca.landonjw.remoraids.implementation.ui.pages.spawning.RespawnSettingsUI;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * A user interface to open the main page of the ui.
 * Can be viewed by: Registry > Options > Edit
 *
 * @author landonjw
 * @since 1.0.0
 */
public class EditorUI extends BaseBossUI {

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public EditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	public void open() {
		if (bossNotInBattle()) {
			Config config = RemoRaids.getMessageConfig();

			Button respawnSettings = Button.builder().item(new ItemStack(PixelmonItemsHeld.luckyEgg)).displayName(config.get(MessageConfig.UI_EDITOR_RESPAWN_SETTINGS)).onClick(() -> {
				RespawnSettingsUI spawnSettings = new RespawnSettingsUI(this, player, bossEntity);
				spawnSettings.open();
			}).build();

			Button generalSettings = Button.builder().item(new ItemStack(PixelmonItems.porygonPieces)).displayName(config.get(MessageConfig.UI_EDITOR_GENERAL_SETTINGS)).onClick(() -> {
				GeneralSettingsUI generalUI = new GeneralSettingsUI(this, player, bossEntity);
				generalUI.open();
			}).build();

			Button battleSettings = Button.builder().item(new ItemStack(PixelmonItemsPokeballs.pokeBall)).displayName(config.get(MessageConfig.UI_EDITOR_BATTLE_SETTINGS)).onClick(() -> {
				BattleSettingsUI battleSettingsUI = new BattleSettingsUI(this, player, bossEntity);
				battleSettingsUI.open();
			}).build();

			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
				source.open();
			}).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 2, respawnSettings).set(2, 4, generalSettings).set(2, 6, battleSettings).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(config.get(MessageConfig.UI_EDITOR_TITLE)).build();

			page.forceOpenPage(player);
		}
	}

}
