package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IBossSpawnLocation extends DataSerializable {

    World getWorld();

    Vec3d getLocation();

    float getRotation();

    static IBossSpawnLocationBuilder builder() {
        return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossSpawnLocationBuilder.class);
    }

    interface IBossSpawnLocationBuilder extends IBuilder.Deserializable<IBossSpawnLocation, IBossSpawnLocationBuilder> {

        IBossSpawnLocationBuilder world(World world);

        IBossSpawnLocationBuilder location(Vec3d location);

        IBossSpawnLocationBuilder rotation(float rotation);

    }

}
