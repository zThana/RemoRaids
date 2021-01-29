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
package ca.landonjw.remoraids.api.messages.services;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;

/**
 * A service with the soul purpose of providing a means of evaluation of an input token that will be
 * contextually populated.
 *
 * <p>
 * It is recommended that any placeholder should be registered during the {@link net.minecraftforge.fml.common.event.FMLInitializationEvent}.
 * </p>
 *
 * <p>
 * This service will make no assumptions about how a placeholder parses/tokenizes a string
 * in order to determine what placeholders to use.
 * </p>
 *
 * @author dualspiral
 * @author NickImpact
 * @author landonjw
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
	 * @see #parse(String, IParsingContext, Collection)
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
	 * @see #parse(String, IParsingContext, Collection)
	 *
	 * @param token   The token we are attempting to parse
	 * @param context The context of the parsing, which may include objects necessary for parsing information
	 * @return A parsed representation of the incoming token, or the direct input to signify a failure to parse
	 */
	default Optional<String> parse(@NonNull String token, @Nullable IParsingContext context) {
		return this.parse(token, context, Collections.emptyList());
	}

	/**
	 * Attempts to parse a token based on the given input.
	 *
	 * @see #parse(String, IParsingContext, Collection)
	 *
	 * @param token     The token we are attempting to parse
	 * @param context   The context of the parsing, which may include objects necessary for parsing information
	 * @param arguments A collection of contextual arguments for the parser
	 * @return A parsed representation of the incoming token, or the direct input to signify a failure to parse
	 */
	default Optional<String> parse(@NonNull String token, @Nullable IParsingContext context, @Nullable String... arguments) {
		return this.parse(token, context, arguments != null ? Lists.newArrayList(arguments) : Collections.emptyList());
	}

	/**
	 * Attempts to parse a token based on the given input. The context of this call will have, if non-null,
	 * one or more objects as well as a collection of contextual arguments that a given parser
	 * will be able to interact with.
	 *
	 * <p>
	 * Should a parser be unavailable for the input token, or the parser failed to parse given the context,
	 * this call should return the direct input of the token.
	 * </p>
	 *
	 * @param token     The token we are attempting to parse
	 * @param context   The context of the parsing, which may include objects necessary for parsing information
	 * @param arguments A collection of contextual arguments for the parser
	 * @return A parsed representation of the placeholder token, or the exact input (e.g. {{player:s}}, where
	 *         the ":s" represents placing a space after the token if it is populated.
	 */
	Optional<String> parse(@NonNull String token, @Nullable IParsingContext context, @Nullable Collection<String> arguments);

	/**
	 * Generates a set of context that'll be used for placeholder parsing.
	 *
	 * @param context   The context that we've established
	 * @param arguments The arguments that'll be supplied to the placeholder parser
	 * @return A new context specifying a set of arguments and source objects
	 */
	IPlaceholderContext contextualize(@Nullable IParsingContext context, @Nullable Collection<String> arguments);

	/**
	 * Attempts to locate a parser for the given token.
	 *
	 * @param token The lookup key
	 * @return An optionally wrapped placeholder parser if one is mapped to the input, empty otherwise
	 */
	Optional<IPlaceholderParser> getParser(@NonNull String token);

}
