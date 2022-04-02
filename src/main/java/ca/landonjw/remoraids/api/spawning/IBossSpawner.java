package ca.landonjw.remoraids.api.spawning;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * Represents a system that will be run in order to spawn a raid boss. A spawner takes into consideration
 * three main variables. The first being the boss it'll spawn, the location to spawn it at, and the announcement
 * template to send when the boss is spawned.
 *
 * @author NickImpact
 * @since 1.0.0
 */
public interface IBossSpawner extends DataSerializable {

	/**
	 * Represents the unique identifier of this spawner. This is the exact same key as what you should
	 * use when registering your Boss Spawner's builder to the {@link ca.landonjw.remoraids.api.registry.IRaidRegistry
	 * Raid Registry}, via the {@link ca.landonjw.remoraids.api.registry.IRaidRegistry#registerSpawnerBuilderSupplier(String, Supplier)
	 * Spawner Builder Supplier}. It's recommended you keep a final static variable in the class scope here
	 * to help reference this key where needed, as to ensure your key will always be the same when accessed.
	 *
	 * <p>
	 * NOTE: This key will be used for helping deserialize boss spawner data.
	 * </p>
	 *
	 * @return The identifier of this spawner.
	 */
	String getKey();

	/**
	 * Attempts to spawn a new raid pokemon at the specified spawn location. Typically, this will be a singular
	 * pokemon, but this can be chosen from a set. If a set of raid pokemon exists larger than a size of 1, the spawner
	 * should select a pokemon from the spawn set to actually spawn. How the spawner selects the new raid pokemon is
	 * entirely up to its implementation.
	 *
	 * @param announce Whether or not this spawn request will announce the raid boss spawning in
	 * @return An Optional value containing the {@link IBossEntity} created, or {@link Optional#empty()} if the spawn
	 *         failed
	 */
	Optional<IBossEntity> spawn(boolean announce);

	/**
	 * An internal method to be used by reference of the raid boss battle entity. During chunk unloads, the battle
	 * entity to a raid boss may be lost. This here will respawn that raid boss if it is found to be missing.
	 *
	 * NOTE: Let the system use this, there's no reason to call this externally.
	 *
	 * @return The newly crafted entity representing this raid boss for battles
	 */
	EntityPixelmon fix();

	/**
	 * Specifies the raid boss that'll be spawned in from this raid boss spawner.
	 *
	 * @return The boss that'll be spawned by this spawner
	 */
	IBoss getBoss();

	/**
	 * Specifies the spawn location of the raid pokemon. This system is meant to allow for custom spawn
	 * location mechanics, but by default, will typically be the general default instance.
	 *
	 * @return The spawn location manager for this spawner
	 */
	IBossSpawnLocation getSpawnLocation();

	/**
	 * Applies a new spawn location to this raid boss. This will only come into effect during spawning, aka,
	 * respawns or initial spawns.
	 *
	 * @param location The desired location
	 */
	void setSpawnLocation(IBossSpawnLocation location);

	/**
	 * Specifies the announcement system that will be used to advertise the boss as it is spawned in.
	 *
	 * @return The announcement procedure to use for the spawn of the raid pokemon
	 */
	ISpawnAnnouncement getAnnouncement();

	/**
	 * Gets the overlay that is to be shown as long as the boss is alive
	 *
	 * @return the overlay to use for the overlay display
	 */
	List<String> getOverlayText();

	/**
	 * @return if the overlay shall be displayed or not
	 */
	boolean overlayDisabled();

	/**
	 * Returns a set of respawn data for the raid boss. Given that a raid boss might not have any respawns,
	 * this data can return optionally to help suggest that a certain raid boss has no attempt
	 * of respawning. It is still entirely possible that a raid boss spawner will have a set of respawn
	 * data associated with it such that there are no respawns associated with it.
	 *
	 * @return A optionally populated set of Respawn Data
	 */
	Optional<IRespawnData> getRespawnData();

	/**
	 * Allows for the creation of new Respawn data for this spawner, and also allowing for the call
	 * to be chained with the newly established data.
	 *
	 * @return A newly created set of Respawn Data
	 */
	IRespawnData createRespawnData();

	/**
	 * Specifies whether or not this spawner will persist across restarts.
	 *
	 * @return True if it should persist, false otherwise
	 */
	boolean doesPersist();

	/**
	 * Represents a simple marker flag that'll indicate whether this spawner has spawned in its
	 * raid boss this session. This data is not serialized.
	 *
	 * @return True if a spawn has taken place, false otherwise
	 */
	boolean hasSpawned();

	/**
	 * Returns if dynamax shall be allowed in this battle
	 *
	 * @return if dynamax shall be allowed in this battle
	 */
	boolean allowDynamax();

	/**
	 * Creates a new builder for a raid boss spawner.
	 *
	 * @return A new builder allowing construction of a raid boss spawner
	 */
	static IBossSpawner.IBossSpawnerBuilder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossSpawner.IBossSpawnerBuilder.class);
	}

	/**
	 * Builder for a {@link IBossSpawner}.
	 */
	interface IBossSpawnerBuilder extends IBuilder<IBossSpawner, IBossSpawnerBuilder> {

		/**
		 * Sets the boss the spawner will spawn.
		 *
		 * @param boss the boss to spawn
		 * @return builder instance with boss set
		 */
		IBossSpawnerBuilder boss(@Nonnull IBoss boss);

		/**
		 * Sets the location the boss will spawn at.
		 *
		 * @param location the location boss will spawn at
		 * @return builder instance with location set
		 */
		IBossSpawnerBuilder location(@Nonnull IBossSpawnLocation location);

		/**
		 * Sets the announcement the boss will spawn with.
		 * If the announcement is null, no announcement will be set.
		 *
		 * @param announcement the announcement that will be sent with
		 * @return builder instance with announcement set
		 */
		IBossSpawnerBuilder announcement(@Nullable ISpawnAnnouncement announcement);

		/**
		 * Sets the overlay that will be displayed when the boss is alive.
		 * If the overlay is null, no overlay will be shown.
		 *
		 * @param overlay the overlay that will be displayed
		 * @return builder instance with overlay set
		 */
		IBossSpawnerBuilder overlayText(@Nullable List<String> overlay, boolean overlayDisabled);

		/**
		 * Sets if dynamax shall be allowed
		 * Defaults to true
		 *
		 * @param allowDynamax sets if dynamax is allowed or not
		 * @return builder instance with dynamax set
		 */
		IBossSpawnerBuilder allowDynamax(boolean allowDynamax);

		/**
		 * Sets the respawn data that defines how the spawner will respawn.
		 *
		 * @param data respawn data to apply to the spawner
		 * @return builder instance with respawn data set
		 */
		IBossSpawnerBuilder respawns(@Nullable IRespawnData data);

		/**
		 * Sets if the spawner should persist over server restarts.
		 *
		 * @param persists if the spawner should persist over server restarts
		 * @return builder instance with persistence set
		 */
		IBossSpawnerBuilder persists(boolean persists);

	}

}