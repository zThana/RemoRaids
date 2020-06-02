package ca.landonjw.remoraids.implementation.listeners.pixelmon;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import com.pixelmonmod.pixelmon.api.events.PixelmonSendOutEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

/**
 * Listens for players sending out pixelmon, and if they are within range of a boss, will start battle with it.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class EngageListener {

    /**
     * Invoked when a player sends out a pokemon.
     * This will start a boss battle if player is in range of one.
     *
     * @param event event caused player sending out pokemon
     */
    @SubscribeEvent
    public void onSendOut(PixelmonSendOutEvent event){
        if(event.pokemon.getHealth() > 0){
            for(IBossEntity bossEntity : BossEntityRegistry.getInstance().getAllBossEntities()){
                if(bossEntity.getBossEngager().isPlayerInRange(event.player)){
                    event.setCanceled(true);
                    IBossBattle battle = IBossAPI.getInstance().getBossBattleRegistry().getBossBattle(bossEntity).get();
                    battle.startBattle(event.player, event.pokemon.getOrSpawnPixelmon(event.player));
                }
            }
        }
    }

}
