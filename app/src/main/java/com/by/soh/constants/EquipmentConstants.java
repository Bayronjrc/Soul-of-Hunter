package com.by.soh.constants;

public class EquipmentConstants {

    // Equipment rarity
    public static final int EQUIPMENT_GRAY = 1;      // Gris
    public static final int EQUIPMENT_GREEN = 2;     // Verde
    public static final int EQUIPMENT_BLUE = 3;      // Azul
    public static final int EQUIPMENT_PURPLE = 4;    // Púrpura
    public static final int EQUIPMENT_ORANGE = 5;    // Naranja
    public static final int EQUIPMENT_RED = 6;       // Rojo

    public static final String[] EQUIPMENT_RARITY_NAMES = {
            "", "Gris", "Verde", "Azul", "Púrpura", "Naranja", "Rojo"
    };

    public static final String[] EQUIPMENT_RARITY_COLORS = {
            "", "#808080", "#00FF00", "#0080FF", "#8000FF", "#FF8000", "#FF0000"
    };

    public static final int[] RARITY_MULTIPLIERS = {
            0,  // Placeholder para índice 0
            1,  // EQUIPMENT_GRAY
            3,  // EQUIPMENT_GREEN
            5,  // EQUIPMENT_BLUE
            8,  // EQUIPMENT_PURPLE
            12, // EQUIPMENT_ORANGE
            20  // EQUIPMENT_RED
    };

    // Equipment types
    public static final int EQUIPMENT_WEAPON = 1;        // Zanpakutō/Armas
    public static final int EQUIPMENT_ARMOR = 2;         // Armadura/Ropa
    public static final int EQUIPMENT_ACCESSORY = 3;     // Accesorios
    public static final int EQUIPMENT_BOOTS = 4;         // Calzado
    public static final int EQUIPMENT_GLOVES = 5;        // Guantes
    public static final int EQUIPMENT_HELMET = 6;        // Casco/Máscara

    public static final String[] EQUIPMENT_TYPE_NAMES = {
            "", "Zanpakutō", "Armadura", "Accesorio", "Calzado", "Guantes", "Casco"
    };

    // Equipment slots
    public static final int MAX_EQUIPMENT_SLOTS = 6;

    // Equipment enhancement
    public static final int MIN_EQUIPMENT_ENHANCEMENT = 0;
    public static final int MAX_EQUIPMENT_ENHANCEMENT = 9;
    // Multiplicadores de stats por refinamiento
    public static final float EQUIPMENT_ENHANCEMENT_MULTIPLIER = 0.12f; // +12% por +


    // Equipment base stats
    // Stats base para equipamiento Gris (rareza 1)
    public static final int[] BASE_WEAPON_ATK = {0, 100, 125, 150, 200, 275, 400}; // Por rareza
    public static final int[] BASE_ARMOR_DEF = {0, 80, 100, 120, 160, 220, 320};
    public static final int[] BASE_ACCESSORY_HP = {0, 200, 250, 300, 400, 550, 800};
    public static final int[] BASE_BOOTS_SPEED = {0, 15, 20, 25, 35, 50, 75};
    public static final int[] BASE_GLOVES_CRIT = {0, 5, 8, 12, 18, 25, 40}; // % crit rate
    public static final int[] BASE_HELMET_RESISTANCE = {0, 10, 15, 20, 30, 45, 70}; // % resistencia

    // Rejuve
    public static final int[] REJUVE_GOLD_COST = {0, 1000, 2000, 5000, 10000, 25000, 50000};
    public static final int[] REJUVE_GEM_COST = {0, 0, 0, 10, 25, 50, 100};

    // Probabilidades de mejora en reforja
    public static final float[] REJUVE_UPGRADE_CHANCE = {0f, 0.8f, 0.6f, 0.4f, 0.25f, 0.15f, 0.1f};

    // Melt
    public static final int MELT_ITEMS_REQUIRED = 5; // 5 items → 1-3 items de rareza superior

    // Probabilidades de obtener 1, 2 o 3 items en forja
    public static final float MELT_1_ITEM_CHANCE = 0.6f; // 60%
    public static final float MELT_2_ITEM_CHANCE = 0.3f; // 30%
    public static final float MELT_3_ITEM_CHANCE = 0.1f; // 10%

    // No se puede forjar equipamiento Rojo (máxima rareza)
    public static final int MAX_MELT_RARITY = EQUIPMENT_ORANGE;

    // Secondary attributes random
    public static final String[] SECONDARY_STATS = {
            "ATK%", "DEF%", "HP%", "Speed", "Crit Rate%", "Crit DMG%",
            "Accuracy%", "Evasion%", "Lifesteal%", "Penetration%"
    };

