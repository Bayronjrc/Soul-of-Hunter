package com.by.soh.models;

import android.database.Cursor;
import android.util.Log;

import com.by.soh.constants.EquipmentConstants;
import com.by.soh.constants.GameConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo que representa una pieza de equipamiento
 */
public class Equipment {

    private static final String TAG = "Equipment";

    // Identificadores
    private long id;
    private int equipmentType;
    private int rarity;
    private int enhancement;
    private int setId;

    // Estadísticas principales
    private String mainStatType;
    private int mainStatValue;
    private List<SecondaryStat> secondaryStats;

    // Estados
    private long equippedByHero;
    private boolean isLocked;
    private int powerRating;
    private long obtainedAt;

    // ==================== CONSTRUCTORES ====================

    /**
     * Constructor vacío
     */
    public Equipment() {
        this.secondaryStats = new ArrayList<>();
        this.obtainedAt = System.currentTimeMillis();
    }

    /**
     * Constructor completo
     */
    public Equipment(int equipmentType, int rarity, int enhancement,
                     String mainStatType, int mainStatValue, int setId) {
        this();
        this.equipmentType = equipmentType;
        this.rarity = rarity;
        this.enhancement = enhancement;
        this.mainStatType = mainStatType;
        this.mainStatValue = mainStatValue;
        this.setId = setId;

        // Generar stats secundarios automáticamente
        generateSecondaryStats();

        // Calcular poder
        this.powerRating = calculatePowerRating();
    }

    /**
     * Constructor desde Cursor de base de datos
     */
    public Equipment(Cursor cursor) {
        this();
        loadFromCursor(cursor);
    }

    // ==================== MÉTODOS DE CARGA ====================

    /**
     * Carga los datos desde un cursor de base de datos
     */
    public void loadFromCursor(Cursor cursor) {
        try {
            this.id = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
            this.equipmentType = cursor.getInt(cursor.getColumnIndexOrThrow("equipment_type"));
            this.rarity = cursor.getInt(cursor.getColumnIndexOrThrow("rarity"));
            this.enhancement = cursor.getInt(cursor.getColumnIndexOrThrow("enhancement"));
            this.mainStatType = cursor.getString(cursor.getColumnIndexOrThrow("main_stat_type"));
            this.mainStatValue = cursor.getInt(cursor.getColumnIndexOrThrow("main_stat_value"));
            this.setId = cursor.getInt(cursor.getColumnIndexOrThrow("set_id"));
            this.equippedByHero = cursor.getLong(cursor.getColumnIndexOrThrow("equipped_by_hero"));
            this.isLocked = cursor.getInt(cursor.getColumnIndexOrThrow("is_locked")) == 1;
            this.powerRating = cursor.getInt(cursor.getColumnIndexOrThrow("power_rating"));
            this.obtainedAt = cursor.getLong(cursor.getColumnIndexOrThrow("obtained_at"));

            // Cargar stats secundarios desde JSON
            String secondaryStatsJson = cursor.getString(cursor.getColumnIndexOrThrow("secondary_stats"));
            loadSecondaryStatsFromJson(secondaryStatsJson);

        } catch (Exception e) {
            Log.e(TAG, "Error cargando equipment desde cursor", e);
        }
    }

    /**
     * Carga las estadísticas secundarias desde JSON
     */
    private void loadSecondaryStatsFromJson(String jsonString) {
        this.secondaryStats = new ArrayList<>();

        if (jsonString == null || jsonString.trim().isEmpty()) {
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject statObject = jsonArray.getJSONObject(i);
                String statType = statObject.getString("type");
                int value = statObject.getInt("value");
                this.secondaryStats.add(new SecondaryStat(statType, value));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parseando secondary stats JSON", e);
        }
    }

    // ==================== MÉTODOS DE GENERACIÓN ====================

    /**
     * Genera estadísticas secundarias aleatorias basadas en la rareza
     */
    public void generateSecondaryStats() {
        this.secondaryStats = new ArrayList<>();

        int statsCount = EquipmentConstants.getSecondaryStatsCount(this.rarity);
        int[] valueRange = EquipmentConstants.getSecondaryStatRange(this.rarity);

        if (statsCount == 0 || valueRange[0] == 0) {
            return;
        }

        // Lista de stats disponibles para evitar duplicados
        List<String> availableStats = new ArrayList<>();
        for (String stat : EquipmentConstants.SECONDARY_STATS) {
            // No agregar la misma stat que la principal
            if (!stat.toLowerCase().contains(this.mainStatType.toLowerCase())) {
                availableStats.add(stat);
            }
        }

        // Generar stats aleatorios
        for (int i = 0; i < statsCount && i < availableStats.size(); i++) {
            int randomIndex = (int) (Math.random() * availableStats.size());
            String statType = availableStats.get(randomIndex);
            availableStats.remove(randomIndex); // Evitar duplicados

            int value = valueRange[0] + (int) (Math.random() * (valueRange[1] - valueRange[0] + 1));
            this.secondaryStats.add(new SecondaryStat(statType, value));
        }
    }

