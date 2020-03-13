package ca.landonjw.remoraids.implementation.ui.commandline;

import ca.landonjw.remoraids.implementation.ui.graphical.GUIRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class Editor extends CommandBase {

    @Override
    public String getName() {
        return "registry";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/remoraids registry";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(sender instanceof EntityPlayerMP){
            GUIRegistry registry = new GUIRegistry((EntityPlayerMP) sender);
            registry.openRegistry();
        }
        else{
            sender.sendMessage(new TextComponentString("You must be in-game to use this command!"));
        }
    }

}
