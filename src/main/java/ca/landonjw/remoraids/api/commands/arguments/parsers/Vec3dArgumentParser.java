package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import net.minecraft.util.math.Vec3d;

public class Vec3dArgumentParser implements IArgumentParser<Vec3d> {

	private final Pattern ARRAY_PATTERN = Pattern.compile("(?<=\\[)(.*?)(?=])");

	@Override
	public Optional<Vec3d> parse(@Nonnull String argument) {
		Matcher matcher = ARRAY_PATTERN.matcher(argument);

		// Return out if argument is not enclosed in array notation
		if (!matcher.find()) {
			return Optional.empty();
		}

		// Split into separate values
		String arrayStr = matcher.group();
		String[] arrayElements = arrayStr.split(",");

		// Return out if there isn't an X, Y, and Z, or there's extra bad data
		if (arrayElements.length != 3) {
			return Optional.empty();
		}

		try {
			double x = Double.parseDouble(arrayElements[0]);
			double y = Double.parseDouble(arrayElements[1]);
			double z = Double.parseDouble(arrayElements[2]);

			return Optional.of(new Vec3d(x, y, z));
		} catch (NumberFormatException e) {
			return Optional.empty();
		}
	}

}
