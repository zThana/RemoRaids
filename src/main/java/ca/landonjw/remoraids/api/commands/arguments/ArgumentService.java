package ca.landonjw.remoraids.api.commands.arguments;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;

import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;

public class ArgumentService {

	private Map<String, IRaidsArgument> tokenToArgMap = Maps.newHashMap();

	/**
	 * Pattern for identifying RemoRaids arguments in a String of regular command arguments.
	 * All values will be trimmed and only specify the values ('--', '=', and quotations are not included).
	 * <p>
	 * This identifies the following groups:
	 * Group 0. Full Argument (for example, "--size=10.0")
	 * Group 1. Argument Token (for example, "size" in '--size=10.0')
	 * Group 2. Single-element Argument Value (for example, "10,0" in '--size=10.0')
	 * Group 3. Multi-element Argument Value (for example, "Example Name" in '--name-"Example Name"')
	 * This does NOT include the quotation marks, only the inner value.
	 */
	private final Pattern ARGUMENT_PATTERN = Pattern.compile("(?<=--)([a-zA-Z-]*)=?([a-zA-Z0-9._\\-,\\[\\]]*)?(\"[a-zA-Z0-9_\\- ,\\[\\]]*\")?");
	private final int FULL_ARGUMENT_GROUP = 0;
	private final int ARGUMENT_TOKEN_GROUP = 1;
	private final int SINGLE_ELEMENT_GROUP = 2;
	private final int MULTI_ELEMENT_GROUP = 3;

	public ArgumentService(Iterable<IRaidsArgument> defaults) {
		defaults.forEach(this::registerArgument);
	}

	public void delegateArguments(@Nonnull String input, @Nonnull IParsingContext context) {
		Matcher matcher = ARGUMENT_PATTERN.matcher(input);
		while (matcher.find()) {
			String token = matcher.group(ARGUMENT_TOKEN_GROUP);
			String singleElStr = matcher.group(SINGLE_ELEMENT_GROUP);
			String multiElStr = matcher.group(MULTI_ELEMENT_GROUP);

			if (!tokenToArgMap.containsKey(token))
				continue;
			tokenToArgMap.get(token).interpret(multiElStr == null ? singleElStr : multiElStr, context);
		}
	}

	public void registerArgument(@Nonnull IRaidsArgument argument) {
		for (String token : argument.getTokens()) {
			tokenToArgMap.put(token, argument);
		}
	}

	public String removeArguments(@Nonnull String input) {
		return ARGUMENT_PATTERN.matcher(input).replaceAll("");
	}

}
