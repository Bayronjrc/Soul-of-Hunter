package com.by.soh.constants;

public class HeroConstants {

    // Factions
    public static final int FACTION_SHINIGAMI = 1;
    public static final int FACTION_HOLLOW = 2;
    public static final int FACTION_QUINCY = 3;
    public static final int FACTION_ARRANCAR = 4;
    public static final int FACTION_HUMAN = 5;
    public static final int FACTION_FULLBRING = 6;
    public static final String[] FACTION_NAMES = {
            "", "Shinigami", "Hollow", "Quincy", "Arrancar", "Humano", "Fullbring"
    };

    // Spiritual attributes
    public static final int ATTRIBUTE_POWER = 1;
    public static final int ATTRIBUTE_SOUL = 2;
    public static final int ATTRIBUTE_CORE = 3;
    public static final int ATTRIBUTE_MIND = 4;
    public static final int ATTRIBUTE_HEART = 5;
    public static final int ATTRIBUTE_VOID = 6;

    public static final String[] ATTRIBUTE_NAMES = {
            "", "Power", "Soul", "Core", "Mind", "Heart", "Void"
    };
    public static final String[] ATTRIBUTE_DESCRIPTIONS = {
            "",
            "Power: +25% ATK, +15% Velocidad de Ataque",
            "Soul: +30% HP, +20% Defensa Espiritual",
            "Core: +20% ATK y DEF balanceados, +10% Crítico",
            "Mind: +25% Habilidades Especiales, -15% Cooldown",
            "Heart: +35% Sanación, +20% Soporte a Aliados",
            "Void: +30% Penetración, Ignora 15% Defensa Enemiga"
    };

    // Combat role
    public static final int ROLE_TANK = 1;
    public static final int ROLE_HEALER = 2;
    public static final int ROLE_ASSASSIN = 3;
    public static final int ROLE_RANGE = 4;
    public static final int ROLE_SUPPORT = 5;
    public static final int ROLE_BERSERKER = 6;
    public static final int ROLE_CONTROLLER = 7;
    public static final int ROLE_MAGE = 8;

    public static final String[] ROLE_NAMES = {
            "", "Tank", "Healer", "Assassin", "Range", "Support", "Berserker", "Controller", "Mage"
    };
    public static final String[] ROLE_DESCRIPTIONS = {
            "",
            "Tank: Alta HP y Defensa, atrae ataques enemigos",
            "Healer: Sana aliados, puede revivir caídos",
            "Assassin: Alto daño crítico, ataca objetivos débiles",
            "Range: Daño consistente desde la retaguardia",
            "Support: Buffs a aliados, debuffs a enemigos",
            "Berserker: Más daño al bajar HP, sacrifica defensa por poder",
            "Controller: Efectos de control - stun, slow, silence, veneno",
            "Mage: Especialista en Kido/Magia - AoE damage, buffs/debuffs potentes"
    };

    // Base stats for rarities
    public static final int[] BASE_HP_BY_ROLE = {
            0, 1500, 1000, 800, 900, 1100, 1200, 1000, 950 // Tank=1500, Healer=1000, etc.
    };

    public static final int[] BASE_ATK_BY_ROLE = {
            0, 120, 90, 180, 150, 100, 170, 130, 140
    };

    public static final int[] BASE_DEF_BY_ROLE = {
            0, 200, 120, 80, 110, 130, 90, 140, 120
    };

    public static final int[] BASE_SPEED_BY_ROLE = {
            0, 80, 90, 120, 100, 95, 110, 105, 85
    };

    public static final int[] BASE_DEF_MAGIC_BY_ROLE = {
            0, 180, 150, 70, 100, 140, 80, 160, 170
    };

    // Attribute bonus multipliers
    // Bonos de Power
    public static final float POWER_ATK_BONUS = 0.25f; // +25% ATK
    public static final float POWER_ATTACK_SPEED_BONUS = 0.15f; // +15% Velocidad Ataque

    // Bonos de Soul
    public static final float SOUL_HP_BONUS = 0.30f; // +30% HP
    public static final float SOUL_SPIRITUAL_DEF_BONUS = 0.20f; // +20% Def Espiritual

    // Bonos de Core
    public static final float CORE_BALANCED_BONUS = 0.20f; // +20% ATK y DEF
    public static final float CORE_CRIT_BONUS = 0.10f; // +10% Crítico

    // Bonos de Mind
    public static final float MIND_SPECIAL_SKILLS_BONUS = 0.25f; // +25% Habilidades Especiales
    public static final float MIND_COOLDOWN_REDUCTION = 0.15f; // -15% Cooldown

