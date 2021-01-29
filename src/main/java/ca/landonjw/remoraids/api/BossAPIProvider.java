package ca.landonjw.remoraids.api;

import org.checkerframework.checker.nullness.qual.NonNull;

public class BossAPIProvider {

	private static IBossAPI instance;

	public static @NonNull IBossAPI get() {
		if (instance == null) {
			throw new IllegalStateException("The Boss API is not loaded");
		}

		return instance;
	}

	static void register(IBossAPI api) {
		instance = api;
	}

	static void unregister() {
		instance = null;
	}

	private BossAPIProvider() {
		throw new UnsupportedOperationException("This class cannot be instantiated");
	}

}