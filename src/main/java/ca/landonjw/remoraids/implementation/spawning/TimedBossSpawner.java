package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.internal.tasks.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * An {@link IBossSpawner} that will attempt to respawn a boss if conditions are met.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class TimedBossSpawner extends BossSpawner {

    /** The number of times the boss has respawned. */
    private int numRespawns;
    /** The maximum amount of times the boss may respawn. After this point, respawning will halt. */
    private int respawnLimit;
    /** The number of ticks before the boss will respawn after dying. */
    private long cooldownTimeTicks;

    /**
     * Constructor for the timed boss spawner.
     *
     * @param boss          the boss to spawn
     * @param spawnLocation the location to spawn the boss at
     * @param announcement  the announcement to send to players when the boss is spawned, may be null for no announcement
     * @param respawnLimit  the number of times the boss will respawn
     * @param delay         the delay in ticks before a boss will respawn after dying
     */
    public TimedBossSpawner(@Nonnull IBoss boss,
                            @Nonnull IBossSpawnLocation spawnLocation,
                            @Nullable ISpawnAnnouncement announcement,
                            int respawnLimit,
                            long delay) {
        super(boss, spawnLocation, announcement);
        this.respawnLimit = respawnLimit;
        cooldownTimeTicks = delay;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<IBossEntity> spawn() {
        Optional<IBossEntity> entity = super.spawn();
        entity.ifPresent(boss -> {
            TimedSpawnListener.getInstance().addRespawningEntity(boss, this);
        });
        return entity;
    }

    /**
     * Attempts to respawn the boss, incrementing the number of times the boss has respawned.
     *
     * @return the spawned boss, if successful
     */
    public Optional<IBossEntity> respawn() {
        numRespawns++;
        return spawn();
    }

    /**
     * Starts the cooldown for respawning.
     */
    void startCooldown(){
        Task.builder()
                .identifier("Timed-Spawner")
                .execute(this::respawn)
                .delay(cooldownTimeTicks)
                .iterations(1)
                .build();
    }

    /**
     * Gets the number of times the boss has respawned.
     *
     * @return the number of times the boss has respawned
     */
    public int getTimesRespawned(){
        return numRespawns;
    }

    /**
     * Gets the number of times the boss may respawn.
     *
     * @return the number of times the boss may respawn
     */
    public int getRespawnLimit() {
        return respawnLimit;
    }

    /**
     * Sets the respawn limit for the boss.
     *
     * @param spawnLimit the respawn limit for the boss
     */
    public void setRespawnLimit(int spawnLimit) {
        this.respawnLimit = spawnLimit;
    }

    /**
     * Gets the cooldown before the boss will respawn after dying.
     *
     * @param unit the unit of time to get cooldown in
     * @return the cooldown before the boss will respawn after dying
     */
    public long getCooldownTime(TimeUnit unit) {
        return unit.convert(cooldownTimeTicks / 20, TimeUnit.SECONDS);
    }

    /**
     * Sets the cooldown before the boss will respawn after dying.
     *
     * @param cooldown the cooldown value
     * @param unit     the unit of time cooldown is in
     */
    public void setCooldownTime(long cooldown, TimeUnit unit) {
        this.cooldownTimeTicks = unit.toSeconds(cooldown) * 20;
    }

}
