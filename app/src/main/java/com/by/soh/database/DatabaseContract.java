package com.by.soh.database;

import android.provider.BaseColumns;

/**
 * Contrato de la base de datos - Define las tablas y columnas
 */
public final class DatabaseContract {

    // Constructor privado para prevenir instanciación
    private DatabaseContract() {}

    // ==================== TABLA DE DATOS DEL JUGADOR ====================
    public static class PlayerData implements BaseColumns {
        public static final String TABLE_NAME = "player_data";
        public static final String COLUMN_PLAYER_NAME = "player_name";
        public static final String COLUMN_PLAYER_LEVEL = "player_level";
        public static final String COLUMN_PLAYER_EXP = "player_exp";
        public static final String COLUMN_CURRENT_CHAPTER = "current_chapter";
        public static final String COLUMN_CURRENT_STAGE = "current_stage";
        public static final String COLUMN_GOLD = "gold";
        public static final String COLUMN_GEMS = "gems";
        public static final String COLUMN_PVP_COINS = "pvp_coins";
        public static final String COLUMN_GUILD_COINS = "guild_coins";
        public static final String COLUMN_LAST_AFK_TIME = "last_afk_time";
        public static final String COLUMN_TOTAL_PLAY_TIME = "total_play_time";
        public static final String COLUMN_GACHA_PITY_COUNT = "gacha_pity_count";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_PLAYER_NAME + " TEXT," +
                        COLUMN_PLAYER_LEVEL + " INTEGER DEFAULT 1," +
                        COLUMN_PLAYER_EXP + " INTEGER DEFAULT 0," +
                        COLUMN_CURRENT_CHAPTER + " INTEGER DEFAULT 1," +
                        COLUMN_CURRENT_STAGE + " INTEGER DEFAULT 1," +
                        COLUMN_GOLD + " INTEGER DEFAULT 10000," +
                        COLUMN_GEMS + " INTEGER DEFAULT 500," +
                        COLUMN_PVP_COINS + " INTEGER DEFAULT 0," +
                        COLUMN_GUILD_COINS + " INTEGER DEFAULT 0," +
                        COLUMN_LAST_AFK_TIME + " INTEGER DEFAULT 0," +
                        COLUMN_TOTAL_PLAY_TIME + " INTEGER DEFAULT 0," +
                        COLUMN_GACHA_PITY_COUNT + " INTEGER DEFAULT 0," +
                        COLUMN_CREATED_AT + " INTEGER," +
                        COLUMN_UPDATED_AT + " INTEGER" +
                        ")";
    }

    // ==================== TABLA DE TEMPLATES DE HÉROES ====================
    public static class HeroTemplates implements BaseColumns {
        public static final String TABLE_NAME = "hero_templates";
        public static final String COLUMN_HERO_ID = "hero_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_FACTION = "faction";
        public static final String COLUMN_ATTRIBUTE = "attribute";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_RARITY = "rarity";
        public static final String COLUMN_BASE_HP = "base_hp";
        public static final String COLUMN_BASE_ATK = "base_atk";
        public static final String COLUMN_BASE_DEF = "base_def";
        public static final String COLUMN_BASE_DEF_MAGIC = "base_def_m";
        public static final String COLUMN_BASE_SPEED = "base_speed";
        public static final String COLUMN_SKILL_1_NAME = "skill_1_name";
        public static final String COLUMN_SKILL_1_DESC = "skill_1_desc";
        public static final String COLUMN_SKILL_2_NAME = "skill_2_name";
        public static final String COLUMN_SKILL_2_DESC = "skill_2_desc";
        public static final String COLUMN_SKILL_3_NAME = "skill_3_name";
        public static final String COLUMN_SKILL_3_DESC = "skill_3_desc";
        public static final String COLUMN_SPRITE_NAME = "sprite_name";
        public static final String COLUMN_IS_STARTER = "is_starter";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_HERO_ID + " TEXT UNIQUE," +
                        COLUMN_NAME + " TEXT," +
                        COLUMN_FACTION + " INTEGER," +
                        COLUMN_ATTRIBUTE + " INTEGER," +
                        COLUMN_ROLE + " INTEGER," +
                        COLUMN_RARITY + " INTEGER," +
                        COLUMN_BASE_HP + " INTEGER," +
                        COLUMN_BASE_ATK + " INTEGER," +
                        COLUMN_BASE_DEF + " INTEGER," +
                        COLUMN_BASE_DEF_MAGIC + " INTEGER," +
                        COLUMN_BASE_SPEED + " INTEGER," +
                        COLUMN_SKILL_1_NAME + " TEXT," +
                        COLUMN_SKILL_1_DESC + " TEXT," +
                        COLUMN_SKILL_2_NAME + " TEXT," +
                        COLUMN_SKILL_2_DESC + " TEXT," +
                        COLUMN_SKILL_3_NAME + " TEXT," +
                        COLUMN_SKILL_3_DESC + " TEXT," +
                        COLUMN_SPRITE_NAME + " TEXT," +
                        COLUMN_IS_STARTER + " INTEGER DEFAULT 0" +
                        ")";
    }

