package ca.landonjw.remoraids.implementation.commands.create.arguments;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

public class AllowDynamaxArgument implements IRaidsArgument {

    @Override
    public List<String> getTokens() {
        return Lists.newArrayList("allow-dynamax", "dynamax");
    }

    @Override
    public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
        if (context.getAssociation(IBossSpawner.IBossSpawnerBuilder.class).isPresent()) {
            IBossSpawner.IBossSpawnerBuilder spawnerBuilder = context.getAssociation(IBossSpawner.IBossSpawnerBuilder.class).get();

            spawnerBuilder.allowDynamax(Boolean.parseBoolean(value));
        } else {
            System.out.println("no builder found :(");
        }
    }
}
