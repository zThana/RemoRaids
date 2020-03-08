package ca.landonjw.remoraids.implementation.boss.engage;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.tasks.Task;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class ActionBarEngager extends AbstractBossEngager {

    public ActionBarEngager(@Nonnull IBossEntity bossEntity,
                            float engageRange,
                            @Nonnull String message) {
        super(bossEntity, engageRange, message);
    }

    @Override
    public void sendEngageMessage() {
        EntityStatue bossStatue = getBossEntity().getEntity();

        for(EntityPlayer player : bossStatue.world.playerEntities){
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if(isPlayerInRange(playerMP) && BattleRegistry.getBattle(playerMP) == null){
                playerMP.sendStatusMessage(new TextComponentString(getMessage()), true);
            }
        }
    }

    @Override
    protected void startMessageTask(){
        Task.builder()
                .execute((task) -> {
                    if(!getBossEntity().getEntity().isDead){
                        sendEngageMessage();
                    }
                    else{
                        task.setExpired();
                    }
                })
                .interval(30)
                .build();
    }

}
