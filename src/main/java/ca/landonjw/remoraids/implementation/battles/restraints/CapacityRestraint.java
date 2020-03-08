package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartedEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

public class CapacityRestraint implements IBattleRestraint {

    private IBossEntity bossEntity;
    protected int capacity;
    protected int playerNum;

    public CapacityRestraint(@Nonnull IBossEntity bossEntity, int capacity){
        this.bossEntity = bossEntity;
        this.capacity = capacity;
        RemoRaids.EVENT_BUS.register(this);
    }

    @Override
    public boolean validatePlayer(EntityPlayerMP player) {
        if(playerNum >= capacity){
            player.sendMessage(new TextComponentString(RemoRaids.getMessageConfig().getCapacityRestraintDenied()));
        }
        return playerNum < capacity;
    }

    @SubscribeEvent
    public void onBattleStart(BossBattleStartedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            playerNum++;
        }
        if(!RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()){
            RemoRaids.EVENT_BUS.unregister(this);
        }
    }

    @SubscribeEvent
    public void onBattleEnd(BossBattleEndedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            playerNum--;
        }
        if(!RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()){
            RemoRaids.EVENT_BUS.unregister(this);
        }
    }

    public IBossEntity getBossEntity() {
        return bossEntity;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getPlayerNum() {
        return playerNum;
    }

}
