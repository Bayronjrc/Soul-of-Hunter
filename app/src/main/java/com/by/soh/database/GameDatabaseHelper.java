package com.by.soh.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.by.soh.constants.GameConstants;
import com.by.soh.database.DatabaseContract.*;

/**
 * Helper principal para la base de datos SQLite del juego
 * Maneja creación, actualización, migración y operaciones básicas
 */
public class GameDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "GameDatabaseHelper";

    // Singleton instance
    private static GameDatabaseHelper instance;

    // Constructor privado para patrón Singleton
    private GameDatabaseHelper(Context context) {
        super(context, GameConstants.DATABASE_NAME, null, GameConstants.DATABASE_VERSION);
    }

    /**
     * Obtiene la instancia singleton del helper de base de datos
     */
    public static synchronized GameDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new GameDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creando base de datos...");

        try {
            // Crear todas las tablas
            for (String createStatement : DatabaseContract.ALL_TABLES_CREATE_STATEMENTS) {
                Log.d(TAG, "Ejecutando: " + createStatement);
                db.execSQL(createStatement);
            }

            // Crear índices para optimización
            for (String indexStatement : DatabaseContract.CREATE_INDEXES) {
                Log.d(TAG, "Creando índice: " + indexStatement);
                db.execSQL(indexStatement);
            }

            // Poblar con datos iniciales
            populateInitialData(db);

            Log.i(TAG, "Base de datos creada exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error creando base de datos", e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Actualizando base de datos de versión " + oldVersion + " a " + newVersion);

        // Por ahora, estrategia simple: drop y recrear
        // En producción, implementar migraciones apropiadas
        dropAllTables(db);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Habilitar foreign keys
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    // ==================== MÉTODOS DE INICIALIZACIÓN ====================

    /**
     * Pobla la base de datos con datos iniciales necesarios
     */
    private void populateInitialData(SQLiteDatabase db) {
        Log.i(TAG, "Poblando datos iniciales...");

        long currentTime = System.currentTimeMillis();

        try {
            // Insertar templates de héroes
            for (String heroTemplate : DatabaseContract.INITIAL_HERO_TEMPLATES) {
                db.execSQL(heroTemplate);
            }
            Log.d(TAG, "Templates de héroes insertados");

            // Insertar datos iniciales del jugador
            db.execSQL(DatabaseContract.INITIAL_PLAYER_DATA,
                    new Object[]{currentTime, currentTime});
            Log.d(TAG, "Datos del jugador insertados");

            // Insertar héroe inicial (Ichigo)
            db.execSQL(DatabaseContract.INITIAL_PLAYER_HERO,
                    new Object[]{currentTime});
            Log.d(TAG, "Héroe inicial insertado");

            // Insertar formación inicial
            db.execSQL(DatabaseContract.INITIAL_TEAM_FORMATION,
                    new Object[]{currentTime});
            Log.d(TAG, "Formación inicial insertada");

            // Insertar misiones diarias
            long tomorrowMidnight = getTomorrowMidnightTimestamp();
            for (String mission : DatabaseContract.INITIAL_DAILY_MISSIONS) {
                db.execSQL(mission, new Object[]{tomorrowMidnight, currentTime});
            }
            Log.d(TAG, "Misiones diarias insertadas");

            // Insertar misiones permanentes
            for (String mission : DatabaseContract.INITIAL_PERMANENT_MISSIONS) {
                db.execSQL(mission, new Object[]{currentTime});
            }
            Log.d(TAG, "Misiones permanentes insertadas");

            // Insertar progreso inicial de campaña (nivel 1-1)
            insertInitialCampaignProgress(db);

            Log.i(TAG, "Datos iniciales poblados exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error poblando datos iniciales", e);
            throw e;
        }
    }

    /**
     * Inserta el progreso inicial de la campaña
     */
    private void insertInitialCampaignProgress(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(CampaignProgress.COLUMN_CHAPTER, 1);
        values.put(CampaignProgress.COLUMN_STAGE, 1);
        values.put(CampaignProgress.COLUMN_IS_COMPLETED, 0);
        values.put(CampaignProgress.COLUMN_STARS_EARNED, 0);

        db.insert(CampaignProgress.TABLE_NAME, null, values);
        Log.d(TAG, "Progreso inicial de campaña insertado");
    }

    /**
     * Elimina todas las tablas (para upgrades)
     */
    private void dropAllTables(SQLiteDatabase db) {
        Log.w(TAG, "Eliminando todas las tablas...");

        for (String tableName : DatabaseContract.ALL_TABLE_NAMES) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
        }
    }

    // ==================== OPERACIONES DE JUGADOR ====================

    /**
     * Obtiene los datos del jugador
     */
    public Cursor getPlayerData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PlayerData.TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Actualiza los datos del jugador
     */
    public boolean updatePlayerData(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        values.put(PlayerData.COLUMN_UPDATED_AT, System.currentTimeMillis());

        int rowsAffected = db.update(PlayerData.TABLE_NAME, values, null, null);
        return rowsAffected > 0;
    }

    /**
     * Actualiza el nivel más alto alcanzado en campaña
     */
    public boolean updatePlayerProgress(int chapter, int stage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlayerData.COLUMN_CURRENT_CHAPTER, chapter);
        values.put(PlayerData.COLUMN_CURRENT_STAGE, stage);
        values.put(PlayerData.COLUMN_UPDATED_AT, System.currentTimeMillis());

        int rowsAffected = db.update(PlayerData.TABLE_NAME, values, null, null);
        return rowsAffected > 0;
    }

    /**
     * Actualiza las monedas del jugador
     */
    public boolean updatePlayerCurrencies(long gold, int gems, int pvpCoins, int guildCoins) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlayerData.COLUMN_GOLD, gold);
        values.put(PlayerData.COLUMN_GEMS, gems);
        values.put(PlayerData.COLUMN_PVP_COINS, pvpCoins);
        values.put(PlayerData.COLUMN_GUILD_COINS, guildCoins);
        values.put(PlayerData.COLUMN_UPDATED_AT, System.currentTimeMillis());

        int rowsAffected = db.update(PlayerData.TABLE_NAME, values, null, null);
        return rowsAffected > 0;
    }

    // ==================== OPERACIONES DE HÉROES ====================

    /**
     * Obtiene todos los héroes del jugador con sus datos de template
     */
    public Cursor getAllPlayerHeroes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(DatabaseContract.QUERY_HEROES_WITH_TEMPLATES, null);
    }

    /**
     * Obtiene un héroe específico por ID
     */
    public Cursor getPlayerHeroById(long heroId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = DatabaseContract.QUERY_HEROES_WITH_TEMPLATES + " WHERE ph._id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(heroId)});
    }

    /**
     * Obtiene el equipo activo (héroes en formación)
     */
    public Cursor getActiveTeam() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(DatabaseContract.QUERY_ACTIVE_TEAM, null);
    }

    /**
     * Inserta un nuevo héroe para el jugador
     */
    public long insertPlayerHero(String heroTemplateId, int stars, int enhancement) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Primero obtener datos del template
        Cursor templateCursor = db.query(HeroTemplates.TABLE_NAME, null,
                HeroTemplates.COLUMN_HERO_ID + " = ?", new String[]{heroTemplateId},
                null, null, null);

        if (!templateCursor.moveToFirst()) {
            templateCursor.close();
            return -1;
        }

        // Calcular stats base
        int baseHp = templateCursor.getInt(templateCursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_HP));
        int baseAtk = templateCursor.getInt(templateCursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_ATK));
        int baseDef = templateCursor.getInt(templateCursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_DEF));
        int baseSpeed = templateCursor.getInt(templateCursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_BASE_SPEED));
        int rarity = templateCursor.getInt(templateCursor.getColumnIndexOrThrow(HeroTemplates.COLUMN_RARITY));

        templateCursor.close();

        // Aplicar multiplicadores
        float rarityMult = GameConstants.getRarityMultiplier(rarity);
        float starMult = GameConstants.getStarMultiplier(stars);
        float enhanceMult = GameConstants.getEnhancementMultiplier(enhancement);
        float totalMult = rarityMult * starMult * enhanceMult;

        int finalHp = Math.round(baseHp * totalMult);
        int finalAtk = Math.round(baseAtk * totalMult);
        int finalDef = Math.round(baseDef * totalMult);
        int finalSpeed = Math.round(baseSpeed * totalMult);

        // Preparar datos para inserción
        ContentValues values = new ContentValues();
        values.put(PlayerHeroes.COLUMN_HERO_TEMPLATE_ID, heroTemplateId);
        values.put(PlayerHeroes.COLUMN_STARS, stars);
        values.put(PlayerHeroes.COLUMN_ENHANCEMENT, enhancement);
        values.put(PlayerHeroes.COLUMN_CURRENT_HP, finalHp);
        values.put(PlayerHeroes.COLUMN_MAX_HP, finalHp);
        values.put(PlayerHeroes.COLUMN_ATK, finalAtk);
        values.put(PlayerHeroes.COLUMN_DEF, finalDef);
        values.put(PlayerHeroes.COLUMN_SPEED, finalSpeed);
        values.put(PlayerHeroes.COLUMN_POWER_RATING,
                calculateHeroPower(finalHp, finalAtk, finalDef, finalSpeed));
        values.put(PlayerHeroes.COLUMN_OBTAINED_AT, System.currentTimeMillis());

        return db.insert(PlayerHeroes.TABLE_NAME, null, values);
    }

    /**
     * Actualiza las stats de un héroe
     */
    public boolean updateHeroStats(long heroId, ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();

        int rowsAffected = db.update(PlayerHeroes.TABLE_NAME, values,
                PlayerHeroes._ID + " = ?", new String[]{String.valueOf(heroId)});

        return rowsAffected > 0;
    }

    /**
     * Actualiza la posición de un héroe en el equipo
     */
    public boolean updateHeroTeamPosition(long heroId, int position) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlayerHeroes.COLUMN_TEAM_POSITION, position);

        int rowsAffected = db.update(PlayerHeroes.TABLE_NAME, values,
                PlayerHeroes._ID + " = ?", new String[]{String.valueOf(heroId)});

        return rowsAffected > 0;
    }

    // ==================== OPERACIONES DE EQUIPAMIENTO ====================

    /**
     * Obtiene todo el equipamiento del jugador
     */
    public Cursor getAllEquipment() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(Equipment.TABLE_NAME, null, null, null, null, null,
                Equipment.COLUMN_POWER_RATING + " DESC");
    }

    /**
     * Obtiene el equipamiento de un héroe específico
     */
    public Cursor getHeroEquipment(long heroId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(DatabaseContract.QUERY_HERO_EQUIPMENT,
                new String[]{String.valueOf(heroId)});
    }

    /**
     * Inserta una nueva pieza de equipamiento
     */
    public long insertEquipment(int type, int rarity, String mainStatType,
                                int mainStatValue, String secondaryStats, int setId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Equipment.COLUMN_EQUIPMENT_TYPE, type);
        values.put(Equipment.COLUMN_RARITY, rarity);
        values.put(Equipment.COLUMN_MAIN_STAT_TYPE, mainStatType);
        values.put(Equipment.COLUMN_MAIN_STAT_VALUE, mainStatValue);
        values.put(Equipment.COLUMN_SECONDARY_STATS, secondaryStats);
        values.put(Equipment.COLUMN_SET_ID, setId);
        values.put(Equipment.COLUMN_POWER_RATING,
                calculateEquipmentPower(mainStatValue, secondaryStats));
        values.put(Equipment.COLUMN_OBTAINED_AT, System.currentTimeMillis());

        return db.insert(Equipment.TABLE_NAME, null, values);
    }

    /**
     * Equipa una pieza a un héroe
     */
    public boolean equipItemToHero(long equipmentId, long heroId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Equipment.COLUMN_EQUIPPED_BY_HERO, heroId);

        int rowsAffected = db.update(Equipment.TABLE_NAME, values,
                Equipment._ID + " = ?", new String[]{String.valueOf(equipmentId)});

        return rowsAffected > 0;
    }

    /**
     * Desequipa una pieza de equipamiento
     */
    public boolean unequipItem(long equipmentId) {
        return equipItemToHero(equipmentId, 0);
    }

    // ==================== OPERACIONES DE CAMPAÑA ====================

    /**
     * Obtiene el progreso de la campaña
     */
    public Cursor getCampaignProgress() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(DatabaseContract.QUERY_CAMPAIGN_PROGRESS, null);
    }

    /**
     * Actualiza o inserta progreso de un nivel de campaña
     */
    public boolean updateCampaignLevel(int chapter, int stage, boolean completed,
                                       int starsEarned, long completionTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si ya existe
        Cursor cursor = db.query(CampaignProgress.TABLE_NAME, new String[]{CampaignProgress._ID},
                CampaignProgress.COLUMN_CHAPTER + " = ? AND " + CampaignProgress.COLUMN_STAGE + " = ?",
                new String[]{String.valueOf(chapter), String.valueOf(stage)},
                null, null, null);

        ContentValues values = new ContentValues();
        values.put(CampaignProgress.COLUMN_CHAPTER, chapter);
        values.put(CampaignProgress.COLUMN_STAGE, stage);
        values.put(CampaignProgress.COLUMN_IS_COMPLETED, completed ? 1 : 0);
        values.put(CampaignProgress.COLUMN_STARS_EARNED, starsEarned);
        values.put(CampaignProgress.COLUMN_COMPLETED_AT, System.currentTimeMillis());

        boolean success;
        if (cursor.moveToFirst()) {
            // Actualizar existente
            success = db.update(CampaignProgress.TABLE_NAME, values,
                    CampaignProgress.COLUMN_CHAPTER + " = ? AND " + CampaignProgress.COLUMN_STAGE + " = ?",
                    new String[]{String.valueOf(chapter), String.valueOf(stage)}) > 0;
        } else {
            // Insertar nuevo
            success = db.insert(CampaignProgress.TABLE_NAME, null, values) != -1;
        }

        cursor.close();
        return success;
    }

    // ==================== OPERACIONES DE MISIONES ====================

    /**
     * Obtiene misiones activas
     */
    public Cursor getActiveMissions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(DatabaseContract.QUERY_ACTIVE_MISSIONS,
                new String[]{String.valueOf(System.currentTimeMillis())});
    }

    /**
     * Actualiza el progreso de una misión
     */
    public boolean updateMissionProgress(String missionId, int newProgress) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Missions.COLUMN_CURRENT_PROGRESS, newProgress);

        // Verificar si se completó
        Cursor cursor = db.query(Missions.TABLE_NAME,
                new String[]{Missions.COLUMN_TARGET_VALUE},
                Missions.COLUMN_MISSION_ID + " = ?", new String[]{missionId},
                null, null, null);

        if (cursor.moveToFirst()) {
            int targetValue = cursor.getInt(0);
            if (newProgress >= targetValue) {
                values.put(Missions.COLUMN_IS_COMPLETED, 1);
            }
        }
        cursor.close();

        int rowsAffected = db.update(Missions.TABLE_NAME, values,
                Missions.COLUMN_MISSION_ID + " = ?", new String[]{missionId});

        return rowsAffected > 0;
    }

    /**
     * Marca una misión como reclamada
     */
    public boolean claimMissionReward(String missionId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Missions.COLUMN_IS_CLAIMED, 1);

        int rowsAffected = db.update(Missions.TABLE_NAME, values,
                Missions.COLUMN_MISSION_ID + " = ?", new String[]{missionId});

        return rowsAffected > 0;
    }

    // ==================== OPERACIONES DE FRAGMENTOS ====================

    /**
     * Obtiene fragmentos disponibles
     */
    public Cursor getAvailableShards() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(DatabaseContract.QUERY_AVAILABLE_SHARDS, null);
    }

    /**
     * Actualiza la cantidad de fragmentos de un héroe
     */
    public boolean updateHeroShards(String heroTemplateId, int shardCount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HeroShards.COLUMN_HERO_TEMPLATE_ID, heroTemplateId);
        values.put(HeroShards.COLUMN_SHARD_COUNT, shardCount);
        values.put(HeroShards.COLUMN_UPDATED_AT, System.currentTimeMillis());

        // Usar INSERT OR REPLACE para manejar casos de actualización
        return db.insertWithOnConflict(HeroShards.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    // ==================== OPERACIONES DE BÚSQUEDA ====================

    /**
     * Busca héroes con filtros específicos
     */
    public Cursor searchHeroes(String faction, String rarity, String role, String sortBy) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = DatabaseContract.getHeroSearchQuery(faction, rarity, role, sortBy);

        // Construir parámetros dinámicamente
        java.util.List<String> params = new java.util.ArrayList<>();
        if (faction != null && !faction.equals("all")) params.add(faction);
        if (rarity != null && !rarity.equals("all")) params.add(rarity);
        if (role != null && !role.equals("all")) params.add(role);

        return db.rawQuery(query, params.toArray(new String[0]));
    }

    /**
     * Busca equipamiento con filtros específicos
     */
    public Cursor searchEquipment(String type, String rarity, boolean onlyUnequipped) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = DatabaseContract.getEquipmentSearchQuery(type, rarity, onlyUnequipped);

        java.util.List<String> params = new java.util.ArrayList<>();
        if (type != null && !type.equals("all")) params.add(type);
        if (rarity != null && !rarity.equals("all")) params.add(rarity);

        return db.rawQuery(query, params.toArray(new String[0]));
    }

    // ==================== OPERACIONES DE MANTENIMIENTO ====================

    /**
     * Resetea las misiones diarias (llamar cada día a medianoche)
     */
    public boolean resetDailyMissions() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Eliminar misiones diarias anteriores
        db.delete(Missions.TABLE_NAME,
                Missions.COLUMN_MISSION_TYPE + " = ?", new String[]{"daily"});

        // Insertar nuevas misiones diarias
        long currentTime = System.currentTimeMillis();
        long tomorrowMidnight = getTomorrowMidnightTimestamp();

        try {
            for (String mission : DatabaseContract.INITIAL_DAILY_MISSIONS) {
                db.execSQL(mission, new Object[]{tomorrowMidnight, currentTime});
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error reseteando misiones diarias", e);
            return false;
        }
    }

    /**
     * Limpia datos antiguos para optimizar la base de datos
     */
    public boolean cleanupOldData() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Limpiar eventos antiguos (más de 7 días)
            long weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
            db.delete(GameEvents.TABLE_NAME,
                    GameEvents.COLUMN_END_TIME + " < ? AND " + GameEvents.COLUMN_IS_ACTIVE + " = 0",
                    new String[]{String.valueOf(weekAgo)});

            // Limpiar misiones diarias expiradas
            db.delete(Missions.TABLE_NAME,
                    Missions.COLUMN_MISSION_TYPE + " = 'daily' AND " +
                            Missions.COLUMN_EXPIRES_AT + " < ?",
                    new String[]{String.valueOf(System.currentTimeMillis())});

            // Vacuum para optimizar espacio
            db.execSQL("VACUUM");

            Log.i(TAG, "Limpieza de datos completada");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error en limpieza de datos", e);
            return false;
        }
    }

    // ==================== MÉTODOS HELPER PRIVADOS ====================

    /**
     * Calcula el poder total de un héroe
     */
    private long calculateHeroPower(int hp, int atk, int def, int speed) {
        return Math.round(hp * 0.5 + atk * 2.0 + def * 1.5 + speed * 0.5);
    }

    /**
     * Calcula el poder de una pieza de equipamiento
     */
    private int calculateEquipmentPower(int mainStat, String secondaryStats) {
        // Implementación básica - en el futuro parsear JSON de secondaryStats
        return mainStat * 10; // Factor arbitrario
    }

    /**
     * Obtiene el timestamp de medianoche del día siguiente
     */
    private long getTomorrowMidnightTimestamp() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // ==================== MÉTODOS DE DEBUGGING ====================

    /**
     * Obtiene estadísticas de la base de datos para debugging
     */
    public String getDatabaseStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder stats = new StringBuilder();

        stats.append("=== ESTADÍSTICAS DE BASE DE DATOS ===\n");

        for (String tableName : DatabaseContract.ALL_TABLE_NAMES) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                stats.append(tableName).append(": ").append(count).append(" registros\n");
            }
            cursor.close();
        }

        // Información adicional
        stats.append("\nVersión BD: ").append(GameConstants.DATABASE_VERSION);
        stats.append("\nTamaño BD: ").append(new java.io.File(db.getPath()).length() / 1024).append(" KB");

        return stats.toString();
    }

    /**
     * Verifica la integridad de la base de datos
     */
    public boolean checkDatabaseIntegrity() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("PRAGMA integrity_check", null);
        boolean isIntact = false;

        if (cursor.moveToFirst()) {
            String result = cursor.getString(0);
            isIntact = "ok".equalsIgnoreCase(result);

            if (!isIntact) {
                Log.e(TAG, "Integridad de BD comprometida: " + result);
            }
        }

        cursor.close();
        return isIntact;
    }

    /**
     * Exporta datos de la base de datos para backup (solo en debug)
     */
    public String exportDatabaseForDebug() {
        if (!GameConstants.DEBUG_MODE) {
            return "Exportación solo disponible en modo debug";
        }

        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder export = new StringBuilder();

        export.append("=== EXPORTACIÓN DE DATOS ===\n");
        export.append("Timestamp: ").append(new java.util.Date().toString()).append("\n\n");

        // Exportar datos del jugador
        Cursor playerCursor = getPlayerData();
        if (playerCursor.moveToFirst()) {
            export.append("DATOS DEL JUGADOR:\n");
            for (int i = 0; i < playerCursor.getColumnCount(); i++) {
                export.append(playerCursor.getColumnName(i)).append(": ")
                        .append(playerCursor.getString(i)).append("\n");
            }
            export.append("\n");
        }
        playerCursor.close();

        return export.toString();
    }
}