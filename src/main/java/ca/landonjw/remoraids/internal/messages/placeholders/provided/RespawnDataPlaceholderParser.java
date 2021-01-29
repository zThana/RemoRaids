package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.spawning.IRespawnData;

public class RespawnDataPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "respawnvalue";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		IRespawnData data = context.getAssociation(IRespawnData.class).orElse(null);
		if (data != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if (arguments.size() == 1) {
				switch (arguments.get(0)) {
				case "total":
					if (data.isInfinite()) {
						return Optional.of("Infinite");
					} else {
						return Optional.of("" + data.getTotalRespawns());
					}
				case "remaining":
					if (data.isInfinite()) {
						return Optional.of("Infinite");
					} else {
						return Optional.of("" + Math.max(0, data.getRemainingRespawns()));
					}
				}
			}
		}
		return Optional.empty();
	}

}
