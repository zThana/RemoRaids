package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;

public class PersistentArgument implements IRaidsArgument {

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("persistent", "p");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBossSpawner.IBossSpawnerBuilder.class).isPresent()) {
			IBossSpawner.IBossSpawnerBuilder builder = context.getAssociation(IBossSpawner.IBossSpawnerBuilder.class).get();
			builder.persists(true);
		}
	}

}
