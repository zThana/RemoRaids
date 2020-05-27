package ca.landonjw.remoraids.implementation.spawning;

import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.internal.storage.gson.JObject;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

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

    //TODO
    @Override
    public JObject serialize() {
        return null;
    }

}
