package ca.landonjw.remoraids.implementation.rewards.contents;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * A command that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class CommandContent implements IRewardContent {

	/** The description for the command content. */
	private String description;
	/** Command to be executed. */
	private String command;

	/**
	 * Default constructor for the command content.
	 *
	 * @param command the command to be rewarded
	 * @throws NullPointerException if command is null
	 */
	public CommandContent(@Nonnull String command) {
		this.command = Objects.requireNonNull(command);
	}

	/**
	 * Constructor that allows for user to supply a custom description.
	 *
	 * @param command     the command to be rewarded
	 * @param description description for the reward content
	 * @throws NullPointerException if command is null
	 */
	public CommandContent(@Nonnull String command, @Nullable String description) {
		this(command);
		this.description = description;
	}

	/** {@inheritDoc} */
	@Override
	public void give(EntityPlayerMP player) {
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(EntityPlayerMP.class, () -> player).build();
		String parsedCommand = service.interpret(command, context);
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		server.getCommandManager().executeCommand(server, parsedCommand);
	}

	@Override
	public String getDescription() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(String.class, () -> command).build();
		if (description == null) {
			return service.interpret(config.get(MessageConfig.COMMAND_REWARD_CONTENT_TITLE), context);
		} else {
			return service.interpret(description, context);
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemStack toItemStack() {
		ItemStack item = new ItemStack(Items.PAPER);
		item.setStackDisplayName(getDescription());
		return item;
	}

	@Override
	public JObject serialize() {
		JObject data = new JObject().add("command", command);
		if (description != null) {
			data.add("description", description);
		}
		return data;
	}
}