    // ==================== TABLA DE HÉROES DEL JUGADOR ====================
    public static class PlayerHeroes implements BaseColumns {
        public static final String TABLE_NAME = "player_heroes";
        public static final String COLUMN_HERO_TEMPLATE_ID = "hero_template_id";
        public static final String COLUMN_LEVEL = "level";
        public static final String COLUMN_EXP = "exp";
        public static final String COLUMN_STARS = "stars";
        public static final String COLUMN_ENHANCEMENT = "enhancement";
        public static final String COLUMN_CURRENT_HP = "current_hp";
        public static final String COLUMN_MAX_HP = "max_hp";
        public static final String COLUMN_ATK = "atk";
        public static final String COLUMN_DEF = "def";
        public static final String COLUMN_DEF_MAGIC = "def_magic";
        public static final String COLUMN_SPEED = "speed";
        public static final String COLUMN_CRIT_RATE = "crit_rate";
        public static final String COLUMN_CRIT_DAMAGE = "crit_damage";
        public static final String COLUMN_ACCURACY = "accuracy";
        public static final String COLUMN_EVASION = "evasion";
        public static final String COLUMN_SKILL_1_LEVEL = "skill_1_level";
        public static final String COLUMN_SKILL_2_LEVEL = "skill_2_level";
        public static final String COLUMN_SKILL_3_LEVEL = "skill_3_level";
        public static final String COLUMN_IS_FAVORITED = "is_favorited";
        public static final String COLUMN_TEAM_POSITION = "team_position";
        public static final String COLUMN_POWER_RATING = "power_rating";
        public static final String COLUMN_OBTAINED_AT = "obtained_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_HERO_TEMPLATE_ID + " TEXT," +
                        COLUMN_LEVEL + " INTEGER DEFAULT 1," +
                        COLUMN_EXP + " INTEGER DEFAULT 0," +
                        COLUMN_STARS + " INTEGER DEFAULT 1," +
                        COLUMN_ENHANCEMENT + " INTEGER DEFAULT 0," +
                        COLUMN_CURRENT_HP + " INTEGER," +
                        COLUMN_MAX_HP + " INTEGER," +
                        COLUMN_ATK + " INTEGER," +
                        COLUMN_DEF + " INTEGER," +
                        COLUMN_DEF_MAGIC + " INTEGER," +
                        COLUMN_SPEED + " INTEGER," +
                        COLUMN_CRIT_RATE + " REAL DEFAULT 0.05," +
                        COLUMN_CRIT_DAMAGE + " REAL DEFAULT 1.5," +
                        COLUMN_ACCURACY + " REAL DEFAULT 0.95," +
                        COLUMN_EVASION + " REAL DEFAULT 0.05," +
                        COLUMN_SKILL_1_LEVEL + " INTEGER DEFAULT 1," +
                        COLUMN_SKILL_2_LEVEL + " INTEGER DEFAULT 0," +
                        COLUMN_SKILL_3_LEVEL + " INTEGER DEFAULT 0," +
                        COLUMN_IS_FAVORITED + " INTEGER DEFAULT 0," +
                        COLUMN_TEAM_POSITION + " INTEGER DEFAULT 0," +
                        COLUMN_POWER_RATING + " INTEGER DEFAULT 0," +
                        COLUMN_OBTAINED_AT + " INTEGER," +
                        "FOREIGN KEY(" + COLUMN_HERO_TEMPLATE_ID + ") REFERENCES " +
                        HeroTemplates.TABLE_NAME + "(" + HeroTemplates.COLUMN_HERO_ID + ")" +
                        ")";
    }

