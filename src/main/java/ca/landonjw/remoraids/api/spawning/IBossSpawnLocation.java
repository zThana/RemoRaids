package ca.landonjw.remoraids.api.spawning;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;
import net.minecraft.world.World;

public interface IBossSpawnLocation extends DataSerializable {

    World getWorld();

    double getX();

    double getY();

    double getZ();

    float getRotation();

    static IBossSpawnLocationBuilder builder() {
        return IBossAPI.getInstance().getRaidRegistry().createBuilder(IBossSpawnLocationBuilder.class);
    }

    interface IBossSpawnLocationBuilder extends IBuilder.Deserializable<IBossSpawnLocation, IBossSpawnLocationBuilder> {

        IBossSpawnLocationBuilder world(World world);

        IBossSpawnLocationBuilder x(double x);

        IBossSpawnLocationBuilder y(double y);

        IBossSpawnLocationBuilder z(double z);

        IBossSpawnLocationBuilder rotation(float rotation);

    }

}
