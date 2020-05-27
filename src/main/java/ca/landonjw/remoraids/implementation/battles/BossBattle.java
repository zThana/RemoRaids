package ca.landonjw.remoraids.implementation.battles;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.battles.IBossBattleRules;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartingEvent;
import ca.landonjw.remoraids.api.events.BossHealthChangeEvent;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.implementation.battles.controller.BossStatusController;
import ca.landonjw.remoraids.implementation.battles.controller.participants.BossParticipant;
import ca.landonjw.remoraids.implementation.battles.controller.participants.BossPlayerParticipant;
import ca.landonjw.remoraids.internal.api.config.ConfigKey;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.comm.packetHandlers.battles.gui.HPPacket;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class BossBattle implements IBossBattle {

    private IBossEntity bossEntity;
    private final int MAX_HEALTH;
    private int currentHealth;

    private IBossBattleRules bossBattleRules;
    private Map<EntityPlayerMP, BattleControllerBase> playerBattleMap = new HashMap<>();

    private Map<UUID, Integer> playerDamageDealt = new HashMap<>();
    private UUID killer;

    private List<IReward> defeatRewards;

    public BossBattle(@Nonnull IBossEntity bossEntity,
                      @Nonnull IBossBattleRules bossBattleRules,
                      @Nullable List<IReward> defeatRewards){
        this.bossEntity = bossEntity;
        this.bossBattleRules = bossBattleRules;
        this.defeatRewards = (defeatRewards != null) ? defeatRewards : new ArrayList<>();
        MAX_HEALTH = bossEntity.getBoss().getStat(StatsType.HP);
        currentHealth = MAX_HEALTH;
    }

    /** {@inheritDoc} */
    @Override
    public IBossEntity getBossEntity() {
        return bossEntity;
    }

    /** {@inheritDoc} */
    @Override
    public IBossBattleRules getBattleRules() {
        return bossBattleRules;
    }

    @Override
    public void setBattleRules(@Nonnull IBossBattleRules battleRules) {
        bossBattleRules = battleRules;
    }

    /** {@inheritDoc} */
    @Override
    public List<IReward> getDefeatRewards() {
        return defeatRewards;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<BattleControllerBase> getBattleController(EntityPlayerMP player) {
        return Optional.ofNullable(playerBattleMap.get(player));
    }

    /** {@inheritDoc} */
    @Override
    public Set<BattleControllerBase> getBattleControllers() {
        return new HashSet<>(playerBattleMap.values());
    }

    /** {@inheritDoc} */
    @Override
    public Set<EntityPlayerMP> getPlayersInBattle() {
        return new HashSet<>(playerBattleMap.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Integer> getDamageDealt(@Nonnull UUID uuid) {
        return Optional.ofNullable(playerDamageDealt.get(uuid));
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("ConstantConditions")
    public List<UUID> getTopDamageDealers() {
        List<UUID> damageDealers = new ArrayList<>(playerDamageDealt.keySet());
        damageDealers.stream()
                .filter(x -> FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(x) != null)
                .collect(Collectors.toList())
                .sort((val1, val2) -> playerDamageDealt.get(val2) - playerDamageDealt.get(val1));
        return damageDealers;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<UUID> getKiller() {
        return Optional.ofNullable(killer);
    }

    /** {@inheritDoc} */
    @Override
    public boolean containsPlayer(@Nonnull EntityPlayerMP player) {
        return playerBattleMap.containsKey(player);
    }

    /** {@inheritDoc} */
    @Override
    public void startBattle(@Nonnull EntityPlayerMP player) {
        startBattle(player, null);
    }

    /** {@inheritDoc} */
    @Override
    public void startBattle(@Nonnull EntityPlayerMP player, @Nullable EntityPixelmon startingPixelmon) {
        if(BattleRegistry.getBattle(player) == null){

            BossBattleStartingEvent battleStarting = new BossBattleStartingEvent(bossEntity, player);
            RemoRaids.EVENT_BUS.post(battleStarting);

            if(!battleStarting.isCanceled()){
                if(startingPixelmon == null){
                    startingPixelmon = Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);
                }

                BossPlayerParticipant playerParticipant = new BossPlayerParticipant(bossEntity, player, startingPixelmon);
                BossParticipant bossParticipant = new BossParticipant(this, bossEntity);

                BattleControllerBase battleController = BattleRegistry.startBattle(
                        new BattleParticipant[]{playerParticipant},
                        new BattleParticipant[]{bossParticipant},
                        bossBattleRules.getBattleRules().orElse(new BattleRules())
                );

                battleController.globalStatusController = new BossStatusController(
                        bossEntity,
                        battleController,
                        RemoRaids.getRestraintsConfig().get(RestraintsConfig.DISABLED_STATUSES)
                );

                playerBattleMap.put(player, battleController);

                BossBattleStartedEvent battleStarted = new BossBattleStartedEvent(bossEntity, player, battleController);
                RemoRaids.EVENT_BUS.post(battleStarted);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void endBattle(@Nonnull EntityPlayerMP player) {
        if(playerBattleMap.containsKey(player)){
            playerBattleMap.remove(player);
            BossBattleEndedEvent battleEndedEvent = new BossBattleEndedEvent(bossEntity, player, this);
            RemoRaids.EVENT_BUS.post(battleEndedEvent);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void endAllBattles() {
        for(EntityPlayerMP player : new HashSet<>(playerBattleMap.keySet())){
            endBattle(player);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setBossHealth(int health, @Nullable EntityPlayerMP source) {
        BossHealthChangeEvent event = new BossHealthChangeEvent(this, bossEntity, source, currentHealth - health);
        RemoRaids.EVENT_BUS.post(event);

        if(!event.isCanceled()){
            int damage = event.getDifference();
            int newHealth = currentHealth - damage;

            if(source != null){
                if(playerDamageDealt.containsKey(source.getUniqueID())){
                    playerDamageDealt.replace(source.getUniqueID(), playerDamageDealt.get(source.getUniqueID()) + damage);
                }
                else{
                    playerDamageDealt.put(source.getUniqueID(), damage);
                }
            }

            for(EntityPlayerMP player : playerBattleMap.keySet()){
                BattleControllerBase bcb = playerBattleMap.get(player);
                PixelmonWrapper opponentWrapper = bcb.getPlayer(player).getOpponentPokemon().get(0);
                HPPacket hpPacket = new HPPacket(opponentWrapper, newHealth - currentHealth);
                Pixelmon.network.sendTo(hpPacket, player);
                opponentWrapper.pokemon.setHealth(newHealth);
            }

            if(newHealth <= 0){
                if(source != null){
                    killer = source.getUniqueID();
                }
                distributeRewards();
                this.bossEntity.despawn();
            }
            currentHealth = newHealth;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void distributeRewards() {
        for(IReward reward : defeatRewards){
            reward.distributeReward(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public int getHealth() {
        return currentHealth;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxHealth(){
        return MAX_HEALTH;
    }

}
