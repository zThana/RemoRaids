package ca.landonjw.remoraids.implementation.ui.pages.spawning;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.IRespawnData;
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
 * A user interface to modify the respawn cooldown of the boss entity.
 * Can be viewed by: Registry > Options > Edit > Respawning Settings > Set Respawn Cooldown
 *
 * @author landonjw
 * @since 1.0.0
 */
public class RespawnCooldownEditorUI extends BaseBossUI {

	/** The ordinal of currently selected time unit. */
	private int currentTimeUnitOrdinal = -1;
	/** All time units that are valid and selectable in the user interface. */
	private final TimeUnit[] validTimeUnits = {
			TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS };

	/** Number of seconds in a day. */
	private final long SECONDS_IN_DAY = 86400;
	/** Number of seconds in an hour. */
	private final long SECONDS_IN_HOUR = 3600;
	/** Number of seconds in a minute. */
	private final long SECONDS_IN_MINUTE = 60;

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public RespawnCooldownEditorUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		super(source, player, bossEntity);
	}

	/** {@inheritDoc} */
	@Override
	public void open() {
		if (bossNotInBattle()) {
			Config config = RemoRaids.getMessageConfig();
			IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(config.get(MessageConfig.UI_COMMON_BACK)).onClick(() -> {
				source.open();
			}).build();

			if (!this.bossEntity.getSpawner().getRespawnData().isPresent()) {
				IRespawnData data = this.bossEntity.getSpawner().createRespawnData();
				data.setTotalRespawns(0);
				data.setTotalWaitPeriod(5, TimeUnit.SECONDS);
			}

			IBossSpawner spawner = this.bossEntity.getSpawner();
			IRespawnData respawns = spawner.getRespawnData().get();

			if (currentTimeUnitOrdinal == -1) {
				long cooldownSeconds = respawns.getTotalWaitPeriod(TimeUnit.SECONDS);

				if (cooldownSeconds % SECONDS_IN_DAY == 0) {
					currentTimeUnitOrdinal = 3;
				} else if (cooldownSeconds % SECONDS_IN_HOUR == 0) {
					currentTimeUnitOrdinal = 2;
				} else if (cooldownSeconds % SECONDS_IN_MINUTE == 0) {
					currentTimeUnitOrdinal = 1;
				} else {
					currentTimeUnitOrdinal = 0;
				}
			}

			TimeUnit unit = validTimeUnits[currentTimeUnitOrdinal];
			long value = respawns.getTotalWaitPeriod(unit);

			IParsingContext context = IParsingContext.builder().add(TimeUnit.class, () -> unit).add(Long.class, () -> value).build();

			Button decrementTimeUnit = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_DECREASE_UNIT)).onClick(() -> {
				TimeUnit oldUnit = validTimeUnits[currentTimeUnitOrdinal];
				currentTimeUnitOrdinal = (currentTimeUnitOrdinal - 1 + validTimeUnits.length) % validTimeUnits.length;
				respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(oldUnit), validTimeUnits[currentTimeUnitOrdinal]);
				open();
			}).build();

			Button incrementTimeUnit = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_INCREASE_UNIT)).onClick(() -> {
				TimeUnit oldUnit = validTimeUnits[currentTimeUnitOrdinal];
				currentTimeUnitOrdinal = (currentTimeUnitOrdinal + 1) % validTimeUnits.length;
				respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(oldUnit), validTimeUnits[currentTimeUnitOrdinal]);
				open();
			}).build();

			Button timeUnit = Button.builder().item(new ItemStack(Items.CLOCK)).displayName(service.interpret(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_CURRENT_UNIT), context)).build();

			Button decrementTimeValue = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_DECREASE_VALUE)).onClick(() -> {
				TimeUnit currentUnit = validTimeUnits[currentTimeUnitOrdinal];
				respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(currentUnit) - 1, currentUnit);
				open();
			}).build();

			Button incrementTimeValue = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_INCREASE_VALUE)).onClick(() -> {
				TimeUnit currentUnit = validTimeUnits[currentTimeUnitOrdinal];
				respawns.setTotalWaitPeriod(respawns.getTotalWaitPeriod(currentUnit) + 1, currentUnit);
				open();
			}).build();

			Button timeValue = Button.builder().item(new ItemStack(PixelmonItems.hourglassSilver)).displayName(service.interpret(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_CURRENT_VALUE), context)).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 1, decrementTimeUnit).set(2, 2, timeUnit).set(2, 3, incrementTimeUnit).set(2, 5, decrementTimeValue).set(2, 6, timeValue).set(2, 7, incrementTimeValue).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(service.interpret(config.get(MessageConfig.UI_RESPAWN_COOLDOWN_EDITOR_TITLE), context)).build();

			page.forceOpenPage(player);
		}
	}

}
