package ca.landonjw.remoraids.implementation.commands.executors;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattleSettings;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.battles.BossBattleSettings;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.internal.commands.RaidsCommandExecutor;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CreateRaidBossExecutor implements RaidsCommandExecutor {

	@Override
	public String getUsage(ICommandSender source) {
		return "/raids create [-p] [respawn:(amount)|(seconds)] (pokemon spec) (size:(size), moves:(M1,...,M4), (stat:type|(value[x])]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender source, String[] arguments) throws CommandException {
		try {
			if (arguments.length == 0) {
				source.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: " + getUsage(source)));
				return;
			}

			EntityPlayerMP player = (EntityPlayerMP) source;
			IBossSpawnLocation spawnLoc = new BossSpawnLocation(player);
			ISpawnAnnouncement announcement = null;
			if (RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ENABLED)) {
				ISpawnAnnouncement.ISpawnAnnouncementBuilder announceBuilder = ISpawnAnnouncement.builder().message(RemoRaids.getMessageConfig().get(MessageConfig.RAID_SPAWN_ANNOUNCE));

				if (RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ALLOW_TP)) {
					announceBuilder.warp(spawnLoc.getWorld(), new Vec3d(spawnLoc.getLocation().x, spawnLoc.getLocation().y, spawnLoc.getLocation().z), spawnLoc.getRotation());
				}
				announcement = announceBuilder.build();
			}

			IBossCreator creator = IBossCreator.initialize();
			IBoss.IBossBuilder boss = IBoss.builder();

			BattleRules rules = new BattleRules();
			rules.setNewClauses(RemoRaids.getRestraintsConfig().get(RestraintsConfig.BANNED_CLAUSES));
			IBossBattleSettings settings = new BossBattleSettings();
			settings.setBattleRules(rules);
			boss = boss.battleSettings(settings);

			PokemonSpec design = PokemonSpec.from(arguments);

			ExtraArgs.process(creator, boss, Arrays.stream(arguments).map(String::toLowerCase).map(x -> x.split(":")).filter(x -> x.length == 2).filter(x -> Arrays.stream(design.args).noneMatch(y -> y.toLowerCase().equals(x[0].toLowerCase()))).map(x -> new Tuple<>(x[0], x[1])).collect(Collectors.toList()));

			CmdFlags.process(creator, boss, Arrays.stream(arguments).map(String::toLowerCase).filter(x -> x.startsWith("-")).map(x -> x.substring(1)).collect(Collectors.toList()));

			IBossSpawner spawner = creator.location(spawnLoc).announcement(announcement).boss(boss.spec(design).build()).build();

			spawner.spawn(true);

			if (spawner.doesPersist()) {
				RemoRaids.storage.save(spawner);
			}
		} catch (Exception e) {
			source.sendMessage(new TextComponentString(TextFormatting.RED + e.getMessage()));
		}
	}

	@Override
	public List<String> getTabCompletionOptions(String[] args) {
		return getListOfStringsMatchingLastWord(args, EnumSpecies.getNameList());
	}

	private enum ExtraArgs {
		SIZE((creator, boss, input) -> {
			boss.size(Float.parseFloat(input));
		}),
		MOVES((creator, boss, input) -> {
			Moveset moveset = new Moveset();
			String[] split = input.replace("_", " ").split(",");
			for (int i = 0; i < split.length && i < 4; i++) {
				Attack attack = new Attack(split[i]);
				if (attack.getMove() == null) {
					break;
				}
				moveset.add(attack);
			}
			boss.moveset(moveset);
		}),
		STAT((creator, boss, input) -> {
			String[] separator = input.split("\\|");
			if (separator.length % 2 != 0) {
				return;
			}

			for (int i = 1; i < separator.length; i += 2) {
				int statIndex = i - 1;
				int valueIndex = i;
				StatsType stat = Arrays.stream(StatsType.getStatValues()).filter(st -> st.name().toLowerCase().equals(separator[statIndex])).findAny().orElse(null);
				if (stat == null) {
					return;
				}

				String parsed = separator[valueIndex].replace("x", "");
				int value = Math.max(1, Integer.parseInt(parsed));
				boss.stat(stat, value, separator[valueIndex].contains("x"));
			}
		}),
		RESPAWN((creator, boss, input) -> {
			String[] args = input.split("\\|");
			if (args.length != 2) {
				return;
			}
			if (args[0].equalsIgnoreCase("infinite")) {
				creator.respawns(0, Long.parseLong(args[1]), TimeUnit.SECONDS);
			} else {
				creator.respawns(Integer.parseInt(args[0]), Long.parseLong(args[1]), TimeUnit.SECONDS);
			}
		});

		private Processor<IBossCreator, IBoss.IBossBuilder, String> processor;

		ExtraArgs(Processor<IBossCreator, IBoss.IBossBuilder, String> processor) {
			this.processor = processor;
		}

		public static void process(IBossCreator creator, IBoss.IBossBuilder boss, List<Tuple<String, String>> arguments) {
			for (Tuple<String, String> argument : arguments) {
				Arrays.stream(ExtraArgs.values()).filter(x -> x.name().toLowerCase().equals(argument.getFirst())).findAny().ifPresent(x -> {
					x.processor.accept(creator, boss, argument.getSecond());
				});
			}
		}
	}

	private enum CmdFlags {

		// Persistance
		P((creator, boss, ignore) -> creator.persists(true)),;

		private Processor<IBossCreator, IBoss.IBossBuilder, Void> processor;

		CmdFlags(Processor<IBossCreator, IBoss.IBossBuilder, Void> processor) {
			this.processor = processor;
		}

		public static void process(IBossCreator creator, IBoss.IBossBuilder boss, List<String> flags) {
			for (String flag : flags) {
				Arrays.stream(CmdFlags.values()).filter(x -> x.name().toLowerCase().equals(flag.toLowerCase())).findAny().ifPresent(x -> {
					x.processor.accept(creator, boss, null);
				});
			}
		}

	}

	@FunctionalInterface
	private interface Processor<A, B, C> {

		void accept(A first, B second, C third);

	}

}
