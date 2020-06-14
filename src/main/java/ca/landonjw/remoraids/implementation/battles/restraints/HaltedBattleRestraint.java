package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;

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
    public HaltedBattleRestraint(@Nonnull IBoss boss) {
        super(ID, boss);
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
        Config config = RemoRaids.getMessageConfig();
        IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

        IParsingContext context = IParsingContext.builder()
                .add(EntityPlayerMP.class, () -> player)
                .add(IBoss.class, this::getBoss)
                .build();
        return Optional.of(service.interpret(config.get(MessageConfig.RESTRAINT_HALTED), context));
    }

}
