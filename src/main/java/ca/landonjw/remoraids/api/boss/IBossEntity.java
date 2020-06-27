package ca.landonjw.remoraids.api.boss;

import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public interface IBossEntity {

    IBoss getBoss();

    IBossSpawner getSpawner();

    Optional<EntityStatue> getEntity();

    Optional<EntityPixelmon> getBattleEntity();

    Vec3d getPosition();

    World getWorld();

    IBossEngager getBossEngager();

    void despawn();

}