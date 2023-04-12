package newt.testplugin20;

import com.destroystokyo.paper.Title;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {

    private int RIGHT_SCORE = 3;
    private int WRONG_SCORE = 0;
    private int ANSWERS_MULT = 2;
    public Player player;
    public Location location;
    public List<String> truth = new ArrayList<>();
    public List<String> lie = new ArrayList<>();
    public int score;
    public int answerNum;

    public ArrayList<int[]> particlePositions = new ArrayList<>();
    public HashMap<Integer, Material> blockToMaterial = new HashMap<>();
    public PlayerData(Player p) {
        player = p;
        location = p.getLocation();
        score = 0;

        particlePositions.add(new int[] { 1, 1});
        particlePositions.add(new int[] { 1, 0});
        particlePositions.add(new int[] { 1, -1});
        particlePositions.add(new int[] { -1, 1});
        particlePositions.add(new int[] { -1, 0});
        particlePositions.add(new int[] { -1, -1});
        particlePositions.add(new int[] { 0, 1});
        particlePositions.add(new int[] { 0, -1});

        blockToMaterial.put(0,Material.WHITE_WOOL);
        blockToMaterial.put(1,Material.ORANGE_WOOL);
        blockToMaterial.put(2,Material.MAGENTA_WOOL);
        blockToMaterial.put(3,Material.LIGHT_BLUE_WOOL);
        blockToMaterial.put(4,Material.YELLOW_WOOL);
        blockToMaterial.put(5,Material.LIME_WOOL);
        blockToMaterial.put(6,Material.PINK_WOOL);
        blockToMaterial.put(7,Material.GRAY_WOOL);
        blockToMaterial.put(8,Material.LIGHT_GRAY_WOOL);
        blockToMaterial.put(9,Material.CYAN_WOOL);
        blockToMaterial.put(10,Material.PURPLE_WOOL);
        blockToMaterial.put(11,Material.BLUE_WOOL);
        blockToMaterial.put(12,Material.BROWN_WOOL);
        blockToMaterial.put(13,Material.GREEN_WOOL);
        blockToMaterial.put(14,Material.RED_WOOL);
        blockToMaterial.put(15,Material.BLACK_WOOL);

        blockToMaterial.put(16,Material.WHITE_CONCRETE_POWDER);
        blockToMaterial.put(17,Material.ORANGE_CONCRETE_POWDER);
        blockToMaterial.put(18,Material.MAGENTA_CONCRETE_POWDER);
        blockToMaterial.put(19,Material.LIGHT_BLUE_CONCRETE_POWDER);
        blockToMaterial.put(20,Material.YELLOW_CONCRETE_POWDER);
        blockToMaterial.put(21,Material.LIME_CONCRETE_POWDER);
        blockToMaterial.put(22,Material.PINK_CONCRETE_POWDER);
        blockToMaterial.put(23,Material.GRAY_CONCRETE_POWDER);
        blockToMaterial.put(24,Material.LIGHT_GRAY_CONCRETE_POWDER);
        blockToMaterial.put(25,Material.CYAN_CONCRETE_POWDER);
        blockToMaterial.put(26,Material.PURPLE_CONCRETE_POWDER);
        blockToMaterial.put(27,Material.BLUE_CONCRETE_POWDER);
        blockToMaterial.put(28,Material.BROWN_CONCRETE_POWDER);
        blockToMaterial.put(29,Material.GREEN_CONCRETE_POWDER);
        blockToMaterial.put(30,Material.RED_CONCRETE_POWDER);
        blockToMaterial.put(31,Material.BLACK_CONCRETE_POWDER);
    }

    public void RightAnswer() {
        score += RIGHT_SCORE;
        Build(RIGHT_SCORE, Particle.VILLAGER_HAPPY, 5);
        player.sendTitle(ChatColor.GREEN + "ВЕРНО!","+" + RIGHT_SCORE + " очка!");
    }

    public void WrongAnswer() {
        score += WRONG_SCORE;
        Build(WRONG_SCORE, Particle.REDSTONE, 5);
        player.sendTitle(ChatColor.RED + "НЕВЕРНО!","+" + WRONG_SCORE + " очков!");
    }

    public void Answers(int wrongs) {
        score += wrongs * ANSWERS_MULT;
        Build(wrongs * ANSWERS_MULT, Particle.SNOWBALL, 5);
        player.sendTitle(ChatColor.GOLD + "" + wrongs + " ИГРОКОВ ОШИБЛИСЬ!","+" + wrongs * ANSWERS_MULT + " очков!");
    }

    public void Clear() {
        score = 0;
        answerNum = -1;
        player.sendMessage(ChatColor.YELLOW + "Осталось: " + ChatColor.GREEN + truth.size() + ChatColor.YELLOW + " правдивых и " + ChatColor.RED + lie.size() + ChatColor.YELLOW + " ложных фактов");
    }

    private void Build(int amount, Particle part, int particleAmount) {
        Location oldLocation = location;
        location = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() + amount, location.getBlockZ());

        for (int i = 0; i < amount; i++) {
            new Location(oldLocation.getWorld(), oldLocation.getBlockX(), oldLocation.getBlockY() + i, oldLocation.getBlockZ())
                    .getBlock().setType(blockToMaterial.get(score - amount + i));
        }

        player.teleport(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1, location.getBlockZ() + 0.5, player.getLocation().getYaw(), player.getLocation().getPitch()));

        //for(int i = 0; i < 8; i ++) {
        //    player.spawnParticle(part,
        //            new Location(location.getWorld(), location.getBlockX() + particlePositions.get(i)[0], location.getBlockY(), location.getBlockZ() + particlePositions.get(i)[1]),
        //            particleAmount);
        //}
    }
}
