package ca.landonjw.remoraids.implementation.commands.executors;

import ca.landonjw.remoraids.implementation.ui.GUIRegistry;
import ca.landonjw.remoraids.internal.commands.RaidsCommandExecutor;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class RaidBossRegistryUIExecutor implements RaidsCommandExecutor {

	@Override
	public String getUsage(ICommandSender source) {
		return "/raids registry";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender source, String[] args) throws CommandException {
		GUIRegistry registry = new GUIRegistry((EntityPlayerMP) source);
		registry.open();
	}

}
