package ca.landonjw.remoraids.internal.text;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Implements a command that will execute a given callback if the callback's UUID is given as an argument.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class Callback extends CommandBase {

    /** The command name for the callback. */
    public static final String CALLBACK_COMMAND = "rrcallback";

    /** Map of player callback UUIDs and consumers to be run when given correct UUID. */
    private static Map<UUID, Consumer<ICommandSender>> consumerMap = new HashMap<>();

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
        if(args.length == 1){
            try{
                UUID uuid = UUID.fromString(args[0]);
                if(consumerMap.containsKey(uuid)){
                    consumerMap.get(uuid).accept(sender);
                }
            }
            catch(IllegalArgumentException ignored){

            }
        }
    }

    /**
     * Adds a callback that can be executed via command.
     *
     * @param callbackUUID the uuid of the callback
     * @param consumer     the consumer to run when command is properly executed
     */
    public static void addCallback(UUID callbackUUID, Consumer<ICommandSender> consumer) {
        consumerMap.put(callbackUUID, consumer);
    }

    /**
     * Adds a callback that can be executed via command.
     *
     * @param callbackUUID the uuid of the callback
     * @param runnable     the runnable to run when command is properly executed
     */
    public static void addCallback(UUID callbackUUID, Runnable runnable){
        consumerMap.put(callbackUUID, (task) -> runnable.run());
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
