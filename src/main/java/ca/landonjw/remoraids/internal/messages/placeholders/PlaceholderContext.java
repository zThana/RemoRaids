package ca.landonjw.remoraids.internal.messages.placeholders;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;

public class PlaceholderContext extends ParsingContext implements IPlaceholderContext {

	private final List<String> arguments;

	private PlaceholderContext(Map<Class<?>, List<Supplier<?>>> contextObjects, List<String> arguments) {
		super(contextObjects);
		this.arguments = arguments;
	}

	@Override
	public Optional<List<String>> getArguments() {
		return (arguments != null) ? Optional.of(Lists.newArrayList(arguments)) : Optional.empty();
	}

	public static class PlaceholderContextBuilder implements IPlaceholderContext.Builder {

		private List<String> arguments = Collections.emptyList();
		private Map<Class<?>, List<Supplier<?>>> contextObjects = Collections.emptyMap();

		@Override
		public PlaceholderContextBuilder arguments(String... arguments) {
			this.arguments = Lists.newArrayList(arguments);
			return this;
		}

		@Override
		public PlaceholderContextBuilder arguments(Collection<String> arguments) {
			this.arguments = Lists.newArrayList(arguments);
			return this;
		}

		@Override
		public PlaceholderContextBuilder fromParsingContext(IParsingContext context) {
			this.contextObjects = context.getAllAssociations();
			return this;
		}

		@Override
		public IPlaceholderContext.Builder from(IPlaceholderContext input) {
			this.arguments = input.getArguments().orElse(Collections.emptyList());
			this.contextObjects = input.getAllAssociations();
			return this;
		}

		@Override
		public IPlaceholderContext build() {
			return new PlaceholderContext(contextObjects, arguments);
		}

	}

}
