package newt.testplugin20;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            // Все игроки
            if (command.getName().equalsIgnoreCase("go")) {
                GameManager.AddPlayer(p);
            } else if (command.getName().equalsIgnoreCase("leave")) {
                GameManager.RemovePlayer(p);
            } else if (command.getName().equalsIgnoreCase("true")) {
                if (args.length > 0) {
                    StringBuilder builder = new StringBuilder();

                    for(int i = 0; i < args.length; i++)
                        builder.append(args[i] + " ");

                    GameManager.AddFact(p, true, builder.toString());
                }
            } else if (command.getName().equalsIgnoreCase("false")) {
                if (args.length > 0) {
                    StringBuilder builder = new StringBuilder();

                    for(int i = 0; i < args.length; i++)
                        builder.append(args[i] + " ");

                    GameManager.AddFact(p, false, builder.toString());
                }
            } else if (command.getName().equalsIgnoreCase("clearTrue")) {
                GameManager.GetPlayerData(p).truth.clear();
                p.sendMessage(ChatColor.GOLD + "Правдивые факты " + ChatColor.RED + "удалены");
            } else if (command.getName().equalsIgnoreCase("clearFalse")) {
                GameManager.GetPlayerData(p).lie.clear();
                p.sendMessage(ChatColor.GOLD + "Ложные факты " + ChatColor.RED + "удалены");
            } else if (command.getName().equalsIgnoreCase("answer1")) {
                GameManager.Answer(p, 0);
            } else if (command.getName().equalsIgnoreCase("answer2")) {
                GameManager.Answer(p, 1);
            } else if (command.getName().equalsIgnoreCase("answer3")) {
                GameManager.Answer(p, 2);
            }

            // OP
            if (!p.isOp())
                return false;

            if (command.getName().equalsIgnoreCase("setupGame")) {
                if (args.length < 2)
                    return false;

                boolean isUp = Boolean.parseBoolean(args[0]);
                int score = Integer.parseInt(args[1]);

                if (GameManager.SetupGame(isUp, score)) {
                    if (isUp)
                        p.sendMessage(ChatColor.GOLD + "Начата игра по обычным правилам до " + score + " очков!");
                    else
                        p.sendMessage(ChatColor.GOLD + "Начата игра по обратным правилам до " + score + " очков!");
                }
                else {
                    p.sendMessage(ChatColor.RED + "Игра не начата :(");
                }

            } else if (command.getName().equalsIgnoreCase("startGame")) {
                if (GameManager.StartGame()) {
                    p.sendMessage(ChatColor.GREEN + "Игра ПРАВДА или ЛОЖЬ стартовала!");
                }
                else {
                    p.sendMessage(ChatColor.RED + "Игра не может стартовать - либо вы забыли команду setupGame, либо не все игроки подготовили факты :(");
                }
            } else if (command.getName().equalsIgnoreCase("stopGame")) {
                GameManager.StopGame();
                p.sendMessage(ChatColor.RED + "Игра завершена!");
            }
        }

        return true;
    }
}
