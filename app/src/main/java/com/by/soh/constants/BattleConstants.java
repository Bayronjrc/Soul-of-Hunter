package com.by.soh.constants;

public class BattleConstants {
    // Basic combat configuration
    public static final int MAX_BATTLE_DURATION_SECONDS = 120; // 2 minutos máximo
    public static final int BATTLE_TURN_DURATION_MS = 1000; // 1 segundo por turno base

    // Velocidades de combate
    public static final float[] BATTLE_SPEEDS = {1.0f, 2.0f, 4.0f}; // x1, x2, x4
    public static final String[] BATTLE_SPEED_NAMES = {"x1", "x2", "x4"};

    // Mana System
    public static final int MAX_ENERGY = 100;
    public static final int INITIAL_ENERGY = 0;
    public static final int ENERGY_PER_TURN = 10; // Energía ganada por turno
    public static final int ENERGY_ON_DAMAGE_TAKEN = 15; // Energía extra al recibir daño
    public static final int ENERGY_ON_KILL = 25; // Energía extra al eliminar enemigo

    // Damage System
    // Multiplicadores de daño crítico
    public static final float BASE_CRIT_DAMAGE = 1.5f; // +50% daño en crítico
    public static final float MAX_CRIT_DAMAGE = 3.0f;  // +200% máximo

    // Precisión y evasión base
    public static final float BASE_ACCURACY = 0.95f; // 95% precisión
    public static final float BASE_EVASION = 0.05f;  // 5% evasión
    public static final float MAX_ACCURACY = 1.0f;   // 100% máximo
    public static final float MAX_EVASION = 0.75f;   // 75% máximo

    // Resistencias base
    public static final float BASE_DEBUFF_RESISTANCE = 0.1f; // 10% resistencia a debuffs
    public static final float MAX_DEBUFF_RESISTANCE = 0.9f;  // 90% máximo

    // Battle formulas
    // Factor de defensa (reduce daño recibido)
    public static final float DEFENSE_FACTOR = 0.005f; // 1 punto DEF = 0.5% reducción
    public static final float MAX_DAMAGE_REDUCTION = 0.8f; // 80% reducción máxima

    // Factor de velocidad (determina orden de turnos)
    public static final float SPEED_RANDOMNESS = 0.15f; // ±15% variación aleatoria en velocidad

    // Battle effects
    // Tipos de efectos
    public static final int EFFECT_NONE = 0;
    public static final int EFFECT_POISON = 1;        // Daño por turno
    public static final int EFFECT_BURN = 2;          // Daño por turno (fire)
    public static final int EFFECT_BLEEDING = 3;      // Daño por turno (físico)
    public static final int EFFECT_STUN = 4;          // No puede actuar
    public static final int EFFECT_SILENCE = 5;       // No puede usar habilidades
    public static final int EFFECT_SLOW = 6;          // -50% velocidad
    public static final int EFFECT_FREEZE = 7;        // No puede actuar por 1 turno
    public static final int EFFECT_SLEEP = 8;         // No puede actuar, se quita al recibir daño
    public static final int EFFECT_CONFUSION = 9;     // 50% chance de atacar aliados
    public static final int EFFECT_WEAKNESS = 10;     // -30% ATK
    public static final int EFFECT_ARMOR_BREAK = 11;  // -50% DEF
    public static final int EFFECT_BLIND = 12;        // -70% precisión

    // Efectos positivos (buffs)
    public static final int BUFF_ATK_UP = 13;         // +30% ATK
    public static final int BUFF_DEF_UP = 14;         // +30% DEF
    public static final int BUFF_SPEED_UP = 15;       // +50% velocidad
    public static final int BUFF_CRIT_UP = 16;        // +25% crit rate
    public static final int BUFF_IMMUNITY = 17;       // Inmune a debuffs
    public static final int BUFF_REGENERATION = 18;   // +5% HP por turno
    public static final int BUFF_DAMAGE_SHIELD = 19;  // Absorbe X cantidad de daño
    public static final int BUFF_REFLECT_DAMAGE = 20; // Refleja 50% del daño recibido

    public static final String[] EFFECT_NAMES = {
            "Ninguno", "Veneno", "Quemadura", "Sangrado", "Aturdimiento", "Silencio",
            "Lentitud", "Congelamiento", "Sueño", "Confusión", "Debilidad",
            "Armadura Rota", "Ceguera", "ATK Aumentado", "DEF Aumentada",
            "Velocidad Aumentada", "Crítico Aumentado", "Inmunidad", "Regeneración",
            "Escudo de Daño", "Reflejo de Daño"
    };

