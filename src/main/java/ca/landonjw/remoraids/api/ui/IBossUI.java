package ca.landonjw.remoraids.api.ui;

import java.util.Optional;

import ca.landonjw.remoraids.api.boss.IBossEntity;

/**
 * Represents a user interface to be used for interacting with a boss entity.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossUI {

	/**
	 * Opens the user interface.
	 */
	void open();

	/**
	 * Gets the user interface that opened this user interface, if available.
	 * The optional may contain a null value if the user interface was not opened by a compatible user interface,
	 * or was opened via an external method (ie. command).
	 * This allows for you to reference back to the previous page, in order to make a back button.
	 *
	 * @return the user interface that opened this user interface
	 */
	Optional<IBossUI> getSource();

	/**
	 * Gets the boss entity that this user interface is interacting with.
	 *
	 * @return the boss entity this user interface is interacting with
	 */
	IBossEntity getBossEntity();

}
