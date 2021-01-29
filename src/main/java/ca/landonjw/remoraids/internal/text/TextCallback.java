package ca.landonjw.remoraids.internal.text;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class TextCallback {

	private UUID callbackUUID;
	private Consumer<ICommandSender> consumer;
	private boolean onlyInvokeOnce;

	private Set<UUID> playersInvoked = new HashSet<>();

	public TextCallback(Consumer<ICommandSender> consumer, boolean onlyInvokeOnce) {
		this.callbackUUID = UUID.randomUUID();
		this.consumer = consumer;
		this.onlyInvokeOnce = onlyInvokeOnce;
	}

	public UUID getUUID() {
		return callbackUUID;
	}

	public Consumer<ICommandSender> getConsumer() {
		return consumer;
	}

	public void tryInvokeConsumer(EntityPlayerMP player) {
		if (onlyInvokeOnce) {
			if (playersInvoked.contains(player.getUniqueID())) {
				return;
			}
		}
		consumer.accept(player);
		if (onlyInvokeOnce) {
			playersInvoked.add(player.getUniqueID());
		}
	}

	public boolean onlyInvokeOnce() {
		return onlyInvokeOnce;
	}

}
