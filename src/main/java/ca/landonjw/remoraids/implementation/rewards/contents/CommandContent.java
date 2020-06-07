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
public class CommandContent extends RewardContentBase {

    /** Command to be executed. */
    private String command;

    /**
     * Default constructor for the command content.
     *
     * @param command the command to be rewarded
     * @throws NullPointerException if command is null
     */
    public CommandContent(@Nonnull String command){
        super("Command: " + command);
        this.command = Objects.requireNonNull(command);
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param command     the command to be rewarded
     * @param description description for the reward content
     * @throws NullPointerException if command is null
     */
    public CommandContent(@Nonnull String command, @Nullable String description){
        super(description == null ? "Command: " + command : description);
        this.command = command;
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
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Items.PAPER);
        item.setStackDisplayName(getDescription());
        return item;
    }

}
