package ca.landonjw.remoraids.implementation.commands.create.arguments;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.List;

public class ShowOverlayArgument implements IRaidsArgument {

    @Override
    public List<String> getTokens() {
        return Lists.newArrayList("show-overlay", "sh-ov");
    }

    @Override
    public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
        if (context.getAssociation(IBossSpawner.IBossSpawnerBuilder.class).isPresent()) {
            IBossSpawner.IBossSpawnerBuilder spawnerBuilder = context.getAssociation(IBossSpawner.IBossSpawnerBuilder.class).get();

            spawnerBuilder.overlayText(RemoRaids.getMessageConfig().get(MessageConfig.OVERLAY_TEXT), true);
        } else {
            System.out.println("no builder found :(");
        }
    }
}
