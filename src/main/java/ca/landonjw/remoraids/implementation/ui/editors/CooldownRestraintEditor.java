package ca.landonjw.remoraids.implementation.ui.editors;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.config.PixelmonItems;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.CooldownRestraint;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class CooldownRestraintEditor implements IEditorUI<CooldownRestraint> {

	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull CooldownRestraint cooldownRestraint) {
		new Editor(source, player, source.getBossEntity(), cooldownRestraint).open();
	}

	class Editor extends BaseBossUI {

		private CooldownRestraint restraint;

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
		public Editor(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, CooldownRestraint restraint) {
			super(source, player, bossEntity);
			this.restraint = restraint;
		}

		@Override
		public void open() {
			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back").onClick(() -> {
				source.open();
			}).build();

			Button.Builder decrementTimeUnitBuilder = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft));
			Button.Builder incrementTimeUnitBuilder = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight));
			Button.Builder timeUnitBuilder = Button.builder().item(new ItemStack(Items.CLOCK));

			Button.Builder decrementTimeValueBuilder = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft));
			Button.Builder incrementTimeValueBuilder = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight));
			Button.Builder timeValueBuilder = Button.builder().item(new ItemStack(PixelmonItems.hourglassSilver));

			if (currentTimeUnitOrdinal == -1) {
				long cooldownSeconds = restraint.getCooldown(TimeUnit.SECONDS);

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

			String strPrevUnit = validTimeUnits[(currentTimeUnitOrdinal - 1 + validTimeUnits.length) % validTimeUnits.length].name().toLowerCase();
			strPrevUnit = strPrevUnit.substring(0, 1).toUpperCase().concat(strPrevUnit.substring(1));

			String strCurrentUnit = validTimeUnits[currentTimeUnitOrdinal].name().toLowerCase();
			strCurrentUnit = strCurrentUnit.substring(0, 1).toUpperCase().concat(strCurrentUnit.substring(1));

			String strNextUnit = validTimeUnits[(currentTimeUnitOrdinal + 1) % validTimeUnits.length].name().toLowerCase();
			strNextUnit = strNextUnit.substring(0, 1).toUpperCase().concat(strNextUnit.substring(1));

			decrementTimeUnitBuilder = decrementTimeUnitBuilder.displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Change Time Unit (" + strPrevUnit + ")").onClick(() -> {
				TimeUnit oldUnit = validTimeUnits[currentTimeUnitOrdinal];
				currentTimeUnitOrdinal = (currentTimeUnitOrdinal - 1 + validTimeUnits.length) % validTimeUnits.length;
				restraint.setCooldown(restraint.getCooldown(oldUnit), validTimeUnits[currentTimeUnitOrdinal]);
				open();
			});

			incrementTimeUnitBuilder = incrementTimeUnitBuilder.displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Change Time Unit (" + strNextUnit + ")").onClick(() -> {
				TimeUnit oldUnit = validTimeUnits[currentTimeUnitOrdinal];
				currentTimeUnitOrdinal = (currentTimeUnitOrdinal + 1) % validTimeUnits.length;
				restraint.setCooldown(restraint.getCooldown(oldUnit), validTimeUnits[currentTimeUnitOrdinal]);
				open();
			});

			decrementTimeValueBuilder = decrementTimeValueBuilder.displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Decrement Cooldown Time").onClick(() -> {
				TimeUnit currentUnit = validTimeUnits[currentTimeUnitOrdinal];
				restraint.setCooldown(restraint.getCooldown(currentUnit) - 1, currentUnit);
				open();
			});
			incrementTimeValueBuilder = incrementTimeValueBuilder.displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Increment Cooldown Time").onClick(() -> {
				TimeUnit currentUnit = validTimeUnits[currentTimeUnitOrdinal];
				restraint.setCooldown(restraint.getCooldown(currentUnit) + 1, currentUnit);
				open();
			});

			timeValueBuilder = timeValueBuilder.displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Cooldown Time: " + restraint.getCooldown(validTimeUnits[currentTimeUnitOrdinal]));

			timeUnitBuilder = timeUnitBuilder.displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Time Unit: " + strCurrentUnit);

			Button decrementTimeUnit = decrementTimeUnitBuilder.build();
			Button timeUnit = timeUnitBuilder.build();
			Button incrementTimeUnit = incrementTimeUnitBuilder.build();

			Button decrementTimeValue = decrementTimeValueBuilder.build();
			Button timeValue = timeValueBuilder.build();
			Button incrementTimeValue = incrementTimeValueBuilder.build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 1, decrementTimeUnit).set(2, 2, timeUnit).set(2, 3, incrementTimeUnit).set(2, 5, decrementTimeValue).set(2, 6, timeValue).set(2, 7, incrementTimeValue).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Edit Cooldown Restraint").build();

			page.forceOpenPage(player);
		}

	}
}
