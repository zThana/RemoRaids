package ca.landonjw.remoraids;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pixelmonmod.pixelmon.Pixelmon;

import ca.landonjw.remoraids.api.BossAPIProvider;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.data.ISerializationFactories;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.messages.services.IPlaceholderService;
import ca.landonjw.remoraids.api.rewards.IReward;
import ca.landonjw.remoraids.api.rewards.contents.IRewardContent;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
import ca.landonjw.remoraids.api.spawning.IRespawnData;
import ca.landonjw.remoraids.api.spawning.ISpawnAnnouncement;
import ca.landonjw.remoraids.implementation.BossAPI;
import ca.landonjw.remoraids.implementation.boss.Boss;
import ca.landonjw.remoraids.implementation.boss.BossCreator;
import ca.landonjw.remoraids.implementation.commands.RaidsCommand;
import ca.landonjw.remoraids.implementation.listeners.forge.BossUpdateListener;
import ca.landonjw.remoraids.implementation.listeners.pixelmon.BossDropListener;
import ca.landonjw.remoraids.implementation.listeners.pixelmon.EngageListener;
import ca.landonjw.remoraids.implementation.listeners.pixelmon.StatueInteractListener;
import ca.landonjw.remoraids.implementation.listeners.raids.RaidBossDeathListener;
import ca.landonjw.remoraids.implementation.rewards.contents.factory.RewardContentSerializationFactory;
import ca.landonjw.remoraids.implementation.rewards.factory.RewardSerializationFactory;
import ca.landonjw.remoraids.implementation.spawning.BossSpawnLocation;
import ca.landonjw.remoraids.implementation.spawning.BossSpawner;
import ca.landonjw.remoraids.implementation.spawning.announcements.SpawnAnnouncement;
import ca.landonjw.remoraids.implementation.spawning.respawning.RespawnData;
import ca.landonjw.remoraids.internal.api.APIRegistrationUtil;
import ca.landonjw.remoraids.internal.api.config.Config;
import ca.landonjw.remoraids.internal.config.GeneralConfig;
import ca.landonjw.remoraids.internal.config.MessageConfig;
import ca.landonjw.remoraids.internal.config.RestraintsConfig;
import ca.landonjw.remoraids.internal.config.readers.ForgeConfig;
import ca.landonjw.remoraids.internal.config.readers.ForgeConfigAdapter;
import ca.landonjw.remoraids.internal.factories.InternalRaidsDeserializerFactories;
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import ca.landonjw.remoraids.internal.messages.placeholders.ParsingContext;
import ca.landonjw.remoraids.internal.messages.placeholders.PlaceholderContext;
import ca.landonjw.remoraids.internal.messages.placeholders.PlaceholderParser;
import ca.landonjw.remoraids.internal.messages.services.MessageService;
import ca.landonjw.remoraids.internal.messages.services.PlaceholderService;
import ca.landonjw.remoraids.internal.storage.RaidBossDataStorage;
import ca.landonjw.remoraids.internal.storage.RaidsDataPersistence;
import ca.landonjw.remoraids.internal.tasks.TaskTickListener;
import ca.landonjw.remoraids.internal.text.Callback;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = RemoRaids.MOD_ID, name = RemoRaids.MOD_NAME, version = RemoRaids.VERSION, acceptableRemoteVersions = "*")
public class RemoRaids {

    public static final String MOD_ID = "remoraids";
    public static final String MOD_NAME = "RemoRaids";
    public static final String VERSION = "@VERSION@";

    private static RemoRaids instance;

    public static final EventBus EVENT_BUS = new EventBus();
    public static final Logger logger = LogManager.getLogger(MOD_NAME);

    private static Config generalConfig;
    private static Config restraintsConfig;
    private static Config messageConfig;

    public static RaidBossDataStorage storage;

    private static RaidsDataPersistence raidsPersistence = new RaidsDataPersistence();
    private static ISerializationFactories deserializerFactories = new InternalRaidsDeserializerFactories();

