package ca.landonjw.remoraids.internal.services.placeholders.provided;

import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ItemStackPlaceholderParser implements IPlaceholderParser {

    @Override
    public String getKey() {
        return "itemstack";
    }

    @Override
    public Optional<String> parse(IPlaceholderContext context) {
        ItemStack itemStack = context.get(ItemStack.class).orElse(null);
        if(itemStack != null){
            List<String> arguments = context.getArguments().orElse(Collections.emptyList());
            if(arguments.size() == 1){
                switch(arguments.get(0).toLowerCase()){
                    case "name":
                        return Optional.of(itemStack.getDisplayName());
                    case "count":
                        return Optional.of("" + itemStack.getCount());
                }
            }
            return Optional.of(itemStack.getDisplayName());
        }
        return Optional.empty();
    }

}
