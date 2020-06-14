package ca.landonjw.remoraids.api.services.placeholders;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.IBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * An instance that can be translated into a String. This is based on technology dualspiral is writing
 * for Sponge API 7.3/8, and with our forge environment, it felt necessary to port that design over for
 * its effective power.
 *
 * <p>This class is meant to represent the context of an outgoing placeholder task. In other words, this class
 * is only responsible for supplying the necessary data that a parser will need. As such, it is unexpected for
 * a user to implement this interface, unless they desire to change functionality with their own service.</p>
 *
 * @author dualspiral
 * @author NickImpact
 * @author landonjw
 * @since 1.0.0
 */
public interface IPlaceholderContext extends IParsingContext {

    static Builder builder() {
        return IBossAPI.getInstance().getRaidRegistry().createBuilder(Builder.class);
    }

    /**
     * Represents additional arguments that a parser might make use of. These are not necessary at all,
     * but will allow a parser to work off additional context that might not be available with the associated
     * object.
     *
     * @return A list of arguments useful for contextual parsing, otherwise empty if none were supplied
     */
    Optional<List<String>> getArguments();

    interface Builder extends IBuilder<IPlaceholderContext, Builder> {

        Builder arguments(String... arguments);

        Builder arguments(Collection<String> arguments);

        Builder fromParsingContext(IParsingContext context);

    }

}
