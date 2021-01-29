package ca.landonjw.remoraids.api.rewards;

import java.util.List;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.util.DataSerializable;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Interface for a reward for a boss.
 * When a boss dies, these rewards may be distributed.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IReward extends DataSerializable {

	/**
	 * Distributes the rewards to players who battled a boss.
	 *
	 * @param player the player to distribute reward to.
	 */
	void distributeReward(EntityPlayerMP player);

	/**
	 * Gets all players that will receive the reward.
	 *
	 * @param battle battle to get winners for.
	 *
	 * @return list of players that will receive the reward upon distribution.
	 */
	List<EntityPlayerMP> getWinnersList(IBossBattle battle);

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
	 */
	void addContents(IRewardContent... contents);

	/**
	 * Removes contents from the reward.
	 *
	 * @param contents contents to remove
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

	int getPriority();

}