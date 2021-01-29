package ca.landonjw.remoraids.api.commands.arguments;

import java.util.List;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public interface IRaidsArgument {

	List<String> getTokens();

	void interpret(@Nonnull String value, @Nonnull IParsingContext context) throws IllegalArgumentException;

}