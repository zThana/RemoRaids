package ca.landonjw.remoraids.implementation.rewards.contents.creators;

import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.rewards.contents.CurrencyContent;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class CurrencyContentCreator implements ICreatorUI<IRewardContent> {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List<IRewardContent> toAddTo) {
        new Creator(source, player, toAddTo);
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(Items.GOLD_NUGGET);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Currency Content";
    }

    class Creator {

        private IBossUI source;
        private EntityPlayerMP player;
        private List<IRewardContent> toAddTo;

        public Creator(IBossUI source, EntityPlayerMP player, List<IRewardContent> toAddTo){
            this.source = source;
            this.player = player;
            this.toAddTo = toAddTo;

            InventoryAPI.getInstance().closePlayerInventory(player);
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enter an amount (ie. '100') or 'cancel' to cancel!"));
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onMessage(ServerChatEvent event){
            String message = event.getMessage();
            if(!message.equalsIgnoreCase("cancel")){
                try{
                    int amount = Integer.parseInt(message);

                    if(amount > 0){
                        CurrencyContent content = new CurrencyContent(amount);
                        toAddTo.add(content);
                        source.open();
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                    else{
                        player.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect amount. Try again."));
                    }
                }
                catch (NumberFormatException e){
                    player.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect amount. Try again."));
                }
            }
            else{
                source.open();
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }

    }

}
