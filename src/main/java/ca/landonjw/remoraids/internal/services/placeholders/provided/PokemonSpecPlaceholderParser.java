package ca.landonjw.remoraids.internal.services.placeholders.provided;

import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;

import java.util.Optional;

public class PokemonSpecPlaceholderParser implements IPlaceholderParser {

    @Override
    public String getKey() {
        return "spec";
    }

    @Override
    public Optional<String> parse(IPlaceholderContext context) {
        PokemonSpec spec = context.get(PokemonSpec.class).orElse(null);

        if(spec != null){
            return Optional.ofNullable(spec.name);
        }
        return Optional.empty();
    }

}
