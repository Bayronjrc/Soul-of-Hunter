package com.by.soh.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.by.soh.constants.HeroConstants;
import java.util.ArrayList;
import java.util.List;

public class Hero {

    private long id;
    private String name;
    private int faction;
    private int attribute;
    private int role;
    private int rarity;

    private int level;
    private int stars;
    private int enhancement;
    private long experience;

    private int baseHp;
    private int baseAtk;
    private int baseDef;
    private int baseSpeed;
    private int baseMagicDef;

    private float critRate;
    private float critDamage;
    private float accuracy;
    private float evasion;
    private List<Long> skillIds;
    private List<Long> equipmentIds;

    private boolean bankaiUnlocked;
    private boolean awakeningUnlocked;
    private long createdAt;
    private long lastUsed;
    private boolean isFavorite;
    private String description;
    public Hero() {
        this.skillIds = new ArrayList<>();
        this.equipmentIds = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.lastUsed = 0;
        this.isFavorite = false;
        this.bankaiUnlocked = false;
        this.awakeningUnlocked = false;
        initializeDefaultStats();
    }
    public Hero(String name, int faction, int attribute, int role, int rarity) {
        this();
        this.name = name;
        this.faction = faction;
        this.attribute = attribute;
        this.role = role;
        this.rarity = rarity;
        this.level = 1;
        this.stars = 1;
        this.enhancement = 0;
        this.experience = 0;
        initializeBaseStats();
    }

    private void initializeDefaultStats() {
        this.critRate = HeroConstants.CRIT_RATE_BASE;
        this.critDamage = HeroConstants.CRIT_DAMAGE_BASE;
        this.accuracy = HeroConstants.ACCURACY_BASE;
        this.evasion = HeroConstants.EVASION_BASE;
    }

    private void initializeBaseStats() {
        if (role > 0 && role < HeroConstants.BASE_HP_BY_ROLE.length) {
            this.baseHp = HeroConstants.BASE_HP_BY_ROLE[role];
            this.baseAtk = HeroConstants.BASE_ATK_BY_ROLE[role];
            this.baseDef = HeroConstants.BASE_DEF_BY_ROLE[role];
            this.baseSpeed = HeroConstants.BASE_SPEED_BY_ROLE[role];
            this.baseMagicDef = HeroConstants.BASE_DEF_MAGIC_BY_ROLE[role];
        }
    }

    public boolean isAwakened() {
        return awakeningUnlocked;
    }

    public void updateUnlockedAbilities() {
        this.bankaiUnlocked = (this.enhancement >= 3);
        this.awakeningUnlocked = (this.enhancement >= 7);
    }

    public void markAsUsed() {
        this.lastUsed = System.currentTimeMillis();
    }

    public String getFactionName() {
        return HeroConstants.getFactionName(this.faction);
    }

    public String getAttributeName() {
        return HeroConstants.getAttributeName(this.attribute);
    }

    public String getRoleName() {
        return HeroConstants.getRoleName(this.role);
    }

    public boolean isFavorite() {
        return this.isFavorite;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getFaction() { return faction; }
    public void setFaction(int faction) { this.faction = faction; }

    public int getAttribute() { return attribute; }
    public void setAttribute(int attribute) { this.attribute = attribute; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }

    public int getRarity() { return rarity; }
    public void setRarity(int rarity) { this.rarity = rarity; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, level); }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = Math.max(1, Math.min(5, stars)); }

    public int getEnhancement() { return enhancement; }
    public void setEnhancement(int enhancement) {
        this.enhancement = Math.max(0, Math.min(9, enhancement));
        updateUnlockedAbilities();
    }

    public long getExperience() { return experience; }
    public void setExperience(long experience) { this.experience = Math.max(0, experience); }

    // Stats base
    public int getBaseHp() { return baseHp; }
    public void setBaseHp(int baseHp) { this.baseHp = Math.max(1, baseHp); }

    public int getBaseAtk() { return baseAtk; }
    public void setBaseAtk(int baseAtk) { this.baseAtk = Math.max(1, baseAtk); }

    public int getBaseDef() { return baseDef; }
    public void setBaseDef(int baseDef) { this.baseDef = Math.max(0, baseDef); }

    public int getBaseSpeed() { return baseSpeed; }
    public void setBaseSpeed(int baseSpeed) { this.baseSpeed = Math.max(1, baseSpeed); }

    public int getBaseMagicDef() { return baseMagicDef; }
    public void setBaseMagicDef(int baseMagicDef) { this.baseMagicDef = Math.max(0, baseMagicDef); }

    public float getCritRate() { return critRate; }
    public void setCritRate(float critRate) { this.critRate = Math.max(0.0f, Math.min(1.0f, critRate)); }

    public float getCritDamage() { return critDamage; }
    public void setCritDamage(float critDamage) { this.critDamage = Math.max(1.0f, critDamage); }

    public float getAccuracy() { return accuracy; }
    public void setAccuracy(float accuracy) { this.accuracy = Math.max(0.0f, Math.min(1.0f, accuracy)); }

    public float getEvasion() { return evasion; }
    public void setEvasion(float evasion) { this.evasion = Math.max(0.0f, Math.min(1.0f, evasion)); }

    public List<Long> getSkillIds() { return new ArrayList<>(skillIds); }
    public void setSkillIds(List<Long> skillIds) {
        this.skillIds = skillIds != null ? new ArrayList<>(skillIds) : new ArrayList<>();
    }

    public List<Long> getEquipmentIds() { return new ArrayList<>(equipmentIds); }
    public void setEquipmentIds(List<Long> equipmentIds) {
        this.equipmentIds = equipmentIds != null ? new ArrayList<>(equipmentIds) : new ArrayList<>();
    }

    public boolean isBankaiUnlocked() { return bankaiUnlocked; }
    public void setBankaiUnlocked(boolean bankaiUnlocked) { this.bankaiUnlocked = bankaiUnlocked; }

    public boolean isAwakeningUnlocked() { return awakeningUnlocked; }
    public void setAwakeningUnlocked(boolean awakeningUnlocked) { this.awakeningUnlocked = awakeningUnlocked; }


    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getLastUsed() { return lastUsed; }
    public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }

    public void setFavorite(boolean favorite) { this.isFavorite = favorite; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @NonNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("Hero{id=%d, name='%s', level=%d, enhancement=+%d}",
                id, name, level, enhancement);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hero hero = (Hero) obj;
        return id == hero.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
