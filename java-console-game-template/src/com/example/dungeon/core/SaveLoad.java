package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public class SaveLoad {
    private static final Path SAVE = Paths.get("save.txt");
    private static final Path SCORES = Paths.get("scores.csv");

    public static void save(GameState s) {
        try (BufferedWriter w = Files.newBufferedWriter(SAVE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            Player p = s.getPlayer();
            w.write("player;" + p.getName() + ";" + p.getHp() + ";" + p.getAttack());
            w.newLine();
            for (Item i : p.getInventory()) {
                w.write("ITEM;" + i.getClass().getSimpleName() + ";" + i.getName());
                w.newLine();
            }
            w.write("room;" + s.getCurrent().getName());
            w.newLine();
            System.out.println("Сохранено в " + SAVE.toAbsolutePath());
            writeScore(p.getName(), s.getScore());
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    public static void load(GameState s) {
        if (!Files.exists(SAVE)) {
            System.out.println("Сохранение не найдено.");
            return;
        }
        try (BufferedReader r = Files.newBufferedReader(SAVE)) {
            String line;
            String playerLine = null;
            List<String> items = new ArrayList<>();
            String roomName = null;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("player;")) playerLine = line;
                else if (line.startsWith("ITEM;")) items.add(line);
                else if (line.startsWith("room;")) roomName = line.substring("room;".length());
            }
            if (playerLine != null) {
                String[] pp = playerLine.split(";", 4);
                String name = pp[1];
                int hp = Integer.parseInt(pp[2]);
                int attack = Integer.parseInt(pp[3]);
                Player p = s.getPlayer();
                p.setName(name);
                p.setHp(hp);
                p.setAttack(attack);
                p.getInventory().clear();
                for (String it : items) {
                    String[] parts = it.split(";", 3);
                    String type = parts[1];
                    String iname = parts[2];
                    switch (type) {
                        case "Potion" -> p.getInventory().add(new Potion(iname, 5));
                        case "Key" -> p.getInventory().add(new Key(iname));
                        case "Weapon" -> p.getInventory().add(new Weapon(iname, 2));
                        default -> {
                        }
                    }
                }
                if (roomName != null) {
                    Room cur = s.getCurrent();
                    if (!cur.getName().equals(roomName)) {
                        final String targetRoom = roomName;
                        Optional<Room> maybe = cur.getNeighbors().values().stream()
                                .filter(rm -> rm.getName().equals(targetRoom))
                                .findFirst();
                        maybe.ifPresent(s::setCurrent);
                    }
                }
                System.out.println("Загрузка завершена (упрощённо).");
            } else {
                System.out.println("Сохранение повреждено или пусто.");
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки: " + e.getMessage());
        }
    }

    public static void printScores() {
        if (!Files.exists(SCORES)) {
            System.out.println("Пока нет результатов.");
            return;
        }
        try {
            System.out.println("Таблица лидеров:");
            Files.lines(SCORES)
                    .skip(1)
                    .map(l -> l.split(","))
                    .map(a -> new Score(a[1], Integer.parseInt(a[2])))
                    .sorted(Comparator.comparingInt(Score::score).reversed())
                    .limit(10)
                    .forEach(s -> System.out.println(s.player() + " — " + s.score()));
        } catch (IOException e) {
            System.out.println("Ошибка чтения результатов: " + e.getMessage());
        }
    }

    private static void writeScore(String player, int score) {
        boolean header = !Files.exists(SCORES);
        try (BufferedWriter w = Files.newBufferedWriter(SCORES, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (header) {
                w.write("ts,player,score");
                w.newLine();
            }
            w.write(LocalDateTime.now() + "," + player + "," + score);
            w.newLine();
        } catch (IOException e) {
            System.out.println("Не удалось записать очки: " + e.getMessage());
        }
    }

    private record Score(String player, int score) {
    }
}
