package ca.landonjw.remoraids.implementation.commands.create;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.IRespawnData;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.battles.BossBattleSettings;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;

public class CreationBuilderWrapper {

	private final IBossSpawnLocation.IBossSpawnLocationBuilder spawnLocBuilder = IBossSpawnLocation.builder();
	private final IBoss.IBossBuilder bossBuilder = IBoss.builder();
	private final IBossSpawner.IBossSpawnerBuilder spawnerBuilder = IBossSpawner.builder();
	private final IRespawnData.IRespawnDataBuilder respawnDataBuilder = IRespawnData.builder();
	private final IBossBattleSettings battleSettings = new BossBattleSettings();

	public CreationBuilderWrapper() {
		setDefaultBattleClauses();
	}

	private void setDefaultBattleClauses() {
		BattleRules rules = new BattleRules();
		rules.setNewClauses(RemoRaids.getRestraintsConfig().get(RestraintsConfig.BANNED_CLAUSES));
		battleSettings.setBattleRules(rules);
	}

	public void spawn() throws IllegalStateException {
		IBoss boss = bossBuilder.battleSettings(battleSettings).build();
		IBossSpawnLocation spawnLoc = spawnLocBuilder.build();
		tryAddAnnouncement(spawnerBuilder, spawnLoc);
		tryAddOverlay(spawnerBuilder);

		IRespawnData respawnData = respawnDataBuilder.build();
		IBossSpawner spawner = spawnerBuilder.boss(boss).location(spawnLoc).respawns(respawnData).build();

		validateBoss(boss);
		validateSpawnLocation(spawnLoc);

		spawner.spawn(true);
		if (spawner.doesPersist()) {
			RemoRaids.storage.save(spawner);
		}
	}

	public void setSpawnLocation(@Nonnull EntityPlayerMP player) {
		spawnLocBuilder.world(player.world).location(new Vec3d(player.posX, player.posY, player.posZ)).rotation(player.rotationYaw);
	}

	public void setBossSpec(@Nonnull PokemonSpec spec) {
		bossBuilder.spec(spec);
	}

	private void tryAddAnnouncement(@Nonnull IBossSpawner.IBossSpawnerBuilder spawnerBuilder, @Nonnull IBossSpawnLocation spawnLoc) {
		if (RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ENABLED)) {
			ISpawnAnnouncement.ISpawnAnnouncementBuilder announceBuilder = ISpawnAnnouncement.builder().message(RemoRaids.getMessageConfig().get(MessageConfig.RAID_SPAWN_ANNOUNCE));

			if (RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ALLOW_TP)) {
				announceBuilder.warp(spawnLoc.getWorld(), new Vec3d(spawnLoc.getLocation().x, spawnLoc.getLocation().y, spawnLoc.getLocation().z), spawnLoc.getRotation());
			}
			spawnerBuilder.announcement(announceBuilder.build());
		}
	}

	private void tryAddOverlay(@Nonnull IBossSpawner.IBossSpawnerBuilder spawnerBuilder) {
		spawnerBuilder.overlayText(RemoRaids.getMessageConfig().get(MessageConfig.OVERLAY_TEXT), RemoRaids.getGeneralConfig().get(GeneralConfig.OVERLAY_ENABLED));
	}

	public IParsingContext getContext() {
		return IParsingContext.builder().add(IBossSpawnLocation.IBossSpawnLocationBuilder.class, () -> spawnLocBuilder).add(IBossBattleSettings.class, () -> battleSettings).add(IBoss.IBossBuilder.class, () -> bossBuilder).add(IBossSpawner.IBossSpawnerBuilder.class, () -> spawnerBuilder).add(IRespawnData.IRespawnDataBuilder.class, () -> respawnDataBuilder).build();
	}

	private void validateBoss(@Nonnull IBoss boss) throws IllegalStateException {
		if (boss.getPokemon() == null) {
			throw new IllegalStateException("Illegal boss pokemon supplied");
		}
	}

	private void validateSpawnLocation(@Nonnull IBossSpawnLocation spawnLoc) throws IllegalStateException {
		if (spawnLoc.getLocation() == null) {
			throw new IllegalStateException("Spawn location must be specified");
		} else if (spawnLoc.getWorld() == null) {
			throw new IllegalStateException("World must be specified");
		}
	}

}
