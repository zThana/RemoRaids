package ca.landonjw.remoraids.api.spawning;

import java.util.Optional;

import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.channels.IMessageChannel;
import ca.landonjw.remoraids.api.util.DataSerializable;
import ca.landonjw.remoraids.api.util.IBuilder;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Controls how a raid boss is announced to the server.
 */
public interface ISpawnAnnouncement extends DataSerializable {

	/**
	 * Sets the announcement to be sent to players. Formatting can be applied here.
	 *
	 * @param announcement the announcement to be sent to players
	 */
	void setAnnouncement(@Nullable String announcement);

	/**
	 * Gets the announcement to be sent to players if there is an announcement set.
	 *
	 * @return the announcement to be sent to players if present
	 */
	Optional<String> getAnnouncement();

	/**
	 * Sends the announcement to players.
	 *
	 * @param spawner the spawner sending the announcement
	 */
	void sendAnnouncement(IBossSpawner spawner);

	/**
	 * Specifies a location a user will teleport to when they click on the announcement message that
	 * appears in chat. If this value is empty, then no click event will be available for the user to teleport
	 * to the raid boss spawn location, or wherever this location is set.
	 *
	 * @return A value indicating a teleport location, or empty
	 */
	Optional<ITeleport> getTeleport();

	static ISpawnAnnouncementBuilder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(ISpawnAnnouncementBuilder.class);
	}

	interface ISpawnAnnouncementBuilder extends IBuilder.Deserializable<ISpawnAnnouncement, ISpawnAnnouncementBuilder> {

		ISpawnAnnouncementBuilder message(String message);

		ISpawnAnnouncementBuilder messageChannel(IMessageChannel channel);

		ISpawnAnnouncementBuilder warp(World world, Vec3d pos, float rotation);

		ISpawnAnnouncementBuilder warp(IBossSpawnLocation location);

	}

	interface ITeleport extends DataSerializable {

		World getWorld();

		Vec3d getPosition();

		float getRotation();

	}

}