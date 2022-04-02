package ca.landonjw.remoraids.implementation.spawning;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.client.models.smd.AnimationType;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityStatue;
import com.pixelmonmod.pixelmon.enums.EnumStatueTextureType;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.events.BossSpawnedEvent;
import ca.landonjw.remoraids.api.events.BossSpawningEvent;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.IRespawnData;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.implementation.boss.BossEntity;
import ca.landonjw.remoraids.internal.pokemon.PokemonUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * An implementation of {@link IBossSpawner} that simply spawns a {@link IBoss}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class BossSpawner implements IBossSpawner {

	/** The boss to be spawned. */
	private IBoss boss;
	/** The location to spawn the boss at. */
	private IBossSpawnLocation spawnLocation;
	/** The announcement to be sent to players when the boss is spawned. */
	private ISpawnAnnouncement announcement;
	/** The overlay to be shown to players when the boss is alive. */
	private List<String> overlayText;
	/** Sets if the overlay is disabled or nah */
	private boolean overlayDisabled;
	/** Associated Respawn Data */
	private IRespawnData respawns;
	/** Specifies if the spawner will persist across restarts */
	private transient boolean persists;
	/** A marker flag to indicate a raid boss has spawned */
	private transient boolean spawned;
	/** Sets if dynamax is allowed or not */
	private boolean allowDynamax;

	/**
	 * Constructor for the boss spawner.
	 *
	 * @param boss          the boss to spawn
	 * @param spawnLocation the location to spawn at
	 * @param announcement  the announcement to send on spawn, null for no announcement
	 */
	public BossSpawner(@Nonnull IBoss boss, @Nonnull IBossSpawnLocation spawnLocation, @Nullable ISpawnAnnouncement announcement, @Nullable IRespawnData respawns, boolean persists, @Nullable List<String> overlayText, boolean overlayDisabled, boolean allowDynamax) {
		this.boss = Objects.requireNonNull(boss);
		this.spawnLocation = Objects.requireNonNull(spawnLocation);
		this.announcement = announcement;
		this.overlayText = overlayText;
		this.overlayDisabled = overlayDisabled;
		this.respawns = respawns;
		this.persists = persists;
		this.allowDynamax = allowDynamax;
	}

	@Override
	public String getKey() {
		return "default";
	}

	/** {@inheritDoc} */
	@Override
	public Optional<IBossEntity> spawn(boolean announce) {
		if (boss.getEntity().isPresent()) {
			return Optional.empty();
		}

		BossSpawningEvent spawningEvent = new BossSpawningEvent(this.getBoss(), this);
		RemoRaids.EVENT_BUS.post(spawningEvent);

		if (!spawningEvent.isCanceled()) {
			EntityStatue statue = createAndSpawnStatue();
			EntityPixelmon battleEntity = createAndSpawnBattleEntity();
			setStatueAnimation(statue, battleEntity);

			if (announce && announcement != null) {
				announcement.sendAnnouncement(this);
			}

			IBossEntity bossEntity = new BossEntity(this, this.getBoss(), statue, battleEntity);

			BossSpawnedEvent spawnedEvent = new BossSpawnedEvent(bossEntity, this);
			RemoRaids.EVENT_BUS.post(spawnedEvent);

			this.spawned = true;

			return Optional.of(bossEntity);
		}
		return Optional.empty();
	}

	@Override
	public EntityPixelmon fix() {
		if (this.getBoss().getEntity().get().getBattleEntity().isPresent()) {
			return this.getBoss().getEntity().get().getBattleEntity().get();
		}

		return this.createAndSpawnBattleEntity();
	}

	/**
	 * Creates a statue entity to serve as a decoy for the boss.
	 *
	 * <p>
	 * This is used in lieu of a pixelmon entity because it will persist over chunk unloads,
	 * and appears to have client side performance increases when the entity is scaled.
	 * </p>
	 *
	 * @return the statue entity that was created and spawned
	 */
	private EntityStatue createAndSpawnStatue() {
		EntityStatue statue = new EntityStatue(spawnLocation.getWorld());

		Pokemon bossPokemon = this.getBoss().getPokemon();
		statue.setPokemon(bossPokemon);
		statue.setPixelmonScale(this.getBoss().getSize());

		if (bossPokemon.isShiny()) {
			statue.setTextureType(EnumStatueTextureType.Shiny);
		}

		if (this.getBoss().getTexture().isPresent()) {
			NBTTagCompound nbt = new NBTTagCompound();
			statue.writeToNBT(nbt);
			nbt.setString("CustomTexture", this.getBoss().getTexture().get());
			statue.readFromNBT(nbt);
		}

		Vec3d location = spawnLocation.getLocation();
		statue.setPosition(location.x, location.y, location.z);
		statue.setRotation(spawnLocation.getRotation());
		spawnLocation.getWorld().spawnEntity(statue);

		return statue;
	}

	/**
	 * Sets the animation for the boss statue.
	 * If the boss is midair and is capable of flying, the flying animation will be used.
	 * Otherwise, the idle animation is used.
	 *
	 * @param statue   the statue to apply the animation to
	 * @param pixelmon the pixelmon to search for flying capabilities
	 */
	private void setStatueAnimation(EntityStatue statue, EntityPixelmon pixelmon) {
		Vec3d location = spawnLocation.getLocation();
		BlockPos spawnPos = new BlockPos(location.x, location.y, location.z);

		if (!spawnLocation.getWorld().getBlockState(spawnPos.down()).getMaterial().isSolid() && pixelmon.canFly()) {
			statue.setIsFlying(true);
			statue.setAnimation(AnimationType.FLY);
		} else {
			statue.setAnimation(AnimationType.IDLE);
		}
		statue.setAnimate(true);
	}

	/**
	 * Creates a pixelmon entity to serve as the pixelmon player's battle against.
	 *
	 * @return the pixelmon entity that was created and spawned
	 */
	private EntityPixelmon createAndSpawnBattleEntity() {
		EntityPixelmon battleEntity = new EntityPixelmon(spawnLocation.getWorld());

		Pokemon bossPokemon = this.getBoss().getPokemon();
		battleEntity.setPokemon(PokemonUtils.clonePokemon(bossPokemon));
		battleEntity.getPokemonData().getBonusStats().setPreventsCapture(true);
		battleEntity.setPixelmonScale(0);
		battleEntity.enablePersistence();

		battleEntity.setNoAI(true);

		Vec3d location = spawnLocation.getLocation();
		battleEntity.setPosition(location.x, location.y, location.z);
		spawnLocation.getWorld().spawnEntity(battleEntity);

		return battleEntity;
	}

	/** {@inheritDoc} */
	@Override
	public IBossSpawnLocation getSpawnLocation() {
		return spawnLocation;
	}

	/** {@inheritDoc} */
	@Override
	public void setSpawnLocation(IBossSpawnLocation spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	/** {@inheritDoc} */
	@Override
	public IBoss getBoss() {
		return this.boss;
	}

	/** {@inheritDoc} */
	@Override
	public ISpawnAnnouncement getAnnouncement() {
		return announcement;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getOverlayText() {
		return overlayText;
	}

	/** {@inheritDoc} */
	@Override
	public boolean overlayDisabled() {
		return overlayDisabled;
	}

	@Override
	public Optional<IRespawnData> getRespawnData() {
		return Optional.ofNullable(this.respawns);
	}

	@Override
	public IRespawnData createRespawnData() {
		return this.respawns = IRespawnData.builder().build();
	}

	@Override
	public boolean doesPersist() {
		return this.persists;
	}

	@Override
	public boolean hasSpawned() {
		return this.spawned;
	}

	/** {@inheritDoc} */
	@Override
	public boolean allowDynamax() {
		return allowDynamax;
	}

	/** {@inheritDoc} */
	@Override
	public JObject serialize() {
		return new JObject().add("key", this.getKey()).add("boss", this.boss.serialize()).add("announcement", this.announcement.serialize()).add("location", this.spawnLocation.serialize()).when(this.respawns, Objects::nonNull, o -> o.add("respawning", this.respawns.serialize()));
	}

	public static class BossSpawnerBuilder implements IBossSpawnerBuilder {

		private IBoss boss;
		private IBossSpawnLocation location;
		private ISpawnAnnouncement announcement;
		private IRespawnData data;
		private List<String> overlayText;
		private boolean overlayDisabled;
		private boolean persists;
		private boolean allowDynamax = true;

		@Override
		public IBossSpawnerBuilder boss(IBoss boss) {
			this.boss = boss;
			return this;
		}

		@Override
		public IBossSpawnerBuilder location(IBossSpawnLocation location) {
			this.location = location;
			return this;
		}

		@Override
		public IBossSpawnerBuilder announcement(ISpawnAnnouncement announcement) {
			this.announcement = announcement;
			return this;
		}

		@Override
		public IBossSpawnerBuilder overlayText(@Nullable List<String> overlayText, boolean overlayDisabled) {
			this.overlayText = overlayText;
			this.overlayDisabled = overlayDisabled;
			return this;
		}

		@Override
		public IBossSpawnerBuilder allowDynamax(boolean allowDynamax) {
			this.allowDynamax = allowDynamax;
			return this;
		}

		@Override
		public IBossSpawnerBuilder respawns(IRespawnData data) {
			this.data = data;
			return this;
		}

		@Override
		public IBossSpawnerBuilder persists(boolean persists) {
			this.persists = persists;
			return this;
		}

		@Override
		public IBossSpawnerBuilder from(IBossSpawner input) {
			return this;
		}

		@Override
		public IBossSpawner build() {
			if (boss == null) {
				throw new IllegalStateException("builder requires boss");
			}
			if (location == null) {
				throw new IllegalStateException("builder requires location");
			}
			return new BossSpawner(this.boss, this.location, this.announcement, this.data, this.persists, this.overlayText, this.overlayDisabled, this.allowDynamax);
		}
	}
}
