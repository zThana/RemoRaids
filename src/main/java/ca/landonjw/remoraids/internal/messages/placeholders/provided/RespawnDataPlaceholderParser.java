package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RespawnDataPlaceholderParser implements IPlaceholderParser {

    @Override
    public String getKey() {
        return "respawnvalue";
    }

    @Override
    public Optional<String> parse(IPlaceholderContext context) {
        IBossSpawner.IRespawnData data = context.getAssociation(IBossSpawner.IRespawnData.class).orElse(null);
        if(data != null){
            List<String> arguments = context.getArguments().orElse(Collections.emptyList());
            if(arguments.size() == 1){
                switch(arguments.get(0)){
                    case "current":
                        return Optional.of("" + (data.getTotalRespawns() - data.getRemainingRespawns()));
                    case "total":
                        return Optional.of("" + data.getTotalRespawns());
                    case "remaining":
                        return Optional.of("" + data.getRemainingRespawns());
                }
            }
        }
        return Optional.empty();
    }

}
