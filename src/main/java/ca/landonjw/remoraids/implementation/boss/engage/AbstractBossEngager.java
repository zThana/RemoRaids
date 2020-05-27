package ca.landonjw.remoraids.implementation.boss.engage;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;

public abstract class AbstractBossEngager implements IBossEngager {

    private IBossEntity bossEntity;

    private float engageRange;
    private String message;

    public AbstractBossEngager(@Nonnull IBossEntity bossEntity, float engageRange, @Nonnull String message){
        this.bossEntity = bossEntity;
        this.engageRange = engageRange;
        this.message = message;
        startMessageTask();
    }

    @Override
    public float getRange() {
        return engageRange;
    }

    public IBossEntity getBossEntity() {
        return bossEntity;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean isPlayerInRange(@Nonnull EntityPlayerMP player) {
        EntityStatue bossStatue = bossEntity.getEntity();

        boolean inRange = false;
        if(!bossStatue.isDead){
            if(player.world.equals(bossStatue.world)){
                inRange = player.getDistance(bossStatue) <= engageRange;
            }
        }
        return inRange;
    }

    @Override
    public abstract void sendEngageMessage();

    protected abstract void startMessageTask();

}
