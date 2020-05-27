package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.util.DataSerializable;

import java.util.Optional;

/**
 * Represents a system that will be run in order to spawn a raid boss. A spawner takes into consideration
 * three main variables. The first being the boss it'll spawn, the location to spawn it at, and the annoucement
 * template to send when the boss is spawned.
 */
public interface IBossSpawner extends DataSerializable<IBossSpawner> {

    /**
     * Attempts to spawn a new raid pokemon at the specified spawn location. Typically, this will be a singular
     * pokemon, but this can be chosen from a set. If a set of raid pokemon exists larger than a size of 1, the spawner
     * should select a pokemon from the spawn set to actually spawn. How the spawner selects the new raid pokemon is
     * entirely up to its implementation.
     *
     * @return An Optional value containing the {@link IBossEntity} created, or {@link Optional#empty()} if the spawn
     * failed
     */
    Optional<IBossEntity> spawn();

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
     * Specifies the announcement system that will be used to advertise the boss as it is spawned in.
     *
     * @return The announcement procedure to use for the spawn of the raid pokemon
     */
    ISpawnAnnouncement getAnnouncement();

}