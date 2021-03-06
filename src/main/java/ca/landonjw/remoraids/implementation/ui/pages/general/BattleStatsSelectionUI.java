package ca.landonjw.remoraids.implementation.ui.pages.general;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItemsHeld;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * A user interface to select a stat to modify within the boss ui.
 * Can be viewed by: Registry > Options > Edit > General Settings > Edit Stats
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BattleStatsSelectionUI extends BaseBossUI {

	private final Item[] CHOICE_ITEMS = {
			PixelmonItemsHeld.powerWeight, PixelmonItemsHeld.powerBracer, PixelmonItemsHeld.powerBelt, PixelmonItemsHeld.powerLens, PixelmonItemsHeld.powerBand, PixelmonItemsHeld.powerAnklet };

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public BattleStatsSelectionUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
			source.open();
		}).build();

		Template.Builder templateBuilder = Template.builder(6).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(3, 4, back);

		for (int i = 0; i < 6; i++) {
			StatsType type = StatsType.getStatValues()[i];

			IParsingContext context = IParsingContext.builder().add(StatsType.class, () -> type).add(Integer.class, () -> bossEntity.getBoss().getStat(type)).build();

			Button button;
			if (i == 0 && RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get().getPlayersInBattle().size() != 0) {
				button = Button.builder().item(new ItemStack(CHOICE_ITEMS[i])).displayName(service.interpret(config.get(MessageConfig.UI_BATTLE_STAT_SELECTION_EDIT_STAT), context)).lore(Arrays.asList(config.get(MessageConfig.BOSS_IN_BATTLE_MODIFICATION))).build();
			} else {
				button = Button.builder().item(new ItemStack(CHOICE_ITEMS[i])).displayName(service.interpret(config.get(MessageConfig.UI_BATTLE_STAT_SELECTION_EDIT_STAT), context)).onClick(() -> {
					BossStatEditorUI statEditor = new BossStatEditorUI(this, player, bossEntity, type);
					statEditor.open();
				}).build();
			}

			int column;
			if (i <= 2) {
				column = 1 + i;
			} else {
				column = 2 + i;
			}

			templateBuilder = templateBuilder.set(2, column, button);
		}

		Page page = Page.builder().template(templateBuilder.build()).title(config.get(MessageConfig.UI_BATTLE_STAT_SELECTION_TITLE)).build();

		page.forceOpenPage(player);
	}

}
