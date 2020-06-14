package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PreventRebattleRestraint extends BaseBattleRestraint {

    /** The retraint's identifier. */
    public static final String ID = "Prevent Rebattle Restraint";

    /** The boss entity to apply restraint to. */
    private IBossEntity bossEntity;
    private Set<EntityPlayerMP> restrainedPlayers = new HashSet<>();

    public PreventRebattleRestraint(@Nonnull IBossEntity bossEntity){
        super(ID);
        this.bossEntity = bossEntity;
        RemoRaids.EVENT_BUS.register(this);
    }

    @Override
    public boolean validatePlayer(EntityPlayerMP player) {
        return !restrainedPlayers.contains(player);
    }

    @Override
    public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player) {
        return Optional.of(RemoRaids.getMessageConfig().get(MessageConfig.RAID_NO_REBATLLE));
    }

    @SubscribeEvent
    public void onBattleEnd(BossBattleEndedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            restrainedPlayers.add(event.getPlayer());
        }
    }

    /**
     * Terminates the restraint's listeners once the stored boss entity has died.
     *
     * @param event called when a boss dies
     */
    @SubscribeEvent
    public void onBossDeath(BossDeathEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            RemoRaids.EVENT_BUS.unregister(this);
        }
    }

}
