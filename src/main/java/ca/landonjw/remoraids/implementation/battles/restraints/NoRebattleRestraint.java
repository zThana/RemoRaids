package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class NoRebattleRestraint extends BaseBattleRestraint {

    private Set<EntityPlayerMP> restrainedPlayers = new HashSet<>();

    public NoRebattleRestraint(@Nonnull IBoss boss){
        super(boss);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validatePlayer(EntityPlayerMP player) {
        return !restrainedPlayers.contains(player);
    }

    @Override
    public String getId() {
        Config config = RemoRaids.getMessageConfig();
        return config.get(MessageConfig.NO_REBATTLE_RESTRAINT_TITLE);
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
        return Optional.of(service.interpret(config.get(MessageConfig.NO_REBATTLE_RESTRAINT_WARNING), context));
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
