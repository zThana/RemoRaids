package ca.landonjw.remoraids.api.boss;

import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import java.util.UUID;

public interface IBossEntity {

    UUID getUniqueId();

    IBoss getBoss();

    IBossSpawner getSpawner();

    EntityStatue getEntity();

    EntityPixelmon getBattleEntity();

    IBossEngager getBossEngager();

    void despawn();

}