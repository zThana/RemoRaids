package ca.landonjw.remoraids.api.battles;

import ca.landonjw.remoraids.api.boss.IBoss;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Rules to be enforced upon entrance and during a battle with a {@link IBoss}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface IBossBattleRules {

    /**
     * Gets the native battle rules to be enforced, if available.
     *
     * @return the native battle rules to be enforced, if available
     */
    Optional<BattleRules> getBattleRules();

    /**
     * Gets a set of restraints to be enforced before battle
     * This should be empty of no battle restraints are present.
     *
     * @return
     */
    Set<IBattleRestraint> getBattleRestraints();

    /**
     * Adds a {@link IBattleRestraint} to the rules.
     *
     * @param restraint the battle restraint to add
     */
    void addBattleRestraint(@Nonnull IBattleRestraint restraint);

    /**
     * Removes a battle restraint based on it's ID.
     *
     * @param id the id of battle restraint to remove
     */
    void removeBattleRestraint(@Nonnull String id);

    /**
     * Checks if a battle restraint with specified id is contained within the battle rules.
     *
     * @param id the id of battle restraint to check
     * @return true if battle rules contains the restraint, false if they don't
     */
    boolean containsBattleRestraint(@Nonnull String id);

    /**
     * Checks if a player passes all of the battle restraints and native Pixelmon battle rules if available.
     *
     * @param player the player to validate
     * @return true if the player passes the rules, false if the player doesnt
     */
    boolean validate(@Nonnull EntityPlayerMP player);

    /**
     * Get a list of rejection messages from all restraints that a player did not pass validation of.
     * If player passed validation of all restraints, it will be an empty list.
     *
     * @param player player to get rejection messages for
     * @return list of all rejection messages, or empty list if no rejection messages
     */
    List<String> getRejectionMessages(@Nonnull EntityPlayerMP player);

}
