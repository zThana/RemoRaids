package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.implementation.ui.commandline.Editor;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.Arrays;
import java.util.List;

public class Base extends CommandTreeBase {

    public Base(){
        addSubcommand(new Editor());
    }

    @Override
    public String getName() {
        return "remoraids";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/remoraids [action]";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rr", "raids");
    }

}
