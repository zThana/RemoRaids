package ca.landonjw.remoraids.implementation.ui.editors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.implementation.rewards.contents.PokemonContent;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import ca.landonjw.remoraids.internal.inventory.api.LineType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * The {@link IEditorUI} used for editing an existing {@link PokemonContent}.
 *
 * @author landonjw
 * @since 1.0.0
 */
public class PokemonContentEditor implements IEditorUI<PokemonContent> {

	/** {@inheritDoc} */
	@Override
	public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull PokemonContent pokemonContent) {
		new Editor(source, player, source.getBossEntity(), pokemonContent).open();
	}

	class Editor extends BaseBossUI {

		/** The pokemon content being edited. */
		private PokemonContent content;

		/**
		 * Default constructor.
		 *
		 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
		 * @param player     the player using the user interface
		 * @param bossEntity the boss entity being edited
		 */
		public Editor(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, @Nonnull PokemonContent content) {
			super(source, player, bossEntity);
			this.content = content;
		}

		/** {@inheritDoc} */
		@Override
		public void open() {
			Button back = Button.builder().item(new ItemStack(Blocks.BARRIER)).displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back").onClick(() -> {
				source.open();
			}).build();

			Button setDescription = Button.builder().item(new ItemStack(Items.WRITABLE_BOOK)).displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Set Description").onClick(() -> {
				MinecraftForge.EVENT_BUS.register(this);
				InventoryAPI.getInstance().closePlayerInventory(player);
				player.sendMessage(new TextComponentString(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Enter the new description into chat!"));
			}).build();

			Template template = Template.builder(5).line(LineType.Horizontal, 1, 0, 9, getWhiteFiller()).line(LineType.Horizontal, 3, 0, 9, getWhiteFiller()).border(0, 0, 5, 9, getBlueFiller()).set(0, 4, getBossButton()).set(2, 4, setDescription).set(3, 4, back).build();

			Page page = Page.builder().template(template).title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Reward Settings").build();

			page.forceOpenPage(player);
		}

		/**
		 * Used to receive message from player to determine what he wants to set new description to.
		 *
		 * @param event called when a player sends message on server
		 */
		@SubscribeEvent
		public void onMessage(ServerChatEvent event) {
			if (event.getPlayer().equals(player)) {
				event.setCanceled(true);
				content.setDescription(event.getMessage().replace("&", "\u00a7"));
				source.open();
				MinecraftForge.EVENT_BUS.unregister(this);
			}
		}

	}

}
