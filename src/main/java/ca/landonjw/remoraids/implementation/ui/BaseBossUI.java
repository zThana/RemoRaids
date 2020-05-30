package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A base for user interfaces within the ingame boss editor.
 *
 * @author landonjw
 * @since  1.0.0
 */
public abstract class BaseBossUI {

    /** The player using the user interface. */
    protected EntityPlayerMP player;
    /** The boss entity being edited. */
    protected IBossEntity bossEntity;

    /** A blue glass pane that is used frequently to make the user interface aesthetic. Offers no functionality. */
    private final Button blueFiller = Button.builder()
            .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()))
            .displayName("")
            .build();
    /** A white glass pane that is used frequently to make the user interface aesthetic. Offers no functionality. */
    private final Button whiteFiller = Button.builder()
            .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.WHITE.getMetadata()))
            .displayName("")
            .build();

    /**
     * Default constructor.
     *
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public BaseBossUI(@Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity){
        this.player = Objects.requireNonNull(player);
        this.bossEntity = Objects.requireNonNull(bossEntity);
    }

    /**
     * Gets the blue filler pane that is used for aesthetics.
     *
     * @return blue filler pane
     */
    public Button getBlueFiller(){
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
    public Button getBossButton(){
        return Button.builder()
                .item(ItemPixelmonSprite.getPhoto(bossEntity.getBoss().getPokemon()))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Boss " + bossEntity.getBoss().getPokemon().getSpecies().name)
                .lore(UIUtils.getPokemonLore(bossEntity))
                .build();
    }

    /**
     * Opens the user interface for the player.
     */
    public abstract void open();

}
