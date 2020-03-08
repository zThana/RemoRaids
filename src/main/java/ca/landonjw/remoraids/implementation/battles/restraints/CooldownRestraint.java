package ca.landonjw.remoraids.implementation.battles.restraints;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
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

public class CooldownRestraint implements IBattleRestraint {

    private IBossEntity bossEntity;
    private Map<EntityPlayerMP, Instant> lastBattleTimeMap = new HashMap<>();
    private long cooldownSeconds;

    public CooldownRestraint(@Nonnull IBossEntity bossEntity, long cooldown, @Nonnull TimeUnit unit){
        this.bossEntity = bossEntity;
        cooldownSeconds = unit.toSeconds(cooldown);
        RemoRaids.EVENT_BUS.register(this);
    }

    @Override
    public boolean validatePlayer(@Nonnull EntityPlayerMP player) {
        boolean isValid = true;
        if(lastBattleTimeMap.containsKey(player)){
            Duration timeElapsed = Duration.between(lastBattleTimeMap.get(player), Instant.now());
            if(cooldownSeconds > timeElapsed.getSeconds()){
                isValid = false;
                player.sendMessage(parseCooldownMessage(timeElapsed));
            }
        }
        return isValid;
    }

    private TextComponentString parseCooldownMessage(Duration timeElapsed){
        String message = RemoRaids.getMessageConfig().getCooldownRestraintDenied();

        long secondsRemaining = cooldownSeconds - timeElapsed.getSeconds();
        long minutesRemaining = secondsRemaining / 60;
        long hoursRemaining = minutesRemaining / 60;

        long trimmedSecondsRemaining = secondsRemaining - (minutesRemaining * 60) - (hoursRemaining * 3600);
        long trimmedMinutesRemaining = hoursRemaining - (minutesRemaining * 60);

        message = message
                .replaceAll("\\{seconds}", "" + secondsRemaining)
                .replaceAll("\\{minutes}", "" + minutesRemaining)
                .replaceAll("\\{hours}", "" + hoursRemaining)
                .replaceAll("\\{trimmed-seconds}", "" + trimmedSecondsRemaining)
                .replaceAll("\\{trimmed-minutes}", "" + trimmedMinutesRemaining);

        return new TextComponentString(message);
    }

    @SubscribeEvent
    public void onBattleEnd(BossBattleEndedEvent event){
        if(event.getBossEntity().equals(bossEntity)){
            lastBattleTimeMap.put(event.getPlayer(), Instant.now());
        }
        if(!RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()){
            RemoRaids.EVENT_BUS.unregister(this);
        }
    }

    public IBossEntity getBossEntity() {
        return bossEntity;
    }

    public long getCooldown() {
        return cooldownSeconds;
    }

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