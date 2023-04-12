package newt.testplugin20;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class GameManager {
    public static boolean InSetup = false;
    public static boolean InGame = false;
    public static int MaxScore;
    public static ArrayList<PlayerData> playerDataList = new ArrayList<PlayerData>();

    private static int playerNum = 0;
    private static int trueFactPlace = 0;

    private static int TIME_TO_ANSWER = 20 * 20;
    private static int TIME_BETWEEN_ANSWER = 8 * 20;

    private static int skipCounter = 0;

    public static boolean SetupGame(boolean up, int maxScore) {
        if (InSetup || InGame)
            return false;

        InSetup = true;
        MaxScore = maxScore;
        NotifyEveryone(ChatColor.GOLD + "" + ChatColor.BOLD + "Игра начата - можно писать факты!");
        return true;
    }

    public static boolean StartGame() {
        if (!InSetup)
            return false;

        if(playerDataList.size() == 0)
            return false;

        for (int i = 0; i < playerDataList.size(); i++) {
            if (playerDataList.get(i).lie.size() == 0 || playerDataList.get(i).truth.size() == 0)
                return false;
        }

        InSetup = false;
        InGame = true;
        NotifyEveryone(ChatColor.GOLD + "" + ChatColor.BOLD + "Игра стартовала!");
        playerNum = 0;
        new DelayedTask(() -> {
            GameManager.NextQuestion();
        }, 20 * 5);
        return true;
    }

    public static void StopGame() {
        InGame = false;
        InSetup = false;
        playerDataList.clear();

        NotifyEveryone(ChatColor.RED + "" + ChatColor.BOLD + "Игра закончена!");
    }

    public static void AddPlayer(Player p) {
        if (!InSetup) {
            p.sendMessage(ChatColor.RED + "Игра пока что не начата");
            return;
        } else if (InGame) {
            p.sendMessage(ChatColor.RED + "Игра уже стартовала!");
            return;
        }

        RemovePlayer(p);

        playerDataList.add(new PlayerData(p));
        p.sendMessage(ChatColor.GREEN + "Ты добавлен в игру!");
    }

    public static void RemovePlayer(Player p) {
        for (int i = 0; i < playerDataList.size(); i++) {
            if (playerDataList.get(i).player.getUniqueId() == p.getUniqueId()) {
                p.sendMessage(ChatColor.RED + "Ты удален из игры!");
                playerDataList.remove(i);
                break;
            }
        }
    }

    public static void AddFact(Player p, boolean isTrue, String fact) {
        if (!InSetup) {
            p.sendMessage(ChatColor.RED + "Сейчас нельзя добавить факт.");
            return;
        }
        PlayerData data = GetPlayerData(p);
        if (data != null) {
            if (isTrue)
                data.truth.add(fact);
            else
                data.lie.add(fact);

            p.sendMessage(ChatColor.YELLOW + "Теперь у тебя: " + ChatColor.GREEN + data.truth.size() + ChatColor.YELLOW + " правдивых и " + ChatColor.RED + data.lie.size() + ChatColor.YELLOW + " ложных фактов");
        }
        else {
            p.sendMessage(ChatColor.RED + "Ты не в игре, сначала нужно написать команду go, чтобы поставить себе спавнпоинт");
        }
    }

    public static PlayerData GetPlayerData(Player p) {
        for (int i = 0; i < playerDataList.size(); i++) {
            if (playerDataList.get(i).player.getUniqueId() == p.getUniqueId()) {
                return playerDataList.get(i);
            }
        }

        return null;
    }

    public static void Answer(Player p, int num) {
        GetPlayerData(p).answerNum = num;
        p.sendMessage(ChatColor.GREEN + "Правдивый факт: " + (num + 1));
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,500.0f, 1.0f);
    }

    public static void CollectAnswers() {
        int rightCounter = 0;

        for (int i = 0; i < playerDataList.size(); i++) {
            if (i == playerNum)
                continue;

            if (playerDataList.get(i).answerNum == trueFactPlace) {
                playerDataList.get(i).RightAnswer();
                rightCounter++;
            } else {
                playerDataList.get(i).WrongAnswer();
            }
        }

        playerDataList.get(playerNum).Answers(playerDataList.size() - 1 - rightCounter);

        for (int i = 0; i < playerDataList.size(); i++) {
            if (playerDataList.get(i).score >= MaxScore) {
                Win(playerDataList.get(i).player);
            }
        }
    }

    public static void NoMoreQuestions() {
        Player winner = null;
        int maxScore = 0;

        for (int i = 0; i < playerDataList.size(); i++) {
            if (playerDataList.get(i).score > maxScore) {
                maxScore = playerDataList.get(i).score;
                winner = playerDataList.get(i).player;
            }
        }

        Win(winner);
    }

    private static void Win(Player p) {
        NotifyEveryone(ChatColor.GREEN + "" + ChatColor.BOLD + "===================\nРаунд завершён, очки:");

        for (int i = 0; i < playerDataList.size(); i++) {
            NotifyEveryone(playerDataList.get(i).player.getName() + " - " + playerDataList.get(i).score);
        }

        NotifyEveryone(ChatColor.GOLD + p.getName() + " - ПОБЕДИТЕЛЬ!");

        NextGame();
    }

    private static void NextGame() {
        for (int i = 0; i < playerDataList.size(); i++) {
            playerDataList.get(i).Clear();
        }

        InGame = false;
        InSetup = true;
    }

    private static void NextQuestion(){
        new DelayedTask(() -> {
            Ask();
        }, TIME_BETWEEN_ANSWER);
    }

    private static void Ask() {

        // сбрасываем ответы
        for (int i = 0; i < playerDataList.size(); i++) {
            playerDataList.get(i).answerNum = -1;
        }

        PlayerData data = playerDataList.get(playerNum);

        if (data.truth.size() < 1 || data.lie.size() < 2){
            if (skipCounter > playerDataList.size())
            {
                NoMoreQuestions();
            }
            else
            {
                skipCounter++;
                NextPlayer();
                Ask();
            }

            return;
        }

        skipCounter = 0;

        trueFactPlace = new Random().nextInt(3);
        String fact1;
        String fact2;
        String fact3;

        int falseCounter = 0;

        if (data.lie.size() == 0)
            StopGame();

        if (trueFactPlace == 0){
            fact1 = data.truth.get(0);
        } else {
            fact1 = data.lie.get(falseCounter);
            falseCounter++;
        }

        if (trueFactPlace == 1){
            fact2 = data.truth.get(0);
        } else {
            fact2 = data.lie.get(falseCounter);
            falseCounter++;
        }

        if (trueFactPlace == 2){
            fact3 = data.truth.get(0);
        } else {
            fact3 = data.lie.get(falseCounter);
        }

        data.truth.remove(0);
        data.lie.remove(0);
        data.lie.remove(0);

        new DelayedTask(() -> {
            NotifyEveryone(ChatColor.ITALIC + "" + ChatColor.GOLD + "Факты об игроке " + ChatColor.RESET + "" + ChatColor.WHITE + data.player.getName() + ":");
            PlaySound(Sound.BLOCK_NOTE_BLOCK_BELL);
        }, 20);
        new DelayedTask(() -> {
            NotifyEveryone(ChatColor.GOLD + "[Факт 1]: " + ChatColor.WHITE + fact1);
            PlaySound(Sound.BLOCK_NOTE_BLOCK_BELL);
        }, 40);
        new DelayedTask(() -> {
            NotifyEveryone(ChatColor.GOLD + "[Факт 2]: " + ChatColor.WHITE + fact2);
            PlaySound(Sound.BLOCK_NOTE_BLOCK_BELL);
        }, 80);
        new DelayedTask(() -> {
            NotifyEveryone(ChatColor.GOLD + "[Факт 3]: " + ChatColor.WHITE + fact3);
            PlaySound(Sound.BLOCK_NOTE_BLOCK_BELL);
        }, 120);
        new DelayedTask(() -> {
            ClickableNotifyEveryone();
            Bar.ShowBar(TIME_TO_ANSWER);
            PlaySound(Sound.BLOCK_NOTE_BLOCK_BELL);
        }, 130);
        new DelayedTask(() -> {
            CollectAnswers();
            NextPlayer();
            NextQuestion();
            PlaySound(Sound.BLOCK_CHAIN_HIT);

            if (trueFactPlace == 0)
                NotifyEveryone(ChatColor.BOLD + "Верный ответ: " + fact1);
            else if (trueFactPlace == 1)
                NotifyEveryone(ChatColor.BOLD + "Верный ответ: " + fact2);
            else if (trueFactPlace == 2)
                NotifyEveryone(ChatColor.BOLD + "Верный ответ: " + fact3);

        }, 130 + TIME_TO_ANSWER);
    }

    public static void NotifyEveryone(String msg) {
        Object[] players = Bukkit.getOnlinePlayers().toArray();

        for (int i = 0; i < players.length; i++) {
            ((Player)players[i]).sendMessage(msg);
        }
    }

    public static void PlaySound(Sound sound) {
        Object[] players = Bukkit.getOnlinePlayers().toArray();

        for (int i = 0; i < players.length; i++) {
            Player player = (Player)players[i];
            player.playSound(player.getLocation(), sound,500.0f, 1.0f);
        }
    }

    public static void ClickableNotifyEveryone() {
        TextComponent comp1 = new TextComponent("[Выбрать 1]");
        TextComponent comp2 = new TextComponent("[Выбрать 2]");
        TextComponent comp3 = new TextComponent("[Выбрать 3]");

        comp1.setColor(ChatColor.GREEN.asBungee());
        comp1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer1"));

        comp2.setColor(ChatColor.GREEN.asBungee());
        comp2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer2"));

        comp3.setColor(ChatColor.GREEN.asBungee());
        comp3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/answer3"));

        Object[] players = Bukkit.getOnlinePlayers().toArray();

        for (int i = 0; i < players.length; i++) {
            ((Player)players[i]).spigot().sendMessage(comp1);
            ((Player)players[i]).spigot().sendMessage(comp2);
            ((Player)players[i]).spigot().sendMessage(comp3);
        }
    }

    private static void NextPlayer() {
        playerNum++;
        if (playerNum == playerDataList.size())
            playerNum = 0;
    }
}
