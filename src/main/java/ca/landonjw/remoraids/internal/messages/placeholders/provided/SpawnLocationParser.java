package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;

public class SpawnLocationParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "location";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		IBossSpawnLocation location = context.getAssociation(IBossSpawnLocation.class).orElse(null);

		if (location != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if (arguments.size() == 1) {
				switch (arguments.get(0).toLowerCase()) {
				case "world":
					return Optional.of(location.getWorld().getWorldInfo().getWorldName());
				case "x":
					return Optional.of("" + location.getLocation().x);
				case "y":
					return Optional.of("" + location.getLocation().y);
				case "z":
					return Optional.of("" + location.getLocation().z);
				case "rotation":
					return Optional.of("" + location.getRotation());
				}
			}
			return Optional.of("[" + location.getLocation().x + "," + location.getLocation().y + "," + location.getLocation().z + "]");
		}
		return Optional.empty();
	}

}
