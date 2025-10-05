package com.example.dungeon.model;

import java.util.ArrayList;
import java.util.List;


public class Player extends Entity {
    private int attack;
    private final List<Item> inventory = new ArrayList<>();

    public Player(String name, int hp, int attack) {
        super(name, hp);
        this.attack = attack;
    }

    public Player(String name) {
        super(name, 20);
        this.attack = 5;
    }

    public int getAttack() { return attack; }
    public void setAttack(int attack) { this.attack = attack; }

    public List<Item> getInventory() { return inventory; }

    // SaveLoad
    public void setName(String name) { super.setName(name); }
    public void setHp(int hp) { super.setHp(hp); }
}
