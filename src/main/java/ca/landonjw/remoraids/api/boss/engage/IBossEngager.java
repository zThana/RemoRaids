package ca.landonjw.remoraids.api.boss.engage;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IBossEngager {

    float getRange();

    boolean isPlayerInRange(EntityPlayerMP player);

    void sendEngageMessage();

}
