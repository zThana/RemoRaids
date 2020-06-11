package ca.landonjw.remoraids.api.services.messaging;

import ca.landonjw.remoraids.api.services.IService;

import java.util.function.Supplier;

public interface IMessageService extends IService {

	String interpret(String input, Supplier<Object> association, String... arguments);

}
