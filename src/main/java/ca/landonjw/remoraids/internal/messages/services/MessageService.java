package ca.landonjw.remoraids.internal.messages.services;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.messages.services.IPlaceholderService;

public class MessageService implements IMessageService {

	private static final Pattern PLACEHOLDER_LOCATOR = Pattern.compile("(^[^{]+)?([{][^{} ]+[}])(.+)?");

	@Override
	public String interpret(@Nonnull String input, IParsingContext context) {
		IPlaceholderService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IPlaceholderService.class);

		StringBuilder output = new StringBuilder();
		String working = input;

		while (!working.isEmpty()) {
			Matcher matcher = PLACEHOLDER_LOCATOR.matcher(working);
			if (matcher.find()) {
				String[] token = matcher.group(2).replace("{", "").replace("}", "").toLowerCase().split("\\|");

				String placeholder = token[0];
				String[] arguments = null;
				if (token.length > 1) {
					arguments = token[1].split(",");
				}

				if (matcher.group(1) != null) {
					output.append(matcher.group(1));
					working = working.replaceFirst("^[^{]+", "");
				}

				Optional<String> result = service.parse(placeholder, context, arguments);
				if (result.isPresent()) {
					output.append(result.get());
				} else {
					output.append(matcher.group(2));
				}

				working = working.replaceFirst("[{][^{} ]+[}]", "");
			} else {
				output.append(working);
				break;
			}
		}

		return output.toString().replace('&', '\u00a7');
	}

}
