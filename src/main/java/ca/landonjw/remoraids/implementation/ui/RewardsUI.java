package ca.landonjw.remoraids.implementation.ui;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.battles.IBossBattle;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.internal.inventory.api.*;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

//TODO: Complete

/**
 * A user interface that displays all rewards for the boss entity.
 * Can be viewed by: Registry > Options > Edit > Battle Settings > Edit Rewards
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RewardsUI extends BaseBossUI {

    /**
     * Default constructor.
     *
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     */
    public RewardsUI(@Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity) {
        super(player, bossEntity);
    }

    /** {@inheritDoc} */
    @Override
    public void open() {
        if(bossNotInBattle()){
            Button back = Button.builder()
                    .item(new ItemStack(Blocks.BARRIER))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                    .onClick(() -> {
                        BattleSettingsUI battleSettingsUI = new BattleSettingsUI(player, bossEntity);
                        battleSettingsUI.open();
                    })
                    .build();

            IBossBattle battle = RemoRaids.getBossAPI().getBossBattleRegistry().getBossBattle(bossEntity).get();

            List<IReward> rewards = battle.getDefeatRewards();
            List<Button> rewardButtons = new ArrayList<>();

            for(IReward reward : rewards){
                List<String> contentLore = new ArrayList<>();

                for(IRewardContent content : reward.getContents()){
                    contentLore.add("- " + content.getDescription());
                }

                Button rewardButton = Button.builder()
                        .item(new ItemStack(Blocks.CHEST))
                        .displayName(TextFormatting.AQUA + reward.getDescription())
                        .lore(contentLore)
                        .onClick(() -> {

                        })
                        .build();

                rewardButtons.add(rewardButton);
            }

            Button addReward = Button.builder()
                    .item(new ItemStack(PixelmonItems.pokemonEditor))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Add Reward")
                    .onClick(() -> {})
                    .build();

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
                    .set(1, 4, addReward)
                    .set(2, 1, prevPage)
                    .set(2, 7, nextPage)
                    .set(3, 4, back)
                    .build();

            Page page = Page.builder()
                    .template(template)
                    .dynamicContentArea(2, 2, 1, 5)
                    .dynamicContents(rewardButtons)
                    .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Reward Settings")
                    .build();

            page.forceOpenPage(player);
        }
    }

}