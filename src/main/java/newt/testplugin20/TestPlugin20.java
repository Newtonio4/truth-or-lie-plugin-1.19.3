package newt.testplugin20;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class TestPlugin20 extends JavaPlugin {

    @Override
    public void onEnable() {
        // Commands
        Commands commands = new Commands();
        getCommand("setupGame").setExecutor(commands);
        getCommand("startGame").setExecutor(commands);
        getCommand("stopGame").setExecutor(commands);
        getCommand("go").setExecutor(commands);
        getCommand("leave").setExecutor(commands);
        getCommand("true").setExecutor(commands);
        getCommand("false").setExecutor(commands);
        getCommand("clearTrue").setExecutor(commands);
        getCommand("clearFalse").setExecutor(commands);
        getCommand("answer1").setExecutor(commands);
        getCommand("answer2").setExecutor(commands);
        getCommand("answer3").setExecutor(commands);

        new DelayedTask(this);
        new Bar(this);

        getServer().getPluginManager().registerEvents(new EventListeners(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
