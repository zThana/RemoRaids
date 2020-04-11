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
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RaidsCommand extends CommandBase {

	@Override
	public String getName() {
		return "raids";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/raids create [respawn:(seconds)] (pokemon spec) [size:(size), moves:(M1,...,M4), (stat):type|(value[x])]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayerMP) {
			if(args.length >= 2) {
				String sub = args[0].toLowerCase();
				switch (sub) {
					case "create":
						this.create((EntityPlayerMP) sender, Arrays.asList(args).subList(1, args.length).toArray(new String[]{}));
						break;
				}
			}
		}
	}

	private void create(EntityPlayerMP reference, String... arguments) {
		IBossSpawnLocation spawnLoc = new BossSpawnLocation(reference);
		ISpawnAnnouncement announcement;
		if(RemoRaids.getGeneralConfig().isAnnouncementTeleportable()){
			announcement = new TeleportableSpawnAnnouncement(RemoRaids.getMessageConfig().getSpawnAnnouncement());
		} else{
			announcement = new SpawnAnnouncement(RemoRaids.getMessageConfig().getSpawnAnnouncement());
		}

		PokemonSpec design = PokemonSpec.from(arguments);
		Map<String, String> remaining = Arrays.stream(arguments).filter(arg -> !Arrays.asList(design.args).contains(arg))
				.map(String::toLowerCase)
				.map(x -> x.split(":"))
				.collect(Collectors.toMap(x -> x[0], x -> x[1]));

		IBoss.IBossBuilder boss = IBoss.builder();
		for(Map.Entry<String, String> input : remaining.entrySet()) {
			switch (input.getKey()) {
				case "size":
					boss.size(Float.parseFloat(input.getValue()));
					break;
				case "moves":
					Moveset moveset = new Moveset();
					String[] split = input.getValue().split(",");
					for(int i = 0; i < split.length && i < 4; i++) {
						Attack attack = new Attack(split[i]);
						if(attack.getMove() == null) {
							reference.sendMessage(new TextComponentString("&cUnrecognized move: " + split[i]));
							break;
						}
						moveset.add(attack);
					}
					boss.moveset(moveset);
					break;
				case "stat":
					String[] separator = input.getValue().split("\\|");
					if(separator.length != 2) {
						reference.sendMessage(new TextComponentString("&cInvalid stat input"));
						break;
					}

					StatsType stat = Arrays.stream(StatsType.getStatValues()).filter(st -> st.name().toLowerCase().equals(separator[0])).findAny().orElse(null);
					if(stat == null) {
						reference.sendMessage(new TextComponentString("&cUnrecognized stat type: " + separator[0]));
						break;
					}

					String parsed = separator[1].replace("x", "");
					int value = Math.max(1, Integer.parseInt(parsed));
					boss.stat(stat, value, parsed.equals(separator[1]));
					break;
			}
		}

		IBossSpawner spawner = IBossCreator.initialize()
				.location(spawnLoc)
				.announcement(announcement)
				.boss(boss.spec(design).build())
				.build();

		spawner.spawn().ifPresent(entity -> {
			BossBattleRules rules = new BossBattleRules();
			rules.addBattleRestraint(new PreventRebattleRestraint(entity));

			RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(entity).get().setBattleRules(rules);
		});
	}
}
