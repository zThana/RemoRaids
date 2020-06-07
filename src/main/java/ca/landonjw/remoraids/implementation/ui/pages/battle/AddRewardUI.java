package ca.landonjw.remoraids.implementation.ui.pages.battle;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IBossUIRegistry;
import ca.landonjw.remoraids.api.ui.ICreatorUI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.internal.inventory.api.*;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AddRewardUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public AddRewardUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
        super(source, player, bossEntity);
    }

    @Override
    public void open() {
        Button back = Button.builder()
                .item(new ItemStack(Blocks.BARRIER))
                .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                .onClick(() -> {
                    source.open();
                })
                .build();

        List<Button> creatorButtons = new ArrayList<>();
        List<IReward> bossRewardsList = RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get().getDefeatRewards();
        for(ICreatorUI<IReward> creator : IBossUIRegistry.getInstance().getRewardCreators()){
            Button creatorButton = Button.builder()
                    .item(creator.getCreatorIcon())
                    .displayName(creator.getCreatorTitle())
                    .onClick(() -> creator.open(this, player, bossRewardsList))
                    .build();

            creatorButtons.add(creatorButton);
        }

        Button prevPage = Button.builder()
                .item(new ItemStack(PixelmonItems.LtradeHolderLeft))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Previous Page")
                .type(ButtonType.PreviousPage)
                .build();

        Button nextPage = Button.builder()
                .item(new ItemStack(PixelmonItems.tradeHolderRight))
                .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Next Page")
                .type(ButtonType.NextPage)
                .build();


        Template template = Template.builder(5)
                .line(LineType.Horizontal, 1, 0, 9, getWhiteFiller())
                .line(LineType.Horizontal, 3, 0, 9, getWhiteFiller())
                .border(0,0, 5,9, getBlueFiller())
                .set(0, 4, getBossButton())
                .set(2, 1, prevPage)
                .set(2, 7, nextPage)
                .set(3, 4, back)
                .build();

        Page page = Page.builder()
                .template(template)
                .dynamicContentArea(2, 2, 1, 5)
                .dynamicContents(creatorButtons)
                .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Add Reward")
                .build();

        page.forceOpenPage(player);
    }

}