    // ==================== TABLA DE EQUIPAMIENTO ====================
    public static class Equipment implements BaseColumns {
        public static final String TABLE_NAME = "equipment";
        public static final String COLUMN_EQUIPMENT_TYPE = "equipment_type";
        public static final String COLUMN_RARITY = "rarity";
        public static final String COLUMN_ENHANCEMENT = "enhancement";
        public static final String COLUMN_MAIN_STAT_TYPE = "main_stat_type";
        public static final String COLUMN_MAIN_STAT_VALUE = "main_stat_value";
        public static final String COLUMN_SECONDARY_STATS = "secondary_stats"; // JSON
        public static final String COLUMN_SET_ID = "set_id";
        public static final String COLUMN_EQUIPPED_BY_HERO = "equipped_by_hero";
        public static final String COLUMN_IS_LOCKED = "is_locked";
        public static final String COLUMN_POWER_RATING = "power_rating";
        public static final String COLUMN_OBTAINED_AT = "obtained_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_EQUIPMENT_TYPE + " INTEGER," +
                        COLUMN_RARITY + " INTEGER," +
                        COLUMN_ENHANCEMENT + " INTEGER DEFAULT 0," +
                        COLUMN_MAIN_STAT_TYPE + " TEXT," +
                        COLUMN_MAIN_STAT_VALUE + " INTEGER," +
                        COLUMN_SECONDARY_STATS + " TEXT," + // JSON string
                        COLUMN_SET_ID + " INTEGER DEFAULT 0," +
                        COLUMN_EQUIPPED_BY_HERO + " INTEGER DEFAULT 0," +
                        COLUMN_IS_LOCKED + " INTEGER DEFAULT 0," +
                        COLUMN_POWER_RATING + " INTEGER DEFAULT 0," +
                        COLUMN_OBTAINED_AT + " INTEGER," +
                        "FOREIGN KEY(" + COLUMN_EQUIPPED_BY_HERO + ") REFERENCES " +
                        PlayerHeroes.TABLE_NAME + "(" + _ID + ")" +
                        ")";
    }

    // ==================== TABLA DE FRAGMENTOS DE HÉROES ====================
    public static class HeroShards implements BaseColumns {
        public static final String TABLE_NAME = "hero_shards";
        public static final String COLUMN_HERO_TEMPLATE_ID = "hero_template_id";
        public static final String COLUMN_SHARD_COUNT = "shard_count";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_HERO_TEMPLATE_ID + " TEXT UNIQUE," +
                        COLUMN_SHARD_COUNT + " INTEGER DEFAULT 0," +
                        COLUMN_UPDATED_AT + " INTEGER," +
                        "FOREIGN KEY(" + COLUMN_HERO_TEMPLATE_ID + ") REFERENCES " +
                        HeroTemplates.TABLE_NAME + "(" + HeroTemplates.COLUMN_HERO_ID + ")" +
                        ")";
    }

