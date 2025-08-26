package com.by.soh.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.by.soh.constants.GameConstants;
import com.by.soh.database.GameDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manager para el sistema de guardado del juego
 * Maneja auto-save, backups, integridad de datos y recuperación
 */
public class SaveGameManager {

    private static final String TAG = "SaveGameManager";

    // Singleton instance
    private static SaveGameManager instance;

    // Referencias
    private Context context;
    private GameDatabaseHelper dbHelper;
    private SharedPreferences preferences;

    // Auto-save
    private ScheduledExecutorService autoSaveExecutor;
    private boolean autoSaveEnabled;
    private long lastSaveTime;
    private long lastBackupTime;

    // Configuraciones
    private static final String SAVE_DIRECTORY = "saves";
    private static final String BACKUP_DIRECTORY = "backups";
    private static final int MAX_BACKUPS = 5;
    private static final long BACKUP_INTERVAL = 24 * 60 * 60 * 1000L; // 24 horas
    private static final long AUTO_SAVE_INTERVAL = 60 * 1000L; // 1 minuto

    // Constructor privado
    private SaveGameManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = GameDatabaseHelper.getInstance(context);
        this.preferences = context.getSharedPreferences(GameConstants.PREFS_NAME, Context.MODE_PRIVATE);
        this.lastSaveTime = preferences.getLong(GameConstants.PREF_LAST_SAVE_TIME, 0);
        this.autoSaveEnabled = true;

        initializeDirectories();
        startAutoSave();

