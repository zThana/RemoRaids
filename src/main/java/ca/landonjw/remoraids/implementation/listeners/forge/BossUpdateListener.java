package ca.landonjw.remoraids.implementation.listeners.forge;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import ca.landonjw.remoraids.implementation.spawning.respawning.RespawnData;
import ca.landonjw.remoraids.internal.math.Vec3DMath;
import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Method;
import java.util.Arrays;

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
            if(bossEntity.getEntity().isPresent() && bossEntity.getEntity().get().equals(event.getEntity())){
                event.setCanceled(true);
                updateBossSize(bossEntity);
            }
        }
    }

    /**
     * Fixes the size of any bosses that are not the size they are intended to be.
     *
     * @param bossEntity the entity to check and potentially update
     */
    private void updateBossSize(IBossEntity bossEntity){
        if(bossEntity.getBoss().getSize() != bossEntity.getEntity().get().getPixelmonScale()){
            bossEntity.getEntity().get().setPixelmonScale(bossEntity.getBoss().getSize());
        }

        try {
            Method method = EntityStatue.class.getDeclaredMethod("updateSize");
            method.setAccessible(true);
            method.invoke(bossEntity.getEntity().get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        for(IBossSpawner spawner : RemoRaids.storage.getSpawners()) {
            if(!spawner.hasSpawned()) {
                Vec3d position = spawner.getSpawnLocation().getLocation();

                if(Vec3DMath.withinChunk(chunk, position)) {
                    Arrays.stream(chunk.getEntityLists()).map(entity -> entity.getByClass(EntityStatue.class))
                            .map(Lists::newArrayList)
                            .findAny()
                            .ifPresent(list -> list.forEach(statue -> {
                                if(statue.getPokemon().isPokemon(spawner.getBoss().getPokemon().getSpecies())) {
                                    if(statue.getPositionVector().equals(spawner.getSpawnLocation().getLocation())) {
                                        statue.setDead();
                                    }
                                }
                            }));

                    spawner.spawn(false);
                }
            }

        }
    }

}
