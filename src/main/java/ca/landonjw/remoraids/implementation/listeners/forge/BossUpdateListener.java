package ca.landonjw.remoraids.implementation.listeners.forge;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.google.common.collect.Lists;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import ca.landonjw.remoraids.internal.math.Vec3DMath;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listens for entity updates, and if it is a boss, it will cancel any updates.
 * This will prevent any movement and collision checks to be invoked by native Minecraft,
 * which will massively improve performance when bosses have an increased bounding box.
 * <p>
 * This will also do a check to see if a boss does not have it's intended size, and will update
 * accordingly if so.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossUpdateListener {

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
        Chunk chunk = event.getChunk();
        for (IBossSpawner spawner : RemoRaids.storage.getSpawners()) {
            if (!spawner.hasSpawned()) {
                Vec3d position = spawner.getSpawnLocation().getLocation();

                if (Vec3DMath.withinChunk(chunk, position)) {
                    Arrays.stream(chunk.getEntityLists()).map(entity -> entity.getByClass(EntityStatue.class)).map(Lists::newArrayList).findAny().ifPresent(list -> list.forEach(statue -> {
                        if (statue.getPokemon().isPokemon(spawner.getBoss().getPokemon().getSpecies())) {
                            if (statue.getPositionVector().equals(spawner.getSpawnLocation().getLocation()))
                                statue.setDead();
                        }
                    }));

                    spawner.spawn(false);
                }
            }

        }
    }
}