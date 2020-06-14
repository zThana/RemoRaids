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
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link IBattleRestraint} that puts players on a cooldown after they leave the boss battle,
 * and prevents them from rejoining until the cooldown is over.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class CooldownRestraint extends BaseBattleRestraint {

    /** The retraint's identifier. */
    public static final String ID = "Cooldown Restraint";

    /** Map of players and the last time they left battle with the boss. */
    private Map<EntityPlayerMP, Instant> lastBattleTimeMap = new HashMap<>();
    /** The cooldown in seconds before plays may reenter battle with the boss, after they have left. */
    private long cooldownSeconds;

    /**
     * Constructor for the cooldown restraint.
     *
     * @param boss       boss to apply restraint to
     * @param cooldown   cooldown value
     * @param unit       time unit of the cooldown
     */
    public CooldownRestraint(@Nonnull IBoss boss, long cooldown, @Nonnull TimeUnit unit){
        super(ID, boss);
        cooldownSeconds = unit.toSeconds(cooldown);
    }

    /** {@inheritDoc} */
    @Override
    public boolean validatePlayer(@Nonnull EntityPlayerMP player) {
        if(lastBattleTimeMap.containsKey(player)){
            Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player), Instant.now());
            if(cooldownSeconds > timeElapsed.getSeconds()){
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<String> getRejectionMessage(@Nonnull EntityPlayerMP player) {
        if(lastBattleTimeMap.containsKey(player)){
            Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player), Instant.now());
            if(cooldownSeconds > timeElapsed.getSeconds()){
                Config config = RemoRaids.getMessageConfig();
                IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

                IParsingContext context = IParsingContext.builder()
                        .add(CooldownRestraint.class, () -> this)
                        .add(EntityPlayerMP.class, () -> player)
                        .add(IBoss.class, this::getBoss)
                        .build();
                return Optional.of(service.interpret(config.get(MessageConfig.RESTRAINT_COOLDOWN), context));
            }
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     *
     * When a battle ends, this will add the player to the cooldown map.
     *
     * @param player player leaving battle
     */
    @Override
    public void onBattleEnd(@Nonnull EntityPlayerMP player) {
        lastBattleTimeMap.put(player, Instant.now());
    }

    /**
     * {@inheritDoc}
     *
     * Resets the cooldown map.
     */
    @Override
    public void onBossDespawn() {
        lastBattleTimeMap.clear();
    }

    /**
     * Gets the cooldown in seconds before plays may reenter battle with the boss, after they have left.
     *
     * @return cooldown in seconds before plays may reenter battle with the boss, after they have left.
     */
    public long getCooldown(TimeUnit unit) {
        return unit.convert(cooldownSeconds, TimeUnit.SECONDS);
    }

    public void setCooldown(long time, TimeUnit unit){
        this.cooldownSeconds = unit.toSeconds(time);
    }

    /**
     * Gets the cooldown remaining before a player may reenter battle with the boss.
     *
     * @param player player to get cooldown for
     * @param unit   time unit to get cooldown in
     * @return cooldown remaining before player may reenter battle
     */
    public Optional<Long> getCooldownRemaining(@Nonnull EntityPlayerMP player, TimeUnit unit){
        if(lastBattleTimeMap.containsKey(player)){
            Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player), Instant.now());
            if(cooldownSeconds > timeElapsed.getSeconds()){
                return Optional.of(unit.convert(cooldownSeconds - timeElapsed.getSeconds(), TimeUnit.SECONDS));
            }
        }
        return Optional.empty();
    }

}