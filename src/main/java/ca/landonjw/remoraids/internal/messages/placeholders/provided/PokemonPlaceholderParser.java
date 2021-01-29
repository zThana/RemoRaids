package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.internal.config.MessageConfig;

public class PokemonPlaceholderParser implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "pokemon";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		Pokemon pokemon = context.getAssociation(Pokemon.class).orElse(null);

		if (pokemon != null) {
			List<String> arguments = context.getArguments().orElse(Collections.emptyList());
			if (arguments.size() == 1) {
				return ArgumentParser.parse(arguments.get(0), pokemon);
			}
			return Optional.of(pokemon.getSpecies().getPokemonName());
		}
		return Optional.empty();
	}

	private enum ArgumentParser {

		Species(pokemon -> pokemon.getSpecies().getLocalizedName()),
		DisplayName(Pokemon::getDisplayName),
		Level(Pokemon::getLevel),
		Ability(pokemon -> pokemon.getAbility().getLocalizedName()),
		Nature(pokemon -> pokemon.getNature().getLocalizedName()),
		Gender(pokemon -> pokemon.getGender().getLocalizedName()),
		Form(pokemon -> pokemon.getFormEnum().getLocalizedName()),
		Texture(pokemon -> {
			if (pokemon.getCustomTexture() == null || pokemon.getCustomTexture().isEmpty()) {
				return RemoRaids.getMessageConfig().get(MessageConfig.UI_POKEMON_NO_TEXTURE);
			}

			return pokemon.getCustomTexture();
		}),
		Moveset(pokemon -> {
			String input = RemoRaids.getMessageConfig().get(MessageConfig.UI_POKEMON_MOVESET);
			IParsingContext internal = IParsingContext.builder().add(Moveset.class, pokemon::getMoveset).build();
			IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
			return service.interpret(input, internal);
		}),
		HP(pokemon -> pokemon.getStat(StatsType.HP)),
		Attack(pokemon -> pokemon.getStat(StatsType.Attack)),
		Defence(pokemon -> pokemon.getStat(StatsType.Defence)),
		SpAtk(pokemon -> pokemon.getStat(StatsType.SpecialAttack)),
		SpDef(pokemon -> pokemon.getStat(StatsType.SpecialDefence)),
		Speed(pokemon -> pokemon.getStat(StatsType.Speed)),;

		private Function<Pokemon, Object> parser;

		ArgumentParser(Function<Pokemon, Object> parser) {
			this.parser = parser;
		}

		public static Optional<String> parse(String key, Pokemon context) {
			return Arrays.stream(values()).filter(x -> x.name().toLowerCase().equals(key.toLowerCase())).findAny().map(x -> x.parser.apply(context)).map(Object::toString);
		}

	}

	public static class MovesetPlaceholderParser implements IPlaceholderParser {

		@Override
		public String getKey() {
			return "moveset";
		}

		@Override
		public Optional<String> parse(IPlaceholderContext context) {
			if (context.getArguments().isPresent()) {
				List<String> arguments = context.getArguments().get();
				if (arguments.size() > 0) {
					Moveset source = context.getAssociation(Moveset.class).orElse(null);
					if (source != null) {
						try {
							return getMoveIfPresent(source, Integer.parseInt(arguments.get(0)));
						} catch (Exception ignore) {
						}
					}
				}
			}
			return Optional.empty();
		}

		private Optional<String> getMoveIfPresent(Moveset moveset, int index) {
			if (moveset.size() >= index) {
				return Optional.of(moveset.get(index - 1).getActualMove().getLocalizedName());
			}

			return Optional.of("???");
		}

	}
}
