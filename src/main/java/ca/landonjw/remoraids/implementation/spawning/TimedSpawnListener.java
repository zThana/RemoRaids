package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimedSpawnListener {

    private static TimedSpawnListener instance;

    private static Map<IBossEntity, TimedBossSpawner> respawnList = new HashMap<>();

    @SubscribeEvent
    public void onBossDeath(BossDeathEvent event){
        if(respawnList.containsKey(event.getBossEntity())){
            System.out.println("Found matching entity");
            TimedBossSpawner spawner = respawnList.get(event.getBossEntity());
            respawnList.remove(event.getBossEntity());
            spawner.startCooldown();
        }
    }

    void addRespawningEntity(@Nonnull IBossEntity entity, @Nonnull TimedBossSpawner spawner){
        respawnList.put(entity, spawner);
    }

    public static TimedSpawnListener getInstance() {
        if(instance == null){
            instance = new TimedSpawnListener();
        }
        return instance;
    }

}
