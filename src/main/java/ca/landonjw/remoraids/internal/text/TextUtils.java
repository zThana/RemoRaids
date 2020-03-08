package ca.landonjw.remoraids.internal.text;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Consumer;

public class TextUtils {

    public static ITextComponent addCallback(@Nonnull ITextComponent text,
                                             @Nonnull Consumer<ICommandSender> consumer){
        UUID callbackUUID = UUID.randomUUID();
        Callback.addCallback(callbackUUID, consumer);

        ClickEvent clickEvent = new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/" + Callback.CALLBACK_COMMAND + " " + callbackUUID.toString()
        );

        text.getStyle().setClickEvent(clickEvent);

        return text;
    }

}