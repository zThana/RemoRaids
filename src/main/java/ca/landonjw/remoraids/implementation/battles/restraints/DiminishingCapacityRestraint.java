package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;

import javax.annotation.Nonnull;

/**
 * An implementation of {@link CapacityRestraint} that lowers the capacity each time a player leaves.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class DiminishingCapacityRestraint extends CapacityRestraint {

    /** The retraint's identifier. */
    public static final String ID = "Diminishing Capacity Restraint";

    /**
     * Constructor for the diminishing capacity restraint.
     *
     * @param bossEntity boss entity to apply restraint to
     * @param capacity   the capacity of the boss
     */
    public DiminishingCapacityRestraint(@Nonnull IBossEntity bossEntity, int capacity) {
        super(bossEntity, capacity);
        setId(ID);
    }

    /**
     * When a battle ends with the stored boss entity, this will decrement the player number and capacity.
     *
     * @param event called when a boss battle ends
     */
    @Override
    public void onBattleEnd(BossBattleEndedEvent event) {
        playerNum--;
        capacity--;
    }

}