package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An item that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class ItemContent extends RewardContentBase {

    /** The item to be rewarded. */
    private ItemStack item;

    /**
     * Default constructor for the item content.
     *
     * @param item the item to be rewarded
     * @throws NullPointerException if itemstack is null
     */
    public ItemContent(@Nonnull ItemStack item){
        super("Item: " + item.getDisplayName());
        this.item = Objects.requireNonNull(item);
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param item              the item to be rewarded
     * @param description description for the reward content
     * @throws NullPointerException if itemstack is null
     */
    public ItemContent(@Nonnull ItemStack item, @Nullable String description){
        super(description == null ? "Item: " + item.getDisplayName() : description);
        this.item = Objects.requireNonNull(item);
    }

    /** {@inheritDoc} **/
    @Override
    public void give(EntityPlayerMP player) {
        player.inventory.addItemStackToInventory(item);
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack toItemStack() {
        ItemStack copy = item.copy();
        copy.setStackDisplayName(getDescription());
        return copy;
    }

}
