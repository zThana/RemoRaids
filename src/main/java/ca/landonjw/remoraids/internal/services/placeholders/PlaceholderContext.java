package ca.landonjw.remoraids.internal.services.placeholders;

import ca.landonjw.remoraids.api.services.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Supplier;

public class PlaceholderContext extends ParsingContext implements IPlaceholderContext {

	private final List<String> arguments;

	private PlaceholderContext(Map<Class, List<Supplier>> contextObjects, List<String> arguments){
		super(contextObjects);
		this.arguments = arguments;
	}

	@Override
	public Optional<List<String>> getArguments() {
		return (arguments != null) ? Optional.of(Lists.newArrayList(arguments)) : Optional.empty();
	}

	public static class PlaceholderContextBuilder implements IPlaceholderContext.Builder {

		private List<String> arguments = Collections.emptyList();
		private Map<Class, List<Supplier>> contextObjects = Collections.emptyMap();

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
			this.contextObjects = context.getAll();
			return this;
		}

		@Override
		public IPlaceholderContext.Builder from(IPlaceholderContext input) {
			this.arguments = input.getArguments().orElse(Collections.emptyList());
			this.contextObjects = input.getAll();
			return this;
		}

		@Override
		public IPlaceholderContext build() {
			return new PlaceholderContext(contextObjects, arguments);
		}

	}

}
