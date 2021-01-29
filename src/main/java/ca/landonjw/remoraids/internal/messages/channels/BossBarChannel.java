package ca.landonjw.remoraids.internal.messages.channels;

import org.checkerframework.checker.nullness.qual.NonNull;

import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.internal.tasks.Task;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;

public class BossBarChannel implements IMessageChannel {

	private BossInfo.Color color;
	private BossInfo.Overlay overlay;

	public BossBarChannel(BossInfo.Color color, BossInfo.Overlay overlay) {
		this.color = color;
		this.overlay = overlay;
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull String message) {
		sendMessage(player, new TextComponentString(message));
	}

	@Override
	public void sendMessage(@NonNull EntityPlayerMP player, @NonNull ITextComponent message) {
		BossInfoServer bossBar = new BossInfoServer(message, color, overlay);
		bossBar.setVisible(true);
		bossBar.addPlayer(player);
		Task.builder().execute(() -> bossBar.removePlayer(player)).iterations(1).delay(59).build();
	}

}
