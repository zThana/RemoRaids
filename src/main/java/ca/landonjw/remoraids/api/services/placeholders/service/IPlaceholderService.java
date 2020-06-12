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
package ca.landonjw.remoraids.api.services.placeholders.service;

import ca.landonjw.remoraids.api.services.IService;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderParser;
import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A service with the soul purpose of providing a means of evaluation of an input token that will be
 * contextually populated.
 *
 * <p>It is recommended that any placeholder should be registered during the {@link net.minecraftforge.fml.common.event.FMLInitializationEvent}.</p>
 *
 * <p>This service will make no assumptions about how a placeholder parses/tokenizes a string
 * in order to determine what placeholders to use.</p>
 *
 * @author dualspiral
 * @author NickImpact
 * @since 1.0.0
 */
public interface IPlaceholderService extends IService.Catalog {

	/**
	 * Attempts to register a placeholder parser to the service.
	 *
	 * @param parser The parser we wish to register
	 * @throws IllegalArgumentException If the parser key is invalid or already registered to another parser
	 */
	void register(@NonNull IPlaceholderParser parser) throws IllegalArgumentException;

	/**
	 * Attempts to parse a token based on the given input.
	 *
	 * @see #parse(String, Supplier, Collection)
	 *
	 * @param token The token we are attempting to parse
	 * @return A parsed representation of the incoming token, or the direct input to signify a failure to parse
	 */
	default Optional<String> parse(@NonNull String token) {
		return this.parse(token, null);
	}

	/**
	 * Attempts to parse a token based on the given input.
	 *
	 * @see #parse(String, Supplier, Collection)
	 *
	 * @param token The token we are attempting to parse
	 * @param association The object we wish to associate with
	 * @return A parsed representation of the incoming token, or the direct input to signify a failure to parse
	 */
	default Optional<String> parse(@NonNull String token, Supplier<Object> association) {
		return this.parse(token, association, Collections.emptyList());
	}

	/**
	 * Attempts to parse a token based on the given input.
	 *
	 * @see #parse(String, Supplier, Collection)
	 *
	 * @param token The token we are attempting to parse
	 * @param association The object we wish to associate with
	 * @param arguments A collection of contextual arguments for the parser
	 * @return A parsed representation of the incoming token, or the direct input to signify a failure to parse
	 */
	default Optional<String> parse(@NonNull String token, Supplier<Object> association, String... arguments) {
		return this.parse(token, association, Lists.newArrayList(arguments));
	}

	/**
	 * Attempts to parse a token based on the given input. The context of this call will have, if non-null,
	 * an associated object as well as a collection of contextual arguments that a given parser will be able
	 * to interact with.
	 *
	 * <p>Should a parser be unavailable for the input token, or the parser failed to parse given the context,
	 * this call should return the direct input of the token.</p>
	 *
	 * @param token The token we are attempting to parse
	 * @param association The associated object for the parser
	 * @param arguments A collection of contextual arguments for the parser
	 * @return A parsed representation of the placeholder token, or the exact input (e.g. {{player:s}}, where
	 * the ":s" represents placing a space after the token if it is populated.
	 */
	Optional<String> parse(@NonNull String token, @Nullable Supplier<Object> association, @Nullable Collection<String> arguments);

	/**
	 * Prepares a {@link IPlaceholderContext} based on the given input values.
	 *
	 * @param association The object we want to associate this context with
	 * @param arguments The arguments we want to populate this context with
	 * @return A new placeholder context with the supplied values
	 */
	IPlaceholderContext contextualize(@Nullable Supplier<Object> association, @Nullable Collection<String> arguments);

	/**
	 * Attempts to locate a parser for the given token.
	 *
	 * @param token The lookup key
	 * @return An optionally wrapped placeholder parser if one is mapped to the input, empty otherwise
	 */
	Optional<IPlaceholderParser> getParser(@NonNull String token);

}
