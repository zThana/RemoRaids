package ca.landonjw.remoraids.implementation.spawning.respawning;

import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.internal.tasks.Task;

import java.util.concurrent.TimeUnit;

public class RespawnData implements IBossSpawner.IRespawnData {

	/** A flag marking whether or not this data allows infinite respawns */
	private boolean infinite;

	private int respawns;
	private int total;

	/** The amount of ticks it'll take to handle a respawn attempt */
	private long rate;

	@Override
	public boolean isInfinite() {
		return this.infinite;
	}

	@Override
	public void setInfinite(boolean state) {
		this.infinite = state;
	}

	@Override
	public int getRemainingRespawns() {
		return this.total - this.respawns;
	}

	@Override
	public void setRemainingRespawns(int amount) {
		this.total = Math.max(this.total, this.respawns = amount);

	}

	@Override
	public void incrementRespawnCounter() {
		++this.respawns;
	}

	@Override
	public int getTotalRespawns() {
		return this.total;
	}

	@Override
	public long getTimeRemainingUntilRespawn(TimeUnit unit) {
		if(this.task == null) {
			return this.getTotalWaitPeriod(unit);
		}
		return unit.convert(this.task.getTicksRemaining() / 20, TimeUnit.SECONDS);
	}

	@Override
	public long getTotalWaitPeriod(TimeUnit unit) {
		return unit.convert(this.rate / 20, TimeUnit.SECONDS);
	}

	@Override
	public void setTotalWaitPeriod(long time, TimeUnit unit) {
		this.rate = unit.toSeconds(time) * 20;
	}

	@Override
	public JObject serialize() {
		return new JObject()
				.add("infinite", this.infinite)
				.add("respawn-counts", new JObject()
						.add("respawns", this.respawns)
						.add("total", this.total)
				)
				.add("rate", this.rate);
	}

	private Task task;

	@Override
	public void run(IBossSpawner spawner) {
		this.task = Task.builder()
				.delay(this.rate)
				.iterations(1)
				.execute(() -> {
					this.incrementRespawnCounter();
					spawner.spawn();
					this.task = null;
				})
				.build();
	}
}
