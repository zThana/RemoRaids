package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.IntegerArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.implementation.battles.restraints.CapacityRestraint;

public class CapacityRestraintArgument implements IRaidsArgument {

	private IntegerArgumentParser intParser = new IntegerArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("capacity-restraint", "cap");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBossBattleSettings.class).isPresent()) {
			IBossBattleSettings settings = context.getAssociation(IBossBattleSettings.class).get();

			for (IBattleRestraint restraint : settings.getBattleRestraints()) {
				if (restraint instanceof CapacityRestraint)
					return;
			}

			int capacity = intParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal capacity value"));
			if (capacity < 0)
				throw new IllegalArgumentException("Capacity must not be lower than 0");
			settings.getBattleRestraints().add(new CapacityRestraint(capacity));
		}
	}

}