    // Bonos de Heart
    public static final float HEART_HEALING_BONUS = 0.35f; // +35% Sanación
    public static final float HEART_SUPPORT_BONUS = 0.20f; // +20% Soporte

    // Bonos de Void
    public static final float VOID_PENETRATION_BONUS = 0.30f; // +30% Penetración
    public static final float VOID_IGNORE_DEF_BONUS = 0.15f; // Ignora 15% Defensa

    // Spiritual resonancy
    // Bonos por Facción (requiere 3+ héroes)
    public static final int FACTION_BONUS_REQUIREMENT = 3;

    public static final float SHINIGAMI_EXP_BONUS = 0.15f; // +15% EXP
    public static final float HOLLOW_DAMAGE_VS_NON_HOLLOW = 0.20f; // +20% vs no-Hollow
    public static final float QUINCY_ACCURACY_CRIT_BONUS = 0.25f; // +25% Precisión y Crítico
    public static final float ARRANCAR_ARMOR_PENETRATION = 0.20f; // +20% Penetración Armadura
    public static final float HUMAN_DEBUFF_RESISTANCE = 0.30f; // +30% Resistencia debuffs
    public static final float FULLBRING_ALL_STATS_BONUS = 0.20f; // +20% todas las stats

    // Bonos por Atributo (requiere 4+ héroes)
    public static final int ATTRIBUTE_BONUS_REQUIREMENT = 4;

    public static final float POWER_4_ATK_BONUS = 0.30f; // +30% ATK
    public static final float POWER_4_ATTACK_SPEED_BONUS = 0.20f; // +20% Velocidad
    public static final float SOUL_4_HP_BONUS = 0.40f; // +40% HP
    public static final float SOUL_4_REGENERATION = 0.25f; // +25% Regeneración
    public static final float CORE_4_ALL_STATS = 0.25f; // +25% todas las stats
    public static final float MIND_4_ENERGY_REDUCTION = 0.25f; // -25% coste energía
    public static final float HEART_4_GROUP_HEAL = 0.05f; // 5% HP por turno al equipo
    public static final float VOID_4_IGNORE_DEF_CHANCE = 0.20f; // 20% probabilidad ignorar defensa

    // Epic combination
    // 3 Power + 2 Void: "Fuerza Destructiva"
    public static final float EPIC_DESTRUCTIVE_FORCE_CRIT_DMG = 0.50f; // +50% daño crítico
    public static final float EPIC_DESTRUCTIVE_FORCE_PENETRATION = 0.25f; // +25% penetración

    // 4 Soul + 1 Heart: "Barrera Espiritual"
    public static final float EPIC_SPIRITUAL_BARRIER_RESISTANCE = 0.30f; // +30% resistencia
    // + inmunidad a debuffs

    // 5 Core: "Equilibrio Perfecto"
    public static final float EPIC_PERFECT_BALANCE_ALL_STATS = 0.25f; // +25% todas las stats
    // + habilidad "Armonía Universal"

    // 3 Mind + 2 Heart: "Sabiduría Compasiva"
    public static final float EPIC_WISE_COMPASSION_COOLDOWN = 0.40f; // -40% cooldowns
    public static final float EPIC_WISE_COMPASSION_HEALING = 0.60f; // +60% sanación

    // 2 Power + 2 Soul + 1 Void: "Trinidad Destructora"
    public static final float EPIC_TRINITY_DESTROYER_ATK = 0.35f; // +35% ATK
    public static final float EPIC_TRINITY_DESTROYER_HP = 0.20f; // +20% HP
    public static final float EPIC_TRINITY_DESTROYER_IGNORE_ARMOR = 0.30f; // ignora 30% armadura

    // Heroes shards
    public static final int SHARDS_TO_SUMMON_COMMON = 10;
    public static final int SHARDS_TO_SUMMON_RARE = 20;
    public static final int SHARDS_TO_SUMMON_EPIC = 50;
    public static final int SHARDS_TO_SUMMON_LEGENDARY = 80;
    public static final int SHARDS_TO_SUMMON_MYTHIC = 120;

    // Shards para upgrade de estrellas
    public static final int SHARDS_FOR_STAR_UPGRADE = 20; // Base, se multiplica por nivel de estrella

    // Skill System
    // Tipos de habilidades
    public static final int SKILL_TYPE_BASIC = 1;
    public static final int SKILL_TYPE_SPECIAL = 2;
    public static final int SKILL_TYPE_ULTIMATE = 3;

    // Costes de energía base
    public static final int BASIC_SKILL_ENERGY_COST = 0;
    public static final int SPECIAL_SKILL_ENERGY_COST = 50;
    public static final int ULTIMATE_SKILL_ENERGY_COST = 100;

    // Cooldowns base (en turnos)
    public static final int BASIC_SKILL_COOLDOWN = 0;
    public static final int SPECIAL_SKILL_COOLDOWN = 3;
    public static final int ULTIMATE_SKILL_COOLDOWN = 5;

    // Elements and weakness
    // Ventajas de tipo (multiplicador de daño)
    public static final float QUINCY_VS_HOLLOW_MULTIPLIER = 1.3f; // Quincy vs Hollow
    public static final float SHINIGAMI_VS_HOLLOW_MULTIPLIER = 1.2f; // Shinigami vs Hollow
    public static final float ARRANCAR_VS_SHINIGAMI_MULTIPLIER = 1.15f; // Arrancar vs Shinigami

    // Heroes Limits
    public static final int MAX_HEROES_IN_INVENTORY = 200;
    public static final int MAX_TEAM_FORMATIONS = 5; // Múltiples formaciones guardadas

    // extra stats
    // Fórmulas para stats derivadas
    public static final float CRIT_RATE_BASE = 0.05f; // 5% base
    public static final float CRIT_DAMAGE_BASE = 1.5f; // +50% daño en crítico
    public static final float ACCURACY_BASE = 0.95f; // 95% precisión base
    public static final float EVASION_BASE = 0.05f; // 5% evasión base

    // Helper Methods
    /**
     * Obtiene las stats base para un héroe según su rol y rareza
     */
    public static int getBaseStat(int role, int rarity, String statType) {
        int baseStat;

        switch (statType.toLowerCase()) {
            case "hp":
                baseStat = BASE_HP_BY_ROLE[role];
                break;
            case "atk":
                baseStat = BASE_ATK_BY_ROLE[role];
                break;
            case "def":
                baseStat = BASE_DEF_BY_ROLE[role];
                break;
            case "speed":
                baseStat = BASE_SPEED_BY_ROLE[role];
                break;
            default:
                return 0;
        }

        // Aplicar multiplicador de rareza
        return Math.round(baseStat * GameConstants.getRarityMultiplier(rarity));
    }

    /**
     * Calcula los shards necesarios para invocar un héroe según rareza
     */
    public static int getShardsRequired(int rarity) {
        switch (rarity) {
            case GameConstants.COMMON_RARITY: return SHARDS_TO_SUMMON_COMMON;
            case GameConstants.RARE_RARITY: return SHARDS_TO_SUMMON_RARE;
            case GameConstants.EPIC_RARITY: return SHARDS_TO_SUMMON_EPIC;
            case GameConstants.LEGENDARY_RARITY: return SHARDS_TO_SUMMON_LEGENDARY;
            case GameConstants.MYTHIC_RARITY: return SHARDS_TO_SUMMON_MYTHIC;
            default: return SHARDS_TO_SUMMON_COMMON;
        }
    }

    /**
     * Verifica si una composición de equipo cumple requisitos para bonus de facción
     */
    public static boolean hasFactionBonus(int[] factionCount, int faction) {
        return factionCount[faction] >= FACTION_BONUS_REQUIREMENT;
    }

    /**
     * Verifica si una composición de equipo cumple requisitos para bonus de atributo
     */
    public static boolean hasAttributeBonus(int[] attributeCount, int attribute) {
        return attributeCount[attribute] >= ATTRIBUTE_BONUS_REQUIREMENT;
    }

    /**
     * Obtiene el nombre de la facción por ID
     */
    public static String getFactionName(int faction) {
        if (faction >= 1 && faction < FACTION_NAMES.length) {
            return FACTION_NAMES[faction];
        }
        return "Desconocido";
    }

    /**
     * Obtiene el nombre del atributo por ID
     */
    public static String getAttributeName(int attribute) {
        if (attribute >= 1 && attribute < ATTRIBUTE_NAMES.length) {
            return ATTRIBUTE_NAMES[attribute];
        }
        return "Desconocido";
    }

    /**
     * Obtiene el nombre del rol por ID
     */
    public static String getRoleName(int role) {
        if (role >= 1 && role < ROLE_NAMES.length) {
            return ROLE_NAMES[role];
        }
        return "Desconocido";
    }

    /**
     * Calcula el poder total aproximado de un héroe
     */
    public static long calculateHeroPower(int hp, int atk, int def, int speed,
                                          int rarity, int stars, int enhancement) {
        float rarityMult = GameConstants.getRarityMultiplier(rarity);
        float starMult = GameConstants.getStarMultiplier(stars);
        float enhanceMult = GameConstants.getEnhancementMultiplier(enhancement);

        float totalMult = rarityMult * starMult * enhanceMult;

        return Math.round((hp * 0.5 + atk * 2.0 + def * 1.5 + speed * 0.5) * totalMult);
    }
}
