package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class BossSpawnedEvent extends Event {

    private final IBossEntity bossEntity;
    private final IBossSpawner spawner;

    public BossSpawnedEvent(@Nonnull IBossEntity bossEntity, @Nonnull IBossSpawner spawner){
        this.bossEntity = bossEntity;
        this.spawner = spawner;
    }

    public IBossEntity getBossEntity() {
        return bossEntity;
    }

    public IBossSpawner getSpawner() {
        return spawner;
    }

}
