package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.concurrent.TimeUnit;

public class BossCreator implements IBossCreator {

	private IBossSpawner.IBossSpawnerBuilder controller = IBossAPI.getInstance().getRaidRegistry().createSpawnerBuilder("default");

	private IBoss boss;
	private IBossSpawnLocation location;
	private ISpawnAnnouncement announcement;
	private IBossSpawner.IRespawnData respawnData;

	private boolean persisting;

	@Override
	public IBossCreator controller(String key) {
		this.controller = IBossAPI.getInstance().getRaidRegistry().createSpawnerBuilder(key);
		return this;
	}

	@Override
	public IBossCreator boss(IBoss boss) {
		this.boss = boss;
		return this;
	}

	@Override
	public IBossCreator location(World world, Vec3d location, float rotation) {
		this.location = new BossSpawnLocation(world, location, rotation);
		return this;
	}

	@Override
	public IBossCreator location(IBossSpawnLocation location) {
		this.location = location;
		return this;
	}

	@Override
	public IBossCreator announcement(boolean allowTP, String message) {
		if(allowTP){
			this.announcement = ISpawnAnnouncement.builder()
					.message(message)
					.warp(location.getWorld(), location.getLocation(), location.getRotation())
					.build();
		}
		else{
			this.announcement = ISpawnAnnouncement.builder()
					.message(message)
					.build();
		}
		return this;
	}

	@Override
	public IBossCreator announcement(ISpawnAnnouncement announcement) {
		this.announcement = announcement;
		return this;
	}

	@Override
	public IBossCreator respawns(int amount, long time, TimeUnit unit) {
		this.respawnData = IBossSpawner.IRespawnData.builder()
				.count(amount)
				.period(time, unit)
				.build();
		return this;
	}

	@Override
	public IBossCreator respawns() {
		this.respawnData = IBossSpawner.IRespawnData.builder().infinite(true).period(5, TimeUnit.SECONDS).build();
		return this;
	}

	@Override
	public IBossCreator persists(boolean persists) {
		this.persisting = persists;
		return this;
	}

	@Override
	public IBossCreator from(IBossSpawner input) {
		this.boss = input.getBoss();
		this.location = input.getSpawnLocation();
		return this;
	}

	@Override
	public IBossSpawner build() {
		Preconditions.checkNotNull(boss, "No raid pokemon specified");
		Preconditions.checkNotNull(location, "No spawn location specified");
		if(announcement == null) {
			this.announcement(false, RemoRaids.getMessageConfig().get(MessageConfig.RAID_SPAWN_ANNOUNCE));
		}

		return this.controller
				.boss(this.boss)
				.location(this.location)
				.announcement(this.announcement)
				.respawns(this.respawnData)
				.persists(this.persisting)
				.build();
	}

	@Override
	public IBossCreator deserialize(JsonObject json) {
		this.boss = IBoss.builder().deserialize(json.get("boss").getAsJsonObject()).build();
		if(json.has("respawning")) {
			this.respawnData = IBossSpawner.IRespawnData.builder().deserialize(json.get("respawning").getAsJsonObject()).build();
		}
		this.location = IBossSpawnLocation.builder().deserialize(json.get("location").getAsJsonObject()).build();
		this.announcement = ISpawnAnnouncement.builder().deserialize(json.get("announcement").getAsJsonObject()).build();

		return this;
	}
}
