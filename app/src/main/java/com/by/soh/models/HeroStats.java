package com.by.soh.models;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
public class HeroStats {
    private int finalHp;
    private int finalAtk;
    private int finalDef;
    private int finalSpeed;
    private int finalMagicDef;
    private float finalCritRate;
    private float finalCritDamage;
    private float finalAccuracy;
    private float finalEvasion;
    private float attributeBonus;
    private float equipmentBonus;
    private float formationBonus;
    private float levelBonus;
    private float enhancementBonus;
    private float starBonus;
    private float factionSynergy;
    private float attributeSynergy;
    private float rivalryBonus;
    private float totalAtkMultiplier;
    private float totalDefMultiplier;
    private float totalHpMultiplier;
    private float totalSpeedMultiplier;
    private long heroId;
    private long calculatedAt;
    private boolean includesEquipment;
    private boolean includesFormation;
    public HeroStats() {
        this.calculatedAt = System.currentTimeMillis();
        this.includesEquipment = false;
        this.includesFormation = false;
    }
    public HeroStats(long heroId) {
        this();
        this.heroId = heroId;
    }

    public HeroStats(long heroId, int baseHp, int baseAtk, int baseDef, int baseSpeed, int baseMagicDef) {
        this(heroId);
        this.finalHp = baseHp;
        this.finalAtk = baseAtk;
        this.finalDef = baseDef;
        this.finalSpeed = baseSpeed;
        this.finalMagicDef = baseMagicDef;
        this.totalAtkMultiplier = 1.0f;
        this.totalDefMultiplier = 1.0f;
        this.totalHpMultiplier = 1.0f;
        this.totalSpeedMultiplier = 1.0f;
    }
    public long calculateTotalPower() {
        return Math.round(finalHp * 0.5 + finalAtk * 2.0 + finalDef * 1.5 + finalSpeed * 0.5);
    }

    public boolean hasBonusesApplied() {
        return attributeBonus > 0 || equipmentBonus > 0 || formationBonus > 0 ||
                factionSynergy > 0 || attributeSynergy > 0;
    }

    public String getBonusSummary() {
        StringBuilder summary = new StringBuilder();

        if (attributeBonus > 0) {
            summary.append(String.format("Atributo: +%.0f%% ", attributeBonus * 100));
        }
        if (equipmentBonus > 0) {
            summary.append(String.format("Equipo: +%.0f%% ", equipmentBonus * 100));
        }
        if (formationBonus > 0) {
            summary.append(String.format("Formación: +%.0f%% ", formationBonus * 100));
        }
        if (factionSynergy > 0) {
            summary.append(String.format("Facción: +%.0f%% ", factionSynergy * 100));
        }

        return summary.length() > 0 ? summary.toString().trim() : "Sin bonos";
    }

    public void markAsCalculated() {
        this.calculatedAt = System.currentTimeMillis();
    }

    public int getFinalHp() { return finalHp; }
    public void setFinalHp(int finalHp) { this.finalHp = Math.max(1, finalHp); }

    public int getFinalAtk() { return finalAtk; }
    public void setFinalAtk(int finalAtk) { this.finalAtk = Math.max(1, finalAtk); }

    public int getFinalDef() { return finalDef; }
    public void setFinalDef(int finalDef) { this.finalDef = Math.max(0, finalDef); }

    public int getFinalSpeed() { return finalSpeed; }
    public void setFinalSpeed(int finalSpeed) { this.finalSpeed = Math.max(1, finalSpeed); }

    public int getFinalMagicDef() { return finalMagicDef; }
    public void setFinalMagicDef(int finalMagicDef) { this.finalMagicDef = Math.max(0, finalMagicDef); }
    public float getFinalCritRate() { return finalCritRate; }
    public void setFinalCritRate(float finalCritRate) {
        this.finalCritRate = Math.max(0.0f, Math.min(1.0f, finalCritRate));
    }

