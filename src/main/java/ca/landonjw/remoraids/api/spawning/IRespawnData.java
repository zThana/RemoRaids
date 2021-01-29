package ca.landonjw.remoraids.api.spawning;

import java.util.concurrent.TimeUnit;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * RespawnData represents the actual management of how a raid boss will respawn, if at all.
 *
 * <p>
 * If a spawner has an associated set of RespawnData attached to it, the internal game
 * clock will look to this data to determine if a respawn is available. A respawn timer
 * will only start if the raid boss has been killed or despawned.
 * </p>
 *
 * <p>
 * Users can query this data to understand the spawning conditions on the spawner,
 * and to actively know the general information about a respawn condition's current
 * status.
 * </p>
 */
public interface IRespawnData extends DataSerializable {

	/**
	 * Specifies if a raid boss spawner will respawn infinitely, rather than track a set amount
	 * of respawn opportunities. Essentially, if this is set, all other fields will be ignored.
	 *
	 * @return True if this spawner will be able to respawn the raid boss infinitely, false otherwise.
	 */
	boolean isInfinite();

	/**
	 * Applies a flag marker indicating whether or not this set of respawn data will allow for infinite
	 * respawning of the raid boss attached to its associated spawner.
	 *
	 * @param state The state you wish to set this flag to
	 */
	void setInfinite(boolean state);

	/**
	 * Specifies the amount of respawns this spawner has left. This essentially uses {@link #getTotalRespawns()}
	 * and subtracts that value from the marked number of respawns created thus far.
	 *
	 * @return A value representing the remaining amount of respawns this spawner has
	 */
	int getRemainingRespawns();

	/**
	 * Specifies whether or not this set of respawn data has any more remaining respawns available.
	 * In order for this data to return true, the amount specified by {@link #getRemainingRespawns()}
	 * must be a value larger than 0.
	 *
	 * @return True if the calculated amount is larger than 0, false otherwise
	 */
	default boolean hasRemainingRespawns() {
		return this.getRemainingRespawns() > 0;
	}

	/**
	 * Allows for the dynamic setting of respawns remaining for the spawner. In the event this value exceeds
	 * what is specified by {@link #getTotalRespawns()}, then this call will also adjust that value accordingly.
	 *
	 * <p>
	 * Should the input value be negative or 0, the internal respawn handler will consider this as
	 * a spawner that no longer has any remaining respawns.
	 * </p>
	 *
	 * @param amount The amount of respawns this spawner should have.
	 */
	void setRemainingRespawns(int amount);

	/**
	 * Increments the amount of times this raid boss has spawned.
	 */
	void incrementRespawnCounter();

	/**
	 * Specifies the total number of respawns this data should allow for. This is unlike the alternative,
	 * {@link #getRemainingRespawns()}, in that this method returns to actual number of respawns this data
	 * is meant to allow versus how many remain.
	 *
	 * @return The total amount of respawns this spawner may produce
	 */
	int getTotalRespawns();

	/**
	 * Sets the amount of respawns available to the spawner
	 *
	 * @param amount The amount of respawns you wish to max this spawner to
	 */
	void setTotalRespawns(int amount);

	/**
	 * Specifies the amount of time remaining until this spawner will actually spawn the raid boss.
	 * If the raid boss is alive, this time will always match the result of {@link #getTotalWaitPeriod(TimeUnit)}
	 *
	 * @param unit The unit to apply to receive the remaining time on the respawn counter
	 * @return The time adjusted according to the unit conversion
	 */
	long getTimeRemainingUntilRespawn(TimeUnit unit);

	/**
	 * Specifies the amount of time that this spawner must wait before actively
	 * trying to respawn a raid boss. The addition of the time unit allows for
	 * you to query the time to a specific unit of time, for your ease.
	 *
	 * @return The total amount of time this spawner must wait, adjusted to the
	 *         requested time unit.
	 */
	long getTotalWaitPeriod(TimeUnit unit);

	/**
	 * Allows for the adjustment of the time period that one must wait for the spawner to be able
	 * to respawn a raid boss.
	 *
	 * <p>
	 * The time specified is meant to be reflected by the input time unit, and will be adjusted
	 * to minecraft ticks internally. The usage of a time unit just allows for easier readability
	 * of the input time, rather than enforcing large numbers for tick values.
	 * </p>
	 *
	 * @param time The total amount of time, based on the unit parameter, that this spawner will
	 *             wait to respawn the raid boss after death.
	 * @param unit The time unit representing the amount of time
	 */
	void setTotalWaitPeriod(long time, TimeUnit unit);

	/**
	 * Runs the task responsible for respawning the associated raid boss.
	 *
	 * <p>
	 * This should realistically only be called by the provided death listener. Other uses
	 * of this call will likely produce unwanted results.
	 * </p>
	 *
	 * @param spawner The spawner this respawn data is associated to
	 */
	void run(IBossSpawner spawner);

	/**
	 * Creates a new Respawn Data builder based on the supplied builder constructor provided
	 * for the interface design.
	 *
	 * @return A new Respawn Data builder
	 */
	static IRespawnDataBuilder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(IRespawnDataBuilder.class);
	}

	/**
	 * Builder for respawn data.
	 */
	interface IRespawnDataBuilder extends IBuilder.Deserializable<IRespawnData, IRespawnDataBuilder> {

		/**
		 * Marks a set of respawn data as infinite or not. In other words, this controls whether or not
		 * the spawner's raid boss can respawn on an infinite cycle, or have a limit to the amount
		 * of times it can be respawned.
		 *
		 * @param state the state you wish to set this flag to
		 * @return the builder modified by this call
		 */
		IRespawnDataBuilder infinite(boolean state);

		/**
		 * Sets the total number of times the boss should be respawned.
		 *
		 * @param count the number of times the boss should be respawned
		 * @return the builder instance with count set
		 */
		IRespawnDataBuilder count(int count);

		/**
		 * Sets the period of time before a boss will respawn after death.
		 *
		 * @param time amount of time to elapse before respawn
		 * @param unit unit of time
		 * @return the builder instance with time period set
		 */
		IRespawnDataBuilder period(long time, TimeUnit unit);

		/**
		 * Sets the amount of respawns that have already occurred.
		 * <p>
		 * This is used for the purpose of deserialization and can often be disregarded.
		 *
		 * @param used the number of respawns that have already occurred
		 * @return the builder instance with used respawns set
		 */
		IRespawnDataBuilder used(int used);

	}

}
