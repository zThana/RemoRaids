package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

public class IntArrayArgumentParser implements IArgumentParser<Integer[]> {

	private final Pattern ARRAY_PATTERN = Pattern.compile("(?<=\\[)(.*?)(?=])");

	@Override
	public Optional<Integer[]> parse(@Nonnull String argument) {
		Matcher matcher = ARRAY_PATTERN.matcher(argument);

		// Return out if argument is not enclosed in array notation
		if (!matcher.find()) {
			return Optional.empty();
		}

		// Split into separate values
		String arrayStr = matcher.group();
		String[] strArray = arrayStr.split(",");
		Integer[] intArray = new Integer[strArray.length];

		for (int i = 0; i < strArray.length; i++) {
			try {
				intArray[i] = Integer.parseInt(strArray[i]);
			} catch (NumberFormatException e) {
				return Optional.empty();
			}
		}
		return Optional.of(intArray);
	}

}