    // ==================== TABLA DE INVENTARIO DE ITEMS ====================
    public static class Inventory implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_ITEM_ID = "item_id";
        public static final String COLUMN_ITEM_TYPE = "item_type"; // consumable, material, key, etc
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_ITEM_ID + " TEXT," +
                        COLUMN_ITEM_TYPE + " TEXT," +
                        COLUMN_QUANTITY + " INTEGER DEFAULT 0," +
                        COLUMN_UPDATED_AT + " INTEGER" +
                        ")";
    }

    // ==================== TABLA DE PROGRESO DE CAMPAÑA ====================
    public static class CampaignProgress implements BaseColumns {
        public static final String TABLE_NAME = "campaign_progress";
        public static final String COLUMN_CHAPTER = "chapter";
        public static final String COLUMN_STAGE = "stage";
        public static final String COLUMN_IS_COMPLETED = "is_completed";
        public static final String COLUMN_STARS_EARNED = "stars_earned";
        public static final String COLUMN_BEST_TIME = "best_time";
        public static final String COLUMN_FIRST_CLEAR_REWARD_CLAIMED = "first_clear_reward_claimed";
        public static final String COLUMN_COMPLETED_AT = "completed_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_CHAPTER + " INTEGER," +
                        COLUMN_STAGE + " INTEGER," +
                        COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0," +
                        COLUMN_STARS_EARNED + " INTEGER DEFAULT 0," +
                        COLUMN_BEST_TIME + " INTEGER DEFAULT 0," +
                        COLUMN_FIRST_CLEAR_REWARD_CLAIMED + " INTEGER DEFAULT 0," +
                        COLUMN_COMPLETED_AT + " INTEGER DEFAULT 0," +
                        "UNIQUE(" + COLUMN_CHAPTER + ", " + COLUMN_STAGE + ")" +
                        ")";
    }

    // ==================== TABLA DE PROGRESO DE TORRE (HELL) ====================
    public static class TowerProgress implements BaseColumns {
        public static final String TABLE_NAME = "tower_progress";
        public static final String COLUMN_FLOOR = "floor";
        public static final String COLUMN_IS_COMPLETED = "is_completed";
        public static final String COLUMN_BEST_TIME = "best_time";
        public static final String COLUMN_REWARD_CLAIMED = "reward_claimed";
        public static final String COLUMN_COMPLETED_AT = "completed_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_FLOOR + " INTEGER UNIQUE," +
                        COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0," +
                        COLUMN_BEST_TIME + " INTEGER DEFAULT 0," +
                        COLUMN_REWARD_CLAIMED + " INTEGER DEFAULT 0," +
                        COLUMN_COMPLETED_AT + " INTEGER DEFAULT 0" +
                        ")";
    }

    // ==================== TABLA DE FORMACIONES DE EQUIPO ====================
    public static class TeamFormations implements BaseColumns {
        public static final String TABLE_NAME = "team_formations";
        public static final String COLUMN_FORMATION_NAME = "formation_name";
        public static final String COLUMN_POSITION_1 = "position_1"; // Hero ID
        public static final String COLUMN_POSITION_2 = "position_2";
        public static final String COLUMN_POSITION_3 = "position_3";
        public static final String COLUMN_POSITION_4 = "position_4";
        public static final String COLUMN_POSITION_5 = "position_5";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_TOTAL_POWER = "total_power";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_FORMATION_NAME + " TEXT," +
                        COLUMN_POSITION_1 + " INTEGER DEFAULT 0," +
                        COLUMN_POSITION_2 + " INTEGER DEFAULT 0," +
                        COLUMN_POSITION_3 + " INTEGER DEFAULT 0," +
                        COLUMN_POSITION_4 + " INTEGER DEFAULT 0," +
                        COLUMN_POSITION_5 + " INTEGER DEFAULT 0," +
                        COLUMN_IS_ACTIVE + " INTEGER DEFAULT 0," +
                        COLUMN_TOTAL_POWER + " INTEGER DEFAULT 0," +
                        COLUMN_UPDATED_AT + " INTEGER" +
                        ")";
    }

    // ==================== TABLA DE MISIONES ====================
    public static class Missions implements BaseColumns {
        public static final String TABLE_NAME = "missions";
        public static final String COLUMN_MISSION_ID = "mission_id";
        public static final String COLUMN_MISSION_TYPE = "mission_type"; // daily, permanent
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TARGET_VALUE = "target_value";
        public static final String COLUMN_CURRENT_PROGRESS = "current_progress";
        public static final String COLUMN_IS_COMPLETED = "is_completed";
        public static final String COLUMN_IS_CLAIMED = "is_claimed";
        public static final String COLUMN_REWARD_TYPE = "reward_type";
        public static final String COLUMN_REWARD_AMOUNT = "reward_amount";
        public static final String COLUMN_EXPIRES_AT = "expires_at"; // 0 for permanent
        public static final String COLUMN_CREATED_AT = "created_at";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_MISSION_ID + " TEXT UNIQUE," +
                        COLUMN_MISSION_TYPE + " TEXT," +
                        COLUMN_TITLE + " TEXT," +
                        COLUMN_DESCRIPTION + " TEXT," +
                        COLUMN_TARGET_VALUE + " INTEGER," +
                        COLUMN_CURRENT_PROGRESS + " INTEGER DEFAULT 0," +
                        COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0," +
                        COLUMN_IS_CLAIMED + " INTEGER DEFAULT 0," +
                        COLUMN_REWARD_TYPE + " TEXT," +
                        COLUMN_REWARD_AMOUNT + " INTEGER," +
                        COLUMN_EXPIRES_AT + " INTEGER DEFAULT 0," +
                        COLUMN_CREATED_AT + " INTEGER" +
                        ")";
    }

    // ==================== TABLA DE EVENTOS TEMPORALES ====================
    public static class GameEvents implements BaseColumns {
        public static final String TABLE_NAME = "game_events";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_EVENT_TYPE = "event_type";
        public static final String COLUMN_EVENT_NAME = "event_name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IS_ACTIVE = "is_active";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_BONUS_DATA = "bonus_data"; // JSON
        public static final String COLUMN_PARTICIPATION_COUNT = "participation_count";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_EVENT_ID + " TEXT UNIQUE," +
                        COLUMN_EVENT_TYPE + " TEXT," +
                        COLUMN_EVENT_NAME + " TEXT," +
                        COLUMN_DESCRIPTION + " TEXT," +
                        COLUMN_IS_ACTIVE + " INTEGER DEFAULT 0," +
                        COLUMN_START_TIME + " INTEGER," +
                        COLUMN_END_TIME + " INTEGER," +
                        COLUMN_BONUS_DATA + " TEXT," +
                        COLUMN_PARTICIPATION_COUNT + " INTEGER DEFAULT 0" +
                        ")";
    }

    // ==================== CONFIGURACIÓN DE LA BASE DE DATOS ====================

    public static final String[] ALL_TABLES_CREATE_STATEMENTS = {
            PlayerData.CREATE_TABLE,
            HeroTemplates.CREATE_TABLE,
            PlayerHeroes.CREATE_TABLE,
            Equipment.CREATE_TABLE,
            HeroShards.CREATE_TABLE,
            Inventory.CREATE_TABLE,
            CampaignProgress.CREATE_TABLE,
            TowerProgress.CREATE_TABLE,
            TeamFormations.CREATE_TABLE,
            Missions.CREATE_TABLE,
            GameEvents.CREATE_TABLE
    };

    public static final String[] ALL_TABLE_NAMES = {
            PlayerData.TABLE_NAME,
            HeroTemplates.TABLE_NAME,
            PlayerHeroes.TABLE_NAME,
            Equipment.TABLE_NAME,
            HeroShards.TABLE_NAME,
            Inventory.TABLE_NAME,
            CampaignProgress.TABLE_NAME,
            TowerProgress.TABLE_NAME,
            TeamFormations.TABLE_NAME,
            Missions.TABLE_NAME,
            GameEvents.TABLE_NAME
    };

    // ==================== QUERIES ÚTILES ====================

    /**
     * Query para obtener héroes con sus datos de template
     */
    public static final String QUERY_HEROES_WITH_TEMPLATES =
            "SELECT ph.*, ht.name, ht.faction, ht.attribute, ht.role, ht.rarity, " +
                    "ht.sprite_name, ht.skill_1_name, ht.skill_2_name, ht.skill_3_name " +
                    "FROM " + PlayerHeroes.TABLE_NAME + " ph " +
                    "INNER JOIN " + HeroTemplates.TABLE_NAME + " ht " +
                    "ON ph." + PlayerHeroes.COLUMN_HERO_TEMPLATE_ID + " = ht." + HeroTemplates.COLUMN_HERO_ID;

    /**
     * Query para obtener el equipo activo
     */
    public static final String QUERY_ACTIVE_TEAM =
            "SELECT ph.*, ht.name, ht.faction, ht.attribute, ht.role " +
                    "FROM " + PlayerHeroes.TABLE_NAME + " ph " +
                    "INNER JOIN " + HeroTemplates.TABLE_NAME + " ht " +
                    "ON ph." + PlayerHeroes.COLUMN_HERO_TEMPLATE_ID + " = ht." + HeroTemplates.COLUMN_HERO_ID + " " +
                    "WHERE ph." + PlayerHeroes.COLUMN_TEAM_POSITION + " > 0 " +
                    "ORDER BY ph." + PlayerHeroes.COLUMN_TEAM_POSITION;

    /**
     * Query para obtener equipamiento equipado por un héroe
     */
    public static final String QUERY_HERO_EQUIPMENT =
            "SELECT * FROM " + Equipment.TABLE_NAME + " " +
                    "WHERE " + Equipment.COLUMN_EQUIPPED_BY_HERO + " = ?";

    /**
     * Query para obtener fragmentos disponibles
     */
    public static final String QUERY_AVAILABLE_SHARDS =
            "SELECT hs.*, ht.name, ht.rarity " +
                    "FROM " + HeroShards.TABLE_NAME + " hs " +
                    "INNER JOIN " + HeroTemplates.TABLE_NAME + " ht " +
                    "ON hs." + HeroShards.COLUMN_HERO_TEMPLATE_ID + " = ht." + HeroTemplates.COLUMN_HERO_ID + " " +
                    "WHERE hs." + HeroShards.COLUMN_SHARD_COUNT + " > 0";

    /**
     * Query para obtener progreso de campaña
     */
    public static final String QUERY_CAMPAIGN_PROGRESS =
            "SELECT * FROM " + CampaignProgress.TABLE_NAME + " " +
                    "ORDER BY " + CampaignProgress.COLUMN_CHAPTER + ", " + CampaignProgress.COLUMN_STAGE;

    /**
     * Query para obtener misiones activas
     */
    public static final String QUERY_ACTIVE_MISSIONS =
            "SELECT * FROM " + Missions.TABLE_NAME + " " +
                    "WHERE " + Missions.COLUMN_IS_COMPLETED + " = 0 " +
                    "AND (" + Missions.COLUMN_EXPIRES_AT + " = 0 OR " + Missions.COLUMN_EXPIRES_AT + " > ?) " +
                    "ORDER BY " + Missions.COLUMN_MISSION_TYPE + ", " + Missions.COLUMN_CREATED_AT;

    /**
     * Query para obtener eventos activos
     */
    public static final String QUERY_ACTIVE_EVENTS =
            "SELECT * FROM " + GameEvents.TABLE_NAME + " " +
                    "WHERE " + GameEvents.COLUMN_IS_ACTIVE + " = 1 " +
                    "AND " + GameEvents.COLUMN_START_TIME + " <= ? " +
                    "AND " + GameEvents.COLUMN_END_TIME + " > ?";

    // ==================== ÍNDICES PARA OPTIMIZACIÓN ====================

    public static final String[] CREATE_INDEXES = {
            // Índices para búsquedas frecuentes
            "CREATE INDEX IF NOT EXISTS idx_player_heroes_template ON " +
                    PlayerHeroes.TABLE_NAME + "(" + PlayerHeroes.COLUMN_HERO_TEMPLATE_ID + ")",

            "CREATE INDEX IF NOT EXISTS idx_player_heroes_team_position ON " +
                    PlayerHeroes.TABLE_NAME + "(" + PlayerHeroes.COLUMN_TEAM_POSITION + ")",

            "CREATE INDEX IF NOT EXISTS idx_equipment_hero ON " +
                    Equipment.TABLE_NAME + "(" + Equipment.COLUMN_EQUIPPED_BY_HERO + ")",

            "CREATE INDEX IF NOT EXISTS idx_campaign_chapter_stage ON " +
                    CampaignProgress.TABLE_NAME + "(" + CampaignProgress.COLUMN_CHAPTER + ", " + CampaignProgress.COLUMN_STAGE + ")",

            "CREATE INDEX IF NOT EXISTS idx_missions_type ON " +
                    Missions.TABLE_NAME + "(" + Missions.COLUMN_MISSION_TYPE + ")",

            "CREATE INDEX IF NOT EXISTS idx_events_active ON " +
                    GameEvents.TABLE_NAME + "(" + GameEvents.COLUMN_IS_ACTIVE + ")"
    };

    // ==================== DATOS INICIALES ====================

    /**
     * Inserts para poblar la tabla de templates de héroes con datos iniciales
     */
    public static final String[] INITIAL_HERO_TEMPLATES = {
            // Ichigo Kurosaki - Héroe inicial
            "INSERT INTO " + HeroTemplates.TABLE_NAME + " VALUES " +
                    "(1, 'ichigo_kurosaki', 'Ichigo Kurosaki', 1, 1, 3, 3, 800, 180, 80, 70, 120" +
                    "'Getsuga Tenshō', 'Ataque espiritual básico', " +
                    "'Hollow Mask', 'Incrementa ATK temporalmente', " +
                    "'Mugetsu', 'Ataque devastador que ignora defensa', " +
                    "'ichigo_normal', 1)",

            // Byakuya Kuchiki
            "INSERT INTO " + HeroTemplates.TABLE_NAME + " VALUES " +
                    "(2, 'byakuya_kuchiki', 'Byakuya Kuchiki', 1, 3, 4, 4, 750, 170, 90, 85, 110 " +
                    "'Senbonzakura', 'Ataque múltiple con pétalos', " +
                    "'Senkei', 'Modo crítico aumentado', " +
                    "'Hakuteiken', 'AoE masivo con execution', " +
                    "'byakuya_normal', 0)",

            // Kenpachi Zaraki
            "INSERT INTO " + HeroTemplates.TABLE_NAME + " VALUES " +
                    "(3, 'kenpachi_zaraki', 'Kenpachi Zaraki', 1, 1, 6, 4, 900, 200, 70,60, 95" +
                    "'Corte Salvaje', 'Más daño cuando está herido', " +
                    "'Sed de Batalla', 'ATK aumenta por cada enemigo derrotado', " +
                    "'Liberación', 'Poder destructivo máximo', " +
                    "'kenpachi_normal', 0)",

            // Grimmjow (Arrancar)
            "INSERT INTO " + HeroTemplates.TABLE_NAME + " VALUES " +
                    "(4, 'grimmjow_jaegerjaquez', 'Grimmjow Jaegerjaquez', 4, 1, 3, 4, 850, 185, 85,75, 125" +
                    "'Garra del Pantera', 'Ataque físico con sangrado', " +
                    "'Desgarrón', 'Críticos guaranteed', " +
                    "'Pantera', 'Resurrección completa', " +
                    "'grimmjow_normal', 0)",

            // Uryu Ishida (Quincy)
            "INSERT INTO " + HeroTemplates.TABLE_NAME + " VALUES " +
                    "(5, 'uryu_ishida', 'Uryū Ishida', 3, 4, 4, 3, 700, 160, 75, 90, 130" +
                    "'Flecha Heilig', 'Ataque de precisión', " +
                    "'Licht Regen', 'Lluvia de flechas', " +
                    "'Quincy: Vollständig', 'Forma final Quincy', " +
                    "'uryu_normal', 0)"
    };

    /**
     * Insert para datos iniciales del jugador
     */
    public static final String INITIAL_PLAYER_DATA =
            "INSERT INTO " + PlayerData.TABLE_NAME + " " +
                    "(" + PlayerData.COLUMN_PLAYER_NAME + ", " + PlayerData.COLUMN_CREATED_AT + ", " + PlayerData.COLUMN_UPDATED_AT + ") " +
                    "VALUES ('Jugador', ?, ?)";

    /**
     * Insert para el héroe inicial del jugador (Ichigo)
     */
    public static final String INITIAL_PLAYER_HERO =
            "INSERT INTO " + PlayerHeroes.TABLE_NAME + " " +
                    "(" + PlayerHeroes.COLUMN_HERO_TEMPLATE_ID + ", " + PlayerHeroes.COLUMN_TEAM_POSITION + ", " +
                    PlayerHeroes.COLUMN_CURRENT_HP + ", " + PlayerHeroes.COLUMN_MAX_HP + ", " +
                    PlayerHeroes.COLUMN_ATK + ", " + PlayerHeroes.COLUMN_DEF + ", " + PlayerHeroes.COLUMN_SPEED + ", " +
                    PlayerHeroes.COLUMN_POWER_RATING + ", " + PlayerHeroes.COLUMN_OBTAINED_AT + ") " +
                    "VALUES ('ichigo_kurosaki', 1, 800, 800, 180, 80, 120, 1200, ?)";

    /**
     * Insert para formación inicial
     */
    public static final String INITIAL_TEAM_FORMATION =
            "INSERT INTO " + TeamFormations.TABLE_NAME + " " +
                    "(" + TeamFormations.COLUMN_FORMATION_NAME + ", " + TeamFormations.COLUMN_POSITION_1 + ", " +
                    TeamFormations.COLUMN_IS_ACTIVE + ", " + TeamFormations.COLUMN_TOTAL_POWER + ", " +
                    TeamFormations.COLUMN_UPDATED_AT + ") " +
                    "VALUES ('Equipo Principal', 1, 1, 1200, ?)";

    /**
     * Misiones diarias iniciales
     */
    public static final String[] INITIAL_DAILY_MISSIONS = {
            "INSERT INTO " + Missions.TABLE_NAME + " VALUES " +
                    "(null, 'daily_login', 'daily', 'Inicio de Sesión Diario', 'Inicia sesión en el juego', 1, 0, 0, 0, 'gold', 1000, ?, ?)",

            "INSERT INTO " + Missions.TABLE_NAME + " VALUES " +
                    "(null, 'daily_battles', 'daily', 'Batallas Diarias', 'Completa 5 batallas', 5, 0, 0, 0, 'gems', 50, ?, ?)",

            "INSERT INTO " + Missions.TABLE_NAME + " VALUES " +
                    "(null, 'daily_upgrade', 'daily', 'Mejora Diaria', 'Mejora un héroe o equipamiento', 1, 0, 0, 0, 'gold', 2000, ?, ?)"
    };

    /**
     * Misiones permanentes iniciales
     */
    public static final String[] INITIAL_PERMANENT_MISSIONS = {
            "INSERT INTO " + Missions.TABLE_NAME + " VALUES " +
                    "(null, 'reach_level_10', 'permanent', 'Alcanzar Nivel 10', 'Llega al nivel 10 de jugador', 10, 0, 0, 0, 'gems', 200, 0, ?)",

            "INSERT INTO " + Missions.TABLE_NAME + " VALUES " +
                    "(null, 'collect_5_heroes', 'permanent', 'Coleccionista', 'Obtén 5 héroes diferentes', 5, 1, 0, 0, 'gems', 500, 0, ?)",

            "INSERT INTO " + Missions.TABLE_NAME + " VALUES " +
                    "(null, 'complete_chapter_1', 'permanent', 'Primer Capítulo', 'Completa el capítulo 1 de la campaña', 1, 0, 0, 0, 'gems', 300, 0, ?)"
    };

    // ==================== MÉTODOS HELPER PARA QUERIES ====================

    /**
     * Obtiene el query para buscar héroes por filtros
     */
    public static String getHeroSearchQuery(String faction, String rarity, String role, String sortBy) {
        StringBuilder query = new StringBuilder(QUERY_HEROES_WITH_TEMPLATES);

        // Agregar filtros
        boolean hasWhere = false;

        if (faction != null && !faction.equals("all")) {
            query.append(hasWhere ? " AND " : " WHERE ");
            query.append("ht.faction = ?");
            hasWhere = true;
        }

        if (rarity != null && !rarity.equals("all")) {
            query.append(hasWhere ? " AND " : " WHERE ");
            query.append("ht.rarity = ?");
            hasWhere = true;
        }

        if (role != null && !role.equals("all")) {
            query.append(hasWhere ? " AND " : " WHERE ");
            query.append("ht.role = ?");
            hasWhere = true;
        }

        // Agregar ordenamiento
        if (sortBy != null) {
            switch (sortBy.toLowerCase()) {
                case "power":
                    query.append(" ORDER BY ph.power_rating DESC");
                    break;
                case "level":
                    query.append(" ORDER BY ph.level DESC");
                    break;
                case "rarity":
                    query.append(" ORDER BY ht.rarity DESC");
                    break;
                case "name":
                    query.append(" ORDER BY ht.name ASC");
                    break;
                default:
                    query.append(" ORDER BY ph.obtained_at DESC");
                    break;
            }
        }

        return query.toString();
    }

    /**
     * Obtiene el query para buscar equipamiento por filtros
     */
    public static String getEquipmentSearchQuery(String type, String rarity, boolean onlyUnequipped) {
        StringBuilder query = new StringBuilder("SELECT * FROM " + Equipment.TABLE_NAME);
        boolean hasWhere = false;

        if (type != null && !type.equals("all")) {
            query.append(" WHERE equipment_type = ?");
            hasWhere = true;
        }

        if (rarity != null && !rarity.equals("all")) {
            query.append(hasWhere ? " AND " : " WHERE ");
            query.append("rarity = ?");
            hasWhere = true;
        }

        if (onlyUnequipped) {
            query.append(hasWhere ? " AND " : " WHERE ");
            query.append("equipped_by_hero = 0");
            hasWhere = true;
        }

        query.append(" ORDER BY power_rating DESC");
        return query.toString();
    }
}