package ca.landonjw.remoraids.internal.services.placeholders.provided;

import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpawnLocationParser implements IPlaceholderParser {

    @Override
    public String getKey() {
        return "location";
    }

    @Override
    public Optional<String> parse(IPlaceholderContext context) {
        IBossSpawnLocation location = context.get(IBossSpawnLocation.class).orElse(null);

        if(location != null){
            List<String> arguments = context.getArguments().orElse(Collections.emptyList());
            if(arguments.size() == 1){
                switch(arguments.get(0).toLowerCase()){
                    case "world":
                        return Optional.of(location.getWorld().getWorldInfo().getWorldName());
                    case "x":
                        return Optional.of("" + location.getX());
                    case "y":
                        return Optional.of("" + location.getY());
                    case "z":
                        return Optional.of("" + location.getZ());
                    case "rotation":
                        return Optional.of("" + location.getRotation());
                }
            }
            return Optional.of("[" + location.getX() + "," + location.getY() + "," + location.getZ() + "]");
        }
        return Optional.empty();
    }

}
