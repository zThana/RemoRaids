package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

public class BossBattleStartedEvent extends Event {

    private final IBossEntity bossEntity;
    private final EntityPlayerMP player;
    private final BattleControllerBase battleController;

    public BossBattleStartedEvent(@Nonnull IBossEntity bossEntity,
                                  @Nonnull EntityPlayerMP player,
                                  @Nonnull BattleControllerBase battleController){
        this.bossEntity = bossEntity;
        this.player = player;
        this.battleController = battleController;
    }

    public IBossEntity getBossEntity(){
        return bossEntity;
    }

    public EntityPlayerMP getPlayer(){
        return player;
    }

    public BattleControllerBase getBattleController() {
        return battleController;
    }

}
