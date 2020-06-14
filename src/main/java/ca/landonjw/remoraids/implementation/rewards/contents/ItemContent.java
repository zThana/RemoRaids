package ca.landonjw.remoraids.implementation.rewards.contents;

import ca.landonjw.remoraids.RemoRaids;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * An item that is given from a {@link IReward}.
 *
 * @author landonjw
 * @since  1.0.0
 */
public class ItemContent implements IRewardContent {

    /** The description of the reward content. */
    private String description;
    /** The item to be rewarded. */
    private ItemStack item;

    /**
     * Default constructor for the item content.
     *
     * @param item the item to be rewarded
     * @throws NullPointerException if itemstack is null
     */
    public ItemContent(@Nonnull ItemStack item){
        this.item = Objects.requireNonNull(item);
    }

    /**
     * Constructor that allows for user to supply a custom description.
     *
     * @param item              the item to be rewarded
     * @param description description for the reward content
     * @throws NullPointerException if itemstack is null
     */
    public ItemContent(@Nonnull ItemStack item, @Nullable String description){
        this(item);
        this.description = description;
    }

    /** {@inheritDoc} **/
    @Override
    public void give(EntityPlayerMP player) {
        player.inventory.addItemStackToInventory(item.copy());
    }

    @Override
    public String getDescription() {
        if(description == null){
            Config config = RemoRaids.getMessageConfig();
            IMessageService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IMessageService.class);
            IParsingContext context = IParsingContext.builder()
                    .add(ItemStack.class, () -> item)
                    .build();
            return service.interpret(config.get(MessageConfig.ITEM_REWARD_CONTENT_TITLE), context);
        }
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public ItemStack toItemStack() {
        ItemStack copy = item.copy();
        copy.setStackDisplayName(getDescription());
        return copy;
    }

}
