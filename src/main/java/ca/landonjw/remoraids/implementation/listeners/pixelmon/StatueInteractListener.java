package ca.landonjw.remoraids.implementation.listeners.pixelmon;

import com.pixelmonmod.pixelmon.api.events.StatueEvent;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listens for interactions with a {@link IBossEntity}'s statue entity.
 * It will prevent any misuse of boss statues with a chisel.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class StatueInteractListener {

	/**
	 * Invoked when a statue is destroyed.
	 * This will prevent a boss statue from being destroyed and send a message to the player that the action isn't allowed.
	 *
	 * @param event event caused by statue destruction
	 */
	@SubscribeEvent
	public void onStatueDestroy(StatueEvent.DestroyStatue event) {
		for (IBossEntity bossEntity : IBossAPI.getInstance().getBossEntityRegistry().getAllBossEntities()) {
			if (bossEntity.getEntity().isPresent() && bossEntity.getEntity().get().equals(event.statue)) {
				event.setCanceled(true);
				Config config = RemoRaids.getMessageConfig();
				event.player.sendMessage(new TextComponentString(config.get(MessageConfig.ERROR_CHISEL_INTERACT)));
			}
		}
	}

	/**
	 * Invoked when a statue is modified.
	 * This will prevent a boss statue from being modified and send a message to the player that the action isn't allowed.
	 *
	 * @param event event caused by statue modification
	 */
	@SubscribeEvent
	public void onStatueModify(StatueEvent.ModifyStatue event) {
		for (IBossEntity bossEntity : IBossAPI.getInstance().getBossEntityRegistry().getAllBossEntities()) {
			if (bossEntity.getEntity().isPresent() && bossEntity.getEntity().get().equals(event.getStatue())) {
				event.setCanceled(true);
				Config config = RemoRaids.getMessageConfig();
				event.player.sendMessage(new TextComponentString(config.get(MessageConfig.ERROR_CHISEL_INTERACT)));
			}
		}
	}

}
