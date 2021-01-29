package ca.landonjw.remoraids.implementation.ui.pages.general;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
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
import net.minecraft.item.ItemStack;

/**
 * A user interface to edit a stat of a boss.
 * Can be viewed by: Registry > Options > Edit > General Settings > Edit Stats > Select Any Stat
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossStatEditorUI extends BaseBossUI {

	private StatsType type;

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public BossStatEditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, StatsType type) {
		super(source, player, bossEntity);
		this.type = type;
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		if (bossNotInBattle()) {
			Config config = RemoRaids.getMessageConfig();
			IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

			IParsingContext context = IParsingContext.builder().add(StatsType.class, () -> type).add(Integer.class, () -> bossEntity.getBoss().getStat(type)).build();

			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
				source.open();
			}).build();

			Button decrementStat = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(service.interpret(config.get(MessageConfig.UI_BOSS_STAT_EDITOR_DECREASE), context)).onClick(() -> {
				if (bossEntity.getBoss().getStat(type) - 25 > 0) {
					bossEntity.getBoss().setStat(type, bossEntity.getBoss().getStat(type) - 25);
					if (type == StatsType.HP) {
						bossEntity.getBoss().getPokemon().setHealthPercentage(100);
						IBossBattle battle = RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get();
						battle.setBossHealth(battle.getMaxHealth(), null);
					}
					open();
				}
			}).build();

			Button incrementStat = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(service.interpret(config.get(MessageConfig.UI_BOSS_STAT_EDITOR_INCREASE), context)).onClick(() -> {
				if (bossEntity.getBoss().getStat(type) + 25 < Short.MAX_VALUE) {
					bossEntity.getBoss().setStat(type, bossEntity.getBoss().getStat(type) + 25);
					if (type == StatsType.HP) {
						bossEntity.getBoss().getPokemon().setHealthPercentage(100);
						IBossBattle battle = RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get();
						battle.setBossHealth(battle.getMaxHealth(), null);
					}
					open();
				}
			}).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(2, 3, decrementStat).set(2, 4, getBossButton()).set(2, 5, incrementStat).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(service.interpret(config.get(MessageConfig.UI_BOSS_STAT_EDITOR_TITLE), context)).build();

			page.forceOpenPage(player);
		}
	}

}
