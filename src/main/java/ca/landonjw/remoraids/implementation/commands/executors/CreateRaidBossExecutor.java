package ca.landonjw.remoraids.implementation.commands.executors;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.internal.commands.RaidsCommandExecutor;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Moveset;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

public class CreateRaidBossExecutor implements RaidsCommandExecutor {

	@Override
	public String getUsage(ICommandSender source) {
		return "/raids create [-p] [respawn:(amount)|(seconds)] (pokemon spec) (size:(size), moves:(M1,...,M4), (stat:type|(value[x])]";
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
				ISpawnAnnouncement.ISpawnAnnouncementBuilder announceBuilder = ISpawnAnnouncement.builder()
						.message(RemoRaids.getMessageConfig().get(MessageConfig.RAID_SPAWN_ANNOUNCE));

				if(RemoRaids.getGeneralConfig().get(GeneralConfig.ANNOUNCEMENTS_ALLOW_TP)) {
					announceBuilder.warp(
							spawnLoc.getWorld(),
							new Vec3d(spawnLoc.getX(), spawnLoc.getY(), spawnLoc.getZ()),
							spawnLoc.getRotation()
					);
				}
				announcement = announceBuilder.build();
			}

			IBossCreator creator = IBossCreator.initialize();
			IBoss.IBossBuilder boss = IBoss.builder();
			PokemonSpec design = PokemonSpec.from(arguments);

			ExtraArgs.process(creator, boss, Arrays.stream(arguments)
					.map(String::toLowerCase)
					.map(x -> x.split(":"))
					.filter(x -> x.length == 2)
					.filter(x -> Arrays.stream(design.args).noneMatch(y -> y.toLowerCase().equals(x[0].toLowerCase())))
					.collect(Collectors.toMap(x -> x[0], x -> x[1])));

			CmdFlags.process(creator, boss, Arrays.stream(arguments)
					.map(String::toLowerCase)
					.filter(x -> x.startsWith("-"))
					.collect(Collectors.toList())
			);

			IBossSpawner spawner = creator
					.location(spawnLoc)
					.announcement(announcement)
					.boss(boss.spec(design).build())
					.build();

			spawner.spawn(true);

			if(spawner.doesPersist()) {
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
			String[] split = input.split(",");
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
			if (separator.length != 2) {
				return;
			}

			StatsType stat = Arrays.stream(StatsType.getStatValues()).filter(st -> st.name().toLowerCase().equals(separator[0])).findAny().orElse(null);
			if (stat == null) {
				return;
			}

			String parsed = separator[1].replace("x", "");
			int value = Math.max(1, Integer.parseInt(parsed));
			boss.stat(stat, value, separator[1].contains("x"));
		}),
		RESPAWN((creator, boss, input) -> {
			String[] args = input.split("\\|");
			if (args.length != 2) {
				return;
			}
			creator.respawns(Integer.parseInt(args[0]), Long.parseLong(args[1]), TimeUnit.SECONDS);
		})
		;

		private Processor<IBossCreator, IBoss.IBossBuilder, String> processor;

		ExtraArgs(Processor<IBossCreator, IBoss.IBossBuilder, String> processor) {
			this.processor = processor;
		}

		public static void process(IBossCreator creator, IBoss.IBossBuilder boss, Map<String, String> arguments) {
			for(Map.Entry<String, String> argument : arguments.entrySet()) {
				Arrays.stream(ExtraArgs.values())
						.filter(x -> x.name().toLowerCase().equals(argument.getKey()))
						.findAny()
						.ifPresent(x -> {
							x.processor.accept(creator, boss, argument.getValue());
						});
			}
		}
	}

	private enum CmdFlags {

		PERSISTS((creator, boss, ignore) -> creator.persists(true)),
		;

		private Processor<IBossCreator, IBoss.IBossBuilder, Void> processor;

		CmdFlags(Processor<IBossCreator, IBoss.IBossBuilder, Void> processor) {
			this.processor = processor;
		}

		public static void process(IBossCreator creator, IBoss.IBossBuilder boss, List<String> flags) {
			for(String flag : flags) {
				Arrays.stream(CmdFlags.values())
						.filter(x -> x.name().toLowerCase().equals(flag.toLowerCase()))
						.findAny()
						.ifPresent(x -> {
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
