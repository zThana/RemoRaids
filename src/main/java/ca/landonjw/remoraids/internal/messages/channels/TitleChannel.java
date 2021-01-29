package ca.landonjw.remoraids.internal.messages.channels;

import org.checkerframework.checker.nullness.qual.NonNull;

import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class TitleChannel implements IMessageChannel {

	private SPacketTitle.Type type;

	public TitleChannel(SPacketTitle.Type type) {
		this.type = type;
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull String message) {
		sendMessage(player, new TextComponentString(message));
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull ITextComponent message) {
		player.connection.sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, new TextComponentString("")));
		player.connection.sendPacket(new SPacketTitle(type, message));
	}

}