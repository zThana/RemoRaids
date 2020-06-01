package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import ca.landonjw.remoraids.implementation.spawning.respawning.RespawnData;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listens for entity updates, and if it is a boss, it will cancel any updates.
 * This will prevent any movement and collision checks to be invoked by native Minecraft,
 * which will massively improve performance when bosses have an increased bounding box.
 *
 * This will also do a check to see if a boss does not have it's intended size, and will update
 * accordingly if so.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossUpdateListener {

    /**
     * Invoked when a living entity updates.
     *
     * @param event event caused by living entities updating
     */
    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        for(IBossEntity bossEntity : BossEntityRegistry.getInstance().getAllBossEntities()){
            if(bossEntity.getEntity().equals(event.getEntity())){
                event.setCanceled(true);
                updateBossSize(bossEntity);
            }
        }
    }

    /**
     * Listens for the death of a raid boss. This listener is responsible for simply ensuring
     * that, if a raid boss spawner has any respawn data attached to it, that it will attempt
     * to respawn the raid boss after death given the spawner's running task.
     *
     * @param event The event marking the death of a raid boss
     */
    @SubscribeEvent
    public void onRaidBossDeath(BossDeathEvent event) {
        final IBossSpawner spawner = event.getBossEntity().getSpawner();
        spawner.getRespawnData().ifPresent(data -> {
            if(data.isInfinite() || data.hasRemainingRespawns()) {
                data.run(spawner);
            }
        });
    }

    /**
     * Fixes the size of any bosses that are not the size they are intended to be.
     *
     * @param bossEntity the entity to check and potentially update
     */
    private void updateBossSize(IBossEntity bossEntity){
        if(bossEntity.getBoss().getSize() != bossEntity.getEntity().getPixelmonScale()){
            bossEntity.getEntity().setPixelmonScale(bossEntity.getBoss().getSize());
        }
    }

}
