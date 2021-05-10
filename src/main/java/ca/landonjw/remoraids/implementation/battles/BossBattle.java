package ca.landonjw.remoraids.implementation.battles;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.internal.config.GeneralConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.tasks.HPUpdateTask;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossBattleEndedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartedEvent;
import ca.landonjw.remoraids.api.events.BossBattleStartingEvent;
import ca.landonjw.remoraids.api.events.BossHealthChangeEvent;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.implementation.battles.controller.BossStatusController;
import ca.landonjw.remoraids.implementation.battles.controller.participants.BossParticipant;
import ca.landonjw.remoraids.implementation.battles.controller.participants.BossPlayerParticipant;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Implementation of {@link IBossBattle}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossBattle implements IBossBattle {

	/** The boss entity players will battle. */
	private IBossEntity bossEntity;
	/** The current health of the boss. This value is volatile. */
	private int currentHealth;
	/** Map of all players currently in battle with the boss, and the corresponding battle controllers. */
	private Map<EntityPlayerMP, BattleControllerBase> playerBattleMap = new HashMap<>();
	/** Map of player UUIDs and the amount of damage they have dealt to the boss. */
	private Map<UUID, Integer> playerDamageDealt = new HashMap<>();
	/** The UUID of the player who dealt the killing blow to the boss. May be null if boss is not dead. */
	private UUID killer;

	/**
	 * Constructor for the boss battle.
	 *
	 * @param bossEntity the boss entity players will battle
	 */
	public BossBattle(@Nonnull IBossEntity bossEntity) {
		this.bossEntity = bossEntity;
		currentHealth = bossEntity.getBoss().getStat(StatsType.HP);
	}

	/** {@inheritDoc} */
	@Override
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

	/** {@inheritDoc} */
	@Override
	public IBossBattleSettings getBattleSettings() {
		return bossEntity.getBoss().getBattleSettings();
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
	public List<UUID> getTopDamageDealers() {
		return playerDamageDealt.keySet().stream().filter(x -> FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(x) != null).sorted((val1, val2) -> playerDamageDealt.get(val2) - playerDamageDealt.get(val1)).collect(Collectors.toList());
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
		if (BattleRegistry.getBattle(player) == null) {
			if (!getBattleSettings().validate(player, bossEntity.getBoss())) {
				List<String> rejectionReasons = getBattleSettings().getRejectionMessages(player, bossEntity.getBoss());
				for (String reason : rejectionReasons)
					player.sendMessage(new TextComponentString(reason));

				return;
			}

			// Create battle starting event for RemoRaids and post it to the event bus to check if battle should proceed.
			BossBattleStartingEvent battleStarting = new BossBattleStartingEvent(bossEntity, player);
			RemoRaids.EVENT_BUS.post(battleStarting);

			if (!battleStarting.isCanceled()) {
				// If the starting pixelmon isn't specified, use first available Pokemon in player's party.
				if (startingPixelmon == null)
					startingPixelmon = Pixelmon.storageManager.getParty(player).getAndSendOutFirstAblePokemon(player);

				// Create battle using our custom participants and the bosses battle rules.
				BossPlayerParticipant playerParticipant = new BossPlayerParticipant(bossEntity, player, startingPixelmon);
				BossParticipant bossParticipant = new BossParticipant(this, bossEntity);

				BattleControllerBase battleController = BattleRegistry.startBattle(new BattleParticipant[] {
						playerParticipant }, new BattleParticipant[] {
								bossParticipant }, getBattleSettings().getBattleRules().orElse(new BattleRules()));

				battleController.globalStatusController = new BossStatusController(bossEntity, battleController, RemoRaids.getRestraintsConfig().get(RestraintsConfig.DISABLED_STATUSES));

				getBattleSettings().getBattleRestraints().forEach((restraint) -> restraint.onBattleStart(player, bossEntity.getBoss()));
				playerBattleMap.put(player, battleController);

				BossBattleStartedEvent battleStarted = new BossBattleStartedEvent(bossEntity, player, this, battleController);
				RemoRaids.EVENT_BUS.post(battleStarted);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void endBattle(@Nonnull EntityPlayerMP player) {
		if (playerBattleMap.containsKey(player)) {
			BattleControllerBase bc = playerBattleMap.get(player);
			playerBattleMap.remove(player);
			bc.endBattle();
			getBattleSettings().getBattleRestraints().forEach((restraint) -> restraint.onBattleEnd(player, bossEntity.getBoss()));
			BossBattleEndedEvent battleEndedEvent = new BossBattleEndedEvent(bossEntity, player, this);
			RemoRaids.EVENT_BUS.post(battleEndedEvent);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void endAllBattles() {
		for (EntityPlayerMP player : new HashSet<>(playerBattleMap.keySet())) {
			endBattle(player);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setBossHealth(int health, @Nullable EntityPlayerMP source) {
		BossHealthChangeEvent event = new BossHealthChangeEvent(this, bossEntity, source, currentHealth - health);
		RemoRaids.EVENT_BUS.post(event);

		if (!event.isCanceled()) {
			int damage = event.getDifference();
			int newHealth = currentHealth - damage;

			if (source != null) {
				if (playerDamageDealt.containsKey(source.getUniqueID())) {
					playerDamageDealt.replace(source.getUniqueID(), playerDamageDealt.get(source.getUniqueID()) + damage);
				} else {
					playerDamageDealt.put(source.getUniqueID(), damage);
				}
			}

			for (EntityPlayerMP player : playerBattleMap.keySet()) {
				BattleControllerBase bcb = playerBattleMap.get(player);
				PixelmonWrapper opponentWrapper = bcb.getPlayer(player).getOpponentPokemon().get(0);
				HPUpdateTask hpPacket = new HPUpdateTask(opponentWrapper, newHealth - currentHealth);
				Pixelmon.network.sendTo(hpPacket, player);
				opponentWrapper.pokemon.setHealth(newHealth);
			}

			if (newHealth <= 0) {
				if (source != null) {
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
	private void displayBattleSummary() {
		// Create an list of players in order of highest damage dealt in the battle
		List<EntityPlayerMP> contributors = getTopDamageDealers().stream().map(id -> FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(id)).collect(Collectors.toList());

		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext.Builder bossContext = IParsingContext.builder().add(IBoss.class, () -> bossEntity.getBoss());

		// Create output to send to players
		List<String> output = Lists.newArrayList();
		getKiller().ifPresent(killer -> {
			EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(killer);
			bossContext.add(EntityPlayerMP.class, () -> player);
		});

		IParsingContext context = bossContext.add(IBoss.class, () -> bossEntity.getBoss()).add(Integer.class, () -> Math.min(3, contributors.size())).build();

		output.addAll(service.interpret(config.get(MessageConfig.RESULTS_HEADER), context));
		for (int i = 0; i < Math.min(3, contributors.size()); i++) {
			int damage = getDamageDealt(contributors.get(i).getUniqueID()).get();
			EntityPlayerMP player = contributors.get(i);
			IParsingContext damageDealerContext = IParsingContext.builder().add(IBoss.class, () -> bossEntity.getBoss()).add(EntityPlayerMP.class, () -> player).add(Integer.class, () -> damage).build();
			output.add(service.interpret(config.get(MessageConfig.RESULTS_BODY_TOP_DAMAGE_CONTENT), damageDealerContext));
		}
		output.add(service.interpret(config.get(MessageConfig.RESULTS_FOOTER), context));

		boolean sendSummaryToServer = RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_DISPLAY_BATTLE_SUMMARY_TO_SERVER);

		Collection<EntityPlayerMP> messageReceiver;
		if (sendSummaryToServer){
			messageReceiver = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
		} else {
			messageReceiver = getPlayersInBattle();
		}

		// Send summary to receivers
		for (EntityPlayerMP player : messageReceiver) {
			for (String out : output)
				player.sendMessage(new TextComponentString(out));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void distributeRewards() {
		List<EntityPlayerMP> rewardsReceived = Lists.newArrayList();
		Map<Integer, List<IReward>> rewardsByPriority = Maps.newHashMap();
		for (IReward reward : getBattleSettings().getRewards()) {
			if (!rewardsByPriority.containsKey(reward.getPriority()))
				rewardsByPriority.put(reward.getPriority(), Lists.newArrayList());

			rewardsByPriority.get(reward.getPriority()).add(reward);
		}

		List<Map.Entry<Integer, List<IReward>>> sortedEntries = Lists.newArrayList(rewardsByPriority.entrySet());
		sortedEntries.sort((entry1, entry2) -> entry2.getKey() - entry1.getKey());
		for (Map.Entry<Integer, List<IReward>> entry : sortedEntries) {
			List<EntityPlayerMP> playersReceivedPerPriority = Lists.newArrayList();
			for (IReward reward : entry.getValue()) {
				for (EntityPlayerMP player : reward.getWinnersList(this)) {
					if (!rewardsReceived.contains(player)) {
						reward.distributeReward(player);
						if (!playersReceivedPerPriority.contains(player))
							playersReceivedPerPriority.add(player);
					}
				}
			}

			rewardsReceived.addAll(playersReceivedPerPriority);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getHealth() {
		return currentHealth;
	}

	/** {@inheritDoc} */
	@Override
	public int getMaxHealth() {
		return bossEntity.getBoss().getStat(StatsType.HP);
	}
}