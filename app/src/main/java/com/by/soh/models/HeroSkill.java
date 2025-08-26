package com.by.soh.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.by.soh.constants.HeroConstants;

public class HeroSkill {
    private long id;
    private long heroId;
    private String name;
    private String description;

    private int skillType;
    private int unlockRequirement;
    private int maxLevel;
    private int currentLevel;
    private boolean isUnlocked;
    private boolean isLearned;
    private int energyCost;

    private float baseDamageMultiplier;
    private float levelDamageIncrease;
    private String effectType;
    private int effectDuration;
    private float effectPower;
    private String targetType;
    private int maxTargets;
    private boolean canTargetDead;
    private boolean requiresBankai;
    private boolean isPassive;
    private float activationChance;

    private long createdAt;
    private boolean isFavorite;
    private int timesUsed;

    public HeroSkill() {
        this.maxLevel = 3;
        this.currentLevel = 1;
        this.isUnlocked = false;
        this.isLearned = false;
        this.timesUsed = 0;
        this.createdAt = System.currentTimeMillis();
        this.activationChance = 1.0f;
        this.maxTargets = 1;
    }

    public HeroSkill(long heroId, String name, int skillType, int unlockRequirement) {
        this();
        this.heroId = heroId;
        this.name = name;
        this.skillType = skillType;
        this.unlockRequirement = unlockRequirement;
        setupDefaultsByType();
    }

    public HeroSkill(long heroId, String name, String description, int skillType,
                     int unlockRequirement, float baseDamageMultiplier, String effectType) {
        this(heroId, name, skillType, unlockRequirement);
        this.description = description;
        this.baseDamageMultiplier = baseDamageMultiplier;
        this.effectType = effectType;
    }

    private void setupDefaultsByType() {
        switch (skillType) {
            case HeroConstants.SKILL_TYPE_BASIC:
                this.energyCost = HeroConstants.BASIC_SKILL_ENERGY_COST;
                this.isUnlocked = true;
                this.isLearned = true;
                break;

            case HeroConstants.SKILL_TYPE_SPECIAL:
                this.energyCost = HeroConstants.SPECIAL_SKILL_ENERGY_COST;
                break;

            case HeroConstants.SKILL_TYPE_ULTIMATE:
                this.energyCost = HeroConstants.ULTIMATE_SKILL_ENERGY_COST;
                this.requiresBankai = true;
                break;
        }
    }

    public boolean canBeUsed(int currentEnergy, boolean bankaiActive) {
        return isUnlocked &&
                isLearned &&
                currentEnergy >= energyCost &&
                (!requiresBankai || bankaiActive);
    }
    public boolean levelUp() {
        if (currentLevel < maxLevel) {
            currentLevel++;
            return true;
        }
        return false;
    }

    public float getCurrentDamageMultiplier() {
        return baseDamageMultiplier + (levelDamageIncrease * (currentLevel - 1));
    }

    @SuppressLint("DefaultLocale")
    public String getFullName() {
        return String.format("%s Lv.%d", name, currentLevel);
    }

    public String getSkillTypeName() {
        switch (skillType) {
            case HeroConstants.SKILL_TYPE_BASIC: return "Básica";
            case HeroConstants.SKILL_TYPE_SPECIAL: return "Especial";
            case HeroConstants.SKILL_TYPE_ULTIMATE: return "Definitiva";
            default: return "Desconocida";
        }
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getHeroId() { return heroId; }
    public void setHeroId(long heroId) { this.heroId = heroId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getSkillType() { return skillType; }
    public void setSkillType(int skillType) { this.skillType = skillType; }

    public int getUnlockRequirement() { return unlockRequirement; }
    public void setUnlockRequirement(int unlockRequirement) { this.unlockRequirement = Math.max(0, unlockRequirement); }

    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = Math.max(1, maxLevel); }

    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = Math.max(1, Math.min(maxLevel, currentLevel));
    }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }

    public boolean isLearned() { return isLearned; }
    public void setLearned(boolean learned) { this.isLearned = learned; }

    // Mecánicas
    public int getEnergyCost() { return energyCost; }
    public void setEnergyCost(int energyCost) { this.energyCost = Math.max(0, energyCost); }

    public float getBaseDamageMultiplier() { return baseDamageMultiplier; }
    public void setBaseDamageMultiplier(float baseDamageMultiplier) {
        this.baseDamageMultiplier = Math.max(0.0f, baseDamageMultiplier);
    }

    public float getLevelDamageIncrease() { return levelDamageIncrease; }
    public void setLevelDamageIncrease(float levelDamageIncrease) {
        this.levelDamageIncrease = Math.max(0.0f, levelDamageIncrease);
    }

    public String getEffectType() { return effectType; }
    public void setEffectType(String effectType) { this.effectType = effectType; }

    public int getEffectDuration() { return effectDuration; }
    public void setEffectDuration(int effectDuration) { this.effectDuration = Math.max(0, effectDuration); }

    public float getEffectPower() { return effectPower; }
    public void setEffectPower(float effectPower) { this.effectPower = effectPower; }

    // Targeting
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public int getMaxTargets() { return maxTargets; }
    public void setMaxTargets(int maxTargets) { this.maxTargets = Math.max(1, maxTargets); }

    public boolean isCanTargetDead() { return canTargetDead; }
    public void setCanTargetDead(boolean canTargetDead) { this.canTargetDead = canTargetDead; }

    public boolean isRequiresBankai() { return requiresBankai; }
    public void setRequiresBankai(boolean requiresBankai) { this.requiresBankai = requiresBankai; }

    public boolean isPassive() { return isPassive; }
    public void setPassive(boolean passive) { this.isPassive = passive; }

    public float getActivationChance() { return activationChance; }
    public void setActivationChance(float activationChance) {
        this.activationChance = Math.max(0.0f, Math.min(1.0f, activationChance));
    }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { this.isFavorite = favorite; }

    public int getTimesUsed() { return timesUsed; }
    public void setTimesUsed(int timesUsed) { this.timesUsed = Math.max(0, timesUsed); }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("HeroSkill{id=%d, name='%s', type='%s', level=%d/%d, unlocked=%b}",
                id, name, getSkillTypeName(), currentLevel, maxLevel, isUnlocked);
    }
}