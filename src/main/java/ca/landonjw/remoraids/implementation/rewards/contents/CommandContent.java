package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A command that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class CommandContent implements IRewardContent {

    /** Command to be executed. */
    private String command;
    /** A custom description to be used for the item. */
    private String customDescription;

    /**
     * Default constructor for the command content.
     *
     * @param command the command to be rewarded
     * @throws NullPointerException if command is null
     */
    public CommandContent(@Nonnull String command){
        this.command = Objects.requireNonNull(command);
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param command              the command to be rewarded
     * @param customDescription custom description for the reward content
     * @throws NullPointerException if command is null
     */
    public CommandContent(@Nonnull String command, @Nullable String customDescription){
        this.command = Objects.requireNonNull(command);
        this.customDescription = customDescription;
    }

    /** {@inheritDoc} */
    @Override
    public void give(EntityPlayerMP player) {
        String parsedCommand = command.replace("{player}", player.getName());
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        server.getCommandManager().executeCommand(server, parsedCommand);
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        if(customDescription == null){
            String defaultDescription = "Command: " + command;
            return defaultDescription;
        }
        else{
            return customDescription;
        }
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Items.PAPER);
        item.setStackDisplayName(getDescription());
        return item;
    }

}
