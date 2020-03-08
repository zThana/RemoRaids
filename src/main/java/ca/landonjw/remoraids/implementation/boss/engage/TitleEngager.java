package ca.landonjw.remoraids.implementation.boss.engage;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.tasks.Task;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class TitleEngager extends AbstractBossEngager {

    private SPacketTitle.Type titleType;

    public TitleEngager(@Nonnull IBossEntity bossEntity,
                        float engageRange,
                        @Nonnull String message,
                        SPacketTitle.Type titleType) {
        super(bossEntity, engageRange, message);
        this.titleType = titleType;
    }

    @Override
    public void sendEngageMessage() {
        EntityStatue bossStatue = getBossEntity().getEntity();

        SPacketTitle title = new SPacketTitle(titleType, new TextComponentString(getMessage()), 1, 1, 1);

        for(EntityPlayer player : bossStatue.world.playerEntities){
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if(isPlayerInRange(playerMP) && BattleRegistry.getBattle(playerMP) == null){
                playerMP.connection.sendPacket(title);
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
