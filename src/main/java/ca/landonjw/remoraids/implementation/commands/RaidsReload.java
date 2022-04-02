package ca.landonjw.remoraids.implementation.commands;

import ca.landonjw.remoraids.RemoRaids;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class RaidsReload extends CommandBase {

    @Override
    public String getName() {
        return "reload";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsage(@Nonnull ICommandSender source) {
        return "/raids reload";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender source, @Nonnull String[] args) {
        server.sendMessage(new TextComponentString("Reloading remo raids..."));
        RemoRaids.getInstance().reload();
        server.sendMessage(new TextComponentString("Reload complete."));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender.canUseCommand(4, "remoraids.commands.reload");
    }

}
