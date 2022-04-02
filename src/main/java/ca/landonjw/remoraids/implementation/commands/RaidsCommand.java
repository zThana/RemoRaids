package ca.landonjw.remoraids.implementation.commands;

import java.util.StringJoiner;

import ca.landonjw.remoraids.implementation.commands.create.CreateRaidBossCommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class RaidsCommand extends CommandTreeBase {

    public RaidsCommand() {
        this.addSubcommand(new CreateRaidBossCommand());
        this.addSubcommand(new RaidsRegistryUICommand());
        this.addSubcommand(new RaidsReload());
    }

    @Override
    public String getName() {
        return "raids";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        StringJoiner commandJoiner = new StringJoiner("/");
        getSubCommands().forEach((command) -> commandJoiner.add(command.getName()));
        return "/" + getName() + " <" + commandJoiner.toString() + ">";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, "remoraids.commands.base");
    }

}
