package ca.landonjw.remoraids.internal.services.placeholders;

import ca.landonjw.remoraids.api.services.placeholders.IPlaceholderContext;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PlaceholderContext implements IPlaceholderContext {

	private final Supplier<Object> association;
	private final List<String> arguments;

	private PlaceholderContext(Supplier<Object> association, List<String> arguments) {
		this.association = association;
		this.arguments = arguments;
	}

	@Override
	public Optional<Object> getAssociatedObject() {
		return Optional.ofNullable(this.association != null ? this.association.get() : null);
	}

	@Override
	public Optional<List<String>> getArguments() {
		return Optional.ofNullable(this.arguments);
	}

	public static class PlaceholderContextBuilder implements Builder {

		private Supplier<Object> association;
		private List<String> arguments;

		@Override
		public Builder setAssociatedObject(Supplier<Object> association) {
			this.association = association;
			return this;
		}

		@Override
		public Builder arguments(String... arguments) {
			this.arguments = Lists.newArrayList(arguments);
			return this;
		}

		@Override
		public Builder arguments(Collection<String> arguments) {
			this.arguments = Lists.newArrayList(arguments);
			return this;
		}

		@Override
		public Builder from(IPlaceholderContext input) {
			this.association = () -> input.getAssociatedObject().orElse(null);
			this.arguments = input.getArguments().orElse(null);
			return this;
		}

		@Override
		public IPlaceholderContext build() {
			return new PlaceholderContext(this.association, this.arguments);
		}

	}
}
