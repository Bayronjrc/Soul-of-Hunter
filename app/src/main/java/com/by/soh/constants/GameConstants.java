package com.by.soh.constants;

public class GameConstants {

    public static final String GAME_VERSION = "1.0.0";
    public static final String DATABASE_NAME = "Soul_of_Hunter";
    public static final int DATABASE_VERSION = 1;
    // Sistema de formacion
    public static final int MAX_TEAM_SIZE = 5;
    public static final int INITIAL_TEAM_SIZE = 1;

    // Unlock formation slots
    public static final int TEAM_SLOT_2_UNLOCK_LEVEL = 5;
    public static final int TEAM_SLOT_3_UNLOCK_LEVEL = 10;
    public static final int TEAM_SLOT_4_UNLOCK_LEVEL = 15;
    public static final int TEAM_SLOT_5_UNLOCK_LEVEL = 20;

    // Rarity System
    public static final int COMMON_RARITY = 1;
    public static final int RARE_RARITY = 2;
    public static final int EPIC_RARITY = 3;
    public static final int LEGENDARY_RARITY = 4;
    public static final int MYTHIC_RARITY = 5;

    public static final String[] RARITY_NAMES = {
            "", "Común", "Raro", "Épico", "Legendario", "Mítico"
    };

    // Stars System
    public static final int MIN_STARS = 1;
    public static final int MAX_STARS = 5;

    // Enhancment System
    public static final int MIN_ENHANCEMENT = 0;
    public static final int MAX_ENHANCEMENT = 9;
    public static final int FIRST_EVOLUTION_LEVEL = 3;  // +3 primera evolución
    public static final int SECOND_EVOLUTION_LEVEL = 7; // +7 segunda evolución
    public static final int AWAKENING_LEVEL = 9;

    // Skill System
    public static final int MAX_SKILL_LEVEL = 3;
    public static final int SPECIAL_SKILL_UNLOCK_ENHANCEMENT = 3;  // +3
    public static final int ULTIMATE_SKILL_UNLOCK_ENHANCEMENT = 7; // +7

    // Bankai System
    public static final int BANKAI_UNLOCK_ENHANCEMENT = 3;
    public static final int BANKAI_DURATION_SECONDS = 30;
    public static final int BANKAI_COOLDOWN_BATTLES = 2;
    public static final float BANKAI_STAT_MULTIPLIER = 2.0f; // +100% estadística principal

    // AFK System
    public static final long AFK_REWARD_INTERVAL_MINUTES = 1; // 1 minuto para testing, cambiar a 60 para producción
    public static final int MAX_AFK_HOURS = 12; // Máximo 12 horas de recompensas AFK
    public static final float AFK_GOLD_BASE_RATE = 100.0f;
    public static final float AFK_EXP_BASE_RATE = 50.0f;

    // Currency System
    public static final int INITIAL_GOLD = 10000;
    public static final int INITIAL_GEMS = 500;
    public static final int INITIAL_PVP_COINS = 0;
    public static final int INITIAL_GUILD_COINS = 0;

    // Level System
    public static final int MAX_HERO_LEVEL = 100;
    public static final int MAX_PLAYER_LEVEL = 200;
    public static final float EXP_MULTIPLIER_BASE = 1.0f;

    // Gacha System
    public static final int GEMS_PER_SINGLE_PULL = 100;
    public static final int GEMS_PER_10_PULL = 900; // 10% descuento
    public static final int PITY_SYSTEM_THRESHOLD = 30; // Garantía después de 30 pulls

    // Gacha Props System
    public static final float COMMON_GACHA_RATE = 60.0f;
    public static final float RARE_GACHA_RATE = 25.0f;
    public static final float EPIC_GACHA_RATE = 12.0f;
    public static final float LEGENDARY_GACHA_RATE = 2.8f;
    public static final float MYTHIC_GACHA_RATE = 0.2f;

    // Battle System
    public static final float BATTLE_SPEED_1X = 1.0f;
    public static final float BATTLE_SPEED_2X = 2.0f;
    public static final float BATTLE_SPEED_4X = 4.0f;

    public static final int BATTLE_TIMEOUT_SECONDS = 120; // 2 minutos máximo por batalla

    // Hell System
    public static final int TOWER_MAX_FLOORS = 999;
    public static final float TOWER_DIFFICULTY_SCALING = 1.1f; // 10% más difícil cada piso

    // PVP System
    public static final int PVP_BATTLES_PER_DAY = 10;
    public static final float PVP_BOT_SCALING_FACTOR = 0.95f; // Bots 5% más débiles que el jugador

    // Shop System
    public static final int SHOP_REFRESH_HOUR = 0; // Medianoche
    public static final int SHOP_SPECIAL_DAY = 7;   // Domingo = items especiales

