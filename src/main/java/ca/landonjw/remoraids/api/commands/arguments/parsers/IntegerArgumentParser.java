package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;

import javax.annotation.Nonnull;

public class IntegerArgumentParser implements IArgumentParser<Integer> {

	@Override
	public Optional<Integer> parse(@Nonnull String argument) {
		try {
			int value = Integer.parseInt(argument);
			return Optional.of(value);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

}