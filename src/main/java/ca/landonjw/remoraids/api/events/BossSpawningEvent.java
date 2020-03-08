package ca.landonjw.remoraids.api.events;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;

@Cancelable
public class BossSpawningEvent extends Event {

    private final IBoss boss;
    private final IBossSpawner spawner;

    public BossSpawningEvent(@Nonnull IBoss boss, @Nonnull IBossSpawner spawner){
        this.boss = boss;
        this.spawner = spawner;
    }

    public IBoss getBoss() {
        return boss;
    }

    public IBossSpawner getSpawner() {
        return spawner;
    }

}