    // Rangos de valores para stats secundarios (min, max por rareza)
    public static final int[][] SECONDARY_STAT_RANGES = {
            {0, 0},      // Placeholder
            {1, 3},      // Gris: 1-3%
            {2, 5},      // Verde: 2-5%
            {3, 8},      // Azul: 3-8%
            {5, 12},     // Púrpura: 5-12%
            {8, 18},     // Naranja: 8-18%
            {12, 25}     // Rojo: 12-25%
    };

    // Número de stats secundarios por rareza
    public static final int[] SECONDARY_STATS_COUNT = {0, 0, 1, 1, 2, 2, 3};

    // Equipment sets
    public static final int SET_NONE = 0;
    public static final int SET_GOTEI_13 = 1;        // Set Shinigami
    public static final int SET_ESPADA = 2;          // Set Arrancar
    public static final int SET_STERNRITTER = 3;     // Set Quincy
    public static final int SET_VIZARD = 4;          // Set Híbrido
    public static final int SET_FULLBRING = 5;       // Set Fullbringer
    public static final int SET_HOLLOW = 6;          // Set Hollow

    public static final String[] SET_NAMES = {
            "Sin Set", "Gotei 13", "Espada", "Sternritter",
            "Vizard", "Fullbring", "Hollow"
    };

    // Bonos de set (2 piezas, 4 piezas, 6 piezas)
    public static final String[] SET_BONUSES_2_PIECES = {
            "",
            "Gotei 13 (2): +15% EXP ganada",
            "Espada (2): +20% Penetración de Armadura",
            "Sternritter (2): +25% Precisión",
            "Vizard (2): +20% a todas las estadísticas",
            "Fullbring (2): +30% Resistencia a debuffs",
            "Hollow (2): +25% Daño crítico"
    };

    public static final String[] SET_BONUSES_4_PIECES = {
            "",
            "Gotei 13 (4): +25% EXP + Habilidades cuestan -20% energía",
            "Espada (4): +35% Penetración + Ignora 20% defensa enemiga",
            "Sternritter (4): +40% Precisión + 30% probabilidad de ataque doble",
            "Vizard (4): +35% todas las stats + Resistencia a efectos negativos",
            "Fullbring (4): +50% Resistencia + Inmunidad a control de masas",
            "Hollow (4): +40% Daño crítico + Lifesteal del 25%"
    };

    public static final String[] SET_BONUSES_6_PIECES = {
            "",
            "Gotei 13 (6): Todos los bonos anteriores + Bankai disponible 2 veces por batalla",
            "Espada (6): Todos los bonos anteriores + Resurreción automática al 50% HP",
            "Sternritter (6): Todos los bonos anteriores + Vollständig: +100% todas las stats por 15 segundos",
            "Vizard (6): Todos los bonos anteriores + Hollowficación: Transformación definitiva disponible",
            "Fullbring (6): Todos los bonos anteriores + Fullbring: Manipula el campo de batalla",
            "Hollow (6): Todos los bonos anteriores + Cero Oscuras: Ataque definitivo devastador"
    };

    // equipment upgrade cost
    // Costo base de oro para mejorar equipamiento
    public static final long[] EQUIPMENT_UPGRADE_BASE_COST = {
            0, 500, 1000, 2500, 5000, 12500, 25000
    };

    // Materiales especiales requeridos para ciertas mejoras
    public static final int SPECIAL_MATERIAL_REQUIRED_ENHANCEMENT = 6; // +6 en adelante


    // Drops and probabilities
    // Probabilidad de drop de equipamiento por rareza en niveles normales
    public static final float[] EQUIPMENT_DROP_RATES = {
            0f, 0.4f, 0.3f, 0.2f, 0.08f, 0.019f, 0.001f
    }; // Gris=40%, Verde=30%, etc.

    // Bonus de drop por completar nivel por primera vez
    public static final float FIRST_CLEAR_BONUS_RATE = 2.0f; // x2 probabilidades

    // Inventory limit
    public static final int MAX_EQUIPMENT_INVENTORY = 300;
    public static final int INVENTORY_EXPANSION_COST = 100; // Gems por 10 slots adicionales

    // filtros y organización
    public static final String[] SORT_OPTIONS = {
            "Poder", "Rareza", "Tipo", "Nivel", "Nombre"
    };

    public static final String[] FILTER_OPTIONS = {
            "Todos", "Equipado", "No Equipado", "Favoritos"
    };

