package ca.landonjw.remoraids.api.boss;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The Boss Entity Registry is simply a manager that contains references to all active raid bosses that
 * are spawned in on the server. You can use it to locate a specific raid boss entity that you'd like
 * to view information on.
 */
public interface IBossEntityRegistry {

    Optional<IBossEntity> getBossEntity(UUID bossUniqueId);

    Set<IBossEntity> getAllBossEntities();

}
