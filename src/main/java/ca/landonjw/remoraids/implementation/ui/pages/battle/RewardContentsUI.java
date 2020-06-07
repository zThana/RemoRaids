package ca.landonjw.remoraids.implementation.ui.pages.battle;

import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.ui.IBossUI;
import ca.landonjw.remoraids.api.ui.IBossUIRegistry;
import ca.landonjw.remoraids.api.ui.IEditorUI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.ui.pages.BaseBossUI;
import ca.landonjw.remoraids.implementation.ui.pages.battle.AddRewardContentUI;
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

/**
 * A user interface to view all reward contents contained in a given {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class RewardContentsUI extends BaseBossUI {

    /** Reward to view all reward contents of */
    private IReward reward;

    /**
     * Default constructor.
     *
     * @param source     the user interface that opened this user interface, may be null if no previous UI opened this
     * @param player     the player using the user interface
     * @param bossEntity the boss entity being edited
     * @param reward     reward to view all reward contents of
     */
    public RewardContentsUI(@Nullable IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull IBossEntity bossEntity, @Nonnull IReward reward) {
        super(source, player, bossEntity);
        this.reward = reward;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void open() {
        if(bossNotInBattle()){
            Button back = Button.builder()
                    .item(new ItemStack(Blocks.BARRIER))
                    .displayName(TextFormatting.RED + "" + TextFormatting.BOLD + "Go Back")
                    .onClick(() -> {
                        source.open();
                    })
                    .build();

            List<Button> rewardContentButtons = new ArrayList<>();

            for(IRewardContent content : reward.getContents()){
                Button rewardContentButton = Button.builder()
                        .item(content.toItemStack())
                        .displayName(TextFormatting.AQUA + content.getDescription())
                        .onClick(() -> {
                            IBossUIRegistry registry = IBossUIRegistry.getInstance();
                            if(registry.getRewardContentEditor(content.getClass()).isPresent()){
                                IEditorUI editor = registry.getRewardContentEditor(content.getClass()).get();
                                editor.open(this, player, content);
                            }
                        })
                        .build();

                rewardContentButtons.add(rewardContentButton);
            }

            Button addRewardContent = Button.builder()
                    .item(new ItemStack(PixelmonItems.pokemonEditor))
                    .displayName(TextFormatting.AQUA + "" + TextFormatting.BOLD + "Add Reward Content")
                    .onClick(() -> {
                        AddRewardContentUI addContentUI = new AddRewardContentUI(this, player, bossEntity, reward);
                        addContentUI.open();
                    })
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
                    .set(1, 4, addRewardContent)
                    .set(2, 1, prevPage)
                    .set(2, 7, nextPage)
                    .set(3, 4, back)
                    .build();

            Page page = Page.builder()
                    .template(template)
                    .dynamicContentArea(2, 2, 1, 5)
                    .dynamicContents(rewardContentButtons)
                    .title(TextFormatting.BLUE + "" + TextFormatting.BOLD + "Reward Settings")
                    .build();

            page.forceOpenPage(player);
        }
    }

}
