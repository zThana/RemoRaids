package ca.landonjw.remoraids.implementation.commands.executors;

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
import ca.landonjw.remoraids.internal.commands.RaidsCommandExecutor;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CreateRaidBossExecutor implements RaidsCommandExecutor {

	@Override
	public String getUsage(ICommandSender source) {
		return "/raids create [respawn:(amount)|(seconds)] (pokemon spec) (size:(size), moves:(M1,...,M4), (stat:type|(value[x])]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender source, String[] arguments) throws CommandException {
		try {
			if(arguments.length == 0){
				source.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: " + getUsage(source)));
				return;
			}

			EntityPlayerMP player = (EntityPlayerMP) source;
			IBossSpawnLocation spawnLoc = new BossSpawnLocation(player);
			ISpawnAnnouncement announcement = null;
			if (RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ENABLED)) {
				if (RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ALLOW_TP)) {
					announcement = new TeleportableSpawnAnnouncement(RemoRaids.getMessageConfig().get(MessageConfig.RAID_SPAWN_ANNOUNCE));
				} else {
					announcement = new SpawnAnnouncement(RemoRaids.getMessageConfig().get(MessageConfig.RAID_SPAWN_ANNOUNCE));
				}
			}

			IBossCreator creator = IBossCreator.initialize();
			PokemonSpec design = PokemonSpec.from(arguments);
			Map<String, String> remaining = Arrays.stream(arguments)
					.map(String::toLowerCase)
					.map(x -> x.split(":"))
					.filter(x -> x.length == 2)
					.collect(Collectors.toMap(x -> x[0], x -> x[1]));

			IBoss.IBossBuilder boss = IBoss.builder();
			for (Map.Entry<String, String> input : remaining.entrySet()) {
				switch (input.getKey()) {
					case "size":
						boss.size(Float.parseFloat(input.getValue()));
						break;
					case "moves":
						Moveset moveset = new Moveset();
						String[] split = input.getValue().split(",");
						for (int i = 0; i < split.length && i < 4; i++) {
							Attack attack = new Attack(split[i]);
							if (attack.getMove() == null) {
								player.sendMessage(new TextComponentString(TextFormatting.RED + "Unrecognized move: " + split[i]));
								break;
							}
							moveset.add(attack);
						}
						boss.moveset(moveset);
						break;
					case "stat":
						String[] separator = input.getValue().split("\\|");
						if (separator.length != 2) {
							player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid stat input"));
							break;
						}

						StatsType stat = Arrays.stream(StatsType.getStatValues()).filter(st -> st.name().toLowerCase().equals(separator[0])).findAny().orElse(null);
						if (stat == null) {
							player.sendMessage(new TextComponentString(TextFormatting.RED + "Unrecognized stat type: " + separator[0]));
							break;
						}

						String parsed = separator[1].replace("x", "");
						int value = Math.max(1, Integer.parseInt(parsed));
						boss.stat(stat, value, separator[1].contains("x"));
						break;
					case "respawn":
						String[] args = input.getValue().split("\\|");
						if (args.length != 2) {
							player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid respawn input"));
							break;
						}
						creator.respawns(Integer.parseInt(args[0]), Long.parseLong(args[1]), TimeUnit.SECONDS);
						break;
				}
			}

			IBossSpawner spawner = creator
					.location(spawnLoc)
					.announcement(announcement)
					.boss(boss.spec(design).build())
					.build();

			spawner.spawn().ifPresent(entity -> {
				BossBattleRules rules = new BossBattleRules();
				rules.addBattleRestraint(new PreventRebattleRestraint(entity));

				RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(entity).get().setBattleRules(rules);
			});
		} catch (Exception e) {
			source.sendMessage(new TextComponentString(TextFormatting.RED + e.getMessage()));
		}
	}

}
