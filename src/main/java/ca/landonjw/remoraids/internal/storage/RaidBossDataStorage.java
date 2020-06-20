package ca.landonjw.remoraids.internal.storage;

import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.util.gson.JObject;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class RaidBossDataStorage {

	private final File directory = new File("config/remoraids/data");

	public RaidBossDataStorage() {
		this.initialize();
		this.read();
	}

	private void initialize() {
		if(!this.directory.exists()) {
			this.directory.mkdirs();
		}
	}

	private void read() {
		for(File file : Objects.requireNonNull(directory.listFiles((d, s) -> s.toLowerCase().endsWith(".json")))) {
			try {
				IBossSpawner spawner = IBossCreator.initialize().deserialize(JObject.PRETTY.fromJson(new FileReader(file), JsonObject.class)).build();
				spawner.spawn(false);
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
}
