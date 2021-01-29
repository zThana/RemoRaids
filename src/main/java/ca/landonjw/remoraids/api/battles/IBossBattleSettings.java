package ca.landonjw.remoraids.api.battles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.pixelmonmod.pixelmon.battles.rules.BattleRules;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.util.DataSerializable;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Rules to be enforced upon entrance and during a battle with a {@link IBoss}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossBattleSettings extends DataSerializable {

	/**
	 * Gets the native battle rules to be enforced, if available.
	 *
	 * @return the native battle rules to be enforced, if available
	 */
	Optional<BattleRules> getBattleRules();

	/**
	 * Sets the battle rules of the battle settings.
	 *
	 * @param battleRules battle rules to set
	 */
	void setBattleRules(@Nullable BattleRules battleRules);

	/**
	 * Gets a set of restraints to be enforced before battle
	 * This will be empty if no battle restraints are present.
	 *
	 * @return set of restraints to be enforced before battle
	 */
	Set<IBattleRestraint> getBattleRestraints();

	/**
	 * Checks if the battle settings contains a battle restraint with a given identifier.
	 *
	 * @param id identifier to check
	 * @return true if settings contains restraint, false if it doesn't contain restraint
	 */
	boolean containsBattleRestraint(String id);

	/**
	 * Removes a battle restraint with a given identifier from the settings if present
	 *
	 * @param id identifier of battle restraint to remove
	 */
	void removeBattleRestraint(String id);

	/**
	 * Gets a set of rewards to be distributed after the boss dies.
	 * This will be empty if no rewards are present.
	 *
	 * @return set of rewards to be distributed after boss dies
	 */
	Set<IReward> getRewards();

	/**
	 * Checks if a player passes all of the battle restraints and native Pixelmon battle rules if available.
	 *
	 * @param player the player to validate
	 * @return true if the player passes the rules, false if the player doesnt
	 */
	boolean validate(@NonNull EntityPlayerMP player, @Nonnull IBoss boss);

	/**
	 * Get a list of rejection messages from all restraints that a player did not pass validation of.
	 * If player passed validation of all restraints, it will be an empty list.
	 *
	 * @param player player to get rejection messages for
	 * @return list of all rejection messages, or empty list if no rejection messages
	 */
	List<String> getRejectionMessages(@NonNull EntityPlayerMP player, @Nonnull IBoss boss);

}
