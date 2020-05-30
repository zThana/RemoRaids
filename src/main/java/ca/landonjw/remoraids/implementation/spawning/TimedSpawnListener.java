package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Listener that works with {@link TimedBossSpawner} to allow for respawning bosses.
 * This holds a map of boss entities and the spawners, and when a boss dies, will invoke the spawner to start the cooldown.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class TimedSpawnListener {

    /** The instance of the timed spawn listener. */
    private static TimedSpawnListener instance;
    /** Map of boss entities and their corresponding spawners. */
    private Map<IBossEntity, TimedBossSpawner> respawnList = new HashMap<>();

    /**
     * Constructor for the timed spawn listener. This will register the boss death listener.
     */
    public TimedSpawnListener(){
        RemoRaids.EVENT_BUS.register(this);
    }

    /**
     * Invoked when a boss dies.
     * This will start the respawn cooldown for any bosses that have a timed boss spawner.
     *
     * @param event event caused by a boss dying
     */
    @SubscribeEvent
    public void onBossDeath(BossDeathEvent event){
        if(respawnList.containsKey(event.getBossEntity())){
            TimedBossSpawner spawner = respawnList.get(event.getBossEntity());
            respawnList.remove(event.getBossEntity());
            if(spawner.getTimesRespawned() < spawner.getRespawnLimit()){
                spawner.startCooldown();
            }
        }
    }

    /**
     * Adds a boss entity as a respawning entity.
     *
     * @param entity  the boss that respawns
     * @param spawner the spawner that spawns the boss
     */
    public void addRespawningEntity(@Nonnull IBossEntity entity, @Nonnull TimedBossSpawner spawner){
        respawnList.put(entity, spawner);
    }

    /**
     * Gets the spawner of an entity if available
     *
     * @param bossEntity the entity to get spawner for
     * @return the spawner corresponding to the entity, if available
     */
    public Optional<TimedBossSpawner> getTimedBossSpawner(@Nonnull IBossEntity bossEntity){
        return Optional.ofNullable(respawnList.get(bossEntity));
    }

    /**
     * Gets the instance of the timed spawn listener.
     * If the instance is not created, it will construct it.
     *
     * @return the instance of the timed spawn listener
     */
    public static TimedSpawnListener getInstance() {
        if(instance == null){
            instance = new TimedSpawnListener();
        }
        return instance;
    }

}
