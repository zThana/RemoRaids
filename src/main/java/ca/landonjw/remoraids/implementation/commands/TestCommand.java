package ca.landonjw.remoraids.implementation.commands;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.battles.BossBattleRules;
import ca.landonjw.remoraids.implementation.battles.restraints.PreventRebattleRestraint;
import ca.landonjw.remoraids.implementation.boss.Boss;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.BossSpawner;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.announcements.TeleportableSpawnAnnouncement;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public class TestCommand extends CommandBase {

	@Override
	public String getName() {
		return "test";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/test";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayerMP){
			EntityPlayerMP player = (EntityPlayerMP) sender;

			Pokemon pokemon = Pixelmon.pokemonFactory.create(EnumSpecies.randomPoke());

			Boss boss = new Boss(pokemon);
			boss.setSize(10);
			boss.setStat(StatsType.HP, 1000);

			BossSpawnLocation location = new BossSpawnLocation(player);
			ISpawnAnnouncement announcement;
			if(RemoRaids.getGeneralConfig().isAnnouncementTeleportable()){
				announcement = new TeleportableSpawnAnnouncement(RemoRaids.getMessageConfig().getSpawnAnnouncement());
			}
			else{
				announcement = new SpawnAnnouncement(RemoRaids.getMessageConfig().getSpawnAnnouncement());
			}

			BossSpawner spawner = new BossSpawner(boss, location, announcement);

			Optional<IBossEntity> maybeBossEntity = spawner.spawn();

			if(maybeBossEntity.isPresent()){
				IBossEntity bossEntity = maybeBossEntity.get();
				BossBattleRules rules = new BossBattleRules();
				rules.addBattleRestraint(new PreventRebattleRestraint(bossEntity));

				RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get().setBattleRules(rules);
			}
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
