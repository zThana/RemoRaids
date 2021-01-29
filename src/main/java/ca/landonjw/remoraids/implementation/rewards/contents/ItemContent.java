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
import net.minecraft.item.ItemStack;

/**
 * An item that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class ItemContent implements IRewardContent {

	/** The description of the reward content. */
	private String description;
	/** The item to be rewarded. */
	private ItemStack item;

	/**
	 * Default constructor for the item content.
	 *
	 * @param item the item to be rewarded
	 * @throws NullPointerException if itemstack is null
	 */
	public ItemContent(@Nonnull ItemStack item) {
		this.item = Objects.requireNonNull(item);
	}

	/**
	 * Constructor that allows for user to supply a custom description.
	 *
	 * @param item        the item to be rewarded
	 * @param description description for the reward content
	 * @throws NullPointerException if itemstack is null
	 */
	public ItemContent(@Nonnull ItemStack item, @Nullable String description) {
		this(item);
		this.description = description;
	}

	/** {@inheritDoc} **/
	@Override
	public void give(EntityPlayerMP player) {
		player.inventory.addItemStackToInventory(item.copy());
	}

	@Override
	public String getDescription() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(ItemStack.class, () -> item).build();
		if (description == null) {
			return service.interpret(config.get(MessageConfig.ITEM_REWARD_CONTENT_TITLE), context);
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
		ItemStack copy = item.copy();
		copy.setStackDisplayName(getDescription());
		return copy;
	}

	@Override
	public JObject serialize() {
		JObject data = new JObject().add("item", item.getItem().getRegistryName().toString()).add("amount", item.getCount());
		if (description != null) {
			data.add("description", description);
		}
		return data;
	}
}
