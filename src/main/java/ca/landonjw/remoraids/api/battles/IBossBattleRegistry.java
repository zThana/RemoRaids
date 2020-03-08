package ca.landonjw.remoraids.api.battles;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface IBossBattleRegistry {

    Optional<IBossBattle> getBossBattle(@Nonnull IBossEntity boss);

    Optional<IBossBattle> getBossBattle(@Nonnull EntityPlayerMP player);

    boolean isPlayerInBattle(@Nonnull EntityPlayerMP player);

}
