package ca.landonjw.remoraids.implementation.rewards.contents.creators;

import ca.landonjw.remoraids.api.editor.IBossUI;
import ca.landonjw.remoraids.api.editor.ICreatorUI;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.implementation.rewards.contents.PokemonContent;
import ca.landonjw.remoraids.internal.inventory.api.Button;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.config.PixelmonItemsPokeballs;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;

public class PokemonContentCreator implements ICreatorUI<IRewardContent> {

    @Override
    public void open(@Nonnull IBossUI source, @Nonnull EntityPlayerMP player, @Nonnull List<IRewardContent> toAddTo) {
        new Creator(source, player, toAddTo);
    }

    @Override
    public ItemStack getCreatorIcon() {
        return new ItemStack(PixelmonItemsPokeballs.pokeBall);
    }

    @Override
    public String getCreatorTitle() {
        return TextFormatting.AQUA + "" + TextFormatting.BOLD + "Pokemon Content";
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
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Enter a pokemon spec (ie. 'mawile s') or 'cancel' to cancel!"));
            MinecraftForge.EVENT_BUS.register(this);
        }

        @SubscribeEvent
        public void onMessage(ServerChatEvent event){
            String message = event.getMessage();
            if(!message.equalsIgnoreCase("cancel")){
                PokemonSpec spec = new PokemonSpec(message);
                if(spec.name != null){
                    PokemonContent content = new PokemonContent(spec);
                    toAddTo.add(content);
                    source.open();
                    MinecraftForge.EVENT_BUS.unregister(this);
                }
                else{
                    player.sendMessage(new TextComponentString(TextFormatting.RED + "Invalid pokemon given. Try again."));
                }
            }
            else{
                source.open();
                MinecraftForge.EVENT_BUS.unregister(this);
            }
        }

    }

}