    public float getFinalCritDamage() { return finalCritDamage; }
    public void setFinalCritDamage(float finalCritDamage) {
        this.finalCritDamage = Math.max(1.0f, finalCritDamage);
    }

    public float getFinalAccuracy() { return finalAccuracy; }
    public void setFinalAccuracy(float finalAccuracy) {
        this.finalAccuracy = Math.max(0.0f, Math.min(1.0f, finalAccuracy));
    }

    public float getFinalEvasion() { return finalEvasion; }
    public void setFinalEvasion(float finalEvasion) {
        this.finalEvasion = Math.max(0.0f, Math.min(1.0f, finalEvasion));
    }
    public float getAttributeBonus() { return attributeBonus; }
    public void setAttributeBonus(float attributeBonus) { this.attributeBonus = Math.max(0.0f, attributeBonus); }

    public float getEquipmentBonus() { return equipmentBonus; }
    public void setEquipmentBonus(float equipmentBonus) { this.equipmentBonus = Math.max(0.0f, equipmentBonus); }

    public float getFormationBonus() { return formationBonus; }
    public void setFormationBonus(float formationBonus) { this.formationBonus = Math.max(0.0f, formationBonus); }

    public float getLevelBonus() { return levelBonus; }
    public void setLevelBonus(float levelBonus) { this.levelBonus = Math.max(0.0f, levelBonus); }

    public float getEnhancementBonus() { return enhancementBonus; }
    public void setEnhancementBonus(float enhancementBonus) { this.enhancementBonus = Math.max(0.0f, enhancementBonus); }

    public float getStarBonus() { return starBonus; }
    public void setStarBonus(float starBonus) { this.starBonus = Math.max(0.0f, starBonus); }
    public float getFactionSynergy() { return factionSynergy; }
    public void setFactionSynergy(float factionSynergy) { this.factionSynergy = Math.max(0.0f, factionSynergy); }

    public float getAttributeSynergy() { return attributeSynergy; }
    public void setAttributeSynergy(float attributeSynergy) { this.attributeSynergy = Math.max(0.0f, attributeSynergy); }

    public float getRivalryBonus() { return rivalryBonus; }
    public void setRivalryBonus(float rivalryBonus) { this.rivalryBonus = Math.max(0.0f, rivalryBonus); }
    public float getTotalAtkMultiplier() { return totalAtkMultiplier; }
    public void setTotalAtkMultiplier(float totalAtkMultiplier) { this.totalAtkMultiplier = Math.max(0.1f, totalAtkMultiplier); }

    public float getTotalDefMultiplier() { return totalDefMultiplier; }
    public void setTotalDefMultiplier(float totalDefMultiplier) { this.totalDefMultiplier = Math.max(0.1f, totalDefMultiplier); }

    public float getTotalHpMultiplier() { return totalHpMultiplier; }
    public void setTotalHpMultiplier(float totalHpMultiplier) { this.totalHpMultiplier = Math.max(0.1f, totalHpMultiplier); }

    public float getTotalSpeedMultiplier() { return totalSpeedMultiplier; }
    public void setTotalSpeedMultiplier(float totalSpeedMultiplier) { this.totalSpeedMultiplier = Math.max(0.1f, totalSpeedMultiplier); }
    public long getHeroId() { return heroId; }
    public void setHeroId(long heroId) { this.heroId = heroId; }

    public long getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(long calculatedAt) { this.calculatedAt = calculatedAt; }

    public boolean isIncludesEquipment() { return includesEquipment; }
    public void setIncludesEquipment(boolean includesEquipment) { this.includesEquipment = includesEquipment; }

    public boolean isIncludesFormation() { return includesFormation; }
    public void setIncludesFormation(boolean includesFormation) { this.includesFormation = includesFormation; }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public String toString() {
        return String.format("HeroStats{heroId=%d, HP=%d, ATK=%d, DEF=%d, Speed=%d, Power=%d}",
                heroId, finalHp, finalAtk, finalDef, finalSpeed, calculateTotalPower());
    }
}