package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

/**
 * Currency that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class CurrencyContent extends RewardContentBase {

    /** Amount of currency to be given. */
    private int amount;

    /**
     * Default constructor for the pokemon content.
     *
     * @param  amount the amount of currency to be rewarded
     */
    public CurrencyContent(int amount){
        super("Currency: " + amount);
        this.amount = amount;
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param amount      the amount of currency to be rewarded
     * @param description description for the reward content
     */
    public CurrencyContent(int amount, @Nullable String description){
        super(description == null ? "Currency: " + amount : description);
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
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Items.GOLD_NUGGET);
        item.setStackDisplayName(getDescription());
        return item;
    }

}
