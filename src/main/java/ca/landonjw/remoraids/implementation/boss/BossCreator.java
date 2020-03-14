package ca.landonjw.remoraids.implementation.boss;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.BossSpawner;
import ca.landonjw.remoraids.implementation.spawning.TimedBossSpawner;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.announcements.TeleportableSpawnAnnouncement;
import com.google.common.base.Preconditions;
import net.minecraft.world.World;

import java.util.concurrent.TimeUnit;

public class BossCreator implements IBossCreator {

	private IBoss boss;
	private IBossSpawnLocation location;
	private ISpawnAnnouncement announcement;

	private int amount;
	private long ticks;
	private boolean respawns;

	@Override
	public IBossCreator boss(IBoss boss) {
		this.boss = boss;
		return this;
	}

	@Override
	public IBossCreator location(World world, double x, double y, double z, float yaw) {
		this.location = new BossSpawnLocation(
				world,
				x,
				y,
				z,
				yaw
		);
		return this;
	}

	@Override
	public IBossCreator location(IBossSpawnLocation location) {
		this.location = location;
		return this;
	}

	@Override
	public IBossCreator announcement(boolean allowTP, String message) {
		this.announcement = allowTP ? new TeleportableSpawnAnnouncement(message) : new SpawnAnnouncement(message);
		return this;
	}

	@Override
	public IBossCreator announcement(ISpawnAnnouncement announcement) {
		this.announcement = announcement;
		return this;
	}

	@Override
	public IBossCreator respawns(int amount, long time, TimeUnit unit) {
		this.respawns = true;
		this.amount = amount;
		this.ticks = unit.toSeconds(time) * 20;
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
			this.announcement(false, RemoRaids.getMessageConfig().getSpawnAnnouncement());
		}

		if(this.respawns) {
			return new TimedBossSpawner(boss, location, announcement, amount, ticks);
		} else {
			return new BossSpawner(boss, location, announcement);
		}
	}
}
