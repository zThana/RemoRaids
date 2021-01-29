package ca.landonjw.remoraids.api.messages.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public interface IMessageService extends IService {

	/**
	 * Allows for message interpretation by parsing the incoming input and applying color codes
	 * as well as filling placeholder data when available.
	 *
	 * <p>
	 * For placeholders, this setup allows for contextual arguments to be applied to your placeholders.
	 * For example, you can use {player} to read the players name by default. However, you can also do
	 * something such as {player|uuid} or {balance|format=#,###.0} to fetch the player's UUID or
	 * their balance with the specified format respectively. Note that a placeholder parser must implement
	 * arguments for any to actually be effective.
	 * </p>
	 *
	 * @param input   The message input that is desired to be interpreted
	 * @param context The context of the parsing. This allows for information to be collected about specific
	 *                items related to the parsing. For example, you may store a player to fetch their name for
	 *                {player}.
	 * @return The input string interpreted, applying color codes and such respectively
	 */
	String interpret(@Nonnull String input, @Nullable IParsingContext context);

	default List<String> interpret(@Nonnull List<String> input, @Nullable IParsingContext context) {
		return input.stream().map(x -> this.interpret(x, context)).collect(Collectors.toList());
	}

}
