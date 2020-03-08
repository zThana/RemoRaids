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

    private int numRespawns;
    private long cooldownTimeTicks;

    public TimedBossSpawner(@Nonnull IBoss boss,
                            @Nonnull IBossSpawnLocation spawnLocation,
                            @Nullable ISpawnAnnouncement announcement,
                            int numRespawns,
                            long cooldown,
                            @Nonnull TimeUnit timeUnit) {
        super(boss, spawnLocation, announcement);
        this.numRespawns = numRespawns;
        cooldownTimeTicks = timeUnit.toSeconds(cooldown);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<IBossEntity> spawn() {
        Optional<IBossEntity> entity = super.spawn();
        if(entity.isPresent()){
            TimedSpawnListener.getInstance().addRespawningEntity(entity.get(), this);
        }

        return entity;
    }

    public void startRespawnCooldown(){
        Task.builder()
                .execute((task) -> spawn())
                .delay(cooldownTimeTicks)
                .iterations(1)
                .build();
    }

    public int getNumRespawns() {
        return numRespawns;
    }

    public long getCooldownTime(TimeUnit unit) {
        return unit.convert(cooldownTimeTicks / 20, TimeUnit.SECONDS);
    }

}
