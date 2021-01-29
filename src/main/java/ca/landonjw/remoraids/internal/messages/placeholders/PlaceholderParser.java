package ca.landonjw.remoraids.internal.messages.placeholders;

import java.util.Optional;
import java.util.function.Function;

import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;

public class PlaceholderParser implements IPlaceholderParser {

	private final String key;
	private final Function<IPlaceholderContext, Optional<String>> parser;

	private PlaceholderParser(String key, Function<IPlaceholderContext, Optional<String>> parser) {
		this.key = key;
		this.parser = parser;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public Optional<String> parse(IPlaceholderContext context) {
		return this.parser.apply(context);
	}

	public static class PlaceholderParserBuilder implements Builder {

		private String key;
		private Function<IPlaceholderContext, Optional<String>> parser;

		@Override
		public Builder key(String key) {
			this.key = key;
			return this;
		}

		@Override
		public Builder parser(Function<IPlaceholderContext, Optional<String>> parser) {
			this.parser = parser;
			return this;
		}

		@Override
		public Builder from(IPlaceholderParser input) {
			this.key = input.getKey();
			this.parser = context -> Optional.empty();
			return this;
		}

		@Override
		public IPlaceholderParser build() {
			return new PlaceholderParser(this.key, this.parser);
		}

	}

}
