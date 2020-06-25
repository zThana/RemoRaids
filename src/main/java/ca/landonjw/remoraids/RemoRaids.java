package ca.landonjw.remoraids;

import ca.landonjw.remoraids.api.BossAPIProvider;
import ca.landonjw.remoraids.api.IBossAPI;
import ca.landonjw.remoraids.api.boss.IBoss;
import ca.landonjw.remoraids.api.boss.IBossCreator;
import ca.landonjw.remoraids.api.boss.IBossEntity;
import ca.landonjw.remoraids.api.messages.placeholders.IParsingContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderContext;
import ca.landonjw.remoraids.api.messages.placeholders.IPlaceholderParser;
import ca.landonjw.remoraids.api.messages.services.IMessageService;
import ca.landonjw.remoraids.api.messages.services.IPlaceholderService;
import ca.landonjw.remoraids.api.spawning.IBossSpawnLocation;
import ca.landonjw.remoraids.api.spawning.IBossSpawner;
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
import ca.landonjw.remoraids.internal.inventory.api.InventoryAPI;
import ca.landonjw.remoraids.internal.messages.placeholders.ParsingContext;
import ca.landonjw.remoraids.internal.messages.placeholders.PlaceholderContext;
import ca.landonjw.remoraids.internal.messages.placeholders.PlaceholderParser;
import ca.landonjw.remoraids.internal.messages.services.MessageService;
import ca.landonjw.remoraids.internal.messages.services.PlaceholderService;
import ca.landonjw.remoraids.internal.storage.RaidBossDataStorage;
import ca.landonjw.remoraids.internal.tasks.TaskTickListener;
import ca.landonjw.remoraids.internal.text.Callback;
import com.pixelmonmod.pixelmon.Pixelmon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.util.stream.Collectors;

@Mod(
        modid = RemoRaids.MOD_ID,
        name = RemoRaids.MOD_NAME,
        version = RemoRaids.VERSION,
        acceptableRemoteVersions = "*"
)
public class RemoRaids {

    public static final String MOD_ID = "remoraids";
    public static final String MOD_NAME = "RemoRaids";
    public static final String VERSION = "1.0.0";

    private static RemoRaids instance;

    public static final EventBus EVENT_BUS = new EventBus();
    public static final Logger logger = LogManager.getLogger(MOD_NAME);

    private static Config generalConfig;
    private static Config restraintsConfig;
    private static Config messageConfig;

    public static RaidBossDataStorage storage;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        instance = this;
        APIRegistrationUtil.register(new BossAPI());
        InventoryAPI.register();

        File directory = new File(event.getModConfigurationDirectory(), "remoraids");

        generalConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("general.conf")), new GeneralConfig());
        restraintsConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("restraints.conf")), new RestraintsConfig());
        messageConfig = new ForgeConfig(new ForgeConfigAdapter(directory.toPath().resolve("messages.conf")), new MessageConfig());

        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossCreator.class, BossCreator::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBoss.IBossBuilder.class, Boss.BossBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossSpawner.IRespawnData.IRespawnDataBuilder.class, RespawnData.RespawnDataBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IBossSpawnLocation.IBossSpawnLocationBuilder.class, BossSpawnLocation.BossSpawnLocationBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(ISpawnAnnouncement.ISpawnAnnouncementBuilder.class, SpawnAnnouncement.SpawnAnnouncementBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IParsingContext.Builder.class, ParsingContext.ParsingContextBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IPlaceholderContext.Builder.class, PlaceholderContext.PlaceholderContextBuilder::new);
        getBossAPI().getRaidRegistry().registerBuilderSupplier(IPlaceholderParser.Builder.class, PlaceholderParser.PlaceholderParserBuilder::new);

        getBossAPI().getRaidRegistry().registerSpawnerBuilderSupplier("default", BossSpawner.BossSpawnerBuilder::new);

        getBossAPI().getRaidRegistry().register(IMessageService.class, new MessageService());

        IPlaceholderService placeholders = new PlaceholderService();
        placeholders.registerDefaults();
        getBossAPI().getRaidRegistry().register(IPlaceholderService.class, placeholders);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new TaskTickListener());
        MinecraftForge.EVENT_BUS.register(new BossUpdateListener());

        Pixelmon.EVENT_BUS.register(new EngageListener());
        Pixelmon.EVENT_BUS.register(new BossDropListener());
        Pixelmon.EVENT_BUS.register(new StatueInteractListener());

        RemoRaids.EVENT_BUS.register(this);
        RemoRaids.EVENT_BUS.register(new RaidBossDeathListener());
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new Callback());
        event.registerServerCommand(new RaidsCommand());
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartedEvent event){
        storage = new RaidBossDataStorage();
    }

    @Mod.EventHandler
    public void onShutdown(FMLServerStoppingEvent event) {
        for(IBossSpawner spawner : IBossAPI.getInstance().getBossEntityRegistry().getAllBossEntities().stream().map(IBossEntity::getSpawner).collect(Collectors.toList())) {
            if(spawner.doesPersist()) {
                storage.save(spawner);
            }
            spawner.getBoss().getEntity().ifPresent(entity -> entity.getEntity().setDead());
        }
    }

    public static RemoRaids getInstance() {
        return instance;
    }

    public static IBossAPI getBossAPI(){
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

    public static InputStream getResourceStream(String path) {
        return RemoRaids.instance.getClass().getClassLoader().getResourceAsStream(path);
    }

}