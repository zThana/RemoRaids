package ca.landonjw.remoraids.internal.services;

import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.services.messaging.IMessageService;
import ca.landonjw.remoraids.api.services.placeholders.service.IPlaceholderService;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageService implements IMessageService {

	private static final Pattern PLACEHOLDER_LOCATOR = Pattern.compile("(^[^{]+)?([{][{][^{} ]+[}][}])(.+)?");

	@Override
	public String interpret(String input, Supplier<Object> association) {
		IPlaceholderService service = IBossAPI.getInstance().getRaidRegistry().getUnchecked(IPlaceholderService.class);

		StringBuilder output = new StringBuilder();
		String working = input;

		while(!working.isEmpty()) {
			Matcher matcher = PLACEHOLDER_LOCATOR.matcher(working);
			if(matcher.find()) {
				String[] token = matcher.group(2).replace("{", "").replace("}", "").toLowerCase().split("\\|");

				String placeholder = token[0];
				String[] arguments = token[1].split(",");
				Optional<String> result = service.parse(placeholder, association, arguments);

				if(matcher.group(1) != null) {
					output.append(matcher.group(1));
					working = working.replaceFirst("^[^{]+", "");
				}

				if(result.isPresent()) {
					output.append(result.get());
				} else {
					output.append(matcher.group(2));
				}

				working = working.replaceFirst("[{][{][^{} ]+[}][}]", "");
			} else {
				output.append(working);
				break;
			}
		}

		return output.toString().replace('&', '\u00a7');
	}

}
