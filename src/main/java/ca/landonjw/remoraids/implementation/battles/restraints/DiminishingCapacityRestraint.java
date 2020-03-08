package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;

import javax.annotation.Nonnull;

public class DiminishingCapacityRestraint extends CapacityRestraint {

    public DiminishingCapacityRestraint(@Nonnull IBossEntity bossEntity, int capacity) {
        super(bossEntity, capacity);
    }

    @Override
    public void onBattleEnd(BossBattleEndedEvent event) {
        playerNum--;
        capacity--;
        if(!RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(getBossEntity()).isPresent()){
            RemoRaids.EVENT_BUS.unregister(this);
        }
    }

}