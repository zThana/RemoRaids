package ca.landonjw.remoraids.api.boss;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IBossEntityRegistry {

    Optional<IBossEntity> getBossEntity(UUID bossUniqueId);

    Set<IBossEntity> getAllBossEntities();

}
