package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.implementation.boss.BossEntityRegistry;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BossUpdateListener {

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event){
        for(IBossEntity bossEntity : BossEntityRegistry.getInstance().getAllBossEntities()){
            if(bossEntity.getEntity().equals(event.getEntity())){
                event.setCanceled(true);
            }
        }
    }

}