    // ==================== MÉTODOS DE MEJORA ====================

    /**
     * Mejora el equipamiento (+1 enhancement)
     */
    public boolean enhance() {
        if (this.enhancement >= EquipmentConstants.MAX_EQUIPMENT_ENHANCEMENT) {
            return false;
        }

        this.enhancement++;

        // Recalcular stat principal
        int baseStat = EquipmentConstants.getBaseEquipmentStat(this.equipmentType, this.rarity);
        float enhancementMultiplier = 1.0f + (this.enhancement * EquipmentConstants.EQUIPMENT_ENHANCEMENT_MULTIPLIER);
        this.mainStatValue = Math.round(baseStat * enhancementMultiplier);

        // Recalcular poder
        this.powerRating = calculatePowerRating();

        return true;
    }

    /**
     * Realiza reforja (rejuve) del equipamiento
     */
    public boolean reforge() {
        if (!EquipmentConstants.canMeltEquipment(this.rarity)) {
            return false;
        }

        // Probabilidad de mejora basada en rareza
        float upgradeChance = EquipmentConstants.REJUVE_UPGRADE_CHANCE[this.rarity];

        if (Math.random() < upgradeChance) {
            // Regenerar stats secundarios con posible mejora
            generateSecondaryStats();

            // 25% chance de mejorar valores ligeramente
            if (Math.random() < 0.25f) {
                for (SecondaryStat stat : this.secondaryStats) {
                    stat.value += 1;
                }
            }

            this.powerRating = calculatePowerRating();
            return true;
        }

        return false;
    }

    // ==================== CÁLCULOS ====================

    /**
     * Calcula el poder total del equipamiento
     */
    public int calculatePowerRating() {
        int power = this.mainStatValue * 10;

        // Agregar valor de stats secundarios
        for (SecondaryStat stat : this.secondaryStats) {
            power += stat.value * 8; // Factor ligeramente menor para stats secundarios
        }

        // Bonus por enhancement
        power = Math.round(power * (1.0f + this.enhancement * 0.1f));

        // Bonus por rareza
        power = Math.round(power * EquipmentConstants.getRarityMultiplier(this.rarity));

        return power;
    }

    /**
     * Obtiene el costo de mejora
     */
    public long getUpgradeCost() {
        return EquipmentConstants.getEquipmentUpgradeCost(this.rarity, this.enhancement);
    }

    /**
     * Obtiene el costo de reforja
     */
    public int getReforgeCost() {
        if (this.rarity < EquipmentConstants.REJUVE_GOLD_COST.length) {
            return EquipmentConstants.REJUVE_GOLD_COST[this.rarity];
        }
        return 0;
    }

    /**
     * Obtiene el costo de gemas para reforja
     */
    public int getReforgeGemCost() {
        if (this.rarity < EquipmentConstants.REJUVE_GEM_COST.length) {
            return EquipmentConstants.REJUVE_GEM_COST[this.rarity];
        }
        return 0;
    }

    // ==================== MÉTODOS DE INFORMACIÓN ====================

    /**
     * Obtiene el nombre del tipo de equipamiento
     */
    public String getTypeName() {
        return EquipmentConstants.getEquipmentTypeName(this.equipmentType);
    }

    /**
     * Obtiene el nombre de la rareza
     */
    public String getRarityName() {
        return EquipmentConstants.getEquipmentRarityName(this.rarity);
    }

    /**
     * Obtiene el color de la rareza
     */
    public String getRarityColor() {
        return EquipmentConstants.getEquipmentRarityColor(this.rarity);
    }

    /**
     * Obtiene el nombre del set
     */
    public String getSetName() {
        if (this.setId >= 0 && this.setId < EquipmentConstants.SET_NAMES.length) {
            return EquipmentConstants.SET_NAMES[this.setId];
        }
        return "Sin Set";
    }

    /**
     * Obtiene la descripción completa del equipamiento
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(getRarityName()).append(" ").append(getTypeName());

        if (this.enhancement > 0) {
            desc.append(" +").append(this.enhancement);
        }

        desc.append("\n\nStat Principal: ").append(this.mainStatType)
                .append(" +").append(this.mainStatValue);

        if (!this.secondaryStats.isEmpty()) {
            desc.append("\n\nStats Secundarios:");
            for (SecondaryStat stat : this.secondaryStats) {
                desc.append("\n• ").append(stat.type).append(" +").append(stat.value);
                if (stat.type.contains("%")) {
                    desc.append("%");
                }
            }
        }

        if (this.setId > 0) {
            desc.append("\n\nSet: ").append(getSetName());
        }

        desc.append("\n\nPoder: ").append(this.powerRating);

        return desc.toString();
    }

    /**
     * Verifica si puede ser equipado por un tipo específico de héroe
     */
    public boolean canBeEquippedBy(int heroRole, int heroFaction) {
        // Por defecto, cualquier héroe puede equipar cualquier cosa
        // Aquí puedes agregar lógica específica si quieres restricciones

        // Ejemplo: Solo Shinigami pueden usar ciertos sets
        if (this.setId == EquipmentConstants.SET_GOTEI_13) {
            return heroFaction == 1; // HeroConstants.FACTION_SHINIGAMI
        }

        return true;
    }

