package ca.landonjw.remoraids.internal.messages.channels;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;

import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.internal.tasks.Task;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;

public class OverlayChannel implements IMessageChannel {

	private NoticeOverlay.Builder overlayBuilder;

	public OverlayChannel(NoticeOverlay.Builder overlayBuilder) {
		this.overlayBuilder = overlayBuilder;
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull String message) {
		overlayBuilder.setLines(message);
		overlayBuilder.sendTo(player);
		Task.builder().execute(() -> NoticeOverlay.hide(player)).iterations(1).delay(59).build();
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull ITextComponent message) {
		sendMessage(player, message.getFormattedText());
	}

}
