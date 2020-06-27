package ca.landonjw.remoraids.implementation.listeners.raids;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RaidBossDeathListener {

	/**
	 * Listens for the death of a raid boss. This listener is responsible for simply ensuring
	 * that, if a raid boss spawner has any respawn data attached to it, that it will attempt
	 * to respawn the raid boss after death given the spawner's running task.
	 *
	 * @param event The event marking the death of a raid boss
	 */
	@SubscribeEvent
	public void onRaidBossDeath(BossDeathEvent event) {
		final IBossSpawner spawner = event.getBossEntity().getSpawner();
		spawner.getRespawnData().ifPresent(data -> {
			if(data.isInfinite() || data.hasRemainingRespawns()) {
				data.run(spawner);
			} else {
				RemoRaids.storage.delete(spawner);
			}
		});
	}

}
