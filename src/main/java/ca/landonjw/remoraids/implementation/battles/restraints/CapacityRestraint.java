package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartedEvent;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

/**
 * An implementation of {@link IBattleRestraint} that prevents players from battling the boss
 * if the boss reaches a specified player capacity.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class CapacityRestraint extends BaseBattleRestraint {

    /** The retraint's identifier. */
    public static final String ID = "Capacity Restraint";

    /** The boss entity to apply restraint to. */
    protected IBossEntity bossEntity;
    /** The capacity of the boss. */
    protected int capacity;
    /** The number of players currently in battle with the boss. */
    protected int playerNum;

    /**
     * Constructor for the capacity restraint.
     *
     * @param bossEntity the boss entity to apply identifier to
     * @param capacity   the capacity of the boss
     */
    public CapacityRestraint(@Nonnull IBossEntity bossEntity, int capacity){
        super(ID);
        this.bossEntity = bossEntity;
        this.capacity = capacity;
        RemoRaids.EVENT_BUS.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validatePlayer(EntityPlayerMP player) {
        return playerNum < capacity;
    }

    /** {@inheritDoc} */
    @Override
    public String getRejectionMessage(EntityPlayerMP player) {
        return RemoRaids.getMessageConfig().get(MessageConfig.RAID_CAPACITY_REACHED);
    }

    /**
     * When a battle starts with the stored boss entity, this will increment the player number.
     *
     * @param event called when a boss battle starts
     */
    @SubscribeEvent
    public void onBattleStart(BossBattleStartedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            playerNum++;
        }
    }

    /**
     * When a battle ends with the stored boss entity, this will decrement the player number.
     *
     * @param event called when a boss battle ends
     */
    @SubscribeEvent
    public void onBattleEnd(BossBattleEndedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            playerNum--;
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