    // Helper methods
    /**
     * Obtiene el stat base de un equipamiento según tipo y rareza
     */
    public static int getBaseEquipmentStat(int type, int rarity) {
        if (rarity < 1 || rarity >= BASE_WEAPON_ATK.length) return 0;

        switch (type) {
            case EQUIPMENT_WEAPON:
                return BASE_WEAPON_ATK[rarity];
            case EQUIPMENT_ARMOR:
                return BASE_ARMOR_DEF[rarity];
            case EQUIPMENT_ACCESSORY:
                return BASE_ACCESSORY_HP[rarity];
            case EQUIPMENT_BOOTS:
                return BASE_BOOTS_SPEED[rarity];
            case EQUIPMENT_GLOVES:
                return BASE_GLOVES_CRIT[rarity];
            case EQUIPMENT_HELMET:
                return BASE_HELMET_RESISTANCE[rarity];
            default:
                return 0;
        }
    }

    /**
     * Calcula el costo de mejora de equipamiento
     */
    public static long getEquipmentUpgradeCost(int rarity, int currentEnhancement) {
        if (rarity < 1 || rarity >= EQUIPMENT_UPGRADE_BASE_COST.length) return 0;

        long baseCost = EQUIPMENT_UPGRADE_BASE_COST[rarity];
        long enhancementMultiplier = (long) Math.pow(currentEnhancement + 1, 1.8);

        return baseCost * enhancementMultiplier;
    }

    /**
     * Verifica si el equipamiento puede ser forjado (melted)
     */
    public static boolean canMeltEquipment(int rarity) {
        return rarity >= EQUIPMENT_GRAY && rarity <= MAX_MELT_RARITY;
    }

    /**
     * Obtiene el nombre de rareza de equipamiento
     */
    public static String getEquipmentRarityName(int rarity) {
        if (rarity >= 1 && rarity < EQUIPMENT_RARITY_NAMES.length) {
            return EQUIPMENT_RARITY_NAMES[rarity];
        }
        return "Desconocido";
    }

    /**
     * Obtiene el color hex de rareza de equipamiento
     */
    public static String getEquipmentRarityColor(int rarity) {
        if (rarity >= 1 && rarity < EQUIPMENT_RARITY_COLORS.length) {
            return EQUIPMENT_RARITY_COLORS[rarity];
        }
        return "#FFFFFF";
    }

    /**
     * Obtiene el nombre del tipo de equipamiento
     */
    public static String getEquipmentTypeName(int type) {
        if (type >= 1 && type < EQUIPMENT_TYPE_NAMES.length) {
            return EQUIPMENT_TYPE_NAMES[type];
        }
        return "Desconocido";
    }

    /**
     * Calcula el número de stats secundarios para una rareza
     */
    public static int getSecondaryStatsCount(int rarity) {
        if (rarity >= 1 && rarity < SECONDARY_STATS_COUNT.length) {
            return SECONDARY_STATS_COUNT[rarity];
        }
        return 0;
    }

    /**
     * Obtiene el rango de valores para un stat secundario
     */
    public static int[] getSecondaryStatRange(int rarity) {
        if (rarity >= 1 && rarity < SECONDARY_STAT_RANGES.length) {
            return SECONDARY_STAT_RANGES[rarity].clone();
        }
        return new int[]{0, 0};
    }

    /**
     * Verifica cuántas piezas de un set tiene equipado un héroe
     */
    public static int getSetPiecesEquipped(int[] equippedSets, int setId) {
        int count = 0;
        for (int set : equippedSets) {
            if (set == setId) count++;
        }
        return count;
    }

    /**
     * Calcula el poder total de una pieza de equipamiento
     */
    public static long calculateEquipmentPower(int type, int rarity, int enhancement,
                                               int[] secondaryStats) {
        long basePower = getBaseEquipmentStat(type, rarity);

        // Aplicar multiplicador de enhancement
        float enhancementMultiplier = 1.0f + (enhancement * EQUIPMENT_ENHANCEMENT_MULTIPLIER);
        basePower = Math.round(basePower * enhancementMultiplier);

        // Añadir valor de stats secundarios
        int secondaryPower = 0;
        for (int stat : secondaryStats) {
            secondaryPower += stat * 10; // Factor arbitrario para convertir % en poder
        }

        return basePower + secondaryPower;
    }

    /**
     * Genera stats secundarios aleatorios para equipamiento
     */
    public static int[] generateRandomSecondaryStats(int rarity) {
        int count = getSecondaryStatsCount(rarity);
        if (count == 0) return new int[0];

        int[] stats = new int[count];
        int[] range = getSecondaryStatRange(rarity);

        for (int i = 0; i < count; i++) {
            stats[i] = range[0] + (int)(Math.random() * (range[1] - range[0] + 1));
        }

        return stats;
    }


    public static int getRarityMultiplier(int rarity) {
        if (rarity >= 1 && rarity < RARITY_MULTIPLIERS.length) {
            return RARITY_MULTIPLIERS[rarity];
        }
        // Valor por defecto o manejo de error si la rareza no está en el array
        return 1; // O podrías lanzar IllegalArgumentException
    }
}
