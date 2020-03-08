package ca.landonjw.remoraids.api.battles;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.rewards.IReward;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A battle between a {@link IBossEntity} and potentially several players.
 *
 * @author landonjw
 * @since  1.0.0
 */
public interface IBossBattle {

    /**
     * Gets the boss entity that the boss battle is created for.
     *
     * @return the boss entity
     */
    IBossEntity getBossEntity();

    /**
     * Gets the {@link IBossBattleRules} that are validated and used during the boss battle.
     *
     * @return the boss battle rules
     */
    IBossBattleRules getBattleRules();

    /**
     * Sets the {@link IBossBattleRules} that are used during the boss battle.
     *
     * @param battleRules the new battle rules
     */
    void setBattleRules(@Nonnull IBossBattleRules battleRules);

    /**
     * Gets a list of the rewards to be distributed when the boss is defeated.
     *
     * @return list of rewards to be distributed when the boss is defeated in battle
     */
    List<IReward> getDefeatRewards();

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

    void endBattle(@Nonnull EntityPlayerMP player);

    void endAllBattles();

    void setBossHealth(int health, @Nullable EntityPlayerMP source);

    void distributeRewards();

    int getHealth();

    int getMaxHealth();

}