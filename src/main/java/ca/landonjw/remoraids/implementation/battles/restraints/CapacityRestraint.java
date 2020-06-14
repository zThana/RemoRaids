package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartedEvent;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

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
    private int capacity;
    /** The number of players currently in battle with the boss. */
    private int playerNum;
    /** If the capacity reduces on each player battle end. */
    private boolean diminishing;

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
    public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player) {
        Config config = RemoRaids.getMessageConfig();
        IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

        IParsingContext context = IParsingContext.builder()
                .add(CapacityRestraint.class, () -> this)
                .build();
        return Optional.of(service.interpret(config.get(MessageConfig.RAID_CAPACITY_REACHED), context));
    }

    public int getPlayerNum() {
        return playerNum;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDiminishing(boolean diminishing) {
        this.diminishing = diminishing;
    }

    public boolean isDiminishing() {
        return diminishing;
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
            if(diminishing){
                capacity--;
            }
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
