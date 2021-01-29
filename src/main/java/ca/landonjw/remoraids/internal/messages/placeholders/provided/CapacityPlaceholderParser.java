package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.implementation.battles.restraints.CapacityRestraint;

public class CapacityPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "capacity";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		CapacityRestraint restraint = context.getAssociation(CapacityRestraint.class).orElse(null);

		if (restraint != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());

			if (arguments.size() == 1) {
				switch (arguments.get(0).toLowerCase()) {
				case "current":
					return Optional.of("" + restraint.getPlayerNum());
				case "max":
					return Optional.of("" + restraint.getCapacity());
				}
			}

			return Optional.of("" + restraint.getCapacity());
		}
		return Optional.empty();
	}

}
