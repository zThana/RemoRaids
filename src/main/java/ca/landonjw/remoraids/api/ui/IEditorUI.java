package ca.landonjw.remoraids.api.ui;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Represents a user interface used to edit the properties of a reward, reward content, or battle restraint in the native
 * boss editor user interface. The boss editor will iterate through all registered editors via {@link IBossUIRegistry}
 * and invoke {@link #open(IBossUI, EntityPlayerMP, Object)} in order to open the appropriate editor.
 *
 * This allows for custom implementations of rewards, reward contents, and battle restraints to be
 * managed through the already existing boss editor user interface.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface IEditorUI<T> {

	/**
	 * Opens the user interface for a player.
	 *
	 * @param source the user interface that opened this creator, will never be null
	 * @param player the player the user interface is to be opened for
	 * @param t      the item being edited
	 */
	void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull T t);

}
