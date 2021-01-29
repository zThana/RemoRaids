package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.implementation.battles.restraints.CooldownRestraint;
import net.minecraft.entity.player.EntityPlayerMP;

public class CooldownPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "cooldown";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		CooldownRestraint restraint = context.getAssociation(CooldownRestraint.class).orElse(null);
		EntityPlayerMP player = context.getAssociation(EntityPlayerMP.class).orElse(null);

		if (restraint != null && player != null) {
			long secondsRemaining = restraint.getCooldownRemaining(player, TimeUnit.SECONDS).orElse(0L);
			long minutesRemaining = restraint.getCooldownRemaining(player, TimeUnit.MINUTES).orElse(0L);
			long hoursRemaining = restraint.getCooldownRemaining(player, TimeUnit.HOURS).orElse(0L);

			long trimmedSecondsRemaining = secondsRemaining - (minutesRemaining * 60);
			long trimmedMinutesRemaining = minutesRemaining - (hoursRemaining * 60);

			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if (arguments.size() == 1) {
				switch (arguments.get(0).toLowerCase()) {
				case "seconds":
					return Optional.of("" + secondsRemaining);
				case "minutes":
					return Optional.of("" + minutesRemaining);
				case "hours":
					return Optional.of("" + hoursRemaining);
				case "trimmed-seconds":
					return Optional.of("" + trimmedSecondsRemaining);
				case "trimmed-minutes":
					return Optional.of("" + trimmedMinutesRemaining);
				}
			}
		}
		return Optional.empty();
	}

}
