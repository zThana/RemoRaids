package ca.landonjw.remoraids.implementation.commands;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.battles.BossBattleRules;
import ca.landonjw.remoraids.implementation.battles.restraints.PreventRebattleRestraint;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.announcements.TeleportableSpawnAnnouncement;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.TimeUnit;

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

			IBossSpawnLocation location = new BossSpawnLocation(player);
			ISpawnAnnouncement announcement;
			if(RemoRaids.getGeneralConfig().isAnnouncementTeleportable()){
				announcement = new TeleportableSpawnAnnouncement(RemoRaids.getMessageConfig().getSpawnAnnouncement());
			}
			else{
				announcement = new SpawnAnnouncement(RemoRaids.getMessageConfig().getSpawnAnnouncement());
			}

			IBossSpawner spawner = IBossCreator.initialize()
					.boss(IBoss.builder()
							.species(EnumSpecies.Bidoof)
							.level(5)
							.size(5)
							.stat(StatsType.HP, 1000, false)
							.build()
					)
					.location(location)
					.announcement(announcement)
					.respawns(1, 5, TimeUnit.SECONDS)
					.build();

			spawner.spawn().ifPresent(entity -> {
				BossBattleRules rules = new BossBattleRules();
				rules.addBattleRestraint(new PreventRebattleRestraint(entity));

				RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(entity).get().setBattleRules(rules);
			});
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
