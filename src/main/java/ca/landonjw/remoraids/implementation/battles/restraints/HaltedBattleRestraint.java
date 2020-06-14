package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * An implementation of {@link IBattleRestraint} that prevents any battles with the boss.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class HaltedBattleRestraint extends BaseBattleRestraint {

    /** The retraint's identifier. */
    public static final String ID = "Halted Battle Restraint";

    /**
     * Constructor for the halted battle restraint.
     */
    public HaltedBattleRestraint() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     *
     * Always returns false.
     */
    @Override
    public boolean validatePlayer(@Nonnull EntityPlayerMP player) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player) {
        return Optional.of(TextFormatting.RED + "This boss is not allowed to battle currently!");
    }

}
