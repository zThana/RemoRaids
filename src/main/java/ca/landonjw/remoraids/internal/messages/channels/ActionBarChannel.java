package ca.landonjw.remoraids.internal.messages.channels;

import org.checkerframework.checker.nullness.qual.NonNull;

import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ActionBarChannel implements IMessageChannel {

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull String message) {
		sendMessage(player, new TextComponentString(message));
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull ITextComponent message) {
		player.sendStatusMessage(message, true);
	}

}