    // Events and Weather System
    public static final String[] WEATHER_EFFECTS = {
            "Soleado", "Tormenta Espiritual", "Niebla Densa",
            "Aurora Espiritual", "Eclipse", "Calma Total", "Viento Feroz"
    };

    // Investigation System
    public static final int INVESTIGATION_MIN_HOURS = 2;
    public static final int INVESTIGATION_MAX_HOURS = 8;

    // Resources Limit
    public static final long MAX_GOLD = 999_999_999L;
    public static final int MAX_GEMS = 99_999;
    public static final int MAX_INVENTORY_SLOTS = 200;

    // Save Configuration
    public static final String SAVE_FILE_PREFIX = "save_";
    public static final long AUTO_SAVE_INTERVAL_MS = 60_000; // Auto-save cada minuto

    // UI Configuration
    public static final int ANIMATION_DURATION_SHORT = 200;
    public static final int ANIMATION_DURATION_MEDIUM = 500;
    public static final int ANIMATION_DURATION_LONG = 1000;

    // Master System
    public static final int MENTOR_MIN_LEVEL = 50;
    public static final int APPRENTICE_MAX_LEVEL = 30;
    public static final int MENTORING_DURATION_HOURS = 24;
    public static final float MENTOR_EXP_PENALTY = 0.8f; // -20% EXP temporal
    public static final float APPRENTICE_EXP_BONUS = 4.0f; // +300% EXP

    // Debug Configuration
    public static final boolean DEBUG_MODE = true; // Cambiar a false en producción
    public static final boolean ENABLE_LOGGING = true;
    public static final boolean ENABLE_CHEATS = DEBUG_MODE; // Solo en debug

    // Shared Preferences Keys
    public static final String PREFS_NAME = "BleachGamePrefs";
    public static final String PREF_FIRST_RUN = "first_run";
    public static final String PREF_PLAYER_NAME = "player_name";
    public static final String PREF_CURRENT_LEVEL = "current_level";
    public static final String PREF_LAST_SAVE_TIME = "last_save_time";
    public static final String PREF_AFK_START_TIME = "afk_start_time";
    public static final String PREF_SOUND_ENABLED = "sound_enabled";
    public static final String PREF_MUSIC_ENABLED = "music_enabled";

    // Helper Methods
    /**
     * Calcula el costo de oro para mejorar un héroe
     */
    public static long getUpgradeCost(int currentEnhancement, int rarity) {
        long baseCost = 1000;
        long rarityCost = rarity * 500;
        long enhancementCost = (long) Math.pow(currentEnhancement + 1, 2) * 100;
        return baseCost + rarityCost + enhancementCost;
    }

    /**
     * Calcula la experiencia necesaria para subir de nivel
     */
    public static long getExpRequiredForLevel(int level) {
        return (long) (100 * Math.pow(level, 1.5));
    }

    /**
     * Verifica si un nivel desbloquea un slot de formación
     */
    public static boolean isTeamSlotUnlockLevel(int level) {
        return level == TEAM_SLOT_2_UNLOCK_LEVEL ||
                level == TEAM_SLOT_3_UNLOCK_LEVEL ||
                level == TEAM_SLOT_4_UNLOCK_LEVEL ||
                level == TEAM_SLOT_5_UNLOCK_LEVEL;
    }

    /**
     * Obtiene el número de slots disponibles según el nivel
     */
    public static int getAvailableTeamSlots(int level) {
        if (level >= TEAM_SLOT_5_UNLOCK_LEVEL) return 5;
        if (level >= TEAM_SLOT_4_UNLOCK_LEVEL) return 4;
        if (level >= TEAM_SLOT_3_UNLOCK_LEVEL) return 3;
        if (level >= TEAM_SLOT_2_UNLOCK_LEVEL) return 2;
        return 1;
    }

    /**
     * Convierte rareza numérica a nombre
     */
    public static String getRarityName(int rarity) {
        if (rarity >= 1 && rarity < RARITY_NAMES.length) {
            return RARITY_NAMES[rarity];
        }
        return "Desconocido";
    }

    /**
     * Calcula el multiplicador de stats por rareza
     */
    public static float getRarityMultiplier(int rarity) {
        switch (rarity) {
            case COMMON_RARITY: return 1.0f;
            case RARE_RARITY: return 1.25f;
            case EPIC_RARITY: return 1.6f;
            case LEGENDARY_RARITY: return 2.1f;
            case MYTHIC_RARITY: return 2.8f;
            default: return 1.0f;
        }
    }

    /**
     * Calcula el multiplicador de stats por estrellas
     */
    public static float getStarMultiplier(int stars) {
        return 1.0f + (stars - 1) * 0.2f; // +20% por cada estrella adicional
    }

    /**
     * Calcula el multiplicador de stats por enhancement
     */
    public static float getEnhancementMultiplier(int enhancement) {
        return 1.0f + enhancement * 0.15f; // +15% por cada +
    }
}
