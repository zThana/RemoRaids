package ca.landonjw.remoraids.implementation.ui.pages;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.ButtonType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

/**
 * A user interface to display all registered boss entities.
 * Can be viewed by using /raids registry
 *
 * @author landonjw
 * @since 1.0.0
 */
public class RegistryUI {

	/** The player using the user interface. */
	private EntityPlayerMP player;

	/**
	 * Constructor for the user interface.
	 *
	 * @param player the player using the user interface
	 */
	public RegistryUI(@Nonnull EntityPlayerMP player) {
		this.player = player;
	}

	/**
	 * Opens the user interface for the player.
	 */
	public void open() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);

		Button filler = Button.builder().item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.LIGHT_BLUE.getMetadata())).displayName("").build();

		Button nextButton = Button.builder().item(new ItemStack(PixelmonItems.tradeHolderRight)).displayName(config.get(MessageConfig.UI_COMMON_NEXT_PAGE)).type(ButtonType.NextPage).build();

		Button infoButton = Button.builder().item(new ItemStack(PixelmonItems.tradeMonitor)).displayName(config.get(MessageConfig.UI_COMMON_CURR_PAGE)).type(ButtonType.PageInfo).build();

		Button previousButton = Button.builder().item(new ItemStack(PixelmonItems.LtradeHolderLeft)).displayName(config.get(MessageConfig.UI_COMMON_PREVIOUS_PAGE)).type(ButtonType.PreviousPage).build();

		List<Button> bossButtons = new ArrayList<>();
		for (IBossEntity bossEntity : RemoRaids.getBossAPI().getBossEntityRegistry().getAllBossEntities()) {
			IParsingContext context = IParsingContext.builder().add(IBoss.class, bossEntity::getBoss).add(Pokemon.class, () -> bossEntity.getBoss().getPokemon()).build();

			Button bossButton = Button.builder().item(ItemPixelmonSprite.getPhoto(bossEntity.getBoss().getPokemon())).displayName(service.interpret(config.get(MessageConfig.UI_RAID_BOSS_TITLE), context)).lore(service.interpret(config.get(MessageConfig.UI_RAID_BOSS_LORE), context)).onClick((action) -> {
				BossOptionsUI options = new BossOptionsUI(null, player, bossEntity);
				options.open();
			}).build();

			bossButtons.add(bossButton);
		}

		Template template = Template.builder(5).border(0, 0, 5, 9, filler).set(4, 3, previousButton).set(4, 4, infoButton).set(4, 5, nextButton).build();

		Page page = Page.builder().template(template).title(config.get(MessageConfig.UI_REGISTRY_TITLE)).dynamicContentArea(1, 1, 3, 7).dynamicContents(bossButtons).build();

		page.forceOpenPage(player);
	}

}
