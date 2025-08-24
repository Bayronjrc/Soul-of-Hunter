package com.bleachafk.game;

import java.util.ArrayList;
import java.util.List;

public class Hero {

    // Enums
    public enum Rarity {
        COMMON(1), RARE(2), EPIC(3), LEGENDARY(4), MYTHIC(5);

        private final int value;
        Rarity(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public enum Faction {
        SHINIGAMI("Shinigami"),
        HOLLOW("Hollow"),
        QUINCY("Quincy"),
        ARRANCAR("Arrancar"),
        HUMAN("Human"),
        FULLBRING("Fullbring");

        private final String name;
        Faction(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public enum Attribute {
        POWER("Power"),    // +25% ATK, +15% Attack Speed
        SOUL("Soul"),      // +30% HP, +20% Spiritual Defense
        CORE("Core"),      // +20% ATK and DEF balanced, +10% Critical
        MIND("Mind"),      // +25% Special Skills, -15% Cooldown
        HEART("Heart"),    // +35% Healing, +20% Support to Allies
        VOID("Void");      // +30% Penetration, Ignores 15% Enemy Defense

        private final String name;
        Attribute(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public enum Role {
        TANK("Tank"),
        HEALER("Healer"),
        ASSASSIN("Assassin"),
        RANGE("Range"),
        SUPPORT("Support"),
        BERSERKER("Berserker"),
        CONTROLLER("Controller"),
        MAGE("Mage");

        private final String name;
        Role(String name) { this.name = name; }
        public String getName() { return name; }
    }

    // Basic properties
    private String id;
    private String name;
    private Rarity rarity;
    private Faction faction;
    private Attribute attribute;
    private Role role;

    // Level and progression
    private int level;
    private int stars;
    private int enhancement; // +0 to +9
    private int fragments;
    private int requiredFragments;

    // Base stats
    private int baseHp;
    private int baseAtk;
    private int basePhysicalDef;
    private int baseMagicalDef;
    private int baseSpeed;

    // Current stats (after equipment and bonuses)
    private int currentHp;
    private int currentAtk;
    private int currentPhysicalDef;
    private int currentMagicalDef;
    private int currentSpeed;

    // Equipment slots
    private Equipment weapon;
    private Equipment armor;
    private Equipment accessory1;
    private Equipment accessory2;
    private Equipment accessory3;
    private Equipment accessory4;

    // Skills
    private List<Skill> skills;

    // Visual
    private String spritePath;
    private String iconPath;

    // Constructors
    public Hero() {
        this.skills = new ArrayList<>();
        this.level = 1;
        this.stars = 1;
        this.enhancement = 0;
        this.fragments = 0;
        this.requiredFragments = 20;
    }

    public Hero(String id, String name, Rarity rarity, Faction faction, Attribute attribute, Role role) {
        this();
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.faction = faction;
        this.attribute = attribute;
        this.role = role;
        initializeBaseStats();
    }

    // Initialize base stats based on role and rarity
    private void initializeBaseStats() {
        int rarityMultiplier = rarity.getValue();

        switch (role) {
            case TANK:
                baseHp = 300 * rarityMultiplier;
                baseAtk = 40 * rarityMultiplier;
                basePhysicalDef = 25 * rarityMultiplier;
                baseMagicalDef = 20 * rarityMultiplier;
                baseSpeed = 30 * rarityMultiplier;
                break;
            case ASSASSIN:
                baseHp = 180 * rarityMultiplier;
                baseAtk = 80 * rarityMultiplier;
                basePhysicalDef = 15 * rarityMultiplier;
                baseMagicalDef = 12 * rarityMultiplier;
                baseSpeed = 60 * rarityMultiplier;
                break;
            case HEALER:
                baseHp = 200 * rarityMultiplier;
                baseAtk = 35 * rarityMultiplier;
                basePhysicalDef = 18 * rarityMultiplier;
                baseMagicalDef = 30 * rarityMultiplier;
                baseSpeed = 45 * rarityMultiplier;
                break;
            case RANGE:
                baseHp = 220 * rarityMultiplier;
                baseAtk = 65 * rarityMultiplier;
                basePhysicalDef = 20 * rarityMultiplier;
                baseMagicalDef = 18 * rarityMultiplier;
                baseSpeed = 50 * rarityMultiplier;
                break;
            default:
                baseHp = 250 * rarityMultiplier;
                baseAtk = 50 * rarityMultiplier;
                basePhysicalDef = 20 * rarityMultiplier;
                baseMagicalDef = 20 * rarityMultiplier;
                baseSpeed = 40 * rarityMultiplier;
                break;
        }

        calculateCurrentStats();
    }

    // Calculate current stats with all modifiers
    public void calculateCurrentStats() {
        currentHp = (int) (baseHp * getLevelMultiplier() * getEnhancementMultiplier() * getAttributeHpBonus());
        currentAtk = (int) (baseAtk * getLevelMultiplier() * getEnhancementMultiplier() * getAttributeAtkBonus());
        currentPhysicalDef = (int) (basePhysicalDef * getLevelMultiplier() * getEnhancementMultiplier());
        currentMagicalDef = (int) (baseMagicalDef * getLevelMultiplier() * getEnhancementMultiplier());
        currentSpeed = (int) (baseSpeed * getLevelMultiplier() * getAttributeSpeedBonus());

        // Add equipment bonuses
        addEquipmentStats();
    }

    private void addEquipmentStats() {
        if (weapon != null) {
            currentAtk += weapon.getAtkBonus();
            currentHp += weapon.getHpBonus();
            currentPhysicalDef += weapon.getPhysicalDefBonus();
            currentMagicalDef += weapon.getMagicalDefBonus();
            currentSpeed += weapon.getSpeedBonus();
        }

        if (armor != null) {
            currentHp += armor.getHpBonus();
            currentPhysicalDef += armor.getPhysicalDefBonus();
            currentMagicalDef += armor.getMagicalDefBonus();
        }

        // Add accessory bonuses
        Equipment[] accessories = {accessory1, accessory2, accessory3, accessory4};
        for (Equipment accessory : accessories) {
            if (accessory != null) {
                currentAtk += accessory.getAtkBonus();
                currentHp += accessory.getHpBonus();
                currentPhysicalDef += accessory.getPhysicalDefBonus();
                currentMagicalDef += accessory.getMagicalDefBonus();
                currentSpeed += accessory.getSpeedBonus();
            }
        }
    }

    private double getLevelMultiplier() {
        return 1.0 + (level - 1) * 0.1;
    }

    private double getEnhancementMultiplier() {
        return 1.0 + enhancement * 0.15;
    }

    private double getAttributeHpBonus() {
        switch (attribute) {
            case SOUL: return 1.3;
            case CORE: return 1.2;
            default: return 1.0;
        }
    }

    private double getAttributeAtkBonus() {
        switch (attribute) {
            case POWER: return 1.25;
            case CORE: return 1.2;
            case MIND: return 1.25;
            case VOID: return 1.3;
            default: return 1.0;
        }
    }

    private double getAttributeSpeedBonus() {
        switch (attribute) {
            case POWER: return 1.15;
            default: return 1.0;
        }
    }

    // Calculate total power for display
    public int getTotalPower() {
        return (currentHp / 10) + currentAtk + currentPhysicalDef + currentMagicalDef + currentSpeed;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Rarity getRarity() { return rarity; }
    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
        calculateCurrentStats();
    }

    public Faction getFaction() { return faction; }
    public void setFaction(Faction faction) { this.faction = faction; }

    public Attribute getAttribute() { return attribute; }
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
        calculateCurrentStats();
    }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public int getLevel() { return level; }
    public void setLevel(int level) {
        this.level = level;
        calculateCurrentStats();
    }

    public int getStars() { return stars; }
    public void setStars(int stars) {
        this.stars = Math.max(1, Math.min(5, stars));
        calculateCurrentStats();
    }

    public int getEnhancement() { return enhancement; }
    public void setEnhancement(int enhancement) {
        this.enhancement = Math.max(0, Math.min(9, enhancement));
        calculateCurrentStats();
    }

    public int getFragments() { return fragments; }
    public void setFragments(int fragments) { this.fragments = fragments; }

    public int getRequiredFragments() { return requiredFragments; }
    public void setRequiredFragments(int requiredFragments) { this.requiredFragments = requiredFragments; }

    // Current stats getters
    public int getCurrentHp() { return currentHp; }
    public int getCurrentAtk() { return currentAtk; }
    public int getCurrentPhysicalDef() { return currentPhysicalDef; }
    public int getCurrentMagicalDef() { return currentMagicalDef; }
    public int getCurrentSpeed() { return currentSpeed; }

    // Base stats getters
    public int getBaseHp() { return baseHp; }
    public int getBaseAtk() { return baseAtk; }
    public int getBasePhysicalDef() { return basePhysicalDef; }
    public int getBaseMagicalDef() { return baseMagicalDef; }
    public int getBaseSpeed() { return baseSpeed; }

    // Equipment getters and setters
    public Equipment getWeapon() { return weapon; }
    public void setWeapon(Equipment weapon) {
        this.weapon = weapon;
        calculateCurrentStats();
    }

    public Equipment getArmor() { return armor; }
    public void setArmor(Equipment armor) {
        this.armor = armor;
        calculateCurrentStats();
    }

    public Equipment getAccessory1() { return accessory1; }
    public void setAccessory1(Equipment accessory1) {
        this.accessory1 = accessory1;
        calculateCurrentStats();
    }

    public Equipment getAccessory2() { return accessory2; }
    public void setAccessory2(Equipment accessory2) {
        this.accessory2 = accessory2;
        calculateCurrentStats();
    }

    public Equipment getAccessory3() { return accessory3; }
    public void setAccessory3(Equipment accessory3) {
        this.accessory3 = accessory3;
        calculateCurrentStats();
    }

    public Equipment getAccessory4() { return accessory4; }
    public void setAccessory4(Equipment accessory4) {
        this.accessory4 = accessory4;
        calculateCurrentStats();
    }

    public List<Skill> getSkills() { return skills; }
    public void setSkills(List<Skill> skills) { this.skills = skills; }

    public String getSpritePath() { return spritePath; }
    public void setSpritePath(String spritePath) { this.spritePath = spritePath; }

    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }

    // Utility methods
    public boolean canUpgradeStars() {
        return fragments >= requiredFragments && stars < 5;
    }

    public boolean canEnhance() {
        return enhancement < 9;
    }

    public String getFullDisplayName() {
        return name + " +" + enhancement;
    }

    public String getAttributeDisplayName() {
        return name + "/" + attribute.getName().toLowerCase();
    }

    // Get evolution stage based on enhancement
    public int getEvolutionStage() {
        if (enhancement >= 7) return 2; // Final form
        if (enhancement >= 3) return 1; // First evolution
        return 0; // Base form
    }

    // Check if Bankai is available (requires +3 enhancement)
    public boolean isBankaiAvailable() {
        return enhancement >= 3;
    }

    // Get equipment by slot type
    public Equipment getEquipmentBySlot(Equipment.SlotType slotType) {
        switch (slotType) {
            case WEAPON: return weapon;
            case ARMOR: return armor;
            case ACCESSORY_1: return accessory1;
            case ACCESSORY_2: return accessory2;
            case ACCESSORY_3: return accessory3;
            case ACCESSORY_4: return accessory4;
            default: return null;
        }
    }

    // Set equipment by slot type
    public void setEquipmentBySlot(Equipment.SlotType slotType, Equipment equipment) {
        switch (slotType) {
            case WEAPON: setWeapon(equipment); break;
            case ARMOR: setArmor(equipment); break;
            case ACCESSORY_1: setAccessory1(equipment); break;
            case ACCESSORY_2: setAccessory2(equipment); break;
            case ACCESSORY_3: setAccessory3(equipment); break;
            case ACCESSORY_4: setAccessory4(equipment); break;
        }
    }

    @Override
    public String toString() {
        return "Hero{" +
                "name='" + name + '\'' +
                ", rarity=" + rarity +
                ", faction=" + faction +
                ", attribute=" + attribute +
                ", role=" + role +
                ", level=" + level +
                ", stars=" + stars +
                ", enhancement=" + enhancement +
                ", totalPower=" + getTotalPower() +
                '}';
    }