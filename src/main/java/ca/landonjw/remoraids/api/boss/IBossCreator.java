package ca.landonjw.remoraids.api.boss;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.api.util.IBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Provides a builder like construction to create a boss. This can be used to simplify the overall
 * process of creating a boss from a third party perspective, and allows for easier control of plugins
 * implementing the design.
 *
 * @author NickImpact
 * @since 1.0.0
 */
public interface IBossCreator extends IBuilder.Deserializable<IBossSpawner, IBossCreator> {

	/**
	 * Initializes a new instance of the boss creator.
	 *
	 * @return new instance of boss creator
	 */
	static IBossCreator initialize() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossCreator.class);
	}

	/**
	 * Allows for finer control of the resulting spawner. By setting this, all additional fields set here
	 * will then be applied to the custom spawner type provided by the third party executor.
	 *
	 * Note: This is an optional field. Not setting it will simply default to the original designs.
	 *
	 * @param key A key that maps to a spawner builder
	 * @return The current instance of the builder
	 */
	IBossCreator controller(String key);

	/**
	 * Sets the raid boss that will be the pokemon that players must face off against.
	 *
	 * @param boss The boss to use for the raid
	 * @return The current instance of the builder
	 */
	IBossCreator boss(IBoss boss);

	/**
	 * Specifies the spawning location of the entity, alongside an additional
	 * rotation factor. The yaw will essentially control the direction in which the
	 * entity looks. For reference, 0 = South, 90 = West, -180 = North, -90 = East.
	 * And yes, the negative values are intended. T'is how the game represents them!
	 *
	 * @param world    The world to use for the spawn
	 * @param location The x, y, and z coordinates to spawn the boss at
	 * @param rotation The rotation to apply to the boss once spawned
	 * @return The current instance of the builder
	 */
	IBossCreator location(World world, Vec3d location, float rotation);

	/**
	 * Specifies the spawning location of the entity. This call, unlike its counterpart, allows
	 * the user to provide a custom spawn location implementation to handle the spawning of
	 * the raid pokemon.
	 *
	 * @param location The custom implementation to use for the spawning of the raid pokemon
	 * @return The current instance of the builder
	 */
	IBossCreator location(IBossSpawnLocation location);

	/**
	 * Specifies the announcement to make when a boss is spawned in. This allows the implementation to supply
	 * a custom announcement option outside of what the plugin normally allows.
	 *
	 * @param announcement The custom announcement type to use
	 * @return The current instance of the builder
	 */
	IBossCreator announcement(ISpawnAnnouncement announcement);

	/**
	 * Specifies the overlay to be used and displayed while the boss is alive. This allows the implementation to
	 * supply a custom overlay outside of the default option in the config.
	 *
	 * @param overlayText the custom boss overlay to use
	 * @return the current instance of the builder
	 */
	IBossCreator overlay(List<String> overlayText, boolean disable);

	/**
	 * Specifies whether or not this boss can respawn. Alongside this call, the amount of times it can respawn and
	 * the cooldown period will state how often a raid boss respawns, as well as how often. If the input for amount
	 * is -1, the system will treat this as an unlimited respawn template.
	 *
	 * @param amount The number of times to respawn the raid boss. -1 for unlimited
	 * @param time   The amount of time to wait before respawning the raid boss
	 * @param unit   The time unit to use for specifying the actual intended wait for the respawn cooldown
	 * @return The current instance of the builder
	 */
	IBossCreator respawns(int amount, long time, TimeUnit unit);

	/**
	 * Specifies whether or not this boss can respawn. Unlike it's counterpart, this call marks the associated
	 * set of newly generated Respawn Data with the infinite flag. This means that the raid boss will constantly
	 * respawn until it has been deleted entirely.
	 *
	 * @return The current instance of the builder
	 */
	IBossCreator respawns();

	/**
	 * Specifies whether or not this spawner will persist across restarts. Some instances may be dynamic, and
	 * as such, their spawners should not be saved.
	 *
	 * @return The current instance of the builder
	 */
	IBossCreator persists(boolean persists);
}
