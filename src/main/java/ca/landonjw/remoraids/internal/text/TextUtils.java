package ca.landonjw.remoraids.internal.text;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

/**
 * Utilities that are used for text sent to players in chat.
 *
 * @author landonjw
 */
public class TextUtils {

	/**
	 * Adds a callback to a piece of text, which will be invoked when a player clicks the text.
	 *
	 * @param text     text to add callback to
	 * @param consumer consumer to add to the text
	 * @return text component with callback added
	 */
	public static ITextComponent addCallback(@Nonnull ITextComponent text, @Nonnull Consumer<ICommandSender> consumer, boolean invokeOnlyOnce) {
		TextCallback callback = Callback.createCallback(consumer, invokeOnlyOnce);

		ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + Callback.CALLBACK_COMMAND + " " + callback.getUUID().toString());

		text.getStyle().setClickEvent(clickEvent);

		return text;
	}

}