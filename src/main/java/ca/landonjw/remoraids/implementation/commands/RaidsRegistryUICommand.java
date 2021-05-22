package ca.landonjw.remoraids.implementation.commands;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.implementation.ui.pages.RegistryUI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Sub-command used for opening the registry UI, which contains all raids currently spawned and registered within the
 * {@link IBossEntityRegistry}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class RaidsRegistryUICommand extends CommandBase {

	@Override
	public String getName() {
		return "registry";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUsage(@Nonnull ICommandSender source) {
		return "/raids registry";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender source, @Nonnull String[] args) {
		RegistryUI registry = new RegistryUI((EntityPlayerMP) source);
		registry.open();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(4, "remoraids.commands.registry");
	}

}
