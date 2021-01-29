package ca.landonjw.remoraids.api.boss.engage;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Allows for a player to battle the boss when within a given range.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossEngager {

	/**
	 * Gets the range the player must be within to engage the boss.
	 *
	 * @return the range player must be witin to engage the boss
	 */
	float getRange();

	/**
	 * Checks if a player is within range of the boss to engage battle.
	 *
	 * @param player player to check
	 * @return true if player is within range, false if not
	 */
	boolean isPlayerInRange(@Nonnull EntityPlayerMP player);

	/**
	 * Sends a message to all players within engage range of the boss.
	 */
	void sendEngageMessage();
}