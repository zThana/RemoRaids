package ca.landonjw.remoraids.internal.api;

import java.lang.reflect.Method;

import ca.landonjw.remoraids.api.BossAPIProvider;
import ca.landonjw.remoraids.api.IBossAPI;

public class APIRegistrationUtil {

	private static final Method REGISTER;
	private static final Method UNREGISTER;

	static {
		try {
			REGISTER = BossAPIProvider.class.getDeclaredMethod("register", IBossAPI.class);
			REGISTER.setAccessible(true);

			UNREGISTER = BossAPIProvider.class.getDeclaredMethod("unregister");
			UNREGISTER.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void register(IBossAPI service) {
		try {
			REGISTER.invoke(null, service);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void unregister() {
		try {
			UNREGISTER.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
