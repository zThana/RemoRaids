package ca.landonjw.remoraids.api.battles;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public interface IBossBattleRegistry {

    Optional<IBossBattle> getBossBattle(@Nonnull IBossEntity boss);

    Optional<IBossBattle> getBossBattle(@Nonnull EntityPlayerMP player);

    List<IBossBattle> getAllBossBattles();

    boolean isPlayerInBattle(@Nonnull EntityPlayerMP player);

}
