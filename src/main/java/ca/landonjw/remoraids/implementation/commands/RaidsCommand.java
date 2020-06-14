package ca.landonjw.remoraids.implementation.commands;

import ca.landonjw.remoraids.implementation.commands.executors.CreateRaidBossExecutor;
import ca.landonjw.remoraids.implementation.commands.executors.RaidBossRegistryUIExecutor;
import ca.landonjw.remoraids.internal.commands.RaidsCommandExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RaidsCommand extends CommandBase {

	private static Map<String, RaidsCommandExecutor> executors = Maps.newHashMap();

	static {
		executors.put("create", new CreateRaidBossExecutor());
		executors.put("registry", new RaidBossRegistryUIExecutor());
	}

	@Override
	public String getName() {
		return "raids";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		StringJoiner joiner = new StringJoiner(",");
		for (String key : executors.keySet()) {
			joiner.add(key);
		}

		return "/raids (" + joiner.toString() + ") [additional arguments]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1) {
			String sub = args[0].toLowerCase();
			RaidsCommandExecutor executor = executors.get(sub);
			if (executor != null) {
				String[] arguments = new String[args.length - 1];
				System.arraycopy(args, 1, arguments, 0, args.length - 1);
				executor.execute(server, sender, arguments);
			}
			else {
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "Sub-command not recognized!"));
				sender.sendMessage(new TextComponentString(""));
				sender.sendMessage(new TextComponentString(this.getUsage(sender)));
			}
		}
		else {
			sender.sendMessage(new TextComponentString(this.getUsage(sender)));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if(args.length < 2) {
			return getListOfStringsMatchingLastWord(args, executors.keySet());
		}

		String executor = args[0].toLowerCase();
		if(executors.get(executor) != null) {
			return executors.get(executor).getTabCompletionOptions(args);
		}

		return Collections.emptyList();
	}
}
