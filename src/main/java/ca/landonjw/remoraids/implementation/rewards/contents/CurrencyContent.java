package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
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
public class CurrencyContent implements IRewardContent {

    private String description;
    /** Amount of currency to be given. */
    private int amount;

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
     * @param amount      the amount of currency to be rewarded
     * @param description description for the reward content
     */
    public CurrencyContent(int amount, @Nullable String description){
        this(amount);
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public void give(EntityPlayerMP player) {
        Pixelmon.moneyManager.getBankAccount(player).ifPresent((account) -> {
            account.changeMoney(amount);
            account.updatePlayer();
            Config config = RemoRaids.getMessageConfig();
            IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
            IParsingContext context = IParsingContext.builder()
                    .add(EntityPlayerMP.class, () -> player)
                    .add(Integer.class, () -> amount)
                    .build();
            player.sendMessage(new TextComponentString(service.interpret(config.get(MessageConfig.CURRENCY_RECEIVED), context)));
        });
    }

    @Override
    public String getDescription() {
        if(description == null){
            Config config = RemoRaids.getMessageConfig();
            IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
            IParsingContext context = IParsingContext.builder()
                    .add(Integer.class, () -> amount)
                    .build();
            return service.interpret(config.get(MessageConfig.CURRENCY_REWARD_CONTENT_TITLE), context);
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(Items.GOLD_NUGGET);
        item.setStackDisplayName(getDescription());
        return item;
    }

}
