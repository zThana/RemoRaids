package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;

public class TimePlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "time";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		TimeUnit unit = context.getAssociation(TimeUnit.class).orElse(null);
		Long value = context.getAssociation(Long.class).orElse(null);
		if (unit != null && value != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if (arguments.size() == 1) {
				switch (arguments.get(0)) {
				case "unit":
					String strUnit = unit.name().toLowerCase();
					return Optional.of(strUnit.substring(0, 1).toUpperCase().concat(strUnit.substring(1)));
				case "value":
					return Optional.of("" + value);
				}
			}
		}
		return Optional.empty();
	}

}
