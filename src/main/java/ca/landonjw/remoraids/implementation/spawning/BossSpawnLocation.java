package ca.landonjw.remoraids.implementation.spawning;

import java.util.Arrays;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;

import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.util.gson.JObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class BossSpawnLocation implements IBossSpawnLocation {

	private World world;
	private Vec3d location;
	private float rotation;

	public BossSpawnLocation(@Nonnull World world, Vec3d location, float rotation) {
		this.world = world;
		this.location = location;
		this.rotation = rotation;
	}

	public BossSpawnLocation(@Nonnull Entity parent) {
		this(parent.world, new Vec3d(parent.posX, parent.posY, parent.posZ), parent.rotationYaw);
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Vec3d getLocation() {
		return location;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public JObject serialize() {
		return new JObject().add("world", this.world.getWorldInfo().getWorldName()).add("x", this.location.x).add("y", this.location.y).add("z", this.location.z).add("rotation", this.rotation);
	}

	public static class BossSpawnLocationBuilder implements IBossSpawnLocationBuilder {

		private World world;
		private Vec3d location;
		private float rotation;

		@Override
		public IBossSpawnLocationBuilder world(World world) {
			this.world = world;
			return this;
		}

		@Override
		public IBossSpawnLocationBuilder location(Vec3d location) {
			this.location = location;
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
			return new BossSpawnLocation(world, location, rotation);
		}

		@Override
		public IBossSpawnLocationBuilder deserialize(JsonObject json) {
			this.world = Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().worlds).filter(x -> x.getWorldInfo().getWorldName().equals(json.get("world").getAsString())).findAny().orElseThrow(() -> new RuntimeException("World does not exist..."));
			double x = json.get("x").getAsDouble();
			double y = json.get("y").getAsDouble();
			double z = json.get("z").getAsDouble();
			this.location = new Vec3d(x, y, z);
			this.rotation = json.get("rotation").getAsFloat();

			return this;
		}

	}

}
