package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.IntegerArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class SingleStatArgument implements IRaidsArgument {

	private final IntegerArgumentParser intParser = new IntegerArgumentParser();
	private List<String> tokens;
	private StatsType statType;

	public SingleStatArgument(@Nonnull StatsType statType) {
		this.statType = statType;
		switch (statType) {
		case HP:
			this.tokens = Lists.newArrayList("health", "hp");
			break;
		case Attack:
			this.tokens = Lists.newArrayList("attack", "atk");
			break;
		case Defence:
			this.tokens = Lists.newArrayList("defence", "def");
			break;
		case SpecialAttack:
			this.tokens = Lists.newArrayList("special-attack", "spatk");
			break;
		case SpecialDefence:
			this.tokens = Lists.newArrayList("special-defence", "spdef");
			break;
		case Speed:
			this.tokens = Lists.newArrayList("speed", "spe");
			break;
		default:
			throw new IllegalArgumentException("unexpected stat type");
		}
	}

	@Override
	public List<String> getTokens() {
		return tokens;
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBoss.IBossBuilder.class).isPresent()) {
			IBoss.IBossBuilder builder = context.getAssociation(IBoss.IBossBuilder.class).get();

			int statValue = intParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal stat value"));
			if (statValue < 0) {
				throw new IllegalArgumentException("Stat must not be below 0");
			}
			if (statValue > Short.MAX_VALUE) {
				throw new IllegalArgumentException("Stat must not exceed 32767");
			}
			builder.stat(statType, statValue, false);
		}
	}

}
