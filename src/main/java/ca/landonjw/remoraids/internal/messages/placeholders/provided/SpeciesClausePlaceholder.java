package ca.landonjw.remoraids.internal.messages.placeholders.provided;

import java.util.Optional;

import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;

public class SpeciesClausePlaceholder implements IPlaceholderParser {

	@Override
	public String getKey() {
		return "speciesclause";
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		BattleClause clause = context.getAssociation(BattleClause.class).orElse(null);

		if (clause != null)
			return Optional.of(clause.getID());

		return Optional.empty();
	}
}