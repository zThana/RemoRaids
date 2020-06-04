package ca.landonjw.remoraids.implementation.rewards.contents.creators;

import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.api.editor.IEditorUI;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.rewards.contents.CommandContent;
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

public class CommandContentCreator implements ICreatorUI<IRewardContent> {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List<IRewardContent> toAddTo) {
        new Creator(source, player, toAddTo);
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(Items.PAPER);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Command Content";
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
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enter a command (ie. 'say hello world') or 'cancel' to cancel!"));
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onMessage(ServerChatEvent event){
            String message = event.getMessage();
            CommandContent content = new CommandContent(message);
            toAddTo.add(content);
            MinecraftForge.EVENT_BUS.unregister(this);
        }

    }

}
