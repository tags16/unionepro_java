package com.example.dungeon.core;

import com.example.dungeon.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameState state = new GameState();
    private final Map<String, Command> commands = new LinkedHashMap<>();

    // Для demo alloc — список объектов, чтобы не удалялись сразу сборщиком
    private final List<byte[]> leaks = new ArrayList<>();

    static {
        WorldInfo.touch("Game");
    }

    public Game() {
        registerCommands();
        bootstrapWorld();
    }

    private void registerCommands() {
        // help
        commands.put("help", (ctx, a) ->
                System.out.println("Команды: " + String.join(", ", commands.keySet()))
        );

        // about
        commands.put("about", (ctx, a) ->
                System.out.println("DungeonMini — учебный проект. Измененныя сборка")
        );

        // gc-stats: вывод used/free/total
        commands.put("gc-stats", (ctx, a) -> {
            Runtime rt = Runtime.getRuntime();
            long free = rt.freeMemory();
            long total = rt.totalMemory();
            long used = total - free;
            System.out.println("Память: used=" + used + " free=" + free + " total=" + total);
        });

        // alloc <mb> — выделить побольше памяти в куче (демонстрация GC)
        commands.put("alloc", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Укажите объём в МБ: alloc <mb>");
            int mb;
            try {
                mb = Integer.parseInt(a.get(0));
            } catch (NumberFormatException ex) {
                throw new InvalidCommandException("Неверное число: " + a.get(0));
            }
            System.out.println("Аллоцируем " + mb + " MB...");
            for (int i = 0; i < mb; i++) {
                leaks.add(new byte[1024 * 1024]);
            }
            System.out.println("Готово. Всего аллоцировано блоков: " + leaks.size());
        });

        // look
        commands.put("look", (ctx, a) -> {
            Room cur = ctx.getCurrent();
            if (cur == null) throw new InvalidCommandException("Текущее местоположение неизвестно.");
            System.out.println(cur.describe());
        });

        // move <dir>
        commands.put("move", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Укажите направление: move <north|south|east|west>");
            String dir = a.get(0).toLowerCase(Locale.ROOT);
            Room cur = ctx.getCurrent();
            Room nxt = cur.getNeighbors().get(dir);
            if (nxt == null) throw new InvalidCommandException("Нет выхода в сторону: " + dir);
            ctx.setCurrent(nxt);
            System.out.println("Вы перешли в: " + nxt.getName());
            System.out.println(nxt.describe());
        });

        // take <item name>
        commands.put("take", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Укажите предмет: take <item name>");
            String wanted = String.join(" ", a).trim();
            Room cur = ctx.getCurrent();
            Optional<Item> found = cur.getItems().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(wanted))
                    .findFirst();
            if (found.isEmpty()) throw new InvalidCommandException("В комнате нет предмета: " + wanted);
            Item item = found.get();
            ctx.getPlayer().getInventory().add(item);
            cur.getItems().remove(item);
            System.out.println("Взято: " + item.getName());
        });

        // inventory
        commands.put("inventory", (ctx, a) -> {
            List<Item> inv = ctx.getPlayer().getInventory();
            if (inv.isEmpty()) {
                System.out.println("Инвентарь пуст.");
                return;
            }
            Map<String, List<Item>> grouped = inv.stream()
                    .collect(Collectors.groupingBy(i -> i.getClass().getSimpleName()));
            grouped.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(e -> {
                        String kind = e.getKey();
                        List<Item> items = e.getValue();
                        String names = items.stream().map(Item::getName).collect(Collectors.joining(", "));
                        System.out.println("- " + kind + " (" + items.size() + "): " + names);
                    });
        });

        // use <item>
        commands.put("use", (ctx, a) -> {
            if (a.isEmpty()) throw new InvalidCommandException("Укажите предмет: use <item name>");
            String wanted = String.join(" ", a).trim();
            Player p = ctx.getPlayer();
            Optional<Item> found = p.getInventory().stream()
                    .filter(i -> i.getName().equalsIgnoreCase(wanted))
                    .findFirst();
            if (found.isEmpty()) throw new InvalidCommandException("Нет такого предмета в инвентаре: " + wanted);
            Item it = found.get();
            it.apply(ctx);
            // Многие предметы (Potion, Weapon) удаляют себя в apply, но на всякий случай
            p.getInventory().remove(it);
        });

        // fight
        commands.put("fight", (ctx, a) -> {
            Room cur = ctx.getCurrent();
            Monster m = cur.getMonster();
            Player p = ctx.getPlayer();
            if (m == null || m.getHp() <= 0) throw new InvalidCommandException("В этой комнате нет живого монстра.");

            System.out.println("Начинается бой с " + m.getName() + " (HP: " + m.getHp() + ").");

            while (p.getHp() > 0 && m.getHp() > 0) {
                int playerDmg = p.getAttack();
                m.setHp(m.getHp() - playerDmg);
                System.out.println("Вы бьёте " + m.getName() + " на " + playerDmg + ". HP монстра: " + Math.max(m.getHp(), 0));
                if (m.getHp() <= 0) {
                    System.out.println("Монстр повержен!");
                    // простой дроп
                    Random rnd = new Random();
                    int drop = rnd.nextInt(3);
                    switch (drop) {
                        case 0 -> {
                            Potion pot = new Potion("Малое зелье", 5);
                            cur.getItems().add(pot);
                            System.out.println("Монстр уронил: " + pot.getName());
                        }
                        case 1 -> {
                            Weapon w = new Weapon("Клык монстра", 2);
                            cur.getItems().add(w);
                            System.out.println("Монстр уронил: " + w.getName());
                        }
                        default -> System.out.println("Монстр не уронил ничего полезного.");
                    }
                    ctx.addScore(10);
                    cur.setMonster(null);
                    return;
                }
                // монстр отвечает
                int monsterDmg = Math.max(1, m.getLevel());
                p.setHp(p.getHp() - monsterDmg);
                System.out.println(m.getName() + " отвечает на " + monsterDmg + ". Ваше HP: " + Math.max(p.getHp(), 0));
                if (p.getHp() <= 0) {
                    System.out.println("Вы погибли. Игра окончена.");
                    // записать очки перед выходом
                    SaveLoad.save(ctx);
                    System.exit(0);
                }
            }
        });

        // Save/Load/Scores/Exit
        commands.put("save", (ctx, a) -> SaveLoad.save(ctx));
        commands.put("load", (ctx, a) -> SaveLoad.load(ctx));
        commands.put("scores", (ctx, a) -> SaveLoad.printScores());

        commands.put("exit", (ctx, a) -> {
            // при выходе — сохраняем очки игрока
            SaveLoad.save(state);
            System.out.println("Пока!");
            System.exit(0);
        });
    }

    private void bootstrapWorld() {
        Player hero = new Player("Герой", 20, 5);
        state.setPlayer(hero);

        Room square = new Room("Площадь", "Каменная площадь с фонтаном.");
        Room forest = new Room("Лес", "Шелест листвы и птичий щебет.");
        Room cave = new Room("Пещера", "Темно и сыро.");
        square.getNeighbors().put("north", forest);
        forest.getNeighbors().put("south", square);
        forest.getNeighbors().put("east", cave);
        cave.getNeighbors().put("west", forest);

        forest.getItems().add(new Potion("Малое зелье", 5));
        forest.setMonster(new Monster("Волк", 1, 8));

        state.setCurrent(square);
    }

    public void run() {
        System.out.println("DungeonMini (TEMPLATE). 'help' — команды.");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String line = in.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty()) continue;

                List<String> parts = Arrays.asList(line.split("\\s+"));
                String cmd = parts.get(0).toLowerCase(Locale.ROOT);
                List<String> args = parts.size() > 1 ? parts.subList(1, parts.size()) : Collections.emptyList();
                Command c = commands.get(cmd);
                try {
                    if (c == null) throw new InvalidCommandException("Неизвестная команда: " + cmd);
                    c.execute(state, args);
                    state.addScore(1);
                } catch (InvalidCommandException e) {
                    System.out.println("Ошибка: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Непредвиденная ошибка: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    // Для отладки можно раскомментировать стек:
                    // e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода/вывода: " + e.getMessage());
        }
    }
}
