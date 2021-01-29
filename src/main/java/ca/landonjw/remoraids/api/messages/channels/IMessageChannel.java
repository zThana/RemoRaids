package ca.landonjw.remoraids.api.messages.channels;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

/**
 * Represents a method of messaging a player.
 * An example of a possible implementation would be a channel for an action bar, where the message will\
 * display within the action status bar.
 *
 * @author landonjw
 */
public interface IMessageChannel {

	/**
	 * Sends a message to the player.
	 *
	 * @param player  the player to send the message to
	 * @param message the message to send in string form
	 */
	void sendMessage(@NonNull EntityPlayerMP player, @NonNull String message);

	/**
	 * Sends a message to the player.
	 *
	 * @param player  the player to send the message to
	 * @param message the message to send in text form
	 */
	void sendMessage(@NonNull EntityPlayerMP player, @NonNull ITextComponent message);

}
