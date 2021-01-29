package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.IntArrayArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class FullStatsArgument implements IRaidsArgument {

	private final IntArrayArgumentParser intArrParser = new IntArrayArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("stats", "st");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBoss.IBossBuilder.class).isPresent()) {
			IBoss.IBossBuilder builder = context.getAssociation(IBoss.IBossBuilder.class).get();

			Integer[] stats = intArrParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal stats list value"));
			if (stats.length != 6)
				throw new IllegalArgumentException("List must contain 6 stats");

			for (int i = 0; i < stats.length; i++) {
				StatsType statType = StatsType.getStatValues()[i];
				int statValue = stats[i];
				if (statValue < 0) {
					throw new IllegalArgumentException("Stats must not be below 0");
				}
				if (statValue > Short.MAX_VALUE) {
					throw new IllegalArgumentException("Stats must not exceed 32767");
				}
				builder.stat(statType, statValue, false);
			}
		}
	}

}
