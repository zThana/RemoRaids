package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.commands.arguments.parsers.DoubleArgumentParser;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class SizeArgument implements IRaidsArgument {

	private final DoubleArgumentParser doubleParser = new DoubleArgumentParser();

	@Override
	public List<String> getTokens() {
		return Lists.newArrayList("size", "s");
	}

	@Override
	public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
		if (context.getAssociation(IBoss.IBossBuilder.class).isPresent()) {
			IBoss.IBossBuilder builder = context.getAssociation(IBoss.IBossBuilder.class).get();

			double size = doubleParser.parse(value).orElseThrow(() -> new IllegalArgumentException("Illegal size value"));
			if (size < 0)
				throw new IllegalArgumentException("Size cannot be less than or equal to 0");
			builder.size((float) size);
		}
	}

}
