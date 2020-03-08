package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;

import java.util.Optional;

public interface IBossSpawner {

    Optional<IBossEntity> spawn();

    IBossSpawnLocation getSpawnLocation();

    IBoss getBoss();

    void setSpawnLocation(IBossSpawnLocation location);

}