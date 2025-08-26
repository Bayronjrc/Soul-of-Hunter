package com.by.soh.models;

import com.by.soh.constants.EquipmentConstants;

/**
 * Enumeraciones relacionadas con el sistema de equipamiento
 */
public class EquipmentEnums {

    /**
     * Rareza del equipamiento
     */
    public enum EquipmentRarity {
        GRAY(EquipmentConstants.EQUIPMENT_GRAY, "Gris", "#808080"),
        GREEN(EquipmentConstants.EQUIPMENT_GREEN, "Verde", "#00FF00"),
        BLUE(EquipmentConstants.EQUIPMENT_BLUE, "Azul", "#0080FF"),
        PURPLE(EquipmentConstants.EQUIPMENT_PURPLE, "Púrpura", "#8000FF"),
        ORANGE(EquipmentConstants.EQUIPMENT_ORANGE, "Naranja", "#FF8000"),
        RED(EquipmentConstants.EQUIPMENT_RED, "Rojo", "#FF0000");

        private final int id;
        private final String name;
        private final String colorHex;

        EquipmentRarity(int id, String name, String colorHex) {
            this.id = id;
            this.name = name;
            this.colorHex = colorHex;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getColorHex() { return colorHex; }

        public static EquipmentRarity fromId(int id) {
            for (EquipmentRarity rarity : values()) {
                if (rarity.id == id) {
                    return rarity;
                }
            }
            return GRAY;
        }

        public float getDropRate() {
            if (id < EquipmentConstants.EQUIPMENT_DROP_RATES.length) {
                return EquipmentConstants.EQUIPMENT_DROP_RATES[id];
            }
            return 0.0f;
        }

        public int getSecondaryStatsCount() {
            return EquipmentConstants.getSecondaryStatsCount(this.id);
        }

        public int[] getSecondaryStatRange() {
            return EquipmentConstants.getSecondaryStatRange(this.id);
        }

        public boolean canBeMelted() {
            return EquipmentConstants.canMeltEquipment(this.id);
        }
    }

    /**
     * Tipo de equipamiento
     */
    public enum EquipmentType {
        WEAPON(EquipmentConstants.EQUIPMENT_WEAPON, "Zanpakutō", "ATK", "Arma principal para combate"),
        ARMOR(EquipmentConstants.EQUIPMENT_ARMOR, "Armadura", "DEF", "Protección corporal"),
        ACCESSORY(EquipmentConstants.EQUIPMENT_ACCESSORY, "Accesorio", "HP", "Aumenta vitalidad"),
        BOOTS(EquipmentConstants.EQUIPMENT_BOOTS, "Calzado", "Speed", "Incrementa velocidad"),
        GLOVES(EquipmentConstants.EQUIPMENT_GLOVES, "Guantes", "Crit Rate", "Mejora precisión crítica"),
        HELMET(EquipmentConstants.EQUIPMENT_HELMET, "Casco", "Resistance", "Resistencia a efectos");

        private final int id;
        private final String name;
        private final String mainStat;
        private final String description;

