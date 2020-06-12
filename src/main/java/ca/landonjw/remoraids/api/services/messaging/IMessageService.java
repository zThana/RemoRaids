package ca.landonjw.remoraids.api.services.messaging;

import ca.landonjw.remoraids.api.services.IService;

import java.util.function.Supplier;

public interface IMessageService extends IService {

	/**
	 * Allows for message interpretation by parsing the incoming input and applying color codes
	 * as well as filling placeholder data when available.
	 *
	 * <p>For placeholders, this setup allows for contextual arguments to be applied to your placeholders.
	 * For example, you can use {player} to read the players name by default. However, you can also do
	 * something such as {player|uuid} or {balance|format=#,###.0} to fetch the player's UUID or
	 * their balance with the specified format respectively. Note that a placeholder parser must implement
	 * arguments for any to actually be effective.</p>
	 *
	 * @param input The message input that is desired to be interpreted
	 * @param association The object this message is being sent to. This uses a supplier to ensure objects
	 *                    that shouldn't persist are only available for the request.
	 * @return The input string interpreted, applying color codes and such respectively
	 */
	String interpret(String input, Supplier<Object> association);

}
