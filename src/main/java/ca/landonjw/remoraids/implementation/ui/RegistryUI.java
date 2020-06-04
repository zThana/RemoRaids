package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.ButtonType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A user interface to display all registered boss entities.
 * Can be viewed by using /raids registry
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RegistryUI {

    /** The player using the user interface. */
    private EntityPlayerMP player;

    /**
     * Constructor for the user interface.
     *
     * @param player the player using the user interface
     */
    public RegistryUI(@Nonnull EntityPlayerMP player){
        this.player = player;
    }

    /**
     * Opens the user interface for the player.
     */
    public void open(){
        Button filler = Button.builder()
                .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()))
                .displayName("")
                .build();

        Button nextButton = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeHolderRight))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Next Page")
                .type(ButtonType.NextPage)
                .build();

        Button infoButton = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeMonitor))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Page " + Page.CURRENT_PAGE_PLACEHOLDER + " / " + Page.TOTAL_PAGES_PLACEHOLDER)
                .type(ButtonType.PageInfo)
                .build();

        Button previousButton = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Previous Page")
                .type(ButtonType.PreviousPage)
                .build();

        List<Button> bossButtons = new ArrayList<>();
        for(IBossEntity bossEntity : RemoRaids.getBossAPI().getBossEntityRegistry().getAllBossEntities()){
            Button bossButton = Button.builder()
                    .item(ItemPixelmonSprite.getPhoto(bossEntity.getBoss().getPokemon()))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Boss " + bossEntity.getBoss().getPokemon().getSpecies().name)
                    .lore(UIUtils.getPokemonLore(bossEntity))
                    .onClick((action) -> {
                        BossOptionsUI options = new BossOptionsUI(null, player, bossEntity);
                        options.open();
                    })
                    .build();

            bossButtons.add(bossButton);
        }

        Template template = Template.builder(5)
                .border(0, 0, 5, 9, filler)
                .set(4, 3, previousButton)
                .set(4, 4, infoButton)
                .set(4, 5, nextButton)
                .build();

        Page page = Page.builder()
                .template(template)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Boss Registry")
                .dynamicContentArea(1, 1, 3, 7)
                .dynamicContents(bossButtons)
                .build();

        page.forceOpenPage(player);
    }

}
