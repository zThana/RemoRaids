package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.internal.UIContainer;
import ca.landonjw.remoraids.internal.inventory.internal.UIInventory;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

    /** The boss entity to apply restraint to. */
    private IBossEntity bossEntity;
    /** Map of players and the last time they left battle with the boss. */
    private Map<EntityPlayerMP, Instant> lastBattleTimeMap = new HashMap<>();
    /** The cooldown in seconds before plays may reenter battle with the boss, after they have left. */
    private long cooldownSeconds;

    /**
     * Constructor for the cooldown restraint.
     *
     * @param bossEntity boss entity to apply restraint to
     * @param cooldown   cooldown value
     * @param unit       time unit of the cooldown
     */
    public CooldownRestraint(@Nonnull IBossEntity bossEntity, long cooldown, @Nonnull TimeUnit unit){
        super(ID);
        this.bossEntity = bossEntity;
        cooldownSeconds = unit.toSeconds(cooldown);
        RemoRaids.EVENT_BUS.register(this);
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
    public String getRejectionMessage(EntityPlayerMP player) {
        if(lastBattleTimeMap.containsKey(player)){
            Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player), Instant.now());
            if(cooldownSeconds > timeElapsed.getSeconds()){
                return parseCooldownMessage(timeElapsed);
            }
        }
        return "";
    }

    /**
     * Takes the cooldown message and replaces any placeholders.
     *
     * @param timeElapsed time that has elapsed between now and last time player was in battle
     * @return cooldown message with placeholders replaced
     */
    private String parseCooldownMessage(Duration timeElapsed){
        String message = RemoRaids.getMessageConfig().get(MessageConfig.RAID_COOLDOWN);

        long secondsRemaining = cooldownSeconds - timeElapsed.getSeconds();
        long minutesRemaining = TimeUnit.MINUTES.convert(secondsRemaining, TimeUnit.SECONDS);
        long hoursRemaining = TimeUnit.HOURS.convert(secondsRemaining, TimeUnit.SECONDS);

        long trimmedSecondsRemaining = secondsRemaining - (minutesRemaining * 60);
        long trimmedMinutesRemaining = minutesRemaining - (hoursRemaining * 60);

        message = message
                .replaceAll("\\{seconds}", "" + secondsRemaining)
                .replaceAll("\\{minutes}", "" + minutesRemaining)
                .replaceAll("\\{hours}", "" + hoursRemaining)
                .replaceAll("\\{trimmed-seconds}", "" + trimmedSecondsRemaining)
                .replaceAll("\\{trimmed-minutes}", "" + trimmedMinutesRemaining);

        return message;
    }

    /**
     * When a battle ends with the stored boss entity, this will add player to the cooldown map.
     *
     * @param event called when a boss battle ends
     */
    @SubscribeEvent
    public void onBattleEnd(BossBattleEndedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            lastBattleTimeMap.put(event.getPlayer(), Instant.now());
        }
    }

    /**
     * Terminates the restraint's listeners once the stored boss entity has died.
     *
     * @param event called when a boss dies
     */
    @SubscribeEvent
    public void onBossDeath(BossDeathEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            RemoRaids.EVENT_BUS.unregister(this);
        }
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