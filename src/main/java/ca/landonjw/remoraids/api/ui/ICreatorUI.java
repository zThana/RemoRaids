package ca.landonjw.remoraids.api.ui;

import java.util.Collection;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

/**
 * Represents a user interface used to create a reward, reward content, or battle restraint in the native
 * boss editor user interface. The boss editor will iterate through all registered creators via {@link IBossUIRegistry}
 * and invoke {@link #open(IBossUI, EntityPlayerMP, Collection<T>)} in order to open the appropriate creator.
 *
 * This allows for custom implementations of rewards, reward contents, and battle restraints to be
 * managed through the already existing boss editor user interface.
 *
 * @author landonjw
 * @since 1.0.0
 */
public interface ICreatorUI<T> {

	/**
	 * Opens the user interface for a player.
	 *
	 * @param source  the user interface that opened this creator, will never be null
	 * @param player  the player the user interface is to be opened for
	 * @param toAddTo used for adding the newly created item to boss. The boss editor will
	 *                automatically select the appropriate list to append new item to.
	 */
	void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<T> toAddTo);

	/**
	 * Gets the icon to be displayed in the boss editor.
	 *
	 * @return icon to be displayed in the boss editor
	 */
	ItemStack getCreatorIcon();

	/**
	 * The title to be displayed in the boss editor.
	 *
	 * @return title to be displayed in the boss editor
	 */
	String getCreatorTitle();

}
