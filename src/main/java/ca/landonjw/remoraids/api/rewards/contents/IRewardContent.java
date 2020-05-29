package ca.landonjw.remoraids.api.rewards.contents;

import ca.landonjw.remoraids.api.rewards.IReward;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Interface for content to be given by a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface IRewardContent {

    /**
     * Gives the content to a player.
     *
     * @param player the player to give the content to
     * @throws IllegalArgumentException if player is null
     */
    void give(EntityPlayerMP player);

    /**
     * Gets the description for the reward content.
     *
     * @return description for the reward content
     */
    String getDescription();

    ItemStack toItemStack();

}
