package com.by.soh.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.by.soh.constants.GameConstants;
import com.by.soh.database.GameDatabaseHelper;
import com.by.soh.database.DatabaseContract;

/**
 * Manager para datos del jugador - progreso, monedas, nivel, experiencia, etc.
 */
public class PlayerDataManager {

    private static final String TAG = "PlayerDataManager";

    // Singleton instance
    private static PlayerDataManager instance;

    // Referencias
    private GameDatabaseHelper dbHelper;
    private Context context;

    // Cache de datos del jugador
    private PlayerData cachedPlayerData;
    private long lastCacheUpdate;
    private static final long CACHE_DURATION = 30 * 1000; // 30 segundos

    // Constructor privado
    private PlayerDataManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = GameDatabaseHelper.getInstance(context);
        loadPlayerData();
    }

    /**
     * Obtiene la instancia singleton del manager
     */
    public static synchronized PlayerDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new PlayerDataManager(context);
        }
        return instance;
    }

    // ==================== CARGA DE DATOS ====================

    /**
     * Carga los datos del jugador desde la base de datos
     */
    private void loadPlayerData() {
        Cursor cursor = dbHelper.getPlayerData();

        if (cursor != null && cursor.moveToFirst()) {
            cachedPlayerData = new PlayerData(cursor);
            lastCacheUpdate = System.currentTimeMillis();
            Log.d(TAG, "Datos del jugador cargados: " + cachedPlayerData.toString());
        } else {
            Log.e(TAG, "No se pudieron cargar los datos del jugador");
            cachedPlayerData = createDefaultPlayerData();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Crea datos por defecto si no existen
     */
    private PlayerData createDefaultPlayerData() {
        PlayerData defaultData = new PlayerData();
        defaultData.playerName = "Schwi";
        defaultData.playerLevel = 1;
        defaultData.playerExp = 0;
        defaultData.currentChapter = 1;
        defaultData.currentStage = 1;
        defaultData.gold = GameConstants.INITIAL_GOLD;
        defaultData.gems = GameConstants.INITIAL_GEMS;
        defaultData.pvpCoins = GameConstants.INITIAL_PVP_COINS;
        defaultData.guildCoins = GameConstants.INITIAL_GUILD_COINS;
        defaultData.lastAfkTime = System.currentTimeMillis();
        defaultData.totalPlayTime = 0;
        defaultData.gachaPityCount = 0;

        Log.w(TAG, "Creados datos por defecto del jugador");
        return defaultData;
    }

    /**
     * Refresca los datos desde la base de datos
     */
    public void refreshPlayerData() {
        loadPlayerData();
    }

    /**
     * Verifica si el cache es válido
     */
    private boolean isCacheValid() {
        return (System.currentTimeMillis() - lastCacheUpdate) < CACHE_DURATION;
    }

    // ==================== GETTERS DE DATOS ====================

    /**
     * Obtiene los datos completos del jugador
     */
    public PlayerData getPlayerData() {
        if (!isCacheValid()) {
            refreshPlayerData();
        }
        return cachedPlayerData;
    }

    /**
     * Obtiene el nombre del jugador
     */
    public String getPlayerName() {
        return getPlayerData().playerName;
    }

    /**
     * Obtiene el nivel del jugador
     */
    public int getPlayerLevel() {
        return getPlayerData().playerLevel;
    }

    /**
     * Obtiene la experiencia actual del jugador
     */
    public long getPlayerExp() {
        return getPlayerData().playerExp;
    }

    /**
     * Obtiene la experiencia requerida para el siguiente nivel
     */
    public long getExpRequiredForNextLevel() {
        int currentLevel = getPlayerLevel();
        return GameConstants.getExpRequiredForLevel(currentLevel + 1);
    }

    /**
     * Obtiene el progreso de experiencia (0.0 - 1.0)
     */
    public float getExpProgress() {
        long currentExp = getPlayerExp();
        long requiredExp = getExpRequiredForNextLevel();
        long levelStartExp = GameConstants.getExpRequiredForLevel(getPlayerLevel());

        if (requiredExp <= levelStartExp) return 1.0f;

        return (float)(currentExp - levelStartExp) / (requiredExp - levelStartExp);
    }

    /**
     * Obtiene el capítulo actual
     */
    public int getCurrentChapter() {
        return getPlayerData().currentChapter;
    }

    /**
     * Obtiene la etapa actual
     */
    public int getCurrentStage() {
        return getPlayerData().currentStage;
    }

    /**
     * Obtiene el oro actual
     */
    public long getGold() {
        return getPlayerData().gold;
    }

    /**
     * Obtiene las gemas actuales
     */
    public int getGems() {
        return getPlayerData().gems;
    }

    /**
     * Obtiene las monedas PvP actuales
     */
    public int getPvpCoins() {
        return getPlayerData().pvpCoins;
    }

    /**
     * Obtiene las monedas de guild actuales
     */
    public int getGuildCoins() {
        return getPlayerData().guildCoins;
    }

    /**
     * Obtiene el tiempo de juego total
     */
    public long getTotalPlayTime() {
        return getPlayerData().totalPlayTime;
    }

    /**
     * Obtiene el contador de pity del gacha
     */
    public int getGachaPityCount() {
        return getPlayerData().gachaPityCount;
    }

    // ==================== VERIFICACIONES DE RECURSOS ====================

    /**
     * Verifica si el jugador tiene suficiente oro
     */
    public boolean hasEnoughGold(long amount) {
        return getGold() >= amount;
    }

    /**
     * Verifica si el jugador tiene suficientes gemas
     */
    public boolean hasEnoughGems(int amount) {
        return getGems() >= amount;
    }

    /**
     * Verifica si el jugador tiene suficientes monedas PvP
     */
    public boolean hasEnoughPvpCoins(int amount) {
        return getPvpCoins() >= amount;
    }

    /**
     * Verifica si el jugador tiene suficientes monedas de guild
     */
    public boolean hasEnoughGuildCoins(int amount) {
        return getGuildCoins() >= amount;
    }

    /**
     * Verifica si el jugador puede acceder a un nivel específico
     */
    public boolean canAccessLevel(int chapter, int stage) {
        int currentChapter = getCurrentChapter();
        int currentStage = getCurrentStage();

        if (chapter < currentChapter) return true;
        if (chapter == currentChapter && stage <= currentStage) return true;

        return false;
    }

    // ==================== OPERACIONES DE MONEDAS ====================

    /**
     * Añade oro al jugador
     */
    public boolean addGold(long amount) {
        if (amount <= 0) return false;

        long currentGold = getGold();
        long newGold = Math.min(currentGold + amount, GameConstants.MAX_GOLD);

        return updateCurrency("gold", newGold);
    }

    /**
     * Gasta oro del jugador
     */
    public boolean spendGold(long amount) {
        if (amount <= 0 || !hasEnoughGold(amount)) {
            Log.w(TAG, "No se puede gastar oro: " + amount + " (disponible: " + getGold() + ")");
            return false;
        }

        long newGold = getGold() - amount;
        return updateCurrency("gold", newGold);
    }

    /**
     * Añade gemas al jugador
     */
    public boolean addGems(int amount) {
        if (amount <= 0) return false;

        int currentGems = getGems();
        int newGems = Math.min(currentGems + amount, GameConstants.MAX_GEMS);

        return updateCurrency("gems", newGems);
    }

    /**
     * Gasta gemas del jugador
     */
    public boolean spendGems(int amount) {
        if (amount <= 0 || !hasEnoughGems(amount)) {
            Log.w(TAG, "No se puede gastar gemas: " + amount + " (disponible: " + getGems() + ")");
            return false;
        }

        int newGems = getGems() - amount;
        return updateCurrency("gems", newGems);
    }

    /**
     * Añade monedas PvP al jugador
     */
    public boolean addPvpCoins(int amount) {
        if (amount <= 0) return false;

        int currentCoins = getPvpCoins();
        int newCoins = currentCoins + amount;

        return updateCurrency("pvp_coins", newCoins);
    }

    /**
     * Gasta monedas PvP del jugador
     */
    public boolean spendPvpCoins(int amount) {
        if (amount <= 0 || !hasEnoughPvpCoins(amount)) {
            Log.w(TAG, "No se puede gastar monedas PvP: " + amount);
            return false;
        }

        int newCoins = getPvpCoins() - amount;
        return updateCurrency("pvp_coins", newCoins);
    }

    /**
     * Añade monedas de guild al jugador
     */
    public boolean addGuildCoins(int amount) {
        if (amount <= 0) return false;

        int currentCoins = getGuildCoins();
        int newCoins = currentCoins + amount;

        return updateCurrency("guild_coins", newCoins);
    }

    /**
     * Gasta monedas de guild del jugador
     */
    public boolean spendGuildCoins(int amount) {
        if (amount <= 0 || !hasEnoughGuildCoins(amount)) {
            Log.w(TAG, "No se puede gastar monedas de guild: " + amount);
            return false;
        }

        int newCoins = getGuildCoins() - amount;
        return updateCurrency("guild_coins", newCoins);
    }

    /**
     * Método helper para actualizar monedas
     */
    private boolean updateCurrency(String currencyType, long newValue) {
        ContentValues values = new ContentValues();

        switch (currencyType) {
            case "gold":
                values.put(DatabaseContract.PlayerData.COLUMN_GOLD, newValue);
                cachedPlayerData.gold = newValue;
                break;
            case "gems":
                values.put(DatabaseContract.PlayerData.COLUMN_GEMS, (int)newValue);
                cachedPlayerData.gems = (int)newValue;
                break;
            case "pvp_coins":
                values.put(DatabaseContract.PlayerData.COLUMN_PVP_COINS, (int)newValue);
                cachedPlayerData.pvpCoins = (int)newValue;
                break;
            case "guild_coins":
                values.put(DatabaseContract.PlayerData.COLUMN_GUILD_COINS, (int)newValue);
                cachedPlayerData.guildCoins = (int)newValue;
                break;
            default:
                return false;
        }

        boolean success = dbHelper.updatePlayerData(values);
        if (success) {
            Log.d(TAG, "Actualizada moneda " + currencyType + " a: " + newValue);
        }

        return success;
    }

    // ==================== OPERACIONES DE EXPERIENCIA Y NIVEL ====================

    /**
     * Añade experiencia al jugador
     */
    public LevelUpResult addExperience(long amount) {
        if (amount <= 0) return new LevelUpResult(false, 0, 0);

        long currentExp = getPlayerExp();
        int currentLevel = getPlayerLevel();
        long newExp = currentExp + amount;

        // Verificar level ups
        int newLevel = currentLevel;
        int levelsGained = 0;

        while (newLevel < GameConstants.MAX_PLAYER_LEVEL) {
            long requiredExp = GameConstants.getExpRequiredForLevel(newLevel + 1);

            if (newExp >= requiredExp) {
                newLevel++;
                levelsGained++;
            } else {
                break;
            }
        }

        // Actualizar en base de datos
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerData.COLUMN_PLAYER_EXP, newExp);
        values.put(DatabaseContract.PlayerData.COLUMN_PLAYER_LEVEL, newLevel);

        boolean success = dbHelper.updatePlayerData(values);
        if (success) {
            cachedPlayerData.playerExp = newExp;
            cachedPlayerData.playerLevel = newLevel;

            Log.i(TAG, String.format("EXP añadida: %d (Total: %d, Nivel: %d → %d)",
                    amount, newExp, currentLevel, newLevel));
        }

        return new LevelUpResult(levelsGained > 0, levelsGained, newLevel);
    }

    /**
     * Verifica si el jugador puede subir de nivel
     */
    public boolean canLevelUp() {
        long currentExp = getPlayerExp();
        int currentLevel = getPlayerLevel();
        long requiredExp = GameConstants.getExpRequiredForLevel(currentLevel + 1);

        return currentExp >= requiredExp && currentLevel < GameConstants.MAX_PLAYER_LEVEL;
    }

    // ==================== OPERACIONES DE PROGRESO ====================

    /**
     * Actualiza el progreso de campaña
     */
    public boolean updateCampaignProgress(int chapter, int stage) {
        int currentChapter = getCurrentChapter();
        int currentStage = getCurrentStage();

        // Solo actualizar si es progreso hacia adelante
        boolean isProgress = (chapter > currentChapter) ||
                (chapter == currentChapter && stage > currentStage);

        if (!isProgress) {
            Log.d(TAG, "No es progreso real: " + chapter + "-" + stage);
            return true; // No es error, simplemente no hay progreso
        }

        boolean success = dbHelper.updatePlayerProgress(chapter, stage);
        if (success) {
            cachedPlayerData.currentChapter = chapter;
            cachedPlayerData.currentStage = stage;

            Log.i(TAG, String.format("Progreso actualizado: %d-%d → %d-%d",
                    currentChapter, currentStage, chapter, stage));

            // Verificar recompensas por progreso
            checkProgressRewards(chapter, stage);
        }

        return success;
    }

    /**
     * Verifica y otorga recompensas por progreso
     */
    private void checkProgressRewards(int chapter, int stage) {
        // Recompensas por completar capítulos
        if (stage == 1 && chapter > 1) { // Completó el capítulo anterior
            int gemsReward = 100 * (chapter - 1); // 100 gemas por capítulo
            addGems(gemsReward);
            Log.i(TAG, "Recompensa por capítulo " + (chapter-1) + ": " + gemsReward + " gemas");
        }

        // Recompensas especiales por hitos
        if (chapter == 2 && stage == 1) {
            addGems(200);
            Log.i(TAG, "Recompensa especial: Primer capítulo completado - 200 gemas");
        }

        // Desbloqueo de slots de equipo
        int totalStages = (chapter - 1) * 10 + stage; // Asumiendo 10 stages por capítulo
        if (GameConstants.isTeamSlotUnlockLevel(totalStages)) {
            Log.i(TAG, "¡Nuevo slot de equipo desbloqueado en nivel " + totalStages + "!");
        }
    }

    // ==================== SISTEMA AFK ====================

    /**
     * Actualiza el tiempo de inicio AFK
     */
    public void startAfkMode() {
        long currentTime = System.currentTimeMillis();
        cachedPlayerData.lastAfkTime = currentTime;

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerData.COLUMN_LAST_AFK_TIME, currentTime);

        dbHelper.updatePlayerData(values);
        Log.d(TAG, "Modo AFK iniciado");
    }

    /**
     * Calcula y otorga recompensas AFK
     */
    public AfkRewards calculateAfkRewards() {
        long lastAfkTime = getPlayerData().lastAfkTime;
        long currentTime = System.currentTimeMillis();
        long afkDuration = currentTime - lastAfkTime;

        // Convertir a minutos
        long afkMinutes = afkDuration / (60 * 1000);

        // Limitar a máximo permitido
        long maxAfkMinutes = GameConstants.MAX_AFK_HOURS * 60;
        afkMinutes = Math.min(afkMinutes, maxAfkMinutes);

        if (afkMinutes <= 0) {
            return new AfkRewards(0, 0, 0);
        }

        // Calcular recompensas basadas en nivel y progreso
        int playerLevel = getPlayerLevel();
        int currentChapter = getCurrentChapter();

        float levelMultiplier = 1.0f + (playerLevel - 1) * 0.1f; // +10% por nivel
        float chapterMultiplier = 1.0f + (currentChapter - 1) * 0.2f; // +20% por capítulo

        long goldReward = Math.round(GameConstants.AFK_GOLD_BASE_RATE * afkMinutes * levelMultiplier * chapterMultiplier);
        long expReward = Math.round(GameConstants.AFK_EXP_BASE_RATE * afkMinutes * levelMultiplier);

        // Probabilidad de gemas (5% por hora)
        int gemReward = 0;
        long afkHours = afkMinutes / 60;
        for (int i = 0; i < afkHours; i++) {
            if (Math.random() < 0.05) {
                gemReward += 1 + (int)(Math.random() * 5); // 1-5 gemas
            }
        }

        return new AfkRewards(goldReward, expReward, gemReward);
    }

    /**
     * Reclama las recompensas AFK
     */
    public boolean claimAfkRewards() {
        AfkRewards rewards = calculateAfkRewards();

        if (rewards.gold <= 0 && rewards.exp <= 0 && rewards.gems <= 0) {
            Log.d(TAG, "No hay recompensas AFK para reclamar");
            return false;
        }

        // Otorgar recompensas
        addGold(rewards.gold);
        addExperience(rewards.exp);
        addGems(rewards.gems);

        // Actualizar tiempo AFK
        startAfkMode();

        Log.i(TAG, String.format("Recompensas AFK reclamadas: %d oro, %d exp, %d gemas",
                rewards.gold, rewards.exp, rewards.gems));

        return true;
    }

    // ==================== SISTEMA DE GACHA ====================

    /**
     * Incrementa el contador de pity
     */
    public void incrementGachaPity() {
        int currentPity = getGachaPityCount();
        int newPity = currentPity + 1;

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerData.COLUMN_GACHA_PITY_COUNT, newPity);

        dbHelper.updatePlayerData(values);
        cachedPlayerData.gachaPityCount = newPity;

        Log.d(TAG, "Contador pity incrementado: " + newPity);
    }

    /**
     * Resetea el contador de pity (cuando se obtiene un héroe raro)
     */
    public void resetGachaPity() {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerData.COLUMN_GACHA_PITY_COUNT, 0);

        dbHelper.updatePlayerData(values);
        cachedPlayerData.gachaPityCount = 0;

        Log.d(TAG, "Contador pity reseteado");
    }

    /**
     * Verifica si el pity system debe activarse
     */
    public boolean shouldActivatePity() {
        return getGachaPityCount() >= GameConstants.PITY_SYSTEM_THRESHOLD;
    }

    // ==================== OPERACIONES GENERALES ====================

    /**
     * Actualiza el nombre del jugador
     */
    public boolean updatePlayerName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            Log.w(TAG, "Nombre inválido");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerData.COLUMN_PLAYER_NAME, newName.trim());

        boolean success = dbHelper.updatePlayerData(values);
        if (success) {
            cachedPlayerData.playerName = newName.trim();
            Log.i(TAG, "Nombre del jugador actualizado: " + newName);
        }

        return success;
    }

    /**
     * Añade tiempo de juego
     */
    public void addPlayTime(long milliseconds) {
        long currentPlayTime = getTotalPlayTime();
        long newPlayTime = currentPlayTime + milliseconds;

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerData.COLUMN_TOTAL_PLAY_TIME, newPlayTime);

        dbHelper.updatePlayerData(values);
        cachedPlayerData.totalPlayTime = newPlayTime;
    }

    /**
     * Obtiene estadísticas del jugador
     */
    public PlayerStats getPlayerStats() {
        PlayerData data = getPlayerData();

        PlayerStats stats = new PlayerStats();
        stats.level = data.playerLevel;
        stats.totalExp = data.playerExp;
        stats.expProgress = getExpProgress();
        stats.totalGoldEarned = data.gold; // Esto podría ser un acumulativo en el futuro
        stats.totalPlayTimeHours = data.totalPlayTime / (60 * 60 * 1000);
        stats.chaptersCompleted = data.currentChapter - 1;
        stats.totalStagesCompleted = (data.currentChapter - 1) * 10 + data.currentStage;
        stats.gachaPulls = data.gachaPityCount;

        return stats;
    }

    // ==================== CLASES INTERNAS ====================

    /**
     * Clase para almacenar datos del jugador
     */
    public static class PlayerData {
        public String playerName;
        public int playerLevel;
        public long playerExp;
        public int currentChapter;
        public int currentStage;
        public long gold;
        public int gems;
        public int pvpCoins;
        public int guildCoins;
        public long lastAfkTime;
        public long totalPlayTime;
        public int gachaPityCount;
        public long createdAt;
        public long updatedAt;

        public PlayerData() {}

        public PlayerData(Cursor cursor) {
            loadFromCursor(cursor);
        }

        private void loadFromCursor(Cursor cursor) {
            try {
                playerName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_PLAYER_NAME));
                playerLevel = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_PLAYER_LEVEL));
                playerExp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_PLAYER_EXP));
                currentChapter = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_CURRENT_CHAPTER));
                currentStage = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_CURRENT_STAGE));
                gold = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_GOLD));
                gems = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_GEMS));
                pvpCoins = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_PVP_COINS));
                guildCoins = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_GUILD_COINS));
                lastAfkTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_LAST_AFK_TIME));
                totalPlayTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_TOTAL_PLAY_TIME));
                gachaPityCount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_GACHA_PITY_COUNT));
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_CREATED_AT));
                updatedAt = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerData.COLUMN_UPDATED_AT));
            } catch (Exception e) {
                Log.e(TAG, "Error cargando PlayerData desde cursor", e);
            }
        }

        @Override
        public String toString() {
            return String.format("PlayerData{name='%s', level=%d, exp=%d, chapter=%d-%d, gold=%d, gems=%d}",
                    playerName, playerLevel, playerExp, currentChapter, currentStage, gold, gems);
        }
    }

    /**
     * Resultado de subida de nivel
     */
    public static class LevelUpResult {
        public final boolean leveledUp;
        public final int levelsGained;
        public final int newLevel;

        public LevelUpResult(boolean leveledUp, int levelsGained, int newLevel) {
            this.leveledUp = leveledUp;
            this.levelsGained = levelsGained;
            this.newLevel = newLevel;
        }
    }

    /**
     * Recompensas AFK
     */
    public static class AfkRewards {
        public final long gold;
        public final long exp;
        public final int gems;

        public AfkRewards(long gold, long exp, int gems) {
            this.gold = gold;
            this.exp = exp;
            this.gems = gems;
        }

        @Override
        public String toString() {
            return String.format("AfkRewards{gold=%d, exp=%d, gems=%d}", gold, exp, gems);
        }
    }

    /**
     * Estadísticas del jugador
     */
    public static class PlayerStats {
        public int level;
        public long totalExp;
        public float expProgress;
        public long totalGoldEarned;
        public long totalPlayTimeHours;
        public int chaptersCompleted;
        public int totalStagesCompleted;
        public int gachaPulls;

        @Override
        public String toString() {
            return String.format("PlayerStats{level=%d, exp=%d, gold=%d, playtime=%dh, chapters=%d}",
                    level, totalExp, totalGoldEarned, totalPlayTimeHours, chaptersCompleted);
        }
    }
}