package ca.landonjw.remoraids.internal.services.placeholders.provided;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RaidBossPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "boss";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		IBoss boss = (IBoss) context.getAssociatedObject().filter(x -> x instanceof IBoss).orElse(null);
		if(boss != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());

			// TODO - Additional argument parsing for a boss placeholder

			return Optional.of(boss.getPokemon().getSpecies().getPokemonName());
		}
		return Optional.empty();
	}

}
