package ca.landonjw.remoraids.implementation.rewards;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;

import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * A custom drop query to be used for interacting with the Pixelmon drop UI.
 * This is used to distribute rewards to players after a boss battle.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class RewardDropQuery {

	/** The UUID of the player this query is intended for. */
	private UUID playerUUID;
	/** Map of slots within the drop UI and associated contents to those slots */
	private Map<Integer, IRewardContent> contents = Maps.newHashMap();

	/**
	 * Constructor for the drop query.
	 *
	 * @param playerUUID the player that has a reward drop screen
	 * @param reward     reward to be given
	 */
	public RewardDropQuery(UUID playerUUID, DropRewardBase reward) {
		this.playerUUID = playerUUID;
		int slot = 0;
		for (IRewardContent content : reward.getContents()) {
			contents.put(slot, content);
			slot++;
		}
	}

	/**
	 * Takes reward contents from the drop UI and gives it to the player.
	 *
	 * @param slot the slot to take reward from
	 */
	public void take(EntityPlayerMP player, int slot) {
		if (player.getUniqueID().equals(playerUUID)) {
			if (contents.containsKey(slot)) {
				contents.get(slot).give(player);
				contents.remove(slot);
			}
		}
	}

	/**
	 * Takes all reward contents.
	 */
	public void takeAll(EntityPlayerMP player) {
		if (player.getUniqueID().equals(playerUUID)) {
			for (IRewardContent content : contents.values()) {
				content.give(player);
			}
			contents.clear();
		}
	}

	/**
	 * Checks if all contents have been taken from the drop UI.
	 *
	 * @return true if all drops have been taken
	 */
	public boolean isEmpty() {
		return contents.isEmpty();
	}

}
