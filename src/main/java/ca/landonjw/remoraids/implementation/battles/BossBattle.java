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
import ca.landonjw.remoraids.implementation.rewards.KillerReward;
import ca.landonjw.remoraids.implementation.rewards.ParticipationReward;
import ca.landonjw.remoraids.implementation.rewards.TopDamageReward;
import ca.landonjw.remoraids.implementation.rewards.contents.CurrencyContent;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import com.google.common.collect.Lists;
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
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of {@link IBossBattle}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class BossBattle implements IBossBattle {

    /** The boss entity players will battle. */
    private IBossEntity bossEntity;
    /** The current health of the boss. This value is volatile. */
    private int currentHealth;
    /** The rules imposed before and during battle with the boss. */
    private IBossBattleRules bossBattleRules;
    /** Map of all players currently in battle with the boss, and the corresponding battle controllers. */
    private Map<EntityPlayerMP, BattleControllerBase> playerBattleMap = new HashMap<>();
    /** Map of player UUIDs and the amount of damage they have dealt to the boss. */
    private Map<UUID, Integer> playerDamageDealt = new HashMap<>();
    /** The UUID of the player who dealt the killing blow to the boss. May be null if boss is not dead. */
    private UUID killer;
    /** A list of rewards to be distributed when the boss is slain. */
    private List<IReward> defeatRewards;

    /**
     * Constructor for the boss battle.
     *
     * @param bossEntity      the boss entity players will battle
     * @param bossBattleRules the rules for the battle
     * @param defeatRewards   the rewards to be distributed when the boss is slain
     */
    public BossBattle(@Nonnull IBossEntity bossEntity,
                      @Nonnull IBossBattleRules bossBattleRules,
                      @Nullable List<IReward> defeatRewards){
        this.bossEntity = bossEntity;
        this.bossBattleRules = bossBattleRules;
        this.defeatRewards = (defeatRewards != null) ? defeatRewards : new ArrayList<>();
        currentHealth = bossEntity.getBoss().getStat(StatsType.HP);
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

    /** {@inheritDoc} */
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
            if(!bossBattleRules.validate(player)){
                List<String> rejectionReasons = bossBattleRules.getRejectionMessages(player);
                for(String reason : rejectionReasons){
                    player.sendMessage(new TextComponentString(reason));
                }
                return;
            }

            //Create battle starting event for RemoRaids and post it to the event bus to check if battle should proceed.
            BossBattleStartingEvent battleStarting = new BossBattleStartingEvent(bossEntity, player);
            RemoRaids.EVENT_BUS.post(battleStarting);

            if(!battleStarting.isCanceled()){
                //If the starting pixelmon isn't specified, use first available Pokemon in player's party.
                if(startingPixelmon == null){
                    startingPixelmon = Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);
                }
                //Create battle using our custom participants and the bosses battle rules.
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
                displayBattleSummary();
                distributeRewards();
                this.bossEntity.despawn();
            }
            currentHealth = newHealth;
        }
    }

    /**
     * Creates a summary for the boss battle, including the highest damage dealers and killer.
     * It then sends this to all players on the server.
     */
    private void displayBattleSummary(){
        //Create an list of players in order of highest damage dealt in the battle
        List<EntityPlayerMP> contributors = getTopDamageDealers().stream()
                .map(id -> FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(id))
                .collect(Collectors.toList());

        //Create output to send to players
        List<String> output = Lists.newArrayList();
        Function<String, String> format = str -> str.replaceAll("&", "\u00a7");
        output.add(format.apply("&8&m==============&r &c[Raid Results] &8&m=============="));
        output.add(format.apply("&7Through valiant effort, the raid pokemon,"));
        output.add(format.apply("&e" + bossEntity.getBoss().getPokemon().getSpecies().getLocalizedName() + "&7, was defeated!"));
        output.add(format.apply(""));

        getKiller().ifPresent(killer -> {
            String playerName = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(killer).getName();
            output.add(format.apply("&cKiller: &e" + playerName));
            output.add("");
        });

        output.add(format.apply("&aTop " + Math.min(3, contributors.size()) + " Damage Dealers:"));
        for(int i = 0; i < Math.min(3, contributors.size()); i++) {
            output.add(format.apply("&e" + contributors.get(i).getName() + "&7: &b" + getDamageDealt(contributors.get(i).getUniqueID()).get()));
        }
        output.add(format.apply("&8&m=================================="));

        //Send summary to all players
        for(EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
            for(String out : output) {
                player.sendMessage(new TextComponentString(out));
            }
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
        return bossEntity.getBoss().getStat(StatsType.HP);
    }

}
