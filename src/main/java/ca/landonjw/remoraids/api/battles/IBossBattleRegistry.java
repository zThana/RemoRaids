package ca.landonjw.remoraids.api.battles;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Contains all {@link IBossBattle}s for active {@link IBossEntity}s.
 * You must use this over native Pixelmon's BattleRegistry in order to manipulate a boss battle.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossBattleRegistry {

	/**
	 * Gets a boss battle, if it is available.
	 *
	 * The boss battle should be present throughout the life cycle of the {@link IBossEntity},
	 * however the boss battle will not be present once it has despawned. Therefore, use caution with the
	 * option if you are caching the entity and calling this.
	 *
	 * @param boss the boss to get boss battle for
	 * @return boss battle corresponding to boss entity, if present
	 */
	Optional<IBossBattle> getBossBattle(@Nonnull IBossEntity boss);

	/**
	 * Gets a boss battle that a player is in, if it is available.
	 *
	 * @param player the player to be boss battle from
	 * @return boss battle player is in, if present
	 */
	Optional<IBossBattle> getBossBattle(@Nonnull EntityPlayerMP player);

	/**
	 * Gets all registered boss battles.
	 *
	 * @return all registered boss battles
	 */
	List<IBossBattle> getAllBossBattles();

	/**
	 * Checks if a player is currently in battle with a boss.
	 *
	 * @param player player to check
	 * @return true if player is in battle with a boss, false if player isn't in battle with a boss
	 */
	boolean isPlayerInBattle(@Nonnull EntityPlayerMP player);

}