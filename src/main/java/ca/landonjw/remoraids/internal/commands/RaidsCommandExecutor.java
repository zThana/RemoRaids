package ca.landonjw.remoraids.internal.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface RaidsCommandExecutor {

	String getUsage(ICommandSender source);

	void execute(MinecraftServer server, ICommandSender source, String[] args) throws CommandException;

}
