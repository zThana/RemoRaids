package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@Cancelable
public class BossHealthChangeEvent extends Event {

    private final IBossBattle battle;
    private final IBossEntity bossEntity;
    private final EntityPlayerMP source;
    private int difference;

    public BossHealthChangeEvent(@Nonnull IBossBattle battle,
                                 @Nonnull IBossEntity bossEntity,
                                 @Nullable EntityPlayerMP source,
                                 int difference){
        this.battle = battle;
        this.bossEntity = bossEntity;
        this.source = source;
        this.difference = difference;
    }

    public IBossBattle getBattle() {
        return battle;
    }

    public IBossEntity getBossEntity() {
        return bossEntity;
    }

    public Optional<EntityPlayerMP> getSource() {
        return Optional.ofNullable(source);
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

}
