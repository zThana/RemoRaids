package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;
import ca.landonjw.remoraids.implementation.BossAPI;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Represents a system that will be run in order to spawn a raid boss. A spawner takes into consideration
 * three main variables. The first being the boss it'll spawn, the location to spawn it at, and the announcement
 * template to send when the boss is spawned.
 */
public interface IBossSpawner extends DataSerializable {

    /**
     * Represents the unique identifier of this spawner. This is the exact same key as what you should
     * use when registering your Boss Spawner's builder to the {@link ca.landonjw.remoraids.api.registry.IRaidRegistry
     * Raid Registry}, via the {@link ca.landonjw.remoraids.api.registry.IRaidRegistry#registerSpawnerBuilderSupplier(String, Supplier)
     * Spawner Builder Supplier}. It's recommended you keep a final static variable in the class scope here
     * to help reference this key where needed, as to ensure your key will always be the same when accessed.
     *
     * <p>NOTE: This key will be used for helping deserialize boss spawner data.</p>
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
     * failed
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

    interface IBossSpawnerBuilder extends IBuilder<IBossSpawner, IBossSpawnerBuilder> {

        IBossSpawnerBuilder boss(IBoss boss);

        IBossSpawnerBuilder location(IBossSpawnLocation location);

        IBossSpawnerBuilder announcement(ISpawnAnnouncement announcement);

        IBossSpawnerBuilder respawns(IRespawnData data);

        IBossSpawnerBuilder persists(boolean persists);

    }

    /**
     * RespawnData represents the actual management of how a raid boss will respawn, if at all.
     *
     * <p>If a spawner has an associated set of RespawnData attached to it, the internal game
     * clock will look to this data to determine if a respawn is available. A respawn timer
     * will only start if the raid boss has been killed or despawned.</p>
     *
     * <p>Users can query this data to understand the spawning conditions on the spawner,
     * and to actively know the general information about a respawn condition's current
     * status.</p>
     */
    interface IRespawnData extends DataSerializable {

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
         * <p>Should the input value be negative or 0, the internal respawn handler will consider this as
         * a spawner that no longer has any remaining respawns.</p>
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
         * requested time unit.
         */
        long getTotalWaitPeriod(TimeUnit unit);

        /**
         * Allows for the adjustment of the time period that one must wait for the spawner to be able
         * to respawn a raid boss.
         *
         * <p>The time specified is meant to be reflected by the input time unit, and will be adjusted
         * to minecraft ticks internally. The usage of a time unit just allows for easier readability
         * of the input time, rather than enforcing large numbers for tick values.</p>
         *
         * @param time The total amount of time, based on the unit parameter, that this spawner will
         *             wait to respawn the raid boss after death.
         * @param unit The time unit representing the amount of time
         */
        void setTotalWaitPeriod(long time, TimeUnit unit);

        /**
         * Runs the task responsible for respawning the associated raid boss.
         *
         * <p>This should realistically only be called by the provided death listener. Other uses
         * of this call will likely produce unwanted results.</p>
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

        interface IRespawnDataBuilder extends IBuilder.Deserializable<IRespawnData, IRespawnDataBuilder> {

            /**
             * Marks a set of respawn data as infinite or not. In other words, this controls whether or not
             * the spawner's raid boss can respawn on an infinite cycle, or have a limit to the amount
             * of times it can be respawned.
             *
             * @param state The state you wish to set this flag to
             * @return The builder modified by this call
             */
            IRespawnDataBuilder infinite(boolean state);

            IRespawnDataBuilder count(int count);

            IRespawnDataBuilder period(long time, TimeUnit unit);

            IRespawnDataBuilder used(int used);

        }

    }

}