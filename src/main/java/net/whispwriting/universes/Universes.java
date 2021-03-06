package net.whispwriting.universes;

import net.whispwriting.universes.en.files.*;
import net.whispwriting.universes.en.guis.WorldSettingsUI;
import net.whispwriting.universes.en.tasks.SetSpawnFlagsTask;
import net.whispwriting.universes.en.utils.Generator;
import net.whispwriting.universes.en.utils.PlayersWhoCanConfirm;
import net.whispwriting.universes.en.commands.*;
import net.whispwriting.universes.en.events.*;
import net.whispwriting.universes.lang.LangSwap;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.bukkit.Difficulty.*;

public final class Universes extends JavaPlugin {

    public static WorldSettingsFile worlds;
    public static WorldListFile worldListFile;
    public PlayerSettingsFile playerSettings;
    public ConfigFile config;
    private SpawnFile spawnFile = new SpawnFile(this);
    public net.whispwriting.universes.es.files.PlayerSettingsFile playerSettingsEs;
    public List<PlayersWhoCanConfirm> players = new ArrayList<>();
    public List<net.whispwriting.universes.es.utils.PlayersWhoCanConfirm> playersEs = new ArrayList<>();
    private LangFile lang = new LangFile(this);
    public static net.whispwriting.universes.es.files.WorldSettingsFile worldSettingsEs;
    public static net.whispwriting.universes.es.files.WorldListFile worldListFileEs;
    private KitsFile kitsFile = new KitsFile(this);
    private String defaultWorld;
    public Logger log;