    private File directory;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        APIRegistrationUtil.register(new BossAPI());
        InventoryAPI.register();

        this.directory = new File(event.getModConfigurationDirectory(), "remoraids");
        generalConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("general.conf")), new GeneralConfig());
        restraintsConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("restraints.conf")), new RestraintsConfig());
        messageConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("messages.conf")), new MessageConfig());

        registerBuilders();
        registerFactories();

        getBossAPI().getRaidRegistry().register(IMessageService.class, new MessageService());

        IPlaceholderService placeholders = new PlaceholderService();
        placeholders.registerDefaults();
        getBossAPI().getRaidRegistry().register(IPlaceholderService.class, placeholders);
    }

    private void registerBuilders() {
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossCreator.class, BossCreator::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBoss.IBossBuilder.class, Boss.BossBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossSpawner.IBossSpawnerBuilder.class, BossSpawner.BossSpawnerBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IRespawnData.IRespawnDataBuilder.class, RespawnData.RespawnDataBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossSpawnLocation.IBossSpawnLocationBuilder.class, BossSpawnLocation.BossSpawnLocationBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(ISpawnAnnouncement.ISpawnAnnouncementBuilder.class, SpawnAnnouncement.SpawnAnnouncementBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IParsingContext.Builder.class, ParsingContext.ParsingContextBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IPlaceholderContext.Builder.class, PlaceholderContext.PlaceholderContextBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IPlaceholderParser.Builder.class, PlaceholderParser.PlaceholderParserBuilder::new);
        getBossAPI().getRaidRegistry().registerSpawnerBuilderSupplier("default", BossSpawner.BossSpawnerBuilder::new);
    }

    private void registerFactories() {
        deserializerFactories.registerFactory(IRewardContent.class, new RewardContentSerializationFactory());
        deserializerFactories.registerFactory(IReward.class, new RewardSerializationFactory());
        // TODO: Battle restraint factory registration
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new TaskTickListener());
        MinecraftForge.EVENT_BUS.register(new BossUpdateListener());
        MinecraftForge.EVENT_BUS.register(this);

        Pixelmon.EVENT_BUS.register(new EngageListener());
        Pixelmon.EVENT_BUS.register(new BossDropListener());
        Pixelmon.EVENT_BUS.register(new StatueInteractListener());

        RemoRaids.EVENT_BUS.register(this);
        RemoRaids.EVENT_BUS.register(new RaidBossDeathListener());

        storage = new RaidBossDataStorage();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldEvent.Load event) {
        if (storage.getSpawners() == null) {
            storage.read();
        }
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new Callback());
        event.registerServerCommand(new RaidsCommand());
    }

    @Mod.EventHandler
    public void onShutdown(FMLServerStoppingEvent event) {
        for (IBossSpawner spawner : IBossAPI.getInstance().getBossEntityRegistry().getAllBossEntities().stream().map(IBossEntity::getSpawner).collect(Collectors.toList())) {
            if (spawner.doesPersist()) {
                storage.save(spawner);
            }
            spawner.getBoss().getEntity().ifPresent(IBossEntity::despawn);
        }
    }

    public void reload() {
        generalConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("general.conf")), new GeneralConfig());
        restraintsConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("restraints.conf")), new RestraintsConfig());
        messageConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("messages.conf")), new MessageConfig());
    }

    public static RemoRaids getInstance() {
        return instance;
    }

    public static IBossAPI getBossAPI() {
        return BossAPIProvider.get();
    }

    public static Config getGeneralConfig() {
        return generalConfig;
    }

    public static Config getMessageConfig() {
        return messageConfig;
    }

    public static Config getRestraintsConfig() {
        return restraintsConfig;
    }

    public static ISerializationFactories getDeserializerFactories() {
        return deserializerFactories;
    }

    public static RaidsDataPersistence getRaidsPersistence() {
        return raidsPersistence;
    }

    public static InputStream getResourceStream(String path) {
        return RemoRaids.instance.getClass().getClassLoader().getResourceAsStream(path);
    }

}