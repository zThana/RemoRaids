package ca.landonjw.remoraids.implementation.listeners;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.implementation.battles.controller.participants.BossPlayerParticipant;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BattleEndListener {

    @SubscribeEvent
    public void onBattleEnd(BattleEndEvent event){
        if(event.bc.getPlayers().size() == 1){
            if(event.bc.getPlayers().get(0) instanceof BossPlayerParticipant){
                EntityPlayerMP player = event.bc.getPlayers().get(0).player;
                RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(player).get().endBattle(player);
            }
        }
    }

}
