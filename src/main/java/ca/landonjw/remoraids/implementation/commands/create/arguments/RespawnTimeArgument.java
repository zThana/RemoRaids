package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.IntegerArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IRespawnData;

public class RespawnTimeArgument implements IRaidsArgument {

	private final IntegerArgumentParser intParser = new IntegerArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("respawn-time", "rt");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IRespawnData.IRespawnDataBuilder.class).isPresent()) {
			IRespawnData.IRespawnDataBuilder builder = context.getAssociation(IRespawnData.IRespawnDataBuilder.class).get();
			int respawnTime = intParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal respawn time value"));
			if (respawnTime <= 0) {
				throw new IllegalArgumentException("Respawn time cannot be less than 1 second");
			}
			builder.period(respawnTime, TimeUnit.SECONDS);
		}
	}

}