    // Duración base de efectos (en turnos)
    public static final int[] EFFECT_BASE_DURATION = {
            0, 3, 3, 4, 2, 3, 4, 1, 2, 3, 4, 3, 3, 4, 4, 3, 3, 5, 5, 3, 3
    };

    // chain and combo system
    public static final int MAX_COMBO_COUNT = 10;
    public static final float COMBO_DAMAGE_BONUS = 0.05f; // +5% daño por combo
    public static final int COMBO_RESET_TIME_SECONDS = 5;  // Se resetea si no hay acción

    // Faction res
    // Resistencias específicas (multiplicador de daño recibido)
    public static final float SHINIGAMI_VS_KIDO_RESISTANCE = 0.8f;    // -20% daño de Kido
    public static final float HOLLOW_VS_SPIRITUAL_RESISTANCE = 0.9f;  // -10% daño espiritual
    public static final float QUINCY_VS_HOLLOW_EFFECTIVENESS = 1.3f;  // +30% vs Hollow
    public static final float ARRANCAR_VS_SHINIGAMI_BONUS = 1.15f;    // +15% vs Shinigami
    public static final float HUMAN_VS_DEBUFF_RESISTANCE = 0.7f;      // -30% duración debuffs
    public static final float FULLBRING_ADAPTABILITY = 0.95f;         // -5% a todo tipo de daño

    // ==================== TIPOS DE ATAQUE ====================
    public static final int ATTACK_TYPE_PHYSICAL = 1;    // Físico normal
    public static final int ATTACK_TYPE_SPIRITUAL = 2;   // Energía espiritual
    public static final int ATTACK_TYPE_KIDO = 3;        // Magia/Kido
    public static final int ATTACK_TYPE_REIATSU = 4;     // Presión espiritual pura
    public static final int ATTACK_TYPE_CERO = 5;        // Ataques Cero (Hollow/Arrancar)
    public static final int ATTACK_TYPE_QUINCY_ARROW = 6; // Flechas Quincy

    public static final String[] ATTACK_TYPE_NAMES = {
            "", "Físico", "Espiritual", "Kido", "Reiatsu", "Cero", "Flecha Quincy"
    };

    // Battle position
    public static final int POSITION_FRONT_LEFT = 1;
    public static final int POSITION_FRONT_CENTER = 2;
    public static final int POSITION_FRONT_RIGHT = 3;
    public static final int POSITION_BACK_LEFT = 4;
    public static final int POSITION_BACK_RIGHT = 5;

    public static final String[] POSITION_NAMES = {
            "", "Frente Izquierda", "Frente Centro", "Frente Derecha",
            "Atrás Izquierda", "Atrás Derecha"
    };

    // Multiplicadores de protección por posición
    public static final float[] POSITION_PROTECTION = {
            0f, 1.0f, 0.8f, 1.0f, 1.2f, 1.2f
    }; // Posición central frontal más vulnerable, atrás más protegida

    // Targeting System
    public static final int TARGET_RANDOM = 1;           // Aleatorio
    public static final int TARGET_LOWEST_HP = 2;        // Menor HP
    public static final int TARGET_HIGHEST_ATK = 3;      // Mayor ATK
    public static final int TARGET_FRONT_ROW = 4;        // Fila frontal
    public static final int TARGET_BACK_ROW = 5;         // Fila trasera
    public static final int TARGET_SAME_TYPE = 6;        // Mismo tipo/facción
    public static final int TARGET_OPPOSITE_TYPE = 7;    // Tipo opuesto
    public static final int TARGET_SUPPORT_FIRST = 8;    // Supporters primero


    // IA Configuration
    public static final float AI_SKILL_USE_PROBABILITY = 0.7f; // 70% chance de usar skill
    public static final float AI_ULTIMATE_USE_THRESHOLD = 0.3f; // Usar ultimate si HP < 30%
    public static final float AI_HEALING_THRESHOLD = 0.5f;      // Curar si aliado HP < 50%


    // Battle Reward
    public static final float EXP_PER_ENEMY_LEVEL = 50.0f;  // EXP base por nivel de enemigo
    public static final float GOLD_PER_ENEMY_LEVEL = 25.0f; // Oro base por nivel de enemigo

    // Bonificadores por rendimiento
    public static final float PERFECT_VICTORY_BONUS = 1.5f;  // +50% si no muere nadie
    public static final float QUICK_VICTORY_BONUS = 1.2f;    // +20% si termina en <30 segundos
    public static final float COMBO_BONUS_MULTIPLIER = 1.1f; // +10% por combos altos


    // Limits and validations
    public static final int MIN_BATTLE_PARTICIPANTS = 1;
    public static final int MAX_BATTLE_PARTICIPANTS = 5; // Por equipo
    public static final int MAX_SIMULTANEOUS_EFFECTS = 10; // Por personaje

