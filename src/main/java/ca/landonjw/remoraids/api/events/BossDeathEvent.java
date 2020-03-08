package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BossDeathEvent extends Event{

    private final IBossEntity bossEntity;
    private final IBossBattle bossBattle;

    public BossDeathEvent(@Nonnull IBossEntity bossEntity, @Nullable IBossBattle bossBattle){
        this.bossEntity = bossEntity;
        this.bossBattle = bossBattle;
    }

    public IBossEntity getBossEntity(){
        return bossEntity;
    }

    public Optional<IBossBattle> getBossBattle(){
        return Optional.ofNullable(bossBattle);
    }

    public Optional<UUID> getKiller(){
        if(bossBattle != null){
            return bossBattle.getKiller();
        }
        return Optional.empty();
    }

}
