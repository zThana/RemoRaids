package ca.landonjw.remoraids.api.rewards;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.List;

/**
 * Interface for a reward for a boss.
 * When a boss dies, these rewards may be distributed.
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface IReward {

    /**
     * Distributes the rewards to players who battled a boss.
     *
     * @param battle battle to distribute reward for.
     */
    void distributeReward(IBossBattle battle);

    /**
     * Gets all players that will receive the reward.
     *
     * @param boss the boss that the reward is being distributed to.
     *
     * @return list of players that will receive the reward upon distribution.
     */
    List<EntityPlayerMP> getWinnersList(IBossEntity boss);

    /**
     * Gets the contents of the reward.
     *
     * @return the contents of the reward.
     */
    List<IRewardContent> getContents();

    /**
     * Adds contents to the reward.
     *
     * @param contents contents to add
     * @throws IllegalArgumentException if a reward content is null
     */
    void addContents(IRewardContent... contents);

    /**
     * Removes contents from the reward.
     *
     * @param contents contents to remove
     * @throws IllegalArgumentException if a reward content is null
     */
    void removeContents(IRewardContent... contents);

    /**
     * Clears all contents from the reward.
     */
    void clearContents();

    /**
     * Gets a description of the reward and it's contents.
     *
     * @return A description of the reward and it's contents.
     */
    String getDescription();

}
