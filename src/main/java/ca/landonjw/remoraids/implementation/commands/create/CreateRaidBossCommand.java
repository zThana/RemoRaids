package ca.landonjw.remoraids.implementation.commands.create;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;

import ca.landonjw.remoraids.api.commands.arguments.ArgumentService;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CreateRaidBossCommand extends CommandBase {
	private static final ArgumentService argumentService = new ArgumentService(Arrays.stream(CreateCommandArguments.values()).map((value) -> value.getArgument()).collect(Collectors.toList()));

	@Override
	public String getName() {
		return "create";
	}

	@Override
	public String getUsage(ICommandSender source) {
		return "/raids create <spec> [additional arguments]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString(getUsage(sender)));
			return;
		}

		CreationBuilderWrapper creationWrapper = new CreationBuilderWrapper();

		if (sender instanceof EntityPlayerMP)
			creationWrapper.setSpawnLocation((EntityPlayerMP) sender);

		String fullArgString = String.join(" ", args);

		IParsingContext context = creationWrapper.getContext();
		try {
			argumentService.delegateArguments(fullArgString, context);
		} catch (IllegalArgumentException e) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error: " + e.getMessage()));
			return;
		}

		PokemonSpec suppliedSpec = PokemonSpec.from(argumentService.removeArguments(fullArgString).split(" "));
		if (suppliedSpec.name == null || suppliedSpec.name.equals("")) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error: Invalid pokemon spec supplied"));
			return;
		}

		creationWrapper.setBossSpec(suppliedSpec);

		try {
			creationWrapper.spawn();
		} catch (IllegalStateException e) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Error: " + e.getMessage()));
		}
	}

	public static ArgumentService getArgumentService() {
		return argumentService;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(4, "remoraids.commands.create");
	}
}