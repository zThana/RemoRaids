package ca.landonjw.remoraids.api.battles;

import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;

/**
 * A restraint that may prevent a player from entering battle.
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface IBattleRestraint {

    /**
     * Checks if a player passes the battle restraint.
     *
     * @param player the player to check
     * @return true if the player passes the battle restraint, false if the player doesn't
     */
    boolean validatePlayer(@Nonnull EntityPlayerMP player);

}
