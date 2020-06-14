package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PreventRebattleRestraint extends BaseBattleRestraint {

    /** The retraint's identifier. */
    public static final String ID = "Prevent Rebattle Restraint";

    private Set<EntityPlayerMP> restrainedPlayers = new HashSet<>();

    public PreventRebattleRestraint(@Nonnull IBoss boss){
        super(ID, boss);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validatePlayer(EntityPlayerMP player) {
        return !restrainedPlayers.contains(player);
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
        return Optional.of(service.interpret(config.get(MessageConfig.RESTRAINT_NO_REBATTLE), context));
    }

    /**
     * {@inheritDoc}
     *
     * Adds player to list that prevents them from reentering battle again.
     *
     * @param player player leaving battle
     */
    @Override
    public void onBattleEnd(@Nonnull EntityPlayerMP player) {
        restrainedPlayers.add(player);
    }

    /**
     * {@inheritDoc}
     *
     * Clears the prevented players list.
     */
    @Override
    public void onBossDespawn() {
        restrainedPlayers.clear();
    }

}
