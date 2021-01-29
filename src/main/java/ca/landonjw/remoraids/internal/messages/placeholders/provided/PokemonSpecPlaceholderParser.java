package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Optional;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;

public class PokemonSpecPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "spec";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		PokemonSpec spec = context.getAssociation(PokemonSpec.class).orElse(null);

		if (spec != null) {
			return Optional.ofNullable(spec.name);
		}
		return Optional.empty();
	}

}
