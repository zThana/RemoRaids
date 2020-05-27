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

    public void openRegistry(){
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
                    .lore(getBossLore(bossEntity))
                    .onClick((action) -> {
                        GUIEditor editor = new GUIEditor();
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

    private List<String> getBossLore(IBossEntity boss){
        List<String> lore = new ArrayList<>();

        Pokemon pokemon = boss.getBoss().getPokemon();

        lore.add(TextFormatting.GRAY + "Species: " + TextFormatting.YELLOW + pokemon.getSpecies().name);
        lore.add(TextFormatting.GRAY + "Size: " + TextFormatting.YELLOW + (boss.getBoss().getSize() * 100) + "%");
        if(boss.getBoss().getPokemon().getFormEnum() != EnumNoForm.NoForm) {
            lore.add(TextFormatting.GRAY + "Form: " + TextFormatting.YELLOW + (boss.getBoss().getPokemon().getFormEnum().getLocalizedName()));
        }
        lore.add("");

        String statLine1 = ""
                .concat(TextFormatting.RED + "HP: " + boss.getBoss().getStat(StatsType.HP))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.GOLD + "Atk: " + boss.getBoss().getStat(StatsType.Attack))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.GREEN + "Def: " + boss.getBoss().getStat(StatsType.Defence));
        lore.add(statLine1);

        String statLine2 = ""
                .concat(TextFormatting.BLUE + "SpA: " + boss.getBoss().getStat(StatsType.SpecialAttack))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.GREEN + "SpD: " + boss.getBoss().getStat(StatsType.SpecialDefence))
                .concat(TextFormatting.GRAY + " / ")
                .concat(TextFormatting.LIGHT_PURPLE + "Spe: " + boss.getBoss().getStat(StatsType.Speed));
        lore.add(statLine2);

        lore.add("");
        lore.add(TextFormatting.GRAY + "Moves:");

        Attack[] attacks = pokemon.getMoveset().attacks;
        String attackLine1 = ""
                .concat(TextFormatting.AQUA + ((attacks[0] != null) ? attacks[0].getMove().getAttackName() : "None"))
                .concat(" - " + TextFormatting.AQUA + ((attacks[1] != null) ? attacks[1].getMove().getAttackName() : "None"));
        lore.add(attackLine1);

        String attackLine2 = ""
                .concat(TextFormatting.AQUA + ((attacks[2] != null) ? attacks[2].getMove().getAttackName() : "None"))
                .concat(" - " + TextFormatting.AQUA + ((attacks[3] != null) ? attacks[3].getMove().getAttackName() : "None"));
        lore.add(attackLine2);

        return lore;
    }

}
