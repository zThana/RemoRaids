package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.IntegerArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IRespawnData;

public class RespawnAmountArgument implements IRaidsArgument {

	private final IntegerArgumentParser intParser = new IntegerArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("respawn-amount", "ra");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IRespawnData.IRespawnDataBuilder.class).isPresent()) {
			IRespawnData.IRespawnDataBuilder builder = context.getAssociation(IRespawnData.IRespawnDataBuilder.class).get();
			int spawnAmount = intParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal respawn amount value"));
			if (spawnAmount < -1) {
				throw new IllegalArgumentException("Respawn amount cannot be less than -1");
			}
			builder.count(spawnAmount);
		}
	}

}
