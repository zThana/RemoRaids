package ca.landonjw.remoraids.implementation.rewards.contents.creators;

import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.implementation.rewards.contents.CommandContent;
import ca.landonjw.remoraids.implementation.rewards.contents.ItemContent;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemContentCreator implements ICreatorUI {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List toAddTo) {
        new Creator(source, player, toAddTo);
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(Items.DIAMOND);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Item Content";
    }

    class Creator {

        private IBossUI source;
        private EntityPlayerMP player;
        private List toAddTo;

        public Creator(IBossUI source, EntityPlayerMP player, List toAddTo){
            this.source = source;
            this.player = player;
            this.toAddTo = toAddTo;

            InventoryAPI.getInstance().closePlayerInventory(player);
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enter an item and amount (ie. 'dirt 64') or 'cancel' to cancel!"));
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onMessage(ServerChatEvent event){
            String message = event.getMessage();
            if(!message.equalsIgnoreCase("cancel")){
                String[] args = message.split(" ");
                if(args.length == 1){
                    Item item = Item.getByNameOrId(args[0]);
                    if(item != null){
                        ItemContent content = new ItemContent(new ItemStack(item, 1));
                        toAddTo.add(content);
                        source.open();
                        MinecraftForge.EVENT_BUS.unregister(this);
                    }
                    else{
                        player.sendMessage(new TextComponentString(TextFormatting.RED + "Item not found. Try again."));
                    }
                }
                else if(args.length == 2){
                    Item item = Item.getByNameOrId(args[0]);
                    if(item != null){
                        try{
                            int amount = Integer.parseInt(args[1]);
                            if(amount >= 1 && amount <= 64){
                                ItemContent content = new ItemContent(new ItemStack(item, amount));
                                toAddTo.add(content);
                                source.open();
                                MinecraftForge.EVENT_BUS.unregister(this);
                            }
                            else{
                                player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid amount given. Try again."));
                            }
                        }
                        catch (NumberFormatException e){
                            player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid amount given. Try again."));
                        }
                    }
                    else{
                        player.sendMessage(new TextComponentString(TextFormatting.RED + "Item not found. Try again."));
                    }
                }
                else{
                    player.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect amount of arguments. Try again."));
                }
            }
            else{
                source.open();
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }

    }

}
