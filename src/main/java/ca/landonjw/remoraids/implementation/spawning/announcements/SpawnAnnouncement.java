package ca.landonjw.remoraids.implementation.spawning.announcements;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.api.util.gson.JObject;
import ca.landonjw.remoraids.internal.messages.channels.ChatChannel;
import ca.landonjw.remoraids.internal.text.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.server.FMLServerHandler;

/**
 * Base implementation for {@link ISpawnAnnouncement}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class SpawnAnnouncement implements ISpawnAnnouncement {

	/** The announcement to be sent to players when a spawn occurs. */
	private String announcement;

	/** Represents the teleport information for this announcement */
	private Teleport teleport;

	private IMessageChannel channel;

	/**
	 * Constructor for the spawn announcement.
	 *
	 * @param announcement the announcement to be sent to players when a boss spawn occurs
	 */
	public SpawnAnnouncement(@Nullable String announcement, @Nullable Teleport teleport, @Nullable IMessageChannel channel) {
		this.announcement = announcement;
		this.teleport = teleport;
		this.channel = (channel != null) ? channel : new ChatChannel();
	}

	public SpawnAnnouncement(@Nullable String announcement, @Nullable Teleport teleport) {
		this(announcement, teleport, new ChatChannel());
	}

	private SpawnAnnouncement(SpawnAnnouncementBuilder builder) {
		this.announcement = builder.message;
		this.teleport = builder.teleport;
		this.channel = builder.channel;
	}

	/** {@inheritDoc} */
	@Override
	public void setAnnouncement(@Nullable String announcement) {
		this.announcement = announcement;
	}

	/** {@inheritDoc} */
	@Override
	public Optional<String> getAnnouncement() {
		return Optional.ofNullable(announcement);
	}

	/** {@inheritDoc} */
	@Override
	public void sendAnnouncement(IBossSpawner spawner) {
		if (announcement != null) {
			ITextComponent text = getAnnouncementText(announcement, spawner);
			PlayerList players = FMLServerHandler.instance().getServer().getPlayerList();
			for (EntityPlayerMP player : players.getPlayers()) {
				channel.sendMessage(player, text);
			}
		}
	}

	@Override
	public Optional<ITeleport> getTeleport() {
		return Optional.ofNullable(teleport);
	}

	/**
	 * Gets the text representation of the announcement to be sent to players.
	 *
	 * @param announcement the announcement
	 * @return the announcement as a text object
	 */
	protected ITextComponent getAnnouncementText(@Nonnull String announcement, @Nonnull IBossSpawner spawner) {
		String parsedAnnouncement = getParsedAnnouncement(announcement, spawner);
		TextComponentString text = new TextComponentString(parsedAnnouncement);

		if (this.teleport != null) {
			Consumer<ICommandSender> teleportConsumer = (sender) -> {
				if (sender instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) sender;

					World spawnWorld = this.teleport.getWorld();

					if (player.dimension != spawnWorld.provider.getDimension()) {
						PlayerList playerList = player.getServer().getPlayerList();
						Teleporter teleporter = ((WorldServer) spawnWorld).getDefaultTeleporter();
						playerList.transferPlayerToDimension(player, spawnWorld.provider.getDimension(), teleporter);
					}
					player.setPositionAndUpdate(this.teleport.pos.x, this.teleport.pos.y, this.teleport.pos.z);
				}
			};

			TextUtils.addCallback(text, teleportConsumer, false);
		}

		return text;
	}

	/**
	 * Replaces all messages in the announcement with the true values the placeholder represents.
	 * Placeholders can be found within this class.
	 *
	 * @param announcement the announcement with messages
	 * @param spawner      the spawner being used
	 * @return the announcement with messages replaced
	 */
	private String getParsedAnnouncement(@Nonnull String announcement, @Nonnull IBossSpawner spawner) {
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(IBossSpawnLocation.class, spawner::getSpawnLocation).add(IBoss.class, spawner::getBoss).add(Vec3d.class, () -> new Vec3d(spawner.getSpawnLocation().getLocation().x, spawner.getSpawnLocation().getLocation().y, spawner.getSpawnLocation().getLocation().z)).build();
		return service.interpret(announcement, context);
	}

	/** {@inheritDoc} */
	@Override
	public JObject serialize() {
		return new JObject().add("message", this.announcement).when(this, x -> x.getTeleport().isPresent(), o -> o.add("teleport", this.getTeleport().get().serialize()));
	}

	public static class SpawnAnnouncementBuilder implements ISpawnAnnouncementBuilder {

		private String message;
		private Teleport teleport;
		private IMessageChannel channel = new ChatChannel();

		@Override
		public ISpawnAnnouncementBuilder message(String message) {
			this.message = message;
			return this;
		}

		@Override
		public ISpawnAnnouncementBuilder messageChannel(IMessageChannel channel) {
			this.channel = channel;
			return this;
		}

		@Override
		public ISpawnAnnouncementBuilder warp(World world, Vec3d pos, float rotation) {
			this.teleport = new Teleport(world, pos, rotation);
			return this;
		}

		@Override
		public ISpawnAnnouncementBuilder warp(IBossSpawnLocation location) {
			this.teleport = new Teleport(location.getWorld(), new Vec3d(location.getLocation().x, location.getLocation().y, location.getLocation().z), location.getRotation());
			return this;
		}

		@Override
		public ISpawnAnnouncementBuilder from(ISpawnAnnouncement input) {
			return this;
		}

		@Override
		public ISpawnAnnouncement build() {
			return new SpawnAnnouncement(this);
		}

		@Override
		public ISpawnAnnouncementBuilder deserialize(JsonObject json) {
			this.message = json.get("message").getAsString();
			if (json.has("teleport")) {
				this.teleport = new Teleport(Arrays.stream(FMLCommonHandler.instance().getMinecraftServerInstance().worlds).filter(x -> x.getWorldInfo().getWorldName().equals(json.get("world").getAsString())).findAny().orElseThrow(() -> new RuntimeException("World does not exist...")), new Vec3d(json.get("x").getAsDouble(), json.get("y").getAsDouble(), json.get("z").getAsDouble()), json.get("rotation").getAsFloat());
			}
			return this;
		}

	}

	private static class Teleport implements ITeleport {

		private World world;
		private Vec3d pos;
		private float rotation;

		public Teleport(World world, Vec3d pos, float rotation) {
			this.world = world;
			this.pos = pos;
			this.rotation = rotation;
		}

		public World getWorld() {
			return world;
		}

		public Vec3d getPosition() {
			return pos;
		}

		public float getRotation() {
			return rotation;
		}

		@Override
		public JObject serialize() {
			return new JObject().add("world", world.getWorldInfo().getWorldName()).add("coordinates", new JObject().add("x", pos.x).add("y", pos.y).add("z", pos.z)).add("rotation", this.rotation);
		}
	}

	/**
	 * Represents the placeholder parser that'll be capable of interpreting values of a position source.
	 *
	 * Available arguments:
	 * x - Outputs the x coordinate, rounded to the nearest decimal point
	 * y - Outputs the y coordinate, rounded to the nearest decimal point
	 * z - Outputs the z coordinate, rounded to the nearest decimal point
	 */
	public static class PositionPlaceholderParser implements IPlaceholderParser {

		private final DecimalFormat df = new DecimalFormat("#0.0#");
		private final Function<Double, Optional<String>> converter = input -> Optional.of(this.df.format(input));

		@Override
		public String getKey() {
			return "position";
		}

		@Override
		public Optional<String> parse(IPlaceholderContext context) {
			Vec3d position = context.getAssociation(Vec3d.class).orElse(null);
			if (position != null && context.getArguments().isPresent()) {
				List<String> arguments = context.getArguments().get();

				// We should only have 1 argument for this placeholder per use
				if (arguments.size() == 1) {
					String arg = arguments.get(0);
					switch (arg.toLowerCase()) {
					case "x":
						return this.converter.apply(position.x);
					case "y":
						return this.converter.apply(position.y);
					case "z":
						return this.converter.apply(position.z);
					}
				}
			}

			return Optional.empty();
		}

	}

}
