package ca.landonjw.remoraids.internal.text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Implements a command that will execute a given callback if the callback's UUID is given as an argument.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class Callback extends CommandBase {

	/** The command name for the callback. */
	public static final String CALLBACK_COMMAND = "rrcallback";

	/** Map of player callback UUIDs and consumers to be run when given correct UUID. */
	private static Map<UUID, TextCallback> callbackMap = new HashMap<>();

	/**
	 * Gets the name of the command.
	 *
	 * @return name of the command
	 */
	@Override
	public String getName() {
		return CALLBACK_COMMAND;
	}

	/**
	 * Gets the correct usage of the command.
	 *
	 * @param sender the sender of the command
	 * @return correct usage of the command
	 */
	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + CALLBACK_COMMAND + " <uuid>";
	}

	/**
	 * Executes the command. If UUID argument is correct, executes the corresponding consumer.
	 *
	 * @param server the server the command is being run on
	 * @param sender the sender of the command
	 * @param args   the arguments of the command
	 */
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		if (sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP) sender;
			if (args.length == 1) {
				try {
					UUID uuid = UUID.fromString(args[0]);
					if (callbackMap.containsKey(uuid)) {
						callbackMap.get(uuid).tryInvokeConsumer(player);
					}
				} catch (IllegalArgumentException ignored) {

				}
			}
		}
	}

	/**
	 * Creates a callback that can be executed via command.
	 *
	 * @param consumer       the consumer to run when command is properly executed
	 * @param invokeOnlyOnce if consumer should only be able to be invoked once by a given player
	 * @return the text callback created
	 */
	static TextCallback createCallback(Consumer<ICommandSender> consumer, boolean invokeOnlyOnce) {
		TextCallback callback = new TextCallback(consumer, invokeOnlyOnce);
		callbackMap.put(callback.getUUID(), callback);
		return callback;
	}

	/**
	 * Creates a callback that can be executed via command.
	 *
	 * @param runnable       the runnable to run when command is properly executed
	 * @param invokeOnlyOnce if consumer should only be able to be invoked once by a given player
	 * @return the text callback created
	 */
	static TextCallback createCallback(Runnable runnable, boolean invokeOnlyOnce) {
		return createCallback((sender) -> runnable.run(), invokeOnlyOnce);
	}

	/**
	 * Checks if player has permission to run command. Will always return true.
	 *
	 * @param server the server the command is being run on
	 * @param sender the sender of the command
	 * @return true
	 */
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

}
