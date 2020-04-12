package ca.landonjw.remoraids.internal.config.readers;

import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.api.config.ConfigKey;
import ca.landonjw.remoraids.internal.api.config.ConfigKeyHolder;
import ca.landonjw.remoraids.internal.api.config.ConfigurationAdapter;
import ca.landonjw.remoraids.internal.api.config.keys.EnduringKey;

public class ForgeConfig implements Config {

	private Object[] values = null;

	private final ConfigurationAdapter adapter;
	private final ConfigKeyHolder holder;

	public ForgeConfig(ConfigurationAdapter adapter, ConfigKeyHolder holder) {
		this.adapter = adapter;
		this.holder = holder;
		this.load();
	}

	@Override
	public void reload() {
		this.adapter.reload();
		this.load();
	}

	@Override
	public synchronized void load() {
		// if this is a reload operation
		boolean reload = true;

		// if values are null, must be loading for the first time
		if (this.values == null) {
			this.values = new Object[holder.getSize()];
			reload = false;
		}

		for (ConfigKey<?> key : holder.getKeys().values()) {
			// don't reload enduring keys.
			if (reload && key instanceof EnduringKey) {
				continue;
			}

			// load the value for the key
			Object value = key.get(this.adapter);
			this.values[key.ordinal()] = value;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(ConfigKey<T> key) {
		return (T) this.values[key.ordinal()];
	}

}
