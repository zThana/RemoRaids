package ca.landonjw.remoraids.implementation.ui.creators;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBattleRestraint;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.implementation.battles.restraints.CooldownRestraint;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CooldownRestraintCreator implements ICreatorUI<IBattleRestraint> {

	/** {@inheritDoc} */
	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull Collection<IBattleRestraint> toAddTo) {
		new Creator(source, player, toAddTo);
	}

	/** {@inheritDoc} */
	@Override
	public ItemStack getCreatorIcon() {
		return new ItemStack(Items.EMERALD);
	}

	/** {@inheritDoc} */
	@Override
	public String getCreatorTitle() {
		Config config = RemoRaids.getMessageConfig();
		return config.get(MessageConfig.COOLDOWN_RESTRAINT_TITLE);
	}

	class Creator {

		/** The source that opened this creator. */
		private IBossUI source;
		/** The player that the user interface is intended for. */
		private EntityPlayerMP player;
		/** The collection of reward contents to append creation to. */
		private Collection<IBattleRestraint> toAddTo;

		/**
		 * Constructor for the creator user interface.
		 *
		 * @param source  source that opened this creator
		 * @param player  player that user interface is intended for
		 * @param toAddTo collection of reward contents to append creation to
		 */
		public Creator(IBossUI source, EntityPlayerMP player, Collection<IBattleRestraint> toAddTo) {
			this.source = source;
			this.player = player;
			this.toAddTo = toAddTo;

			InventoryAPI.getInstance().closePlayerInventory(player);
			player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enter cooldown in seconds (ie. '10') or 'cancel' to cancel!"));
			MinecraftForge.EVENT_BUS.register(this);
		}

		/**
		 * Used to receive message from player to determine what command he wants to execute from the reward content.
		 *
		 * @param event called when a player sends message on server
		 */
		@SubscribeEvent
		public void onMessage(ServerChatEvent event) {
			if (event.getPlayer().equals(player)) {
				event.setCanceled(true);
				String message = event.getMessage();

				if (message.equalsIgnoreCase("cancel")) {
					MinecraftForge.EVENT_BUS.unregister(this);
					source.getSource().get().open();
				} else {
					try {
						int cooldown = Integer.parseInt(message);

						CooldownRestraint restraint = new CooldownRestraint(cooldown, TimeUnit.SECONDS);
						toAddTo.add(restraint);
						MinecraftForge.EVENT_BUS.unregister(this);
						source.getSource().get().open();
					} catch (NumberFormatException e) {
						player.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect cooldown value. Try again."));
					}
				}
			}
		}

	}

}
