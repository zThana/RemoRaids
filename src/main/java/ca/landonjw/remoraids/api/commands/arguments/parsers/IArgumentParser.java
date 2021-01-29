package ca.landonjw.remoraids.api.commands.arguments.parsers;

import java.util.Optional;

import javax.annotation.Nonnull;

public interface IArgumentParser<T> {

	Optional<T> parse(@Nonnull String argument);

}
