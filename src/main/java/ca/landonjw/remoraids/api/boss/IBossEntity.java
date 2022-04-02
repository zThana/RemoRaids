package ca.landonjw.remoraids.api.boss;

import java.util.Optional;

import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Represents a living representation of an {@link IBoss}.
 *
 * This is the result of an {@link IBoss} being spawned via an {@link IBossSpawner}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossEntity {

	/**
	 * Gets the boss the entity represents.
	 *
	 * @return the boss entity represents
	 */
	IBoss getBoss();

	/**
	 * Gets the spawner that initially spawned this entity.
	 *
	 * @return spawner that spawned the entity
	 */
	IBossSpawner getSpawner();

	/**
	 * Gets the statue that visually represents the boss entity.
	 *
	 * This is used in lieu of a regular entity pixelmon due to them performing
	 * better client side, and serving a more similar purpose to what we need (motionless, persistent, etc.)
	 *
	 * The result may be an empty optional if the boss entity has died, or if the statue is currently
	 * in an unloaded chunk. Therefore, you should always do appropriate checks to see if the contents of the
	 * optional are valid.
	 *
	 * @return statue entity, if available
	 */
	Optional<EntityStatue> getEntity();

	/**
	 * Gets the battle entity for the boss.
	 *
	 * This entity is invisible to players and used to generate battles. This allows
	 * for no additional entities to be created during battle starts, having beneficial
	 * performance effects.
	 *
	 * The result may be an empty optional if the boss entity has died, or if the statue is currently
	 * in an unloaded chunk. Therefore, you should always do appropriate checks to see if the contents of the
	 * optional are valid.
	 *
	 * @return battle entity, if available
	 */
	Optional<EntityPixelmon> getBattleEntity();

	/**
	 * Gets the position the boss was spawned in.
	 *
	 * @return the position of the boss
	 */
	Vec3d getPosition();

	/**
	 * Gets the world the boss was spawned in.
	 *
	 * @return the world the boss was spawned in
	 */
	World getWorld();

	/**
	 * Gets the engager used to evaluate if a player is in range of the boss entity for battles.
	 *
	 * @return the boss engager
	 */
	IBossEngager getBossEngager();

	/**
	 * Despawns the boss.
	 */
	void despawn();

	/**
	 * Returns if dynamax is allowed in the battle
	 *
	 * @return if dynamax is allowed
	 */
	boolean allowDynamax();

}