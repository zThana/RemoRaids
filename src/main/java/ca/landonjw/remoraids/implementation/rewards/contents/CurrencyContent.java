package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

/**
 * Currency that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class CurrencyContent implements IRewardContent {

    /** Amount of currency to be given. */
    private int amount;
    /** A custom description to be used for the reward. */
    private String customDescription;

    /**
     * Default constructor for the pokemon content.
     *
     * @param  amount the amount of currency to be rewarded
     */
    public CurrencyContent(int amount){
        this.amount = amount;
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param amount            the amount of currency to be rewarded
     * @param customDescription custom description for the reward content
     */
    public CurrencyContent(int amount, String customDescription){
        this.amount = amount;
        this.customDescription = customDescription;
    }

    /** {@inheritDoc} */
    @Override
    public void give(EntityPlayerMP player) {
        Pixelmon.moneyManager.getBankAccount(player).ifPresent((account) -> {
            account.changeMoney(amount);
            account.updatePlayer();
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "You have received " + amount + " dollars!"));
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        if(customDescription == null){
            String defaultDescription = "Currency: " + amount;
            return defaultDescription;
        }
        else{
            return customDescription;
        }
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Items.GOLD_NUGGET);
        item.setStackDisplayName(getDescription());
        return item;
    }

}
