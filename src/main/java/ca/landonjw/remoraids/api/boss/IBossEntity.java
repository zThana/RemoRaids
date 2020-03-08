package ca.landonjw.remoraids.api.boss;

import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import java.util.UUID;

public interface IBossEntity {

    UUID getUniqueId();

    IBoss getBoss();

    EntityStatue getEntity();

    EntityPixelmon getBattleEntity();

    IBossEngager getBossEngager();

    void despawn();

}