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

import java.util.Optional;
import java.util.function.Function;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.util.IBuilder;

/**
 * Provides the logic of how to parse a placeholder token.
 *
 * @author dualspiral
 * @author NickImpact
 * @since 1.0.0
 */
public interface IPlaceholderParser {

	static Builder builder() {
		return IBossAPI.getInstance().getRaidRegistry().createBuilder(Builder.class);
	}

	/**
	 * Represents the key this parser is bound to. This key will be what is used to map this parser
	 * to an incoming context request.
	 *
	 * @return The key representing this parser
	 */
	String getKey();

	/**
	 * Attempts to parse a placeholder given a set of context.
	 *
	 * <p>
	 * This method should never return exceptionally. If this parser is unable to parse
	 * the placeholder given the incoming context, then it should simply return {@link Optional#empty()}
	 * to signify its failure.
	 * </p>
	 *
	 * @param context The incoming context this parser will use in an attempt to read the data
	 * @return An optionally filled value indicating the result of the parse operation
	 */
	Optional<String> parse(IPlaceholderContext context);

	interface Builder extends IBuilder<IPlaceholderParser, Builder> {

		Builder key(String key);

		Builder parser(Function<IPlaceholderContext, Optional<String>> parser);

	}

}
