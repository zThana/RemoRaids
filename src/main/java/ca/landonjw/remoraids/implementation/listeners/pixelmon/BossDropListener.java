package ca.landonjw.remoraids.implementation.listeners.pixelmon;

import com.pixelmonmod.pixelmon.api.events.DropEvent;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listens for drops being given to a player.
 * This will prevent players from getting drops from {@link IBossEntity}'s on death.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossDropListener {

	/**
	 * Invoked when drops are given to a player.
	 * This will prevent drops from being given if the source is from a boss entity.
	 *
	 * @param event event caused by drops being given to a player
	 */
	@SubscribeEvent
	public void onDrop(DropEvent event) {
		if (event.isPokemon()) {
			for (IBossEntity bossEntity : RemoRaids.getBossAPI().getBossEntityRegistry().getAllBossEntities()) {
				if (bossEntity.getBattleEntity().isPresent() && bossEntity.getBattleEntity().get().equals(event.entity)) {
					event.setCanceled(true);
				}
			}
		}
	}

}
