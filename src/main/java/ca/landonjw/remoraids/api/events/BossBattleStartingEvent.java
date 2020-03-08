package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

@Cancelable
public class BossBattleStartingEvent extends Event {

    private final IBossEntity bossEntity;
    private final EntityPlayerMP player;

    public BossBattleStartingEvent(@Nonnull IBossEntity bossEntity, @Nonnull EntityPlayerMP player){
        this.bossEntity = bossEntity;
        this.player = player;
    }

    public IBossEntity getBossEntity(){
        return bossEntity;
    }

    public EntityPlayerMP getPlayer(){
        return player;
    }

}
