package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.internal.tasks.Task;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TimedBossSpawner extends BossSpawner {

    private Task runner;

    private int numRespawns;
    private long cooldownTimeTicks;

    public TimedBossSpawner(@Nonnull IBoss boss,
                            @Nonnull IBossSpawnLocation spawnLocation,
                            @Nullable ISpawnAnnouncement announcement,
                            int numRespawns,
                            long interval) {
        super(boss, spawnLocation, announcement);
        this.numRespawns = numRespawns;
        cooldownTimeTicks = interval;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<IBossEntity> spawn() {
        Optional<IBossEntity> entity = super.spawn();
        entity.ifPresent(boss -> TimedSpawnListener.getInstance().addRespawningEntity(boss, this));
        return entity;
    }

    void startRespawnCooldown(){
        this.runner = Task.builder()
                .execute(this::spawn)
                .interval(cooldownTimeTicks)
                .iterations(this.getNumRespawns())
                .build();
    }

    public int getNumRespawns() {
        return numRespawns;
    }

    public long getCooldownTime(TimeUnit unit) {
        return unit.convert(cooldownTimeTicks / 20, TimeUnit.SECONDS);
    }

}
