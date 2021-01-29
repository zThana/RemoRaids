package ca.landonjw.remoraids.api.boss;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * The Boss Entity Registry is simply a manager that contains references to all active raid bosses that
 * are spawned in on the server. You can use it to locate a specific raid boss entity that you'd like
 * to view information on.
 *
 * @author NickImpact
 * @since 1.0.0
 */
public interface IBossEntityRegistry {

	/**
	 * Gets a boss entity by it's unique id, if available
	 *
	 * This will result in an empty optional if a boss entity with supplied
	 * unique id is not registered.
	 *
	 * @param bossUniqueId unique id of boss to get
	 * @return boss entity, if available
	 */
	Optional<IBossEntity> getBossEntity(UUID bossUniqueId);

	/**
	 * Gets all boss entities in the registry.
	 *
	 * @return all boss entities in the registry
	 */
	Set<IBossEntity> getAllBossEntities();

}
