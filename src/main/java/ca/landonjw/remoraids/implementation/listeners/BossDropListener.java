package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import com.pixelmonmod.pixelmon.api.events.DropEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BossDropListener {

    @SubscribeEvent
    public void onDrop(DropEvent event){
        if(event.isPokemon()){
            for(IBossEntity bossEntity : RemoRaids.getBossAPI().getBossEntityRegistry().getAllBossEntities()){
                if(bossEntity.getBattleEntity().equals(event.entity)){
                    event.setCanceled(true);
                }
            }
        }
    }

}
