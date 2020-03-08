package ca.landonjw.remoraids.api.spawning;

import net.minecraft.world.World;

public interface IBossSpawnLocation {

    World getWorld();

    double getX();

    double getY();

    double getZ();

    float getRotation();

}
