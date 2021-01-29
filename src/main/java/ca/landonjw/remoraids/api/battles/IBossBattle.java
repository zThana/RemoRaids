package ca.landonjw.remoraids.api.battles;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * A battle between a {@link IBossEntity} and zero or many several players.
 *
 * This contains information regarding restraints in the battle, and rewards to be distributed upon successful defeat.
 * When a {@link IBossEntity} is created, this will be created and registered to the {@link IBossBattleRegistry},
 * and persist over the lifecycle of the boss entity until it is killed or forcibly despawned.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossBattle {

	/**
	 * Gets the boss entity that the boss battle is created for.
	 *
	 * @return the boss entity
	 */
	IBossEntity getBossEntity();

	/**
	 * Gets the {@link IBossBattleSettings} that are validated and used during the boss battle.
	 *
	 * @return the boss battle settings
	 */
	IBossBattleSettings getBattleSettings();

	/**
	 * Gets the players that are currently in the boss battle.
	 *
	 * @return the players that are currently in battle
	 */
	Set<EntityPlayerMP> getPlayersInBattle();

	/**
	 * Gets the battles currently ongoing with the boss entity.
	 *
	 * @return the battles currently ongoing with the boss entity
	 */
	Set<BattleControllerBase> getBattleControllers();

	/**
	 * Gets the damage dealt by a player during the boss battle
	 *
	 * @param uuid the uuid of player to get damage from
	 * @return the damage dealt by a player during the boss battle
	 */
	Optional<Integer> getDamageDealt(@Nonnull UUID uuid);

	/**
	 * Gets a list of player UUIDs sorted by highest damage dealt
	 *
	 * @return list of player UUIDs sorted by highest damage dealt
	 */
	List<UUID> getTopDamageDealers();

	/**
	 * Gets the battle controller for a player in the boss battle.
	 *
	 * @param player the player to get battle controller for
	 * @return the battle controller if present
	 */
	Optional<BattleControllerBase> getBattleController(@Nonnull EntityPlayerMP player);

	/**
	 * Gets the player who dealt the killing blow in the battle, if present
	 *
	 * @return the player who dealt the killing blow in the battle, if present
	 */
	Optional<UUID> getKiller();

	/**
	 * Checks if a player is in the boss battle.
	 *
	 * @param player the player to check
	 * @return true if the player is in the boss battle, false if they aren't
	 */
	boolean containsPlayer(@Nonnull EntityPlayerMP player);

	/**
	 * Starts battle between the boss and player.
	 *
	 * @param player the player to start battle for
	 */
	void startBattle(@Nonnull EntityPlayerMP player);

	/**
	 * Starts battle between the boss and player.
	 *
	 * @param player           the player to start battle for
	 * @param startingPixelmon the player's pixelmon to start battle with
	 */
	void startBattle(@Nonnull EntityPlayerMP player, @Nullable EntityPixelmon startingPixelmon);

	/**
	 * Ends a battle between a player and the boss.
	 *
	 * @param player the player to end battle for
	 */
	void endBattle(@Nonnull EntityPlayerMP player);

	/**
	 * Ends all ongoing battles between players and the boss.
	 */
	void endAllBattles();

	/**
	 * Sets the health of the boss.
	 *
	 * @param health new health of the boss
	 * @param source the source that is changing the health
	 */
	void setBossHealth(int health, @Nullable EntityPlayerMP source);

	/**
	 * Distributes any battle rewards.
	 */
	void distributeRewards();

	/**
	 * Gets the current health of the boss.
	 *
	 * @return the current health of the boss
	 */
	int getHealth();

	/**
	 * Gets the max health of the boss.
	 *
	 * @return the max health of the boss
	 */
	int getMaxHealth();

}