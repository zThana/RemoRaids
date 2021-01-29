/*
 * This file is part of SpongeAPI, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ca.landonjw.remoraids.api.messages.placeholders;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * An instance that can be translated into a String. This is based on technology dualspiral is writing
 * for Sponge API 7.3/8, and with our forge environment, it felt necessary to port that design over for
 * its effective power.
 *
 * <p>
 * This class is meant to represent the context of an outgoing placeholder task. In other words, this class
 * is only responsible for supplying the necessary data that a parser will need. As such, it is unexpected for
 * a user to implement this interface, unless they desire to change functionality with their own service.
 * </p>
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
