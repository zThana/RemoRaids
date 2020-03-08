package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import com.pixelmonmod.pixelmon.api.events.PixelmonSendOutEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Optional;

public class EngageListener {

    @SubscribeEvent
    public void onSendOut(PixelmonSendOutEvent event){
        if(event.pokemon.getHealth() > 0){
            for(IBossEntity bossEntity : BossEntityRegistry.getInstance().getAllBossEntities()){
                if(bossEntity.getBossEngager().isPlayerInRange(event.player)){
                    event.setCanceled(true);
                    Optional<IBossBattle> battle = IBossAPI.getInstance().getBossBattleRegistry().getBossBattle(bossEntity);
                    if(battle.isPresent()){
                        battle.get().startBattle(event.player, event.pokemon.getOrSpawnPixelmon(event.player));
                    }
                }
            }
        }
    }

}
