package ca.landonjw.remoraids.implementation.boss;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.pixelmonmod.pixelmon.api.overlay.notice.EnumOverlayLayout;
import com.pixelmonmod.pixelmon.api.overlay.notice.NoticeOverlay;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.boss.engage.IBossEngager;
import ca.landonjw.remoraids.api.events.BossDeathEvent;
import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.implementation.battles.BossBattleRegistry;
import ca.landonjw.remoraids.implementation.boss.engage.BossEngager;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.messages.channels.ActionBarChannel;
import ca.landonjw.remoraids.internal.messages.channels.BossBarChannel;
import ca.landonjw.remoraids.internal.messages.channels.OverlayChannel;
import ca.landonjw.remoraids.internal.messages.channels.TitleChannel;
import ca.landonjw.remoraids.internal.tasks.Task;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Implementation of {@link IBossEntity}.
 *
 * Contains one EntityStatue that serves to be scaled and appear for players, and one
 * invisible EntityPixelmon that is used as a medium for players to battle.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossEntity implements IBossEntity {

	/** The spawner this entity was created from */
	private IBossSpawner spawner;
	/** The boss entity is created from. */
	private IBoss boss;
	/** The UUID of entity that visually represents the boss. */
	private UUID statueEntityUUID;
	/** The UUID of entity that produces participants for players to fight. */
	private UUID battleEntityUUID;
	/** Used for players to engage battles with the boss entity. */
	private IBossEngager bossEngager;
	/** World of the boss entity. */
	private World world;
	/** Location of the boss entity. */
	private BlockPos position;
	/** Overlay text*/
	private List<String> overlayText;
	/** Allow Dynamax */
	private boolean allowDynamax;

	/**
	 * Constructor for the boss entity.
	 *
	 * @param spawner      the spawner responsible for creating this entity
	 * @param boss         boss entity is created from
	 * @param statueEntity visual representation of the boss entity
	 * @param battleEntity the entity used to produce participants for players to fight
	 */
	public BossEntity(@NonNull IBossSpawner spawner, @NonNull IBoss boss, @NonNull EntityStatue statueEntity, @NonNull EntityPixelmon battleEntity) {
		this.spawner = Objects.requireNonNull(spawner, "spawner cannot be null");
		this.boss = Objects.requireNonNull(boss, "boss must not be null");
		this.statueEntityUUID = statueEntity.getUniqueID();
		this.battleEntityUUID = battleEntity.getUniqueID();
		this.world = statueEntity.world;
		this.position = statueEntity.getPosition();
		setEngager();
		setOverlay();
		vanishBattleEntity();
		this.allowDynamax = spawner.allowDynamax();

		BossEntityRegistry.getInstance().register(this);
		BossBattleRegistry.getInstance().createBossBattle(this);
	}

	/**
	 * Sets the engager of the entity depending on configuration values.
	 */
	private void setEngager() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		IParsingContext context = IParsingContext.builder().add(IBoss.class, this::getBoss).build();
		String parsedMessage = service.interpret(config.get(MessageConfig.RAID_ENGAGE), context);
		float engageRange = RemoRaids.getGeneralConfig().get(GeneralConfig.ENGAGE_RANGE);

		switch (RemoRaids.getGeneralConfig().get(GeneralConfig.ENGAGE_MESSAGE_TYPE)) {
		case 1:
			IMessageChannel actionBar = new ActionBarChannel();
			bossEngager = new BossEngager(this, actionBar, engageRange, parsedMessage);
			break;
		case 2:
			IMessageChannel bossBar = new BossBarChannel(BossInfo.Color.RED, BossInfo.Overlay.PROGRESS);
			bossEngager = new BossEngager(this, bossBar, engageRange, parsedMessage);
			break;
		case 3:
			PokemonSpec spec = new PokemonSpec();
			spec.name = boss.getPokemon().getSpecies().name;
			spec.form = boss.getPokemon().getFormEnum().getForm();
			spec.shiny = boss.getPokemon().isShiny();
			NoticeOverlay.Builder overlayBuilder = NoticeOverlay.builder().setLayout(EnumOverlayLayout.LEFT_AND_RIGHT).setPokemonSprite(spec);
			IMessageChannel overlay = new OverlayChannel(overlayBuilder);
			bossEngager = new BossEngager(this, overlay, engageRange, parsedMessage);
			break;
		case 4:
			IMessageChannel title = new TitleChannel(SPacketTitle.Type.SUBTITLE);
			bossEngager = new BossEngager(this, title, engageRange, parsedMessage);
			break;
		}
	}

	private NoticeOverlay.Builder overlayTemplate;

	/**
	 * Creates the overlay initially
	 */
	private void setOverlay() {
		if (spawner.overlayDisabled())
			return;
		overlayText = spawner.getOverlayText();
		if(overlayText == null)
			return;

		PokemonSpec spec = new PokemonSpec();
		spec.name = boss.getPokemon().getSpecies().name;
		spec.form = boss.getPokemon().getFormEnum().getForm();
		spec.shiny = boss.getPokemon().isShiny();
		overlayTemplate = NoticeOverlay.builder().setLayout(EnumOverlayLayout.LEFT_AND_RIGHT).setPokemonSprite(spec);

		startOverlayTask();
	}

	private void startOverlayTask() {
		Task.builder().execute((task) -> {
			if (this.getEntity().isPresent()) {
				showOverlay();
			} else {
				for (EntityPlayer player : world.playerEntities) {
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					NoticeOverlay.hide(playerMP);
				}
				task.setExpired();
			}
		}).interval(60).delay(60).build();
	}

	private void showOverlay() {
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		for (EntityPlayer player : world.playerEntities) {
			EntityPlayerMP playerMP = (EntityPlayerMP) player;
			IParsingContext context = IParsingContext.builder().add(IBossEntity.class, () -> this).add(EntityPlayerMP.class, () -> playerMP).add(IBoss.class, () -> boss).build();
			List<String> parsed = service.interpret(overlayText, context);
			overlayTemplate.setLines(parsed).sendTo(playerMP);
		}
	}

	/**
	 * Keeps the battle entity permanently vanished to all players in the area.
	 */
	private void vanishBattleEntity() {
		Task.builder().execute((task) -> {
			if (getBattleEntity().isPresent()) {
				for (EntityPlayer player : world.playerEntities) {
					if (player.getDistance(getBattleEntity().get()) < 100) {
						SPacketDestroyEntities packet = new SPacketDestroyEntities(getBattleEntity().get().getEntityId());
						((EntityPlayerMP) player).connection.sendPacket(packet);
					}
				}
			} else {
				if (!getBattleEntity().isPresent()) {
					task.setExpired();
				}
			}
		}).interval(10).build();
	}

	/** {@inheritDoc} */
	@Override
	public IBoss getBoss() {
		return boss;
	}

	@Override
	public IBossSpawner getSpawner() {
		return this.spawner;
	}

	/** {@inheritDoc} */
	@Override
	public Optional<EntityStatue> getEntity() {
		ClassInheritanceMultiMap<Entity>[] entityMapList = world.getChunk(position).getEntityLists();
		for (ClassInheritanceMultiMap<Entity> map : entityMapList) {
			for (EntityStatue statueEntity : map.getByClass(EntityStatue.class)) {
				if (statueEntity.getUniqueID().equals(statueEntityUUID)) {
					return Optional.of(statueEntity);
				}
			}
		}

		return Optional.empty();
	}

	/** {@inheritDoc} */
	@Override
	public Optional<EntityPixelmon> getBattleEntity() {
		ClassInheritanceMultiMap<Entity>[] entityMapList = world.getChunk(position).getEntityLists();
		for (ClassInheritanceMultiMap<Entity> map : entityMapList) {
			for (EntityPixelmon statueEntity : map.getByClass(EntityPixelmon.class)) {
				if (statueEntity.getUniqueID().equals(battleEntityUUID)) {
					return Optional.of(statueEntity);
				}
			}
		}

		return Optional.empty();
	}

	@Override
	public Vec3d getPosition() {
		return new Vec3d(position.getX(), position.getY(), position.getZ());
	}

	@Override
	public World getWorld() {
		return world;
	}

	/** {@inheritDoc} */
	@Override
	public IBossEngager getBossEngager() {
		return bossEngager;
	}

	/** {@inheritDoc} */
	@Override
	public void despawn() {
		BossBattleRegistry battleRegistry = BossBattleRegistry.getInstance();
		IBossBattle battle = battleRegistry.getBossBattle(this).get();
		battleRegistry.removeBossBattle(this);
		boss.getBattleSettings().getBattleRestraints().forEach((restraint) -> restraint.onBossDespawn(getBoss()));
		BossDeathEvent deathEvent = new BossDeathEvent(this, battle);
		RemoRaids.EVENT_BUS.post(deathEvent);

		getEntity().ifPresent(Entity::setDead);
		getBattleEntity().ifPresent(Entity::setDead);

		/*
		 * This is delayed by one tick due to drops not cancelling properly due to the pixelmon entity
		 * queueing drop items on death and the boss being deregistered simultaneously, causing the
		 * drop listener to be incapable of cancelling the event.
		 */
		Task.builder().execute(() -> BossEntityRegistry.getInstance().deregister(this)).delay(1).iterations(1).build();
	}

	/**
	 * Returns if dynamax is allowed in the battle
	 *
	 * @return if dynamax is allowed
	 */
	@Override
	public boolean allowDynamax() {
		return allowDynamax;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BossEntity that = (BossEntity) o;
		return Objects.equals(boss, that.boss) && Objects.equals(statueEntityUUID, that.statueEntityUUID) && Objects.equals(battleEntityUUID, that.battleEntityUUID) && Objects.equals(bossEngager, that.bossEngager);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return Objects.hash(boss, statueEntityUUID, battleEntityUUID, bossEngager);
	}

}
