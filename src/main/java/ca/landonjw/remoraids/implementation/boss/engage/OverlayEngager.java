package ca.landonjw.remoraids.implementation.boss.engage;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.tasks.Task;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.comm.packetHandlers.custom.overlays.CustomNoticePacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;

public class OverlayEngager extends AbstractBossEngager {

    public OverlayEngager(@Nonnull IBossEntity bossEntity, float engageRange, @Nonnull String message) {
        super(bossEntity, engageRange, message);
    }

    @Override
    public void sendEngageMessage() {
        EntityStatue bossStatue = getBossEntity().getEntity();

        CustomNoticePacket packet = NoticeOverlay.builder()
                .addLines(this.getMessage())
                .build();
        NoticeOverlay.Builder noticeBuilder = NoticeOverlay.builder()
                .setLines(getMessage())
                .setLayout(EnumOverlayLayout.LEFT_AND_RIGHT)
                .setPokemonSprite(PokemonSpec.from(getBossEntity().getEntity().getPokemon().getSpecies().name));

        for(EntityPlayer player : bossStatue.world.playerEntities){
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            if(isPlayerInRange(playerMP) && BattleRegistry.getBattle(playerMP) == null){
                noticeBuilder.sendTo(playerMP);
            }
            else{
                Pixelmon.network.sendTo(new CustomNoticePacket().setEnabled(false), playerMP);
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