    @Override
    public void onEnable() {
        log = getLogger();
        lang.createConfig();
        lang.get().options().copyDefaults(true);
        lang.save();

        String langStr = lang.get().getString("lang");

        //if (langStr.equals("en")) {
            config = new ConfigFile(this);
            config.createConfig();
            config.get().options().copyDefaults(true);
            config.save();
            spawnFile.get().options().copyDefaults(true);
            worldListFile = new WorldListFile(this);
            worlds = new WorldSettingsFile(this);
            loadWorlds();
            //setSpawnFlags();
            createWorldConfig();
            WorldSettingsUI.init();

            List<World> worldList = Bukkit.getWorlds();
            String gameMode = worlds.get().getString("worlds."+worldList.get(0).getName()+".gameMode");
            if (gameMode == null){
                LangSwap.swapToEnglish(this);
                worldListFile = new WorldListFile(this);
                worlds = new WorldSettingsFile(this);
            }

            this.getCommand("universecreate").setExecutor(new CreateCommand(this));
            this.getCommand("universeimport").setExecutor(new ImportCommand(this));
            this.getCommand("universelist").setExecutor(new ListWorldsCommand());
            this.getCommand("universeteleport").setExecutor(new TeleportCommand(this));
            this.getCommand("universeoverride").setExecutor(new OverrideCommand(this, playerSettings));
            this.getCommand("universemodify").setExecutor(new ModifyCommand(this, worlds, playerSettings));
            this.getCommand("universedelete").setExecutor(new DeleteCommand(this, worldListFile, worlds));
            this.getCommand("universeunload").setExecutor(new UnloadCommand(worldListFile));
            this.getCommand("confirm").setExecutor(new ConfirmCommand(this, worldListFile, worlds));
            this.getCommand("cancel").setExecutor(new CancelCommand(this));
            this.getCommand("universehelp").setExecutor(new HelpCommand());
            this.getCommand("ur").setExecutor(new ReloadCommand(this));
            this.getCommand("universekits").setExecutor(new KitCommand(this));
            this.getCommand("usetspawn").setExecutor(new FirstJoinSpawnCommand(spawnFile));
            this.getCommand("universespawn").setExecutor(new SpawnCommand(this));

            Bukkit.getPluginManager().registerEvents(new TeleportEvent(playerSettings, this, kitsFile), this);
            Bukkit.getPluginManager().registerEvents(new RespawnEvent(this), this);
            Bukkit.getPluginManager().registerEvents(new JoinEvent(this, spawnFile), this);
            Bukkit.getPluginManager().registerEvents(new FlyEvent(this, playerSettings), this);
            Bukkit.getPluginManager().registerEvents(new PVPEvent(this), this);
            Bukkit.getPluginManager().registerEvents(new TeleportHistoryEvent(this), this);

        /*}else if (lang.equals("es")){
            worldSettingsEs = new net.whispwriting.universes.es.files.WorldSettingsFile(this);
            worldListFileEs = new net.whispwriting.universes.es.files.WorldListFile(this);
            loadWorldsEs();
            createWorldConfigEs();

            List<World> worldList = Bukkit.getWorlds();
            String gameMode = worldSettingsEs.get().getString("worlds."+worldList.get(0).getName()+".modoDeJuego");
            if (gameMode == null){
                LangSwap.swapToSpanish(this);
                worldSettingsEs = new net.whispwriting.universes.es.files.WorldSettingsFile(this);
                worldListFileEs = new net.whispwriting.universes.es.files.WorldListFile(this);
            }

            this.getCommand("universecreate").setExecutor(new net.whispwriting.universes.es.commands.CreateCommand(this));
            this.getCommand("universeimport").setExecutor(new net.whispwriting.universes.es.commands.ImportCommand(this));
            this.getCommand("universelist").setExecutor(new net.whispwriting.universes.es.commands.ListWorldsCommand());
            this.getCommand("universeteleport").setExecutor(new net.whispwriting.universes.es.commands.TeleportCommand(worldSettingsEs));
            this.getCommand("universeoverride").setExecutor(new net.whispwriting.universes.es.commands.OverrideCommand(this, playerSettingsEs));
            this.getCommand("universemodify").setExecutor(new net.whispwriting.universes.es.commands.ModifyCommand(this, worldSettingsEs, playerSettingsEs));
            this.getCommand("universedelete").setExecutor(new net.whispwriting.universes.es.commands.DeleteCommand(this, worldListFileEs, worldSettingsEs));
            this.getCommand("universeunload").setExecutor(new net.whispwriting.universes.es.commands.UnloadCommand(worldListFileEs));
            this.getCommand("confirm").setExecutor(new net.whispwriting.universes.es.commands.ConfirmCommand(this, worldListFileEs, worldSettingsEs));
            this.getCommand("cancel").setExecutor(new net.whispwriting.universes.es.commands.CancelCommand(this));
            this.getCommand("universehelp").setExecutor(new net.whispwriting.universes.es.commands.HelpCommand());
            this.getCommand("ur").setExecutor(new net.whispwriting.universes.es.commands.ReloadCommand(this));


            Bukkit.getPluginManager().registerEvents(new net.whispwriting.universes.es.events.TeleportEvent(worldSettingsEs, playerSettingsEs, this), this);
            Bukkit.getPluginManager().registerEvents(new net.whispwriting.universes.es.events.DeathEvent(worldSettingsEs), this);
            Bukkit.getPluginManager().registerEvents(new net.whispwriting.universes.es.events.JoinEvent(this), this);
            Bukkit.getPluginManager().registerEvents(new net.whispwriting.universes.es.events.FlyEvent(this, worldSettingsEs, playerSettingsEs), this);
            Bukkit.getPluginManager().registerEvents(new net.whispwriting.universes.es.events.PVPEvent(worldSettingsEs), this);
        }else{
            log.warning("[Universes] Plugin failed to load. Unsupported language. Please choose en or es.");
            log.warning("[Universes] Fallo al cargar el plugin. Lenguaje no soportado. Por favor, elige en o es.");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }*/

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadWorlds(){
        List<String> worldList = worldListFile.get().getStringList("worlds");

        for (String world : worldList){
            File file = new File(Bukkit.getWorldContainer() + "/" + world + "/");
            if (file.exists()){
                Generator generator = new Generator(this, world);
                if (Bukkit.getWorld(world) == null){
                    String type = worlds.get().getString("worlds."+world+".type");
                    World.Environment env = getTypeFromString(type);
                    generator.setEnvironment(env);
                    generator.createWorld();
                }
                String difficulty = worlds.get().getString("worlds."+world+".difficulty");
                try{
                    difficulty.equals("");
                }catch(NullPointerException e) {
                    difficulty = "easy";
                    worlds.get().set("worlds." + world + ".difficulty", "easy");
                    worlds.save();
                }
                World loadedWorld = generator.getWorld();
                if (loadedWorld == null){
                    loadedWorld = Bukkit.getWorld(world);
                }
                loadedWorld.setDifficulty(getDifficulty(difficulty));
            }
        }
        Bukkit.getScheduler().runTask(this, new SetSpawnFlagsTask());
        worlds.reload();
    }

    private void setSpawnFlags(){
        List<String> worldList = worldListFile.get().getStringList("worlds");
        for (String world : worldList){
            boolean allowAnimals = worlds.get().getBoolean("worlds."+world+".allowAnimals");
            boolean allowMonsters = worlds.get().getBoolean("worlds."+world+".allowMonsters");
            World worldWorld = Bukkit.getWorld(world);
            worldWorld.setSpawnFlags(allowMonsters, allowAnimals);
        }
    }

    private void loadWorldsEs(){
        List<String> worldList = worldListFileEs.get().getStringList("worlds");

        for (String world : worldList){
            File file = new File(Bukkit.getWorldContainer() + "/" + world + "/");
            if (file.exists()){
                if (Bukkit.getWorld(world) == null){
                    Generator generator = new Generator(this, world);
                    String type = worldSettingsEs.get().getString("worlds."+world+".tipo");
                    World.Environment env = getTypeFromString(type);
                    generator.setEnvironment(env);
                    generator.createWorld();
                }
            }
        }
        worldSettingsEs.reload();
    }

    private void createWorldConfig(){
        List<World> loadedWorlds = Bukkit.getWorlds();
        defaultWorld = loadedWorlds.get(0).getName();
        List<String> worldList = worldListFile.get().getStringList("worlds");
        for (World loadedWorld : loadedWorlds) {
            Location worldSpawn = loadedWorld.getSpawnLocation();
            String world = loadedWorld.getName();
            String name = worlds.get().getString("worlds."+world+".name");
            if (name == null) {
                log.warning("Name was null. Setting defaults.");
                double x = worldSpawn.getX();
                double y = worldSpawn.getY();
                double z = worldSpawn.getZ();
                worlds.get().set("worlds." + world + ".name", world);
                worlds.get().set("worlds." + world + ".type", getStringFromType(loadedWorld));
                worlds.get().set("worlds." + world + ".difficulty", getStringDifficulty(loadedWorld.getDifficulty()));
                worlds.get().set("worlds." + world + ".pvp", true);
                worlds.get().set("worlds." + world + ".spawn.world", world);
                worlds.get().set("worlds." + world + ".spawn.x", x);
                worlds.get().set("worlds." + world + ".spawn.y", y);
                worlds.get().set("worlds." + world + ".spawn.z", z);
                worlds.get().set("worlds." + world + ".allowMonsters", true);
                worlds.get().set("worlds." + world + ".allowAnimals", true);
                worlds.get().set("worlds." + world + ".gameMode", "survival");
                worlds.get().set("worlds." + world + ".respawnWorld", defaultWorld);
                worlds.get().set("worlds." + world + ".playerLimit", -1);
                worlds.get().set("worlds." + world + ".allowFlight", true);

                worlds.save();
            }
            if (!worldList.contains(loadedWorld.getName()))
                worldList.add(loadedWorld.getName());
        }
        worldListFile.get().set("worlds", worldList);
        worldListFile.save();
    }

    private void createWorldConfigEs(){
        List<World> loadedWorlds = Bukkit.getWorlds();
        for (World loadedWorld : loadedWorlds) {
            log.info(loadedWorld.getName());
            Location worldSpawn = loadedWorld.getSpawnLocation();
            String world = loadedWorld.getName();
            String name = worldSettingsEs.get().getString("worlds."+world+".nombre");
            log.warning(String.valueOf(name==null));
            if (name == null) {
                log.warning("name was null. Setting defaults.");
                double x = worldSpawn.getX();
                double y = worldSpawn.getY();
                double z = worldSpawn.getZ();
                worldSettingsEs.get().set("worlds." + world + ".nombre", world);
                worldSettingsEs.get().set("worlds." + world + ".tipo", getStringFromType(loadedWorld));
                worldSettingsEs.get().set("worlds." + world + ".pvp", true);
                worldSettingsEs.get().set("worlds." + world + ".spawn.world", world);
                worldSettingsEs.get().set("worlds." + world + ".spawn.x", x);
                worldSettingsEs.get().set("worlds." + world + ".spawn.y", y);
                worldSettingsEs.get().set("worlds." + world + ".spawn.z", z);
                worldSettingsEs.get().set("worlds." + world + ".permitirMonstruos", true);
                worldSettingsEs.get().set("worlds." + world + ".permitirAnimales", true);
                worldSettingsEs.get().set("worlds." + world + ".modoDeJuego", "survival");
                worldSettingsEs.get().set("worlds." + world + ".mundoDeReaparición", world);
                worldSettingsEs.get().set("worlds." + world + ".límiteDeJugadores", -1);
                worldSettingsEs.get().set("worlds." + world + ".permitirVuelo", true);

                worldSettingsEs.save();
                worldListFileEs.save();
            }
        }
    }

    public static void reload(){
        worlds.reload();
        worldListFile.reload();
        worldListFile.save();
        worlds.save();
    }

    public static void reloadEs(){
        worldSettingsEs.reload();
        worldListFileEs.reload();
        worldListFileEs.save();
        worldSettingsEs.save();
    }

    private World.Environment getTypeFromString(String type){
        switch (type){
            case "normal":
                return World.Environment.NORMAL;
            case "nether":
                return World.Environment.NETHER;
            case "end":
                return World.Environment.THE_END;
            default:
                return World.Environment.NORMAL;
        }
    }

    private String getStringFromType(World world){
        if (world.getEnvironment() == World.Environment.NORMAL){
            return "normal";
        }else if (world.getEnvironment() == World.Environment.NETHER){
            return "nether";
        }else{
            return "end";
        }
    }

    private Difficulty getDifficulty(String arg) {
        arg = arg.toLowerCase();
        switch (arg){
            case "peaceful":
                return PEACEFUL;
            case "easy":
                return EASY;
            case "normal":
                return NORMAL;
            case "hard":
                return HARD;
            default:
                return EASY;
        }
    }

    private String getStringDifficulty(Difficulty arg) {
        switch (arg){
            case PEACEFUL:
                return "peaceful";
            case EASY:
                return "easy";
            case NORMAL:
                return "normal";
            case HARD:
                return "hard";
            default:
                return "normal";
        }
    }
}
