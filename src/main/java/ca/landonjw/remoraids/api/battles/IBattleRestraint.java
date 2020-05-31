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

    /**
     * Gets the ID of the battle restraint.
     *
     * This should be unique to the type of restraint created, and define the cause of the restraint.
     * An example is the "Cooldown Restraint", where players who are on cooldown are restrained from battle.
     *
     * @return the id of the battle restraint
     */
    String getId();

    /**
     * Gets the message to be sent to the player when they do not validate the restraint.
     * This allows the player to know why he was rejected.
     *
     * @param player the player to get rejection message for
     * @return message stating why player was rejected
     */
    String getRejectionMessage(@Nonnull EntityPlayerMP player);

}
