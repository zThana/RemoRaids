package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.StringArrayArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class MovesArgument implements IRaidsArgument {

	private final StringArrayArgumentParser stringArrParser = new StringArrayArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("moves", "m");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBoss.IBossBuilder.class).isPresent()) {
			IBoss.IBossBuilder builder = context.getAssociation(IBoss.IBossBuilder.class).get();

			String[] moves = stringArrParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal moves value"));

			Moveset moveset = new Moveset();
			for (int i = 0; i < moves.length && i < 4; i++) {
				Attack attack = new Attack(moves[i].replace("_", " "));
				if (attack.getMove() == null) {
					break;
				}
				moveset.add(attack);
			}
			builder.moveset(moveset);
		}
	}

}
