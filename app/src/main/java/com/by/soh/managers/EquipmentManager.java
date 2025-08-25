package com.by.soh.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.by.soh.constants.EquipmentConstants;
import com.by.soh.constants.GameConstants;
import com.by.soh.database.GameDatabaseHelper;
import com.by.soh.database.DatabaseContract;
import com.by.soh.models.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager para el sistema de equipamiento
 * Maneja todas las operaciones relacionadas con equipment
 */
public class EquipmentManager {

    private static final String TAG = "EquipmentManager";

    // Singleton instance
    private static EquipmentManager instance;

    // Referencias
    private GameDatabaseHelper dbHelper;
    private Context context;

    // Cache
    private Map<Long, Equipment> equipmentCache;
    private long lastCacheUpdate;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutos

    // Constructor privado
    private EquipmentManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = GameDatabaseHelper.getInstance(context);
        this.equipmentCache = new HashMap<>();
        this.lastCacheUpdate = 0;
    }

    /**
     * Obtiene la instancia singleton del manager
     */
    public static synchronized EquipmentManager getInstance(Context context) {
        if (instance == null) {
            instance = new EquipmentManager(context);
        }
        return instance;
    }

    // ==================== OPERACIONES BÁSICAS ====================

    /**
     * Obtiene todo el equipamiento del jugador
     */
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipment = new ArrayList<>();

        Cursor cursor = dbHelper.getAllEquipment();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Equipment item = new Equipment(cursor);
                equipment.add(item);
                equipmentCache.put(item.getId(), item); // Actualizar cache
            }
            cursor.close();
        }

        lastCacheUpdate = System.currentTimeMillis();
        Log.d(TAG, "Cargado equipamiento: " + equipment.size() + " items");

        return equipment;
    }

    /**
     * Obtiene equipamiento específico por ID
     */
    public Equipment getEquipmentById(long equipmentId) {
        // Verificar cache primero
        if (isCacheValid() && equipmentCache.containsKey(equipmentId)) {
            return equipmentCache.get(equipmentId);
        }

        Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseContract.Equipment.TABLE_NAME,
                null,
                DatabaseContract.Equipment._ID + " = ?",
                new String[]{String.valueOf(equipmentId)},
                null, null, null
        );

        Equipment equipment = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                equipment = new Equipment(cursor);
                equipmentCache.put(equipmentId, equipment); // Actualizar cache
            }
            cursor.close();
        }

        return equipment;
    }

    /**
     * Obtiene el equipamiento equipado por un héroe específico
     */
    public List<Equipment> getHeroEquipment(long heroId) {
        List<Equipment> equipment = new ArrayList<>();

        Cursor cursor = dbHelper.getHeroEquipment(heroId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Equipment item = new Equipment(cursor);
                equipment.add(item);
                equipmentCache.put(item.getId(), item); // Actualizar cache
            }
            cursor.close();
        }

        Log.d(TAG, "Equipamiento del héroe " + heroId + ": " + equipment.size() + " items");
        return equipment;
    }

    /**
     * Obtiene equipamiento no equipado (disponible)
     */
    public List<Equipment> getAvailableEquipment() {
        return searchEquipment(null, null, true);
    }

    // ==================== OPERACIONES DE CREACIÓN ====================

    /**
     * Crea nuevo equipamiento
     */
    public long createEquipment(int type, int rarity, String mainStatType,
                                int mainStatValue, int setId) {
        try {
            Equipment equipment = new Equipment(type, rarity, 0, mainStatType, mainStatValue, setId);

            long equipmentId = dbHelper.insertEquipment(
                    type, rarity, mainStatType, mainStatValue,
                    equipment.secondaryStatsToJson(), setId
            );

            if (equipmentId != -1) {
                equipment.setId(equipmentId);
                equipmentCache.put(equipmentId, equipment);
                Log.i(TAG, "Equipamiento creado: " + equipment.toString());
            }

            return equipmentId;

        } catch (Exception e) {
            Log.e(TAG, "Error creando equipamiento", e);
            return -1;
        }
    }

    /**
     * Crea equipamiento aleatorio
     */
    public long createRandomEquipment(int minRarity, int maxRarity, int playerLevel) {
        Equipment randomEquipment = Equipment.createRandomEquipment(minRarity, maxRarity, playerLevel);

        return createEquipment(
                randomEquipment.getEquipmentType(),
                randomEquipment.getRarity(),
                randomEquipment.getMainStatType(),
                randomEquipment.getMainStatValue(),
                randomEquipment.getSetId()
        );
    }

    /**
     * Genera loot de equipamiento para un nivel específico
     */
    public List<Long> generateLoot(int playerLevel, int quantity, boolean guaranteeRare) {
        List<Long> generatedIds = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            int rarity;

            if (guaranteeRare && i == 0) {
                // Primer item garantizado raro o mejor
                rarity = EquipmentConstants.EQUIPMENT_BLUE +
                        (int)(Math.random() * (EquipmentConstants.EQUIPMENT_RED - EquipmentConstants.EQUIPMENT_BLUE + 1));
            } else {
                // Rarity basada en probabilidades
                rarity = determineRandomRarity();
            }

            long equipmentId = createRandomEquipment(rarity, rarity, playerLevel);
            if (equipmentId != -1) {
                generatedIds.add(equipmentId);
            }
        }

        Log.i(TAG, "Loot generado: " + generatedIds.size() + " items para nivel " + playerLevel);
        return generatedIds;
    }

    // ==================== OPERACIONES DE EQUIPAR ====================

    /**
     * Equipa un item a un héroe
     */
    public boolean equipToHero(long equipmentId, long heroId) {
        try {
            Equipment equipment = getEquipmentById(equipmentId);
            if (equipment == null) {
                Log.e(TAG, "Equipment no encontrado: " + equipmentId);
                return false;
            }

            if (equipment.isEquipped()) {
                Log.w(TAG, "Equipment ya está equipado: " + equipmentId);
                return false;
            }

            // Verificar si el héroe ya tiene algo equipado en este slot
            Equipment currentEquipment = getHeroEquipmentByType(heroId, equipment.getEquipmentType());
            if (currentEquipment != null) {
                // Desequipar el item actual
                unequipFromHero(currentEquipment.getId());
            }

            // Equipar el nuevo item
            boolean success = dbHelper.equipItemToHero(equipmentId, heroId);
            if (success) {
                equipment.setEquippedByHero(heroId);
                equipmentCache.put(equipmentId, equipment);
                Log.i(TAG, "Equipment " + equipmentId + " equipado a héroe " + heroId);
            }

            return success;

        } catch (Exception e) {
            Log.e(TAG, "Error equipando item", e);
            return false;
        }
    }

    /**
     * Desequipa un item de un héroe
     */
    public boolean unequipFromHero(long equipmentId) {
        try {
            Equipment equipment = getEquipmentById(equipmentId);
            if (equipment == null) {
                Log.e(TAG, "Equipment no encontrado: " + equipmentId);
                return false;
            }

            if (!equipment.isEquipped()) {
                Log.w(TAG, "Equipment ya está desequipado: " + equipmentId);
                return true;
            }

            boolean success = dbHelper.unequipItem(equipmentId);
            if (success) {
                equipment.setEquippedByHero(0);
                equipmentCache.put(equipmentId, equipment);
                Log.i(TAG, "Equipment " + equipmentId + " desequipado");
            }

            return success;

        } catch (Exception e) {
            Log.e(TAG, "Error desequipando item", e);
            return false;
        }
    }

    /**
     * Obtiene el equipamiento de un héroe por tipo específico
     */
    private Equipment getHeroEquipmentByType(long heroId, int equipmentType) {
        List<Equipment> heroEquipment = getHeroEquipment(heroId);

        for (Equipment equipment : heroEquipment) {
            if (equipment.getEquipmentType() == equipmentType) {
                return equipment;
            }
        }

        return null;
    }

    // ==================== OPERACIONES DE MEJORA ====================

    /**
     * Mejora un equipamiento (+1 enhancement)
     */
    public boolean enhanceEquipment(long equipmentId) {
        try {
            Equipment equipment = getEquipmentById(equipmentId);
            if (equipment == null) {
                Log.e(TAG, "Equipment no encontrado para mejora: " + equipmentId);
                return false;
            }

            if (equipment.getEnhancement() >= EquipmentConstants.MAX_EQUIPMENT_ENHANCEMENT) {
                Log.w(TAG, "Equipment ya está en nivel máximo: " + equipmentId);
                return false;
            }

            // Verificar costo
            long cost = equipment.getUpgradeCost();
            // Aquí deberías verificar si el jugador tiene suficiente oro
            // PlayerDataManager.getInstance(context).hasEnoughGold(cost)

            // Realizar mejora
            boolean success = equipment.enhance();
            if (success) {
                // Actualizar en base de datos
                success = updateEquipmentInDatabase(equipment);
                if (success) {
                    equipmentCache.put(equipmentId, equipment);
                    Log.i(TAG, "Equipment mejorado: " + equipment.toString());
                }
            }

            return success;

        } catch (Exception e) {
            Log.e(TAG, "Error mejorando equipment", e);
            return false;
        }
    }

    /**
     * Reforja un equipamiento (rejuve)
     */
    public boolean reforgeEquipment(long equipmentId) {
        try {
            Equipment equipment = getEquipmentById(equipmentId);
            if (equipment == null) {
                Log.e(TAG, "Equipment no encontrado para reforja: " + equipmentId);
                return false;
            }

            if (!EquipmentConstants.canMeltEquipment(equipment.getRarity())) {
                Log.w(TAG, "Equipment no puede ser reforjado: " + equipmentId);
                return false;
            }

            // Verificar costos
            int goldCost = equipment.getReforgeCost();
            int gemCost = equipment.getReforgeGemCost();
            // Aquí verificar si el jugador tiene suficientes recursos

            // Realizar reforja
            boolean success = equipment.reforge();
            if (success) {
                // Actualizar en base de datos
                success = updateEquipmentInDatabase(equipment);
                if (success) {
                    equipmentCache.put(equipmentId, equipment);
                    Log.i(TAG, "Equipment reforjado: " + equipment.toString());
                }
            }

            return success;

        } catch (Exception e) {
            Log.e(TAG, "Error reforjando equipment", e);
            return false;
        }
    }

    // ==================== SISTEMA DE FORJA (MELT) ====================

    /**
     * Forja varios equipamientos en otros de mayor rareza
     */
    public List<Long> meltEquipment(List<Long> equipmentIds) {
        List<Long> newEquipmentIds = new ArrayList<>();

        if (equipmentIds.size() != EquipmentConstants.MELT_ITEMS_REQUIRED) {
            Log.e(TAG, "Se requieren exactamente " + EquipmentConstants.MELT_ITEMS_REQUIRED + " items para forjar");
            return newEquipmentIds;
        }

        try {
            // Verificar que todos los items existen y tienen la misma rareza
            List<Equipment> itemsToMelt = new ArrayList<>();
            int commonRarity = -1;

            for (Long equipmentId : equipmentIds) {
                Equipment equipment = getEquipmentById(equipmentId);
                if (equipment == null) {
                    Log.e(TAG, "Equipment no encontrado: " + equipmentId);
                    return newEquipmentIds;
                }

                if (equipment.isEquipped()) {
                    Log.e(TAG, "No se puede forjar equipment equipado: " + equipmentId);
                    return newEquipmentIds;
                }

                if (equipment.isLocked()) {
                    Log.e(TAG, "No se puede forjar equipment bloqueado: " + equipmentId);
                    return newEquipmentIds;
                }

                if (commonRarity == -1) {
                    commonRarity = equipment.getRarity();
                } else if (commonRarity != equipment.getRarity()) {
                    Log.e(TAG, "Todos los items deben tener la misma rareza para forjar");
                    return newEquipmentIds;
                }

                if (!EquipmentConstants.canMeltEquipment(equipment.getRarity())) {
                    Log.e(TAG, "Equipment no puede ser forjado: " + equipmentId);
                    return newEquipmentIds;
                }

                itemsToMelt.add(equipment);
            }

            // Determinar cuántos items crear
            int itemsToCreate = determineItemsFromMelt();
            int newRarity = commonRarity + 1;

            // Crear nuevos items
            for (int i = 0; i < itemsToCreate; i++) {
                long newEquipmentId = createRandomEquipment(newRarity, newRarity, 50); // Nivel base
                if (newEquipmentId != -1) {
                    newEquipmentIds.add(newEquipmentId);
                }
            }

            // Eliminar items originales si se crearon los nuevos
            if (!newEquipmentIds.isEmpty()) {
                for (Long equipmentId : equipmentIds) {
                    deleteEquipment(equipmentId);
                }
                Log.i(TAG, "Forjado completado: " + equipmentIds.size() + " → " + newEquipmentIds.size() + " items");
            }

        } catch (Exception e) {
            Log.e(TAG, "Error en proceso de forja", e);
        }

        return newEquipmentIds;
    }

    /**
     * Determina cuántos items se obtienen del melt
     */
    private int determineItemsFromMelt() {
        double random = Math.random();

        if (random < EquipmentConstants.MELT_3_ITEM_CHANCE) {
            return 3;
        } else if (random < EquipmentConstants.MELT_3_ITEM_CHANCE + EquipmentConstants.MELT_2_ITEM_CHANCE) {
            return 2;
        } else {
            return 1;
        }
    }

    // ==================== OPERACIONES DE BÚSQUEDA Y FILTRADO ====================

    /**
     * Busca equipamiento con filtros
     */
    public List<Equipment> searchEquipment(String type, String rarity, boolean onlyUnequipped) {
        List<Equipment> results = new ArrayList<>();

        Cursor cursor = dbHelper.searchEquipment(type, rarity, onlyUnequipped);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Equipment equipment = new Equipment(cursor);
                results.add(equipment);
                equipmentCache.put(equipment.getId(), equipment); // Actualizar cache
            }
            cursor.close();
        }

        return results;
    }

    /**
     * Filtra equipamiento por poder mínimo
     */
    public List<Equipment> filterByMinimumPower(List<Equipment> equipment, int minimumPower) {
        List<Equipment> filtered = new ArrayList<>();

        for (Equipment item : equipment) {
            if (item.getPowerRating() >= minimumPower) {
                filtered.add(item);
            }
        }

        return filtered;
    }

    /**
     * Ordena equipamiento por diferentes criterios
     */
    public List<Equipment> sortEquipment(List<Equipment> equipment, String sortBy) {
        List<Equipment> sorted = new ArrayList<>(equipment);

        switch (sortBy.toLowerCase()) {
            case "power":
                sorted.sort((e1, e2) -> Integer.compare(e2.getPowerRating(), e1.getPowerRating()));
                break;
            case "rarity":
                sorted.sort((e1, e2) -> Integer.compare(e2.getRarity(), e1.getRarity()));
                break;
            case "type":
                sorted.sort((e1, e2) -> Integer.compare(e1.getEquipmentType(), e2.getEquipmentType()));
                break;
            case "enhancement":
                sorted.sort((e1, e2) -> Integer.compare(e2.getEnhancement(), e1.getEnhancement()));
                break;
            case "obtained":
                sorted.sort((e1, e2) -> Long.compare(e2.getObtainedAt(), e1.getObtainedAt()));
                break;
            default:
                // Por defecto, ordenar por poder
                sorted.sort((e1, e2) -> Integer.compare(e2.getPowerRating(), e1.getPowerRating()));
                break;
        }

        return sorted;
    }

    // ==================== ANÁLISIS DE SETS ====================

    /**
     * Analiza los sets equipados por un héroe
     */
    public Map<Integer, Integer> analyzeHeroSets(long heroId) {
        Map<Integer, Integer> setPiecesCount = new HashMap<>();
        List<Equipment> heroEquipment = getHeroEquipment(heroId);

        for (Equipment equipment : heroEquipment) {
            if (equipment.getSetId() > 0) {
                int currentCount = setPiecesCount.getOrDefault(equipment.getSetId(), 0);
                setPiecesCount.put(equipment.getSetId(), currentCount + 1);
            }
        }

        return setPiecesCount;
    }

    /**
     * Obtiene bonos de set activos para un héroe
     */
    public List<String> getActiveSetBonuses(long heroId) {
        List<String> activeBonuses = new ArrayList<>();
        Map<Integer, Integer> setPieces = analyzeHeroSets(heroId);

        for (Map.Entry<Integer, Integer> entry : setPieces.entrySet()) {
            int setId = entry.getKey();
            int pieceCount = entry.getValue();

            if (pieceCount >= 2 && setId < EquipmentConstants.SET_BONUSES_2_PIECES.length) {
                activeBonuses.add(EquipmentConstants.SET_BONUSES_2_PIECES[setId]);
            }

            if (pieceCount >= 4 && setId < EquipmentConstants.SET_BONUSES_4_PIECES.length) {
                activeBonuses.add(EquipmentConstants.SET_BONUSES_4_PIECES[setId]);
            }

            if (pieceCount >= 6 && setId < EquipmentConstants.SET_BONUSES_6_PIECES.length) {
                activeBonuses.add(EquipmentConstants.SET_BONUSES_6_PIECES[setId]);
            }
        }

        return activeBonuses;
    }

    // ==================== OPERACIONES DE BASE DE DATOS ====================

    /**
     * Actualiza un equipamiento en la base de datos
     */
    private boolean updateEquipmentInDatabase(Equipment equipment) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Equipment.COLUMN_ENHANCEMENT, equipment.getEnhancement());
            values.put(DatabaseContract.Equipment.COLUMN_MAIN_STAT_VALUE, equipment.getMainStatValue());
            values.put(DatabaseContract.Equipment.COLUMN_SECONDARY_STATS, equipment.secondaryStatsToJson());
            values.put(DatabaseContract.Equipment.COLUMN_POWER_RATING, equipment.calculatePowerRating());

            int rowsAffected = dbHelper.getWritableDatabase().update(
                    DatabaseContract.Equipment.TABLE_NAME,
                    values,
                    DatabaseContract.Equipment._ID + " = ?",
                    new String[]{String.valueOf(equipment.getId())}
            );

            return rowsAffected > 0;

        } catch (Exception e) {
            Log.e(TAG, "Error actualizando equipment en BD", e);
            return false;
        }
    }

    /**
     * Elimina equipamiento de la base de datos
     */
    public boolean deleteEquipment(long equipmentId) {
        try {
            Equipment equipment = getEquipmentById(equipmentId);
            if (equipment != null && equipment.isEquipped()) {
                Log.e(TAG, "No se puede eliminar equipment equipado: " + equipmentId);
                return false;
            }

            int rowsAffected = dbHelper.getWritableDatabase().delete(
                    DatabaseContract.Equipment.TABLE_NAME,
                    DatabaseContract.Equipment._ID + " = ?",
                    new String[]{String.valueOf(equipmentId)}
            );

            if (rowsAffected > 0) {
                equipmentCache.remove(equipmentId);
                Log.i(TAG, "Equipment eliminado: " + equipmentId);
                return true;
            }

            return false;

        } catch (Exception e) {
            Log.e(TAG, "Error eliminando equipment", e);
            return false;
        }
    }

    /**
     * Elimina múltiples equipamientos
     */
    public boolean deleteMultipleEquipment(List<Long> equipmentIds) {
        boolean allSuccess = true;

        for (Long equipmentId : equipmentIds) {
            if (!deleteEquipment(equipmentId)) {
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    // ==================== UTILIDADES ====================

    /**
     * Determina rareza aleatoria basada en probabilidades
     */
    private int determineRandomRarity() {
        double random = Math.random() * 100.0; // 0-100
        double cumulative = 0.0;

        float[] dropRates = EquipmentConstants.EQUIPMENT_DROP_RATES;

        for (int i = 1; i < dropRates.length; i++) {
            cumulative += dropRates[i] * 100; // Convertir a porcentaje
            if (random <= cumulative) {
                return i;
            }
        }

        // Fallback a común
        return EquipmentConstants.EQUIPMENT_GRAY;
    }

    /**
     * Verifica si el cache es válido
     */
    private boolean isCacheValid() {
        return (System.currentTimeMillis() - lastCacheUpdate) < CACHE_DURATION;
    }

    /**
     * Limpia el cache
     */
    public void clearCache() {
        equipmentCache.clear();
        lastCacheUpdate = 0;
        Log.d(TAG, "Cache de equipamiento limpiado");
    }

    /**
     * Obtiene estadísticas del inventario de equipamiento
     */
    public EquipmentInventoryStats getInventoryStats() {
        List<Equipment> allEquipment = getAllEquipment();

        EquipmentInventoryStats stats = new EquipmentInventoryStats();
        stats.totalItems = allEquipment.size();
        stats.equippedItems = 0;
        stats.lockedItems = 0;
        stats.totalPower = 0;
        stats.rarityCount = new int[EquipmentConstants.EQUIPMENT_RARITY_NAMES.length];
        stats.typeCount = new int[EquipmentConstants.EQUIPMENT_TYPE_NAMES.length];

        for (Equipment equipment : allEquipment) {
            if (equipment.isEquipped()) stats.equippedItems++;
            if (equipment.isLocked()) stats.lockedItems++;

            stats.totalPower += equipment.getPowerRating();

            if (equipment.getRarity() < stats.rarityCount.length) {
                stats.rarityCount[equipment.getRarity()]++;
            }

            if (equipment.getEquipmentType() < stats.typeCount.length) {
                stats.typeCount[equipment.getEquipmentType()]++;
            }
        }

        return stats;
    }

    /**
     * Sugiere mejores equipamientos para un héroe
     */
    public List<Equipment> suggestUpgradesForHero(long heroId) {
        List<Equipment> suggestions = new ArrayList<>();
        List<Equipment> currentEquipment = getHeroEquipment(heroId);
        List<Equipment> availableEquipment = getAvailableEquipment();

        // Para cada slot, buscar mejor alternativa
        for (int type = 1; type <= 6; type++) {
            Equipment currentItem = getCurrentEquipmentOfType(currentEquipment, type);
            Equipment bestAlternative = getBestEquipmentOfType(availableEquipment, type);

            if (bestAlternative != null &&
                    (currentItem == null || bestAlternative.getPowerRating() > currentItem.getPowerRating())) {
                suggestions.add(bestAlternative);
            }
        }

        return suggestions;
    }

    /**
     * Obtiene el equipamiento actual de un tipo específico
     */
    private Equipment getCurrentEquipmentOfType(List<Equipment> currentEquipment, int type) {
        for (Equipment equipment : currentEquipment) {
            if (equipment.getEquipmentType() == type) {
                return equipment;
            }
        }
        return null;
    }

    /**
     * Obtiene el mejor equipamiento disponible de un tipo específico
     */
    private Equipment getBestEquipmentOfType(List<Equipment> availableEquipment, int type) {
        Equipment best = null;

        for (Equipment equipment : availableEquipment) {
            if (equipment.getEquipmentType() == type) {
                if (best == null || equipment.getPowerRating() > best.getPowerRating()) {
                    best = equipment;
                }
            }
        }

        return best;
    }

    // ==================== CLASE INTERNA PARA ESTADÍSTICAS ====================

    public static class EquipmentInventoryStats {
        public int totalItems;
        public int equippedItems;
        public int lockedItems;
        public long totalPower;
        public int[] rarityCount;
        public int[] typeCount;

        @Override
        public String toString() {
            return String.format("Equipment Stats: %d items totales, %d equipados, %d bloqueados, %d poder total",
                    totalItems, equippedItems, lockedItems, totalPower);
        }
    }
}