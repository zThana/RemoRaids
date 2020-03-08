package ca.landonjw.remoraids.api.battles;

import ca.landonjw.remoraids.api.boss.IBoss;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.status.StatusType;
import net.minecraft.entity.player.EntityPlayerMP;

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
    void addBattleRestraint(IBattleRestraint restraint);

    /**
     * Checks if a player passes all of the battle restraints and native Pixelmon battle rules if available.
     *
     * @param player the player to validate
     * @return true if the player passes the rules, false if the player doesnt
     */
    boolean validate(EntityPlayerMP player);

}
