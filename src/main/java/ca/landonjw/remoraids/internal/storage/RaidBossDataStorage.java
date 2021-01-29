package ca.landonjw.remoraids.internal.storage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.util.gson.JObject;

public class RaidBossDataStorage {

	private final File directory = new File("config/remoraids/data/bosses");

	private List<IBossSpawner> spawners;

	public RaidBossDataStorage() {
		this.initialize();
	}

	private void initialize() {
		if (!this.directory.exists()) {
			this.directory.mkdirs();
		}
	}

	public List<IBossSpawner> getSpawners() {
		return this.spawners;
	}

	public void read() {
		this.spawners = Lists.newArrayList();
		for (File file : Objects.requireNonNull(directory.listFiles((d, s) -> s.toLowerCase().endsWith(".json")))) {
			try {
				spawners.add(IBossCreator.initialize().deserialize(JObject.PRETTY.fromJson(new FileReader(file), JsonObject.class)).build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void save(IBossSpawner spawner) {
		try {
			FileWriter fw = new FileWriter(new File(directory, spawner.getBoss().getUniqueId().toString() + ".json"));
			fw.write(spawner.serialize().toString());
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveAsync(IBossSpawner spawner) {
		CompletableFuture.runAsync(() -> this.save(spawner));
	}

	public void delete(IBossSpawner spawner) {
		this.spawners.removeIf(x -> x.getBoss().getUniqueId().equals(spawner.getBoss().getUniqueId()));
		CompletableFuture.runAsync(() -> {
			File target = new File(directory, spawner.getBoss().getUniqueId().toString() + ".json");
			if (target.exists()) {
				target.delete();
			}
		});
	}
}
