package ca.landonjw.remoraids.implementation.listeners.pixelmon;

import java.util.List;

import com.pixelmonmod.pixelmon.api.events.PixelmonSendOutEvent;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Listens for players sending out pixelmon, and if they are within range of a boss, will start battle with it.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class EngageListener {

	/**
	 * Invoked when a player sends out a pokemon.
	 * This will start a boss battle if player is in range of one.
	 *
	 * @param event event caused player sending out pokemon
	 */
	@SubscribeEvent
	public void onSendOut(PixelmonSendOutEvent event) {
		if (event.pokemon.getHealth() > 0) {
			for (IBossEntity bossEntity : BossEntityRegistry.getInstance().getAllBossEntities()) {
				if (bossEntity.getBossEngager().isPlayerInRange(event.player)) {
					event.setCanceled(true);
					EntityPlayerMP player = event.player;
					IBoss boss = bossEntity.getBoss();

					List<String> rejectionReasons = boss.getBattleSettings().getRejectionMessages(player, boss);
					if (!rejectionReasons.isEmpty()) {
						for (String reason : rejectionReasons) {
							player.sendMessage(new TextComponentString(reason));
						}
						return;
					} else {
						IBossBattle battle = IBossAPI.getInstance().getBossBattleRegistry().getBossBattle(bossEntity).get();
						battle.startBattle(event.player, event.pokemon.getOrSpawnPixelmon(event.player));
					}
				}
			}
		}
	}

}
