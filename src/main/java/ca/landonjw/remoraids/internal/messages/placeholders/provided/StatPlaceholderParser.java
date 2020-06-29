package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StatPlaceholderParser implements IPlaceholderParser {

    @Override
    public String getKey() {
        return "stat";
    }

    @Override
    public Optional<String> parse(IPlaceholderContext context) {
        StatsType stat = context.getAssociation(StatsType.class).orElse(null);
        Integer value = context.getAssociation(Integer.class).orElse(null);

        if(stat != null){
            List<String> arguments = context.getArguments().orElse(Collections.emptyList());
            if(arguments.size() == 1 && arguments.get(0).equalsIgnoreCase("value")){
                if(value != null) {
                    return Optional.of("" + value);
                }
            }
            return Optional.of(stat.getTranslatedName().getUnformattedComponentText());
        }
        return Optional.empty();
    }

}
