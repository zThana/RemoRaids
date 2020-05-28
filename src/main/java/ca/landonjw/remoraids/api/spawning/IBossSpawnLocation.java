package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.util.DataSerializable;
import net.minecraft.world.World;

public interface IBossSpawnLocation extends DataSerializable {

    World getWorld();

    double getX();

    double getY();

    double getZ();

    float getRotation();

}
