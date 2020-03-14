package ca.landonjw.remoraids.api.boss;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.api.util.IBuilder;
import net.minecraft.world.World;

import java.util.concurrent.TimeUnit;

/**
 * Provides a builder like construction to create a boss. This can be used to simplify the overall
 * process of creating a boss from a third party perspective, and allows for easier control of plugins
 * implementing the design.
 */
public interface IBossCreator extends IBuilder<IBossSpawner, IBossCreator> {

	static IBossCreator initialize() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossCreator.class);
	}

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
	 * @param world The world to use for the spawn
	 * @param x The x position to spawn the boss
	 * @param y The y position to spawn the boss
	 * @param z The z position to spawn the boss
	 * @param yaw The rotation to apply to the boss once spawned
	 * @return The current instance of the builder
	 */
	IBossCreator location(World world, double x, double y, double z, float yaw);

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
	 * Specifies the announcement to make when a raid pokemon has spawned. Additionally, this
	 * announcement can allow the user to TP to the location if set.
	 *
	 * @param allowTP If this announcement should contain a callback via the message to allow the user
	 *                to TP to its location.
	 * @param message The message to broadcast once the boss is spawned
	 * @return The current instance of the builder
	 */
	IBossCreator announcement(boolean allowTP, String message);

	/**
	 * Specifies the announcement to make when a boss is spawned in. This allows the implementation to supply
	 * a custom announcement option outside of what the plugin normally allows.
	 *
	 * @param announcement The custom announcement type to use
	 * @return The current instance of the builder
	 */
	IBossCreator announcement(ISpawnAnnouncement announcement);

	/**
	 *
	 *
	 * @param time
	 * @param unit
	 * @return
	 */
	IBossCreator respawns(int amount, long time, TimeUnit unit);

}