        EquipmentType(int id, String name, String mainStat, String description) {
            this.id = id;
            this.name = name;
            this.mainStat = mainStat;
            this.description = description;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getMainStat() { return mainStat; }
        public String getDescription() { return description; }

        public static EquipmentType fromId(int id) {
            for (EquipmentType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return WEAPON;
        }

        public int getBaseStat(EquipmentRarity rarity) {
            return EquipmentConstants.getBaseEquipmentStat(this.id, rarity.getId());
        }
    }

    /**
     * Sets de equipamiento
     */
    public enum EquipmentSet {
        NONE(EquipmentConstants.SET_NONE, "Sin Set", "Sin bonificaciones especiales"),
        GOTEI_13(EquipmentConstants.SET_GOTEI_13, "Gotei 13", "Set de la organización Shinigami"),
        ESPADA(EquipmentConstants.SET_ESPADA, "Espada", "Set de los Arrancar élite"),
        STERNRITTER(EquipmentConstants.SET_STERNRITTER, "Sternritter", "Set de la élite Quincy"),
        VIZARD(EquipmentConstants.SET_VIZARD, "Vizard", "Set híbrido Shinigami-Hollow"),
        FULLBRING(EquipmentConstants.SET_FULLBRING, "Fullbring", "Set de manipuladores de la materia"),
        HOLLOW(EquipmentConstants.SET_HOLLOW, "Hollow", "Set de las almas corrompidas");

        private final int id;
        private final String name;
        private final String description;

        EquipmentSet(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }

        public static EquipmentSet fromId(int id) {
            for (EquipmentSet set : values()) {
                if (set.id == id) {
                    return set;
                }
            }
            return NONE;
        }

        public String getTwoPieceBonus() {
            if (id < EquipmentConstants.SET_BONUSES_2_PIECES.length) {
                return EquipmentConstants.SET_BONUSES_2_PIECES[id];
            }
            return "";
        }

        public String getFourPieceBonus() {
            if (id < EquipmentConstants.SET_BONUSES_4_PIECES.length) {
                return EquipmentConstants.SET_BONUSES_4_PIECES[id];
            }
            return "";
        }

        public String getSixPieceBonus() {
            if (id < EquipmentConstants.SET_BONUSES_6_PIECES.length) {
                return EquipmentConstants.SET_BONUSES_6_PIECES[id];
            }
            return "";
        }

        public boolean hasActiveBonus(int pieceCount, int requiredPieces) {
            return pieceCount >= requiredPieces;
        }
    }

    /**
     * Estadísticas secundarias posibles
     */
    public enum SecondaryStat {
        ATK_PERCENT("ATK%", "Ataque porcentual", true),
        DEF_PERCENT("DEF%", "Defensa porcentual", true),
        HP_PERCENT("HP%", "HP porcentual", true),
        SPEED("Speed", "Velocidad", false),
        CRIT_RATE("Crit Rate%", "Probabilidad crítica", true),
        CRIT_DAMAGE("Crit DMG%", "Daño crítico", true),
        ACCURACY("Accuracy%", "Precisión", true),
        EVASION("Evasion%", "Evasión", true),
        LIFESTEAL("Lifesteal%", "Robo de vida", true),
        PENETRATION("Penetration%", "Penetración", true);

        private final String displayName;
        private final String description;
        private final boolean isPercentage;

        SecondaryStat(String displayName, String description, boolean isPercentage) {
            this.displayName = displayName;
            this.description = description;
            this.isPercentage = isPercentage;
        }

        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public boolean isPercentage() { return isPercentage; }

        public static SecondaryStat fromString(String statName) {
            for (SecondaryStat stat : values()) {
                if (stat.displayName.equals(statName)) {
                    return stat;
                }
            }
            return ATK_PERCENT;
        }

        public String formatValue(int value) {
            return isPercentage ? value + "%" : String.valueOf(value);
        }
    }

    /**
     * Filtros de equipamiento para búsquedas
     */
    public enum EquipmentFilter {
        ALL("all", "Todos"),
        EQUIPPED("equipped", "Equipados"),
        UNEQUIPPED("unequipped", "Sin equipar"),
        LOCKED("locked", "Bloqueados"),
        UNLOCKED("unlocked", "Desbloqueados"),
        CAN_ENHANCE("can_enhance", "Mejorables"),
        MAX_ENHANCED("max_enhanced", "Max mejorados");

        private final String value;
        private final String displayName;

        EquipmentFilter(String value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public String getValue() { return value; }
        public String getDisplayName() { return displayName; }

        public static EquipmentFilter fromValue(String value) {
            for (EquipmentFilter filter : values()) {
                if (filter.value.equals(value)) {
                    return filter;
                }
            }
            return ALL;
        }
    }

    /**
     * Opciones de ordenamiento para equipamiento
     */
    public enum EquipmentSort {
        POWER("power", "Poder", true),
        RARITY("rarity", "Rareza", true),
        TYPE("type", "Tipo", false),
        ENHANCEMENT("enhancement", "Mejora", true),
        OBTAINED("obtained", "Obtenido", true),
        NAME("name", "Nombre", false);

        private final String value;
        private final String displayName;
        private final boolean descendingDefault;

        EquipmentSort(String value, String displayName, boolean descendingDefault) {
            this.value = value;
            this.displayName = displayName;
            this.descendingDefault = descendingDefault;
        }

        public String getValue() { return value; }
        public String getDisplayName() { return displayName; }
        public boolean isDescendingDefault() { return descendingDefault; }

        public static EquipmentSort fromValue(String value) {
            for (EquipmentSort sort : values()) {
                if (sort.value.equals(value)) {
                    return sort;
                }
            }
            return POWER;
        }
    }

    /**
     * Resultados de operaciones de equipamiento
     */
    public enum EquipmentOperationResult {
        SUCCESS("Operación exitosa"),
        EQUIPMENT_NOT_FOUND("Equipamiento no encontrado"),
        ALREADY_EQUIPPED("Ya está equipado"),
        ALREADY_UNEQUIPPED("Ya está desequipado"),
        INSUFFICIENT_RESOURCES("Recursos insuficientes"),
        MAX_ENHANCEMENT_REACHED("Mejora máxima alcanzada"),
        CANNOT_MELT("No se puede forjar"),
        EQUIPMENT_LOCKED("Equipamiento bloqueado"),
        HERO_NOT_FOUND("Héroe no encontrado"),
        INVALID_OPERATION("Operación inválida"),
        DATABASE_ERROR("Error de base de datos");

        private final String message;

        EquipmentOperationResult(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }

        public boolean isSuccess() {
            return this == SUCCESS;
        }
    }
}