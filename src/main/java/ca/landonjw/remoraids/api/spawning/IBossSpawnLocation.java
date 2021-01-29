package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Represents a location that an {@link ca.landonjw.remoraids.api.boss.IBoss} may spawn at.
 *
 * This contains information about the world, position, and rotation of the a boss upon spawn.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IBossSpawnLocation extends DataSerializable {

	/**
	 * Gets the world that boss will spawn at.
	 *
	 * @return world boss will spawn at
	 */
	World getWorld();

	/**
	 * Gets the location that boss will spawn at.
	 *
	 * This is a vector containing the X, Y, and Z coordinates of the position it will spawn.
	 *
	 * @return the location that boss will spawn at
	 */
	Vec3d getLocation();

	/**
	 * Gets the yaw rotation of the boss upon spawn.
	 *
	 * @return the yaw rotation of the boss upon spawn
	 */
	float getRotation();

	/**
	 * Gets an instance of a builder for creating an {@link IBossSpawnLocation}.
	 *
	 * @return an instance of a builder for creating a spawn location
	 */
	static IBossSpawnLocationBuilder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossSpawnLocationBuilder.class);
	}

	/**
	 * Builder for creating an {@link IBossSpawnLocation}.
	 */
	interface IBossSpawnLocationBuilder extends IBuilder.Deserializable<IBossSpawnLocation, IBossSpawnLocationBuilder> {

		/**
		 * Sets the world that a boss will spawn at. See {@link #getWorld()}.
		 *
		 * @param world the world boss will spawn at
		 * @return builder instance with world set
		 */
		IBossSpawnLocationBuilder world(World world);

		/**
		 * Sets the location that a boss will spawn at. See {@link #getLocation()}.
		 *
		 * @param location the location boss will spawn at
		 * @return builder instance with location set
		 */
		IBossSpawnLocationBuilder location(Vec3d location);

		/**
		 * Sets the rotation that a boss will spawn with. See {@link #getRotation()}.
		 *
		 * @param rotation rotation that a boss will spawn with
		 * @return builder instance with rotation set
		 */
		IBossSpawnLocationBuilder rotation(float rotation);

	}

}
