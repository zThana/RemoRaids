package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.util.gson.JObject;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class BossSpawnLocation implements IBossSpawnLocation {

    private World world;
    private double x, y, z;
    private float rotation;

    public BossSpawnLocation(@Nonnull World world, double x, double y, double z, float rotation){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
    }

    public BossSpawnLocation(@Nonnull Entity parent){
        this(parent.world, parent.posX, parent.posY, parent.posZ, parent.rotationYaw);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public JObject serialize() {
        return new JObject()
                .add("world", this.world.getWorldInfo().getWorldName())
                .add("x", this.x)
                .add("y", this.y)
                .add("z", this.z)
                .add("rotation",this.rotation);
    }

    public static class BossSpawnLocationBuilder implements IBossSpawnLocationBuilder {

        private World world;
        private double x, y, z;
        private float rotation;

        @Override
        public IBossSpawnLocationBuilder world(World world) {
            this.world = world;
            return this;
        }

        @Override
        public IBossSpawnLocationBuilder x(double x) {
            this.x = x;
            return this;
        }

        @Override
        public IBossSpawnLocationBuilder y(double y) {
            this.y = y;
            return this;
        }

        @Override
        public IBossSpawnLocationBuilder z(double z) {
            this.z = z;
            return this;
        }

        @Override
        public IBossSpawnLocationBuilder rotation(float rotation) {
            this.rotation = rotation;
            return this;
        }

        @Override
        public IBossSpawnLocationBuilder from(IBossSpawnLocation input) {
            return this;
        }

        @Override
        public IBossSpawnLocation build() {
            return new BossSpawnLocation(world, x, y, z, rotation);
        }

        @Override
        public IBossSpawnLocationBuilder deserialize(JsonObject json) {
            this.world = Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
                    .filter(x -> x.getWorldInfo().getWorldName().equals(json.get("world").getAsString()))
                    .findAny().orElseThrow(() -> new RuntimeException("World does not exist..."));
            this.x = json.get("x").getAsDouble();
            this.y = json.get("y").getAsDouble();
            this.z = json.get("z").getAsDouble();
            this.rotation = json.get("rotation").getAsFloat();

            return this;
        }

    }

}
