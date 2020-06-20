package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                switch(arguments.get(0).toLowerCase()){
                    case "species":
                        return Optional.of(pokemon.getSpecies().getPokemonName());
                    case "displayname":
                        return Optional.of(pokemon.getDisplayName());
                    case "level":
                        return Optional.of("" + pokemon.getLevel());
                    case "nature":
                        return Optional.of("" + pokemon.getNature().getLocalizedName());
                    case "ability":
                        return Optional.of("" + pokemon.getAbilityName());
                }
            }
            return Optional.of(pokemon.getSpecies().getPokemonName());
        }
        return Optional.empty();
    }

}
