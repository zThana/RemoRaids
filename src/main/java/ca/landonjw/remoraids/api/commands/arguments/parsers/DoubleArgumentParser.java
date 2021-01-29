package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;

import javax.annotation.Nonnull;

public class DoubleArgumentParser implements IArgumentParser<Double> {

	@Override
	public Optional<Double> parse(@Nonnull String argument) {
		try {
			double value = Double.parseDouble(argument);
			return Optional.of(value);
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

}
