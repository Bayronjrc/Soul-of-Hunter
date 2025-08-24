package com.by.soh;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private String playerName;
    private int level;
    private int vipLevel;
    private int totalPower;
    private int gems;
    private int gold;
    private int currentExp;
    private int maxExp;
    private String profileImagePath;

    // Hero and equipment data
    private List<Hero> heroes;
    private List<Equipment> equipment;
    private List<Item> items;
    private Formation currentFormation;

    // Game progress
    private int currentCampaignLevel;
    private int highestCampaignLevel;
    private int hellTowerLevel;
    private int pvpRank;

    public PlayerData() {
        // Initialize collections
        heroes = new ArrayList<>();
        equipment = new ArrayList<>();
        items = new ArrayList<>();
        currentFormation = new Formation();

        // Set default values
        playerName = "Player";
        level = 1;
        vipLevel = 0;
        totalPower = 0;
        gems = 0;
        gold = 0;
        currentExp = 0;
        maxExp = 100;
        currentCampaignLevel = 1;
        highestCampaignLevel = 1;
        hellTowerLevel = 1;
        pvpRank = 9999;
        profileImagePath = "ichigo_icon";
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        // Update max exp based on level
        this.maxExp = level * 100;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public int getTotalPower() {
        return totalPower;
    }

    public void setTotalPower(int totalPower) {
        this.totalPower = totalPower;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(int currentExp) {
        this.currentExp = currentExp;
        checkLevelUp();
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Hero> heroes) {
        this.heroes = heroes;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<Equipment> equipment) {
        this.equipment = equipment;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Formation getCurrentFormation() {
        return currentFormation;
    }

    public void setCurrentFormation(Formation currentFormation) {
        this.currentFormation = currentFormation;
    }

    public int getCurrentCampaignLevel() {
        return currentCampaignLevel;
    }

    public void setCurrentCampaignLevel(int currentCampaignLevel) {
        this.currentCampaignLevel = currentCampaignLevel;
    }

    public int getHighestCampaignLevel() {
        return highestCampaignLevel;
    }

    public void setHighestCampaignLevel(int highestCampaignLevel) {
        this.highestCampaignLevel = highestCampaignLevel;
    }

    public int getHellTowerLevel() {
        return hellTowerLevel;
    }

    public void setHellTowerLevel(int hellTowerLevel) {
        this.hellTowerLevel = hellTowerLevel;
    }

    public int getPvpRank() {
        return pvpRank;
    }

    public void setPvpRank(int pvpRank) {
        this.pvpRank = pvpRank;
    }

    // Utility methods
    public void addHero(Hero hero) {
        heroes.add(hero);
        updateTotalPower();
    }

    public void addEquipment(Equipment equip) {
        equipment.add(equip);
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void addExperience(int exp) {
        currentExp += exp;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (currentExp >= maxExp) {
            currentExp -= maxExp;
            level++;
            maxExp = level * 100;
            // TODO: Add level up rewards and notifications
        }
    }

    private void updateTotalPower() {
        totalPower = 0;
        for (Hero hero : currentFormation.getActiveHeroes()) {
            if (hero != null) {
                totalPower += hero.getTotalPower();
            }
        }
    }

    // Experience progress percentage for UI
    public int getExpProgressPercentage() {
        return (int) ((double) currentExp / maxExp * 100);
    }

    // Check if player can afford something
    public boolean canAfford(int goldCost, int gemCost) {
        return gold >= goldCost && gems >= gemCost;
    }

    // Spend resources
    public boolean spendResources(int goldCost, int gemCost) {
        if (canAfford(goldCost, gemCost)) {
            gold -= goldCost;
            gems -= gemCost;
            return true;
        }
        return false;
    }

    // Get formation slots available based on level
    public int getAvailableFormationSlots() {
        if (level >= 20) return 5;
        if (level >= 15) return 4;
        if (level >= 10) return 3;
        if (level >= 5) return 2;
        return 1;
    }
}