    //Helper methods
    /**
     * Calcula la reducción de daño por defensa
     */
    public static float calculateDamageReduction(int defense) {
        float reduction = defense * DEFENSE_FACTOR;
        return Math.min(reduction, MAX_DAMAGE_REDUCTION);
    }

    /**
     * Calcula el daño final después de defensas y resistencias
     */
    public static int calculateFinalDamage(int baseDamage, int defense,
                                           float resistance, boolean isCrit) {
        // Aplicar crítico
        float damage = isCrit ? baseDamage * BASE_CRIT_DAMAGE : baseDamage;

        // Aplicar reducción por defensa
        float damageReduction = calculateDamageReduction(defense);
        damage *= (1.0f - damageReduction);

        // Aplicar resistencia específica
        damage *= resistance;

        return Math.max(1, Math.round(damage)); // Mínimo 1 de daño
    }

    /**
     * Determina si un ataque es crítico
     */
    public static boolean isCriticalHit(float critRate) {
        return Math.random() < Math.min(critRate, 1.0f);
    }

    /**
     * Determina si un ataque acierta
     */
    public static boolean doesAttackHit(float accuracy, float evasion) {
        float hitChance = Math.min(accuracy, MAX_ACCURACY) - Math.min(evasion, MAX_EVASION);
        return Math.random() < Math.max(hitChance, 0.05f); // Mínimo 5% de acierto
    }

    /**
     * Calcula la velocidad efectiva con variación aleatoria
     */
    public static float calculateEffectiveSpeed(int baseSpeed) {
        float variation = 1.0f + (float)(Math.random() - 0.5) * 2 * SPEED_RANDOMNESS;
        return baseSpeed * variation;
    }

    /**
     * Obtiene el nombre de un efecto por ID
     */
    public static String getEffectName(int effectId) {
        if (effectId >= 0 && effectId < EFFECT_NAMES.length) {
            return EFFECT_NAMES[effectId];
        }
        return "Desconocido";
    }

    /**
     * Verifica si un efecto es negativo (debuff)
     */
    public static boolean isDebuff(int effectId) {
        return effectId >= EFFECT_POISON && effectId <= EFFECT_BLIND;
    }

    /**
     * Verifica si un efecto es positivo (buff)
     */
    public static boolean isBuff(int effectId) {
        return effectId >= BUFF_ATK_UP && effectId <= BUFF_REFLECT_DAMAGE;
    }

    /**
     * Obtiene la duración base de un efecto
     */
    public static int getEffectDuration(int effectId) {
        if (effectId >= 0 && effectId < EFFECT_BASE_DURATION.length) {
            return EFFECT_BASE_DURATION[effectId];
        }
        return 0;
    }

    /**
     * Calcula la ventaja de tipo entre atacante y defensor
     */
    public static float getTypeAdvantage(int attackerFaction, int defenderFaction) {
        // Quincy vs Hollow
        if (attackerFaction == HeroConstants.FACTION_QUINCY &&
                defenderFaction == HeroConstants.FACTION_HOLLOW) {
            return QUINCY_VS_HOLLOW_EFFECTIVENESS;
        }

        // Shinigami vs Hollow
        if (attackerFaction == HeroConstants.FACTION_SHINIGAMI &&
                defenderFaction == HeroConstants.FACTION_HOLLOW) {
            return 1.2f; // +20% daño
        }

        // Arrancar vs Shinigami
        if (attackerFaction == HeroConstants.FACTION_ARRANCAR &&
                defenderFaction == HeroConstants.FACTION_SHINIGAMI) {
            return ARRANCAR_VS_SHINIGAMI_BONUS;
        }

        return 1.0f; // Sin ventaja
    }

    /**
     * Calcula la resistencia específica del defensor
     */
    public static float getDefenseResistance(int defenderFaction, int attackType) {
        switch (defenderFaction) {
            case HeroConstants.FACTION_SHINIGAMI:
                if (attackType == ATTACK_TYPE_KIDO) {
                    return SHINIGAMI_VS_KIDO_RESISTANCE;
                }
                break;

            case HeroConstants.FACTION_HOLLOW:
                if (attackType == ATTACK_TYPE_SPIRITUAL) {
                    return HOLLOW_VS_SPIRITUAL_RESISTANCE;
                }
                break;

            case HeroConstants.FACTION_FULLBRING:
                return FULLBRING_ADAPTABILITY;
        }

        return 1.0f; // Sin resistencia especial
    }

    /**
     * Determina el objetivo basado en la estrategia de targeting
     */
    public static int selectTarget(int targetingType, int[] availableTargets,
                                   int[] targetHp, int[] targetAtk) {
        if (availableTargets.length == 0) return -1;

        switch (targetingType) {
            case TARGET_RANDOM:
                return availableTargets[(int)(Math.random() * availableTargets.length)];

            case TARGET_LOWEST_HP:
                int lowestHpIndex = 0;
                for (int i = 1; i < availableTargets.length; i++) {
                    if (targetHp[availableTargets[i]] < targetHp[availableTargets[lowestHpIndex]]) {
                        lowestHpIndex = i;
                    }
                }
                return availableTargets[lowestHpIndex];

            case TARGET_HIGHEST_ATK:
                int highestAtkIndex = 0;
                for (int i = 1; i < availableTargets.length; i++) {
                    if (targetAtk[availableTargets[i]] > targetAtk[availableTargets[highestAtkIndex]]) {
                        highestAtkIndex = i;
                    }
                }
                return availableTargets[highestAtkIndex];

            case TARGET_FRONT_ROW:
                // Priorizar posiciones frontales (1, 2, 3)
                for (int target : availableTargets) {
                    if (target <= POSITION_FRONT_RIGHT) {
                        return target;
                    }
                }
                break;

            case TARGET_BACK_ROW:
                // Priorizar posiciones traseras (4, 5)
                for (int target : availableTargets) {
                    if (target >= POSITION_BACK_LEFT) {
                        return target;
                    }
                }
                break;
        }

        // Fallback a aleatorio
        return availableTargets[(int)(Math.random() * availableTargets.length)];
    }

    /**
     * Calcula el multiplicador de protección por posición
     */
    public static float getPositionProtection(int position) {
        if (position >= 1 && position < POSITION_PROTECTION.length) {
            return POSITION_PROTECTION[position];
        }
        return 1.0f;
    }

    /**
     * Obtiene el nombre de una posición
     */
    public static String getPositionName(int position) {
        if (position >= 1 && position < POSITION_NAMES.length) {
            return POSITION_NAMES[position];
        }
        return "Desconocida";
    }

    /**
     * Obtiene el nombre de un tipo de ataque
     */
    public static String getAttackTypeName(int attackType) {
        if (attackType >= 1 && attackType < ATTACK_TYPE_NAMES.length) {
            return ATTACK_TYPE_NAMES[attackType];
        }
        return "Desconocido";
    }

    /**
     * Calcula la energía ganada por recibir daño
     */
    public static int calculateEnergyFromDamage(int damageTaken, int maxHp) {
        float damagePercent = (float)damageTaken / maxHp;
        return Math.round(ENERGY_ON_DAMAGE_TAKEN * damagePercent);
    }

    /**
     * Verifica si un efecto previene el uso de habilidades
     */
    public static boolean preventsSkillUse(int effectId) {
        return effectId == EFFECT_STUN || effectId == EFFECT_SILENCE ||
                effectId == EFFECT_FREEZE || effectId == EFFECT_SLEEP;
    }

    /**
     * Verifica si un efecto previene cualquier acción
     */
    public static boolean preventsAction(int effectId) {
        return effectId == EFFECT_STUN || effectId == EFFECT_FREEZE ||
                effectId == EFFECT_SLEEP;
    }

    /**
     * Calcula el daño por turno de efectos DoT (Damage over Time)
     */
    public static int calculateDotDamage(int effectId, int maxHp, int atk) {
        switch (effectId) {
            case EFFECT_POISON:
                return Math.round(maxHp * 0.08f); // 8% HP máximo por turno
            case EFFECT_BURN:
                return Math.round(atk * 0.3f); // 30% ATK por turno
            case EFFECT_BLEEDING:
                return Math.round(maxHp * 0.05f); // 5% HP máximo por turno
            default:
                return 0;
        }
    }

    /**
     * Calcula la sanación por turno de efectos de regeneración
     */
    public static int calculateHealingOverTime(int effectId, int maxHp) {
        switch (effectId) {
            case BUFF_REGENERATION:
                return Math.round(maxHp * 0.05f); // 5% HP máximo por turno
            default:
                return 0;
        }
    }

    /**
     * Determina si un combate debe terminar por tiempo
     */
    public static boolean isBattleTimedOut(long battleStartTime) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - battleStartTime) > (MAX_BATTLE_DURATION_SECONDS * 1000);
    }

    /**
     * Calcula el bonus de experiencia por rendimiento en combate
     */
    public static float calculateExpBonus(boolean perfectVictory, boolean quickVictory,
                                          int maxCombo) {
        float bonus = 1.0f;

        if (perfectVictory) {
            bonus *= PERFECT_VICTORY_BONUS;
        }

        if (quickVictory) {
            bonus *= QUICK_VICTORY_BONUS;
        }

        if (maxCombo > 5) {
            bonus *= COMBO_BONUS_MULTIPLIER;
        }

        return bonus;
    }
}