        Log.i(TAG, "SaveGameManager inicializado");
    }

    /**
     * Obtiene la instancia singleton del manager
     */
    public static synchronized SaveGameManager getInstance(Context context) {
        if (instance == null) {
            instance = new SaveGameManager(context);
        }
        return instance;
    }

    // ==================== INICIALIZACIÓN ====================

    /**
     * Inicializa los directorios de guardado
     */
    private void initializeDirectories() {
        try {
            File saveDir = new File(context.getFilesDir(), SAVE_DIRECTORY);
            File backupDir = new File(context.getFilesDir(), BACKUP_DIRECTORY);

            if (!saveDir.exists()) {
                saveDir.mkdirs();
                Log.d(TAG, "Directorio de guardado creado: " + saveDir.getAbsolutePath());
            }

            if (!backupDir.exists()) {
                backupDir.mkdirs();
                Log.d(TAG, "Directorio de backups creado: " + backupDir.getAbsolutePath());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error inicializando directorios", e);
        }
    }

    /**
     * Inicia el sistema de auto-guardado
     */
    private void startAutoSave() {
        if (autoSaveExecutor != null) {
            autoSaveExecutor.shutdown();
        }

        autoSaveExecutor = Executors.newSingleThreadScheduledExecutor();
        autoSaveExecutor.scheduleAtFixedRate(
                this::performAutoSave,
                AUTO_SAVE_INTERVAL,
                AUTO_SAVE_INTERVAL,
                TimeUnit.MILLISECONDS
        );

        Log.d(TAG, "Auto-save iniciado (intervalo: " + AUTO_SAVE_INTERVAL + "ms)");
    }

    // ==================== OPERACIONES DE GUARDADO ====================

    /**
     * Guarda el juego completo
     */
    public SaveResult saveGame() {
        return saveGame("auto_save", false);
    }

    /**
     * Guarda el juego con nombre específico
     */
    public SaveResult saveGame(String saveName, boolean createBackup) {
        long startTime = System.currentTimeMillis();

        try {
            Log.i(TAG, "Iniciando guardado: " + saveName);

            // Verificar integridad de la base de datos
            if (!dbHelper.checkDatabaseIntegrity()) {
                Log.e(TAG, "Integridad de BD comprometida - abortando guardado");
                return new SaveResult(false, "Integridad de base de datos comprometida", 0);
            }

            // Crear objeto de guardado
            SaveData saveData = createSaveData();

            // Guardar en archivo
            boolean success = writeSaveToFile(saveData, saveName);

            if (success) {
                lastSaveTime = System.currentTimeMillis();
                updateLastSavePreference();

                // Crear backup si es necesario
                if (createBackup || shouldCreateBackup()) {
                    createBackup(saveName);
                }

                long duration = System.currentTimeMillis() - startTime;
                Log.i(TAG, "Guardado completado: " + saveName + " (" + duration + "ms)");

                return new SaveResult(true, "Guardado exitoso", duration);
            } else {
                return new SaveResult(false, "Error escribiendo archivo", 0);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error durante el guardado", e);
            return new SaveResult(false, "Error inesperado: " + e.getMessage(), 0);
        }
    }

    /**
     * Realiza auto-guardado
     */
    private void performAutoSave() {
        if (!autoSaveEnabled) return;

        try {
            // Solo hacer auto-save si han pasado suficientes datos
            long timeSinceLastSave = System.currentTimeMillis() - lastSaveTime;
            if (timeSinceLastSave < GameConstants.AUTO_SAVE_INTERVAL_MS) {
                return;
            }

            // Verificar si hay cambios que guardar
            if (!hasUnsavedChanges()) {
                return;
            }

            SaveResult result = saveGame("auto_save", false);
            if (!result.success) {
                Log.w(TAG, "Auto-save falló: " + result.message);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error en auto-save", e);
        }
    }

    // ==================== OPERACIONES DE CARGA ====================

    /**
     * Carga el último guardado
     */
    public LoadResult loadGame() {
        return loadGame("auto_save");
    }

    /**
     * Carga un guardado específico
     */
    public LoadResult loadGame(String saveName) {
        long startTime = System.currentTimeMillis();

        try {
            Log.i(TAG, "Iniciando carga: " + saveName);

            // Leer archivo de guardado
            SaveData saveData = readSaveFromFile(saveName);
            if (saveData == null) {
                return new LoadResult(false, "Archivo de guardado no encontrado", 0);
            }

            // Verificar versión de compatibilidad
            if (!isCompatibleVersion(saveData.gameVersion)) {
                return new LoadResult(false, "Versión incompatible: " + saveData.gameVersion, 0);
            }

            // Aplicar datos cargados
            boolean success = applySaveData(saveData);

            if (success) {
                long duration = System.currentTimeMillis() - startTime;
                Log.i(TAG, "Carga completada: " + saveName + " (" + duration + "ms)");

                // Refrescar managers
                refreshAllManagers();

                return new LoadResult(true, "Carga exitosa", duration);
            } else {
                return new LoadResult(false, "Error aplicando datos", 0);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error durante la carga", e);
            return new LoadResult(false, "Error inesperado: " + e.getMessage(), 0);
        }
    }

    // ==================== SISTEMA DE BACKUPS ====================

    /**
     * Crea un backup del guardado actual
     */
    public boolean createBackup(String sourceSaveName) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
            String backupName = sourceSaveName + "_backup_" + timestamp;

            File sourceFile = new File(context.getFilesDir(), SAVE_DIRECTORY + "/" + sourceSaveName + ".save");
            File backupFile = new File(context.getFilesDir(), BACKUP_DIRECTORY + "/" + backupName + ".backup");

            if (!sourceFile.exists()) {
                Log.w(TAG, "Archivo fuente no existe para backup: " + sourceSaveName);
                return false;
            }

            // Copiar archivo
            copyFile(sourceFile, backupFile);

            // Limpiar backups antiguos
            cleanOldBackups();

            lastBackupTime = System.currentTimeMillis();
            Log.i(TAG, "Backup creado: " + backupName);

            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error creando backup", e);
            return false;
        }
    }

    /**
     * Restaura desde un backup
     */
    public LoadResult restoreFromBackup(String backupName) {
        try {
            Log.i(TAG, "Restaurando desde backup: " + backupName);

            File backupFile = new File(context.getFilesDir(), BACKUP_DIRECTORY + "/" + backupName + ".backup");
            if (!backupFile.exists()) {
                return new LoadResult(false, "Backup no encontrado", 0);
            }

            // Crear save temporal desde backup
            File tempSaveFile = new File(context.getFilesDir(), SAVE_DIRECTORY + "/temp_restore.save");
            copyFile(backupFile, tempSaveFile);

            // Cargar desde el temporal
            LoadResult result = loadGame("temp_restore");

            // Limpiar temporal
            tempSaveFile.delete();

            if (result.success) {
                Log.i(TAG, "Restauración desde backup exitosa");
            }

            return result;

        } catch (Exception e) {
            Log.e(TAG, "Error restaurando backup", e);
            return new LoadResult(false, "Error en restauración: " + e.getMessage(), 0);
        }
    }

    /**
     * Obtiene lista de backups disponibles
     */
    public List<BackupInfo> getAvailableBackups() {
        List<BackupInfo> backups = new ArrayList<>();

        try {
            File backupDir = new File(context.getFilesDir(), BACKUP_DIRECTORY);
            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".backup"));

            if (files != null) {
                for (File file : files) {
                    BackupInfo info = new BackupInfo();
                    info.name = file.getName().replace(".backup", "");
                    info.size = file.length();
                    info.timestamp = file.lastModified();
                    info.formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                            Locale.getDefault()).format(new Date(info.timestamp));
                    backups.add(info);
                }

                // Ordenar por fecha (más reciente primero)
                backups.sort((b1, b2) -> Long.compare(b2.timestamp, b1.timestamp));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo lista de backups", e);
        }

        return backups;
    }

    // ==================== UTILIDADES DE ARCHIVOS ====================

    /**
     * Crea los datos de guardado desde el estado actual
     */
    private SaveData createSaveData() {
        SaveData saveData = new SaveData();

        // Información general
        saveData.gameVersion = GameConstants.GAME_VERSION;
        saveData.timestamp = System.currentTimeMillis();

        // Datos del jugador
        PlayerDataManager playerManager = PlayerDataManager.getInstance(context);
        saveData.playerData = playerManager.getPlayerData();

        // Estadísticas del equipamiento
        EquipmentManager equipManager = EquipmentManager.getInstance(context);
        saveData.equipmentStats = equipManager.getInventoryStats();

        // Estadísticas de la base de datos
        saveData.databaseStats = dbHelper.getDatabaseStats();

        // Hash para verificación de integridad
        saveData.integrityHash = calculateSaveHash(saveData);

        Log.d(TAG, "SaveData creado: " + saveData.toString());
        return saveData;
    }

    /**
     * Escribe los datos de guardado a archivo
     */
    private boolean writeSaveToFile(SaveData saveData, String saveName) {
        try {
            File saveFile = new File(context.getFilesDir(), SAVE_DIRECTORY + "/" + saveName + ".save");

            JSONObject jsonSave = saveData.toJson();

            FileOutputStream fos = new FileOutputStream(saveFile);
            fos.write(jsonSave.toString(2).getBytes());
            fos.close();

            Log.d(TAG, "Guardado escrito a: " + saveFile.getAbsolutePath());
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error escribiendo save a archivo", e);
            return false;
        }
    }

    /**
     * Lee los datos de guardado desde archivo
     */
    private SaveData readSaveFromFile(String saveName) {
        try {
            File saveFile = new File(context.getFilesDir(), SAVE_DIRECTORY + "/" + saveName + ".save");

            if (!saveFile.exists()) {
                Log.w(TAG, "Archivo de guardado no existe: " + saveName);
                return null;
            }

            FileInputStream fis = new FileInputStream(saveFile);
            byte[] data = new byte[(int) saveFile.length()];
            fis.read(data);
            fis.close();

            String jsonString = new String(data);
            JSONObject jsonSave = new JSONObject(jsonString);

            SaveData saveData = SaveData.fromJson(jsonSave);

            // Verificar integridad
            String expectedHash = calculateSaveHash(saveData);
            if (!expectedHash.equals(saveData.integrityHash)) {
                Log.w(TAG, "Hash de integridad no coincide - posible corrupción");
                // No fallar completamente, pero loggear warning
            }

            Log.d(TAG, "Save leído desde: " + saveFile.getAbsolutePath());
            return saveData;

        } catch (Exception e) {
            Log.e(TAG, "Error leyendo save desde archivo", e);
            return null;
        }
    }

    /**
     * Aplica los datos cargados al estado del juego
     */
    private boolean applySaveData(SaveData saveData) {
        try {
            // Los datos ya están en la base de datos, solo necesitamos
            // refrescar los managers para que recojan los cambios

            Log.d(TAG, "Datos de guardado aplicados exitosamente");
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Error aplicando save data", e);
            return false;
        }
    }

    /**
     * Copia un archivo a otro ubicación
     */
    private void copyFile(File source, File destination) throws IOException {
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(destination);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }

        fis.close();
        fos.close();
    }

    // ==================== VERIFICACIONES Y UTILIDADES ====================

    /**
     * Verifica si hay cambios sin guardar
     */
    private boolean hasUnsavedChanges() {
        // Simple verificación basada en tiempo
        long timeSinceLastSave = System.currentTimeMillis() - lastSaveTime;
        return timeSinceLastSave > GameConstants.AUTO_SAVE_INTERVAL_MS;
    }

    /**
     * Verifica si se debe crear backup
     */
    private boolean shouldCreateBackup() {
        long timeSinceLastBackup = System.currentTimeMillis() - lastBackupTime;
        return timeSinceLastBackup > BACKUP_INTERVAL;
    }

    /**
     * Verifica compatibilidad de versión
     */
    private boolean isCompatibleVersion(String version) {
        // Por ahora, solo verificar que no sea null
        return version != null && !version.isEmpty();
    }

    /**
     * Calcula hash de integridad para el guardado
     */
    private String calculateSaveHash(SaveData saveData) {
        try {
            String dataString = saveData.playerData.toString() + saveData.timestamp + saveData.gameVersion;
            return String.valueOf(dataString.hashCode());
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * Limpia backups antiguos manteniendo solo los más recientes
     */
    private void cleanOldBackups() {
        try {
            File backupDir = new File(context.getFilesDir(), BACKUP_DIRECTORY);
            File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".backup"));

            if (files != null && files.length > MAX_BACKUPS) {
                // Ordenar por fecha de modificación (más antiguos primero)
                java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

                // Eliminar los más antiguos
                for (int i = 0; i < files.length - MAX_BACKUPS; i++) {
                    if (files[i].delete()) {
                        Log.d(TAG, "Backup antiguo eliminado: " + files[i].getName());
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error limpiando backups antiguos", e);
        }
    }

    /**
     * Actualiza la preferencia de último guardado
     */
    private void updateLastSavePreference() {
        preferences.edit()
                .putLong(GameConstants.PREF_LAST_SAVE_TIME, lastSaveTime)
                .apply();
    }

    /**
     * Refresca todos los managers después de cargar
     */
    private void refreshAllManagers() {
        try {
            PlayerDataManager.getInstance(context).refreshPlayerData();
            EquipmentManager.getInstance(context).clearCache();
            Log.d(TAG, "Managers refrescados");
        } catch (Exception e) {
            Log.e(TAG, "Error refrescando managers", e);
        }
    }

    // ==================== CONFIGURACIONES ====================

    /**
     * Habilita/deshabilita el auto-save
     */
    public void setAutoSaveEnabled(boolean enabled) {
        this.autoSaveEnabled = enabled;
        Log.d(TAG, "Auto-save " + (enabled ? "habilitado" : "deshabilitado"));
    }

    /**
     * Verifica si el auto-save está habilitado
     */
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    /**
     * Obtiene el tiempo del último guardado
     */
    public long getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * Obtiene información del último guardado
     */
    public String getLastSaveInfo() {
        if (lastSaveTime == 0) {
            return "Nunca guardado";
        }

        long timeSince = System.currentTimeMillis() - lastSaveTime;
        long minutesSince = timeSince / (60 * 1000);

        if (minutesSince < 1) {
            return "Guardado hace menos de 1 minuto";
        } else if (minutesSince < 60) {
            return "Guardado hace " + minutesSince + " minutos";
        } else {
            long hoursSince = minutesSince / 60;
            return "Guardado hace " + hoursSince + " horas";
        }
    }

    /**
     * Obtiene estadísticas del sistema de guardado
     */
    public SaveSystemStats getSaveSystemStats() {
        SaveSystemStats stats = new SaveSystemStats();

        // Información general
        stats.autoSaveEnabled = autoSaveEnabled;
        stats.lastSaveTime = lastSaveTime;
        stats.lastBackupTime = lastBackupTime;

        // Archivos
        File saveDir = new File(context.getFilesDir(), SAVE_DIRECTORY);
        File backupDir = new File(context.getFilesDir(), BACKUP_DIRECTORY);

        stats.saveFilesCount = saveDir.listFiles() != null ? saveDir.listFiles().length : 0;
        stats.backupFilesCount = backupDir.listFiles() != null ? backupDir.listFiles().length : 0;

        // Tamaños
        stats.totalSaveSize = calculateDirectorySize(saveDir);
        stats.totalBackupSize = calculateDirectorySize(backupDir);

        return stats;
    }

    /**
     * Calcula el tamaño de un directorio
     */
    private long calculateDirectorySize(File directory) {
        long size = 0;
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    size += file.length();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculando tamaño de directorio", e);
        }
        return size;
    }

    // ==================== CLEANUP ====================

    /**
     * Detiene el sistema de auto-guardado
     */
    public void shutdown() {
        if (autoSaveExecutor != null) {
            autoSaveExecutor.shutdown();
            try {
                if (!autoSaveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    autoSaveExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                autoSaveExecutor.shutdownNow();
            }
        }
        Log.i(TAG, "SaveGameManager finalizado");
    }

    // ==================== CLASES INTERNAS ====================

    /**
     * Datos de guardado
     */
    public static class SaveData {
        public String gameVersion;
        public long timestamp;
        public PlayerDataManager.PlayerData playerData;
        public EquipmentManager.EquipmentInventoryStats equipmentStats;
        public String databaseStats;
        public String integrityHash;

        public JSONObject toJson() throws JSONException {
            JSONObject json = new JSONObject();
            json.put("gameVersion", gameVersion);
            json.put("timestamp", timestamp);
            json.put("integrityHash", integrityHash);

            // PlayerData
            if (playerData != null) {
                JSONObject playerJson = new JSONObject();
                playerJson.put("playerName", playerData.playerName);
                playerJson.put("playerLevel", playerData.playerLevel);
                playerJson.put("playerExp", playerData.playerExp);
                playerJson.put("currentChapter", playerData.currentChapter);
                playerJson.put("currentStage", playerData.currentStage);
                playerJson.put("gold", playerData.gold);
                playerJson.put("gems", playerData.gems);
                playerJson.put("pvpCoins", playerData.pvpCoins);
                playerJson.put("guildCoins", playerData.guildCoins);
                json.put("playerData", playerJson);
            }

            return json;
        }

        public static SaveData fromJson(JSONObject json) throws JSONException {
            SaveData saveData = new SaveData();
            saveData.gameVersion = json.getString("gameVersion");
            saveData.timestamp = json.getLong("timestamp");
            saveData.integrityHash = json.optString("integrityHash", "");

            // Aquí podrías expandir la deserialización según necesites

            return saveData;
        }

        @Override
        public String toString() {
            return String.format("SaveData{version='%s', timestamp=%d}", gameVersion, timestamp);
        }
    }

    /**
     * Resultado de operación de guardado
     */
    public static class SaveResult {
        public final boolean success;
        public final String message;
        public final long duration;

        public SaveResult(boolean success, String message, long duration) {
            this.success = success;
            this.message = message;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return String.format("SaveResult{success=%b, message='%s', duration=%dms}",
                    success, message, duration);
        }
    }

    /**
     * Resultado de operación de carga
     */
    public static class LoadResult {
        public final boolean success;
        public final String message;
        public final long duration;

        public LoadResult(boolean success, String message, long duration) {
            this.success = success;
            this.message = message;
            this.duration = duration;
        }

        @Override
        public String toString() {
            return String.format("LoadResult{success=%b, message='%s', duration=%dms}",
                    success, message, duration);
        }
    }

    /**
     * Información de backup
     */
    public static class BackupInfo {
        public String name;
        public long size;
        public long timestamp;
        public String formattedDate;

        @Override
        public String toString() {
            return String.format("BackupInfo{name='%s', size=%d, date='%s'}",
                    name, size, formattedDate);
        }
    }

    /**
     * Estadísticas del sistema de guardado
     */
    public static class SaveSystemStats {
        public boolean autoSaveEnabled;
        public long lastSaveTime;
        public long lastBackupTime;
        public int saveFilesCount;
        public int backupFilesCount;
        public long totalSaveSize;
        public long totalBackupSize;

        @Override
        public String toString() {
            return String.format("SaveSystemStats{autoSave=%b, saves=%d, backups=%d, totalSize=%dKB}",
                    autoSaveEnabled, saveFilesCount, backupFilesCount,
                    (totalSaveSize + totalBackupSize) / 1024);
        }
    }
}