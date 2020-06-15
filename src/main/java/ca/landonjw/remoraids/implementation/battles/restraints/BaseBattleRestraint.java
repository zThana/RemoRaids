package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBoss;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A base implementation for {@link IBattleRestraint}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public abstract class BaseBattleRestraint implements IBattleRestraint {

    /** The boss this battle restraint is for. */
    private IBoss boss;

    /**
     * Constructor for the base battle restraint.
     */
    public BaseBattleRestraint(@Nonnull IBoss boss){
        this.boss = Objects.requireNonNull(boss);
    }

    /**
     * Gets the boss this battle restraint is for.
     *
     * @return boss this battle restraint is for
     */
    public IBoss getBoss() {
        return boss;
    }

    /** {@inheritDoc} */
    @Override
    public void onBattleStart(@Nonnull EntityPlayerMP player) {}

    /** {@inheritDoc} */
    @Override
    public void onBattleEnd(@Nonnull EntityPlayerMP player) {}

    /** {@inheritDoc} */
    @Override
    public void onBossDespawn() {}

}