    /**
     * Verifica si el equipamiento está equipado
     */
    public boolean isEquipped() {
        return this.equippedByHero > 0;
    }

    /**
     * Convierte las estadísticas secundarias a JSON
     */
    public String secondaryStatsToJson() {
        if (this.secondaryStats == null || this.secondaryStats.isEmpty()) {
            return "";
        }

        try {
            JSONArray jsonArray = new JSONArray();
            for (SecondaryStat stat : this.secondaryStats) {
                JSONObject statObject = new JSONObject();
                statObject.put("type", stat.type);
                statObject.put("value", stat.value);
                jsonArray.put(statObject);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            Log.e(TAG, "Error convirtiendo secondary stats a JSON", e);
            return "";
        }
    }

    // ==================== GETTERS Y SETTERS ====================

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getEquipmentType() { return equipmentType; }
    public void setEquipmentType(int equipmentType) { this.equipmentType = equipmentType; }

    public int getRarity() { return rarity; }
    public void setRarity(int rarity) { this.rarity = rarity; }

    public int getEnhancement() { return enhancement; }
    public void setEnhancement(int enhancement) { this.enhancement = enhancement; }

    public String getMainStatType() { return mainStatType; }
    public void setMainStatType(String mainStatType) { this.mainStatType = mainStatType; }

    public int getMainStatValue() { return mainStatValue; }
    public void setMainStatValue(int mainStatValue) { this.mainStatValue = mainStatValue; }

    public List<SecondaryStat> getSecondaryStats() { return secondaryStats; }
    public void setSecondaryStats(List<SecondaryStat> secondaryStats) { this.secondaryStats = secondaryStats; }

    public int getSetId() { return setId; }
    public void setSetId(int setId) { this.setId = setId; }

    public long getEquippedByHero() { return equippedByHero; }
    public void setEquippedByHero(long equippedByHero) { this.equippedByHero = equippedByHero; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    public int getPowerRating() { return powerRating; }
    public void setPowerRating(int powerRating) { this.powerRating = powerRating; }

    public long getObtainedAt() { return obtainedAt; }
    public void setObtainedAt(long obtainedAt) { this.obtainedAt = obtainedAt; }

    // ==================== CLASE INTERNA ====================

    /**
     * Clase para representar una estadística secundaria
     */
    public static class SecondaryStat {
        public String type;
        public int value;

        public SecondaryStat(String type, int value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return type + ": +" + value + (type.contains("%") ? "%" : "");
        }
    }

    // ==================== FACTORY METHODS ====================

    /**
     * Crea equipamiento aleatorio basado en nivel y rareza
     */
    public static Equipment createRandomEquipment(int minRarity, int maxRarity, int playerLevel) {
        // Determinar rareza aleatoria dentro del rango
        int rarity = minRarity + (int) (Math.random() * (maxRarity - minRarity + 1));

        // Tipo aleatorio
        int type = 1 + (int) (Math.random() * 6); // 1-6 tipos de equipment

        // Set aleatorio (70% chance de no tener set)
        int setId = 0;
        if (Math.random() > 0.7) {
            setId = 1 + (int) (Math.random() * (EquipmentConstants.SET_NAMES.length - 1));
        }

        // Stat principal basado en tipo
        String mainStatType = getMainStatTypeForEquipmentType(type);
        int baseStat = EquipmentConstants.getBaseEquipmentStat(type, rarity);

        // Variación aleatoria ±10%
        float variation = 0.9f + (float) (Math.random() * 0.2f);
        int mainStatValue = Math.round(baseStat * variation);

        return new Equipment(type, rarity, 0, mainStatType, mainStatValue, setId);
    }

    /**
     * Determina la estadística principal según el tipo de equipamiento
     */
    private static String getMainStatTypeForEquipmentType(int type) {
        switch (type) {
            case EquipmentConstants.EQUIPMENT_WEAPON: return "ATK";
            case EquipmentConstants.EQUIPMENT_ARMOR: return "DEF";
            case EquipmentConstants.EQUIPMENT_ACCESSORY: return "HP";
            case EquipmentConstants.EQUIPMENT_BOOTS: return "Speed";
            case EquipmentConstants.EQUIPMENT_GLOVES: return "Crit Rate";
            case EquipmentConstants.EQUIPMENT_HELMET: return "Resistance";
            default: return "ATK";
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s +%d (Power: %d)",
                getRarityName(), getTypeName(), enhancement, powerRating);
    }
}