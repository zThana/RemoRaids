package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.ButtonType;
import ca.landonjw.remoraids.internal.inventory.api.Page;
import ca.landonjw.remoraids.internal.inventory.api.Template;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.forms.EnumNoForm;
import com.pixelmonmod.pixelmon.items.ItemPixelmonSprite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class GUIRegistry {

    private EntityPlayerMP player;

    public GUIRegistry(EntityPlayerMP player){
        this.player = player;
    }

    public void open(){
        Button filler = Button.builder()
                .item(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.LIGHT_BLUE.getMetadata()))
                .displayName("")
                .build();

        Button nextButton = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeHolderRight))
                .displayName(TextFormatting.AQUA + "Next Page")
                .type(ButtonType.NextPage)
                .build();

        Button infoButton = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeMonitor))
                .displayName(TextFormatting.AQUA + Page.CURRENT_PAGE_PLACEHOLDER + " / " + Page.TOTAL_PAGES_PLACEHOLDER)
                .type(ButtonType.PageInfo)
                .build();

        Button previousButton = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(TextFormatting.AQUA + "Previous Page")
                .type(ButtonType.PreviousPage)
                .build();

        List<Button> bossButtons = new ArrayList<>();
        for(IBossEntity bossEntity : RemoRaids.getBossAPI().getBossEntityRegistry().getAllBossEntities()){
            Button bossButton = Button.builder()
                    .item(ItemPixelmonSprite.getPhoto(bossEntity.getBoss().getPokemon()))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Boss " + bossEntity.getBoss().getPokemon().getSpecies().name)
                    .lore(UIUtils.getBossLore(bossEntity))
                    .onClick((action) -> {
                        GUIBossOptions options = new GUIBossOptions(player, bossEntity);
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
