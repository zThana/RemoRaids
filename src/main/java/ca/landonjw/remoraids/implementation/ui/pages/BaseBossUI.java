package ca.landonjw.remoraids.implementation.ui.pages;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;

/**
 * A base for user interfaces within the ingame boss ui.
 *
 * @author landonjw
 * @since 1.0.0
 */
public abstract class BaseBossUI implements IBossUI {

	protected IBossUI source;
	/** The player using the user interface. */
	protected EntityPlayerMP player;
	/** The boss entity being edited. */
	protected IBossEntity bossEntity;

	/** A blue glass pane that is used frequently to make the user interface aesthetic. Offers no functionality. */
	private final Button blueFiller = Button.builder().item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.LIGHT_BLUE.getMetadata())).displayName("").build();
	/** A white glass pane that is used frequently to make the user interface aesthetic. Offers no functionality. */
	private final Button whiteFiller = Button.builder().item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.WHITE.getMetadata())).displayName("").build();

	/**
	 * Default constructor.
	 *
	 * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
	 * @param player     the player using the user interface
	 * @param bossEntity the boss entity being edited
	 */
	public BaseBossUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
		this.source = source;
		this.player = Objects.requireNonNull(player);
		this.bossEntity = Objects.requireNonNull(bossEntity);
	}

	/**
	 * Checks if a boss is in battle or not.
	 *
	 * @return true if boss is not in battle, false if boss is in battle
	 */
	public boolean bossNotInBattle() {
		if (RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).isPresent()) {
			return RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get().getPlayersInBattle().size() == 0;
		}
		return false;
	}

	/**
	 * Gets the blue filler pane that is used for aesthetics.
	 *
	 * @return blue filler pane
	 */
	public Button getBlueFiller() {
		return blueFiller;
	}

	/**
	 * Gets the white filler pane that is used for aesthetics.
	 *
	 * @return white filler pane
	 */
	public Button getWhiteFiller() {
		return whiteFiller;
	}

	/**
	 * Gets a button that displays the appropriate pixelmon photo and stats of the boss.
	 *
	 * @return button that represents the boss
	 */
	public Button getBossButton() {
		Config config = RemoRaids.getMessageConfig();
		IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
		IParsingContext context = IParsingContext.builder().add(IBoss.class, bossEntity::getBoss).add(Pokemon.class, () -> bossEntity.getBoss().getPokemon()).build();
		return Button.builder().item(ItemPixelmonSprite.getPhoto(bossEntity.getBoss().getPokemon())).displayName(service.interpret(config.get(MessageConfig.UI_RAID_BOSS_TITLE), context)).lore(service.interpret(config.get(MessageConfig.UI_RAID_BOSS_LORE), context)).build();
	}

	@Override
	public Optional<IBossUI> getSource() {
		return Optional.ofNullable(source);
	}

	@Override
	public IBossEntity getBossEntity() {
		return bossEntity;
	}

}
