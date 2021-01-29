package ca.landonjw.remoraids.implementation.commands.executors;

import java.util.Collections;
import java.util.List;

import ca.landonjw.remoraids.api.boss.IBossEntityRegistry;
import ca.landonjw.remoraids.implementation.ui.pages.RegistryUI;
import ca.landonjw.remoraids.internal.commands.RaidsCommandExecutor;
import net.minecraft.command.CommandException;
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
public class RaidBossRegistryUIExecutor implements RaidsCommandExecutor {

	/** {@inheritDoc} */
	@Override
	public String getUsage(ICommandSender source) {
		return "/raids registry";
	}

	/** {@inheritDoc} */
	@Override
	public void execute(MinecraftServer server, ICommandSender source, String[] args) throws CommandException {
		RegistryUI registry = new RegistryUI((EntityPlayerMP) source);
		registry.open();
	}

	@Override
	public List<String> getTabCompletionOptions(String[] args) {
		return Collections.emptyList();
	}

}
