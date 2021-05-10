package ca.landonjw.remoraids.implementation.commands.create.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.commands.arguments.IRaidsArgument;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class HeldItemArgument implements IRaidsArgument {

    @Override
    public List<String> getTokens() {
        return Lists.newArrayList("helditem", "hi");
    }

    @Override
    public void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException {
        if (context.getAssociation(IBoss.IBossBuilder.class).isPresent()) {
            IBoss.IBossBuilder builder = context.getAssociation(IBoss.IBossBuilder.class).get();

            String itemName = "pixelmon:" + value;
            ItemStack heldItem = GameRegistry.makeItemStack(itemName, 0, 1, null);
            if(heldItem.isEmpty()) {
                throw new IllegalArgumentException("Held item not found");
            }

            builder.heldItem(heldItem);
        }
    }

}
