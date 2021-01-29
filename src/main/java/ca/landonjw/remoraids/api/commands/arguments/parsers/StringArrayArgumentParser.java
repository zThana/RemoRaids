package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class StringArrayArgumentParser implements IArgumentParser<String[]> {

	private final Pattern ARRAY_PATTERN = Pattern.compile("(?<=\\[)(.*?)(?=])");

	@Override
	public Optional<String[]> parse(@Nonnull String argument) {
		Matcher matcher = ARRAY_PATTERN.matcher(argument);

		// Return out if argument is not enclosed in array notation
		if (!matcher.find()) {
			return Optional.empty();
		}

		// Split into separate values
		String arrayStr = matcher.group();
		return Optional.of(arrayStr.split(","));
	}

}
