package newt.testplugin20;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Bar {
    private static Plugin plugin = null;
    private  static int newTask;
    public static BossBar bar = Bukkit.createBossBar("Время отвечать!", BarColor.RED, BarStyle.SEGMENTED_10);

    public Bar(Plugin instance) {
        plugin = instance;
    }
    public static void AddPlayer(Player player) {
        bar.addPlayer(player);
        bar.setVisible(false);
    }

    public static void ShowBar(double time) {
        bar.setVisible(true);

        newTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            double progress = 1.0;
            @Override
            public void run() {
                progress = progress - 20/time;

                if(progress <= 0) {
                    bar.setVisible(false);
                    Bukkit.getScheduler().cancelTask(newTask);
                } else {
                    bar.setProgress(progress);
                }
            }
        }, 0, 20).getTaskId();
    }
}
