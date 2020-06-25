package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PokemonPlaceholderParser implements IPlaceholderParser {

    @Override
    public String getKey() {
        return "pokemon";
    }

    @Override
    public Optional<String> parse(IPlaceholderContext context) {
        Pokemon pokemon = context.getAssociation(Pokemon.class).orElse(null);

        if(pokemon != null){
            List<String> arguments = context.getArguments().orElse(Collections.emptyList());
            if(arguments.size() == 1){
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
        ;

        private Function<Pokemon, Object> parser;

        ArgumentParser(Function<Pokemon, Object> parser) {
            this.parser = parser;
        }

        public static Optional<String> parse(String key, Pokemon context) {
            return Arrays.stream(values()).filter(x -> x.name().toLowerCase().equals(key.toLowerCase())).findAny()
                    .map(x -> x.parser.apply(context))
                    .map(Object::toString);
        }
    }
}
