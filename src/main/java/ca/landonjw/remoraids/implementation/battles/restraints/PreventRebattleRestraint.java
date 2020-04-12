package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class PreventRebattleRestraint implements IBattleRestraint {

    private IBossEntity bossEntity;
    private Set<EntityPlayerMP> restrainedPlayers = new HashSet<>();

    public PreventRebattleRestraint(@Nonnull IBossEntity bossEntity){
        this.bossEntity = bossEntity;
        RemoRaids.EVENT_BUS.register(this);
    }

    @Override
    public boolean validatePlayer(EntityPlayerMP player) {
        if(restrainedPlayers.contains(player)){
            player.sendMessage(new TextComponentString(RemoRaids.getMessageConfig().get(MessageConfig.RAID_NO_REBATLLE)));
        }
        return !restrainedPlayers.contains(player);
    }

    @SubscribeEvent
    public void onBattleEnd(BossBattleEndedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            restrainedPlayers.add(event.getPlayer());
        }
        if(!RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()){
            RemoRaids.EVENT_BUS.unregister(this);
        }
    }

}
