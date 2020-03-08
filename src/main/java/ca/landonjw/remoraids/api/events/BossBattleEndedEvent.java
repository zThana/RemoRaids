package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class BossBattleEndedEvent extends Event {

    private final IBossEntity bossEntity;
    private final EntityPlayerMP player;
    private final IBossBattle bossBattle;

    public BossBattleEndedEvent(@Nonnull IBossEntity bossEntity,
                                @Nonnull EntityPlayerMP player,
                                @Nonnull IBossBattle bossBattle){
        this.bossEntity = bossEntity;
        this.player = player;
        this.bossBattle = bossBattle;
    }

    public IBossEntity getBossEntity(){
        return bossEntity;
    }

    public EntityPlayerMP getPlayer(){
        return player;
    }

    public IBossBattle getBossBattle() {
        return bossBattle;
    }

}
