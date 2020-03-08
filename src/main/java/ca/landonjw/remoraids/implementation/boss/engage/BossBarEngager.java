package ca.landonjw.remoraids.implementation.boss.engage;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.tasks.Task;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;

import javax.annotation.Nonnull;

public class BossBarEngager extends AbstractBossEngager {

    private BossInfoServer bossBar;

    public BossBarEngager(@Nonnull IBossEntity bossEntity,
                          float engageRange,
                          @Nonnull String message,
                          BossInfo.Color color,
                          BossInfo.Overlay overlay) {
        super(bossEntity, engageRange, message);
        bossBar = new BossInfoServer(new TextComponentString(message), color, overlay);
        bossBar.setVisible(true);
    }

    @Override
    public void sendEngageMessage() {
        EntityStatue bossStatue = getBossEntity().getEntity();

        for(EntityPlayer player : bossStatue.world.playerEntities){
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if(isPlayerInRange(playerMP) && BattleRegistry.getBattle(playerMP) == null){
                bossBar.addPlayer(playerMP);
            }
            else{
                bossBar.removePlayer(playerMP);
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
