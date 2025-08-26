package com.by.soh.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.by.soh.constants.GameConstants;
import com.by.soh.constants.HeroConstants;
import com.by.soh.database.GameDatabaseHelper;
import com.by.soh.database.DatabaseContract;
import com.by.soh.models.Hero;
import com.by.soh.models.HeroStats;
import com.by.soh.models.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager integrado para el sistema de héroes
 * Se conecta con EquipmentManager, PlayerDataManager y SaveGameManager
 */
public class HeroManager {

    private static final String TAG = "HeroManager";

    // Singleton instance
    private static HeroManager instance;

    // Referencias
    private GameDatabaseHelper dbHelper;
    private Context context;
    private EquipmentManager equipmentManager;
    private PlayerDataManager playerDataManager;

    // Cache
    private Map<Long, Hero> heroCache;
    private Map<Long, HeroStats> statsCache;
    private long lastCacheUpdate;
    private static final long CACHE_DURATION = 2 * 60 * 1000; // 2 minutos

    // Constructor privado
    private HeroManager(Context context) {
        this.context = context.getApplicationContext();
        this.dbHelper = GameDatabaseHelper.getInstance(context);
        this.heroCache = new HashMap<>();
        this.statsCache = new HashMap<>();
        this.lastCacheUpdate = 0;

        // Inicializar dependencias
        this.equipmentManager = EquipmentManager.getInstance(context);
        this.playerDataManager = PlayerDataManager.getInstance(context);
    }

    /**
     * Obtiene la instancia singleton del manager
     */
    public static synchronized HeroManager getInstance(Context context) {
        if (instance == null) {
            instance = new HeroManager(context);
        }
        return instance;
    }

    // ==================== OPERACIONES BÁSICAS ====================

    /**
     * Obtiene todos los héroes del jugador
     */
    public List<Hero> getAllPlayerHeroes() {
        List<Hero> heroes = new ArrayList<>();

        Cursor cursor = dbHelper.getAllPlayerHeroes();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Hero hero = createHeroFromCursor(cursor);
                if (hero != null) {
                    heroes.add(hero);
                    heroCache.put(hero.getId(), hero); // Actualizar cache
                }
            }
            cursor.close();
        }

        lastCacheUpdate = System.currentTimeMillis();
        Log.d(TAG, "Cargados " + heroes.size() + " héroes del jugador");

        return heroes;
    }

    /**
     * Obtiene un héroe específico por ID
     */
    public Hero getHeroById(long heroId) {
        // Verificar cache primero
        if (isCacheValid() && heroCache.containsKey(heroId)) {
            return heroCache.get(heroId);
        }

        Cursor cursor = dbHelper.getPlayerHeroById(heroId);
        Hero hero = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                hero = createHeroFromCursor(cursor);
                if (hero != null) {
                    heroCache.put(heroId, hero);
                }
            }
            cursor.close();
        }

        return hero;
    }

    /**
     * Obtiene el equipo activo (héroes en formación)
     */
    public List<Hero> getActiveTeam() {
        List<Hero> team = new ArrayList<>();

        Cursor cursor = dbHelper.getActiveTeam();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Hero hero = createHeroFromCursor(cursor);
                if (hero != null) {
                    team.add(hero);
                    heroCache.put(hero.getId(), hero);
                }
            }
            cursor.close();
        }

        Log.d(TAG, "Equipo activo cargado: " + team.size() + " héroes");
        return team;
    }

    /**
     * Crea un nuevo héroe desde template
     */
    public long createHeroFromTemplate(String heroTemplateId, int stars, int enhancement) {
        try {
            // Usar el método existente del GameDatabaseHelper
            long heroId = dbHelper.insertPlayerHero(heroTemplateId, stars, enhancement);

            if (heroId != -1) {
                // Limpiar cache para que se recargue con el nuevo héroe
                clearCache();
                Log.i(TAG, "Héroe creado desde template: " + heroTemplateId + " (ID: " + heroId + ")");
            }

            return heroId;

        } catch (Exception e) {
            Log.e(TAG, "Error creando héroe desde template", e);
            return -1;
        }
    }

    // ==================== OPERACIONES DE STATS ====================

    /**
     * Calcula las stats finales de un héroe
     */
    public HeroStats calculateHeroStats(long heroId, boolean includeEquipment, boolean includeFormation) {
        Hero hero = getHeroById(heroId);
        if (hero == null) {
            Log.e(TAG, "Héroe no encontrado para calcular stats: " + heroId);
            return null;
        }

        // Verificar cache
        String cacheKey = heroId + "_" + includeEquipment + "_" + includeFormation;
        long cacheKeyHash = cacheKey.hashCode();

        if (isCacheValid() && statsCache.containsKey(cacheKeyHash)) {
            return statsCache.get(cacheKeyHash);
        }

        // Calcular stats base
        HeroStats stats = calculateBaseStats(hero);

        // Aplicar bonos de equipment si se solicita
        if (includeEquipment) {
            applyEquipmentBonuses(stats, heroId);
            stats.setIncludesEquipment(true);
        }

        // Aplicar bonos de formación si se solicita
        if (includeFormation) {
            applyFormationBonuses(stats, heroId);
            stats.setIncludesFormation(true);
        }

        // Aplicar bonos de atributo espiritual
        applyAttributeBonuses(stats, hero);

        // Aplicar sinergia de facción
        applyFactionSynergy(stats, hero);

        // Marcar como calculado y cachear
        stats.markAsCalculated();
        statsCache.put(cacheKeyHash, stats);

        Log.d(TAG, "Stats calculadas para héroe " + heroId + ": " + stats.calculateTotalPower() + " poder");
        return stats;
    }

    /**
     * Calcula stats base del héroe
     */
    private HeroStats calculateBaseStats(Hero hero) {
        // Stats base desde el modelo
        int baseHp = hero.getBaseHp();
        int baseAtk = hero.getBaseAtk();
        int baseDef = hero.getBaseDef();
        int baseSpeed = hero.getBaseSpeed();
        int baseMagicDef = hero.getBaseMagicDef();

        // Aplicar multiplicadores de rareza, estrellas y enhancement
        float rarityMult = GameConstants.getRarityMultiplier(hero.getRarity());
        float starMult = GameConstants.getStarMultiplier(hero.getStars());
        float enhanceMult = GameConstants.getEnhancementMultiplier(hero.getEnhancement());

        // Bonus por nivel (5% por nivel después del 1)
        float levelMult = 1.0f + (hero.getLevel() - 1) * 0.05f;

        float totalMultiplier = rarityMult * starMult * enhanceMult * levelMult;

        // Calcular stats finales
        int finalHp = Math.round(baseHp * totalMultiplier);
        int finalAtk = Math.round(baseAtk * totalMultiplier);
        int finalDef = Math.round(baseDef * totalMultiplier);
        int finalSpeed = Math.round(baseSpeed * totalMultiplier);
        int finalMagicDef = Math.round(baseMagicDef * totalMultiplier);

        // Crear objeto HeroStats
        HeroStats stats = new HeroStats(hero.getId(), finalHp, finalAtk, finalDef, finalSpeed, finalMagicDef);

        // Stats derivadas (crit, accuracy, evasion)
        stats.setFinalCritRate(hero.getCritRate());
        stats.setFinalCritDamage(hero.getCritDamage());
        stats.setFinalAccuracy(hero.getAccuracy());
        stats.setFinalEvasion(hero.getEvasion());

        // Guardar bonos aplicados
        stats.setLevelBonus((levelMult - 1.0f));
        stats.setEnhancementBonus((enhanceMult - 1.0f));
        stats.setStarBonus((starMult - 1.0f));

        return stats;
    }

    /**
     * Aplica bonos específicos de facción
     */
    private void applyFactionBonus(HeroStats stats, int factionId) {
        switch (factionId) {
            case HeroConstants.FACTION_SHINIGAMI:
                // Bonus de experiencia se aplica en otro lugar
                stats.setFactionSynergy(HeroConstants.SHINIGAMI_EXP_BONUS);
                break;
            case HeroConstants.FACTION_HOLLOW:
                stats.setFactionSynergy(HeroConstants.HOLLOW_DAMAGE_VS_NON_HOLLOW);
                break;
            case HeroConstants.FACTION_QUINCY:
                stats.setFinalCritRate(Math.min(stats.getFinalCritRate() + HeroConstants.QUINCY_ACCURACY_CRIT_BONUS, 1.0f));
                stats.setFactionSynergy(HeroConstants.QUINCY_ACCURACY_CRIT_BONUS);
                break;
            case HeroConstants.FACTION_ARRANCAR:
                stats.setFactionSynergy(HeroConstants.ARRANCAR_ARMOR_PENETRATION);
                break;
            case HeroConstants.FACTION_HUMAN:
                stats.setFactionSynergy(HeroConstants.HUMAN_DEBUFF_RESISTANCE);
                break;
            case HeroConstants.FACTION_FULLBRING:
                float bonus = HeroConstants.FULLBRING_ALL_STATS_BONUS;
                stats.setFinalHp(Math.round(stats.getFinalHp() * (1.0f + bonus)));
                stats.setFinalAtk(Math.round(stats.getFinalAtk() * (1.0f + bonus)));
                stats.setFinalDef(Math.round(stats.getFinalDef() * (1.0f + bonus)));
                stats.setFinalSpeed(Math.round(stats.getFinalSpeed() * (1.0f + bonus)));
                stats.setFactionSynergy(bonus);
                break;
        }
    }

    /**
     * Aplica bonos específicos de atributo
     */
    private void applyAttributeBonus(HeroStats stats, int attributeId) {
        switch (attributeId) {
            case HeroConstants.ATTRIBUTE_POWER:
                float powerBonus = HeroConstants.POWER_4_ATK_BONUS;
                stats.setFinalAtk(Math.round(stats.getFinalAtk() * (1.0f + powerBonus)));
                stats.setAttributeSynergy(powerBonus);
                break;
            case HeroConstants.ATTRIBUTE_SOUL:
                float soulBonus = HeroConstants.SOUL_4_HP_BONUS;
                stats.setFinalHp(Math.round(stats.getFinalHp() * (1.0f + soulBonus)));
                stats.setAttributeSynergy(soulBonus);
                break;
            case HeroConstants.ATTRIBUTE_CORE:
                float coreBonus = HeroConstants.CORE_4_ALL_STATS;
                stats.setFinalHp(Math.round(stats.getFinalHp() * (1.0f + coreBonus)));
                stats.setFinalAtk(Math.round(stats.getFinalAtk() * (1.0f + coreBonus)));
                stats.setFinalDef(Math.round(stats.getFinalDef() * (1.0f + coreBonus)));
                stats.setFinalSpeed(Math.round(stats.getFinalSpeed() * (1.0f + coreBonus)));
                stats.setAttributeSynergy(coreBonus);
                break;
            case HeroConstants.ATTRIBUTE_MIND:
                stats.setAttributeSynergy(HeroConstants.MIND_4_ENERGY_REDUCTION);
                break;
            case HeroConstants.ATTRIBUTE_HEART:
                stats.setAttributeSynergy(HeroConstants.HEART_4_GROUP_HEAL);
                break;
            case HeroConstants.ATTRIBUTE_VOID:
                stats.setAttributeSynergy(HeroConstants.VOID_4_IGNORE_DEF_CHANCE);
                break;
        }
    }

    // ==================== BÚSQUEDAS Y FILTROS ====================

    /**
     * Busca héroes con filtros específicos
     */
    public List<Hero> searchHeroes(String faction, String rarity, String role, String sortBy) {
        List<Hero> results = new ArrayList<>();

        Cursor cursor = dbHelper.searchHeroes(faction, rarity, role, sortBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Hero hero = createHeroFromCursor(cursor);
                if (hero != null) {
                    results.add(hero);
                    heroCache.put(hero.getId(), hero);
                }
            }
            cursor.close();
        }

        return results;
    }

    /**
     * Obtiene héroes por facción
     */
    public List<Hero> getHeroesByFaction(int factionId) {
        return searchHeroes(String.valueOf(factionId), null, null, "power");
    }

    /**
     * Obtiene héroes favoritos
     */
    public List<Hero> getFavoriteHeroes() {
        List<Hero> favorites = new ArrayList<>();
        List<Hero> allHeroes = getAllPlayerHeroes();

        for (Hero hero : allHeroes) {
            if (hero.isFavorite()) {
                favorites.add(hero);
            }
        }

        return favorites;
    }

    /**
     * Marca/desmarca héroe como favorito
     */
    public boolean toggleFavorite(long heroId) {
        Hero hero = getHeroById(heroId);
        if (hero == null) return false;

        boolean newFavoriteStatus = !hero.isFavorite();
        hero.setFavorite(newFavoriteStatus);

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerHeroes.COLUMN_IS_FAVORITED, newFavoriteStatus ? 1 : 0);

        boolean success = dbHelper.updateHeroStats(heroId, values);
        if (success) {
            heroCache.put(heroId, hero);
            Log.d(TAG, "Héroe " + heroId + " marcado como favorito: " + newFavoriteStatus);
        }

        return success;
    }

    // ==================== OPERACIONES DE CACHE ====================

    /**
     * Verifica si el cache es válido
     */
    private boolean isCacheValid() {
        return (System.currentTimeMillis() - lastCacheUpdate) < CACHE_DURATION;
    }

    /**
     * Limpia todo el cache
     */
    public void clearCache() {
        heroCache.clear();
        statsCache.clear();
        lastCacheUpdate = 0;
        Log.d(TAG, "Cache de héroes limpiado");
    }

    /**
     * Limpia cache de stats para un héroe específico
     */
    private void clearStatsCache(long heroId) {
        List<Long> keysToRemove = new ArrayList<>();
        for (Long key : statsCache.keySet()) {
            // Si la key contiene el heroId, remover
            if (key.toString().contains(String.valueOf(heroId))) {
                keysToRemove.add(key);
            }
        }
        for (Long key : keysToRemove) {
            statsCache.remove(key);
        }
    }

    /**
     * Limpia todo el cache de stats
     */
    private void clearStatsCache() {
        statsCache.clear();
    }

    // ==================== ESTADÍSTICAS Y ANÁLISIS ====================

    /**
     * Obtiene estadísticas del colección de héroes
     */
    public HeroCollectionStats getCollectionStats() {
        List<Hero> allHeroes = getAllPlayerHeroes();

        HeroCollectionStats stats = new HeroCollectionStats();
        stats.totalHeroes = allHeroes.size();
        stats.favoriteHeroes = 0;
        stats.maxLevelHeroes = 0;
        stats.totalPower = 0;

        // Contadores por rareza, facción, rol
        int[] rarityCount = new int[6]; // 0-5
        int[] factionCount = new int[7]; // 0-6
        int[] roleCount = new int[9]; // 0-8

        for (Hero hero : allHeroes) {
            if (hero.isFavorite()) stats.favoriteHeroes++;
            if (hero.getLevel() >= GameConstants.MAX_HERO_LEVEL) stats.maxLevelHeroes++;

            HeroStats heroStats = calculateHeroStats(hero.getId(), true, false);
            if (heroStats != null) {
                stats.totalPower += heroStats.calculateTotalPower();
            }

            // Contadores
            if (hero.getRarity() < rarityCount.length) rarityCount[hero.getRarity()]++;
            if (hero.getFaction() < factionCount.length) factionCount[hero.getFaction()]++;
            if (hero.getRole() < roleCount.length) roleCount[hero.getRole()]++;
        }

        stats.rarityDistribution = rarityCount;
        stats.factionDistribution = factionCount;
        stats.roleDistribution = roleCount;

        return stats;
    }

    /**
     * Obtiene el poder total del equipo activo
     */
    public long getActiveTeamPower() {
        List<Hero> team = getActiveTeam();
        long totalPower = 0;

        for (Hero hero : team) {
            HeroStats stats = calculateHeroStats(hero.getId(), true, true);
            if (stats != null) {
                totalPower += stats.calculateTotalPower();
            }
        }

        return totalPower;
    }

    /**
     * Analiza sinergias del equipo activo
     */
    public TeamSynergyAnalysis analyzeTeamSynergy() {
        List<Hero> team = getActiveTeam();
        TeamSynergyAnalysis analysis = new TeamSynergyAnalysis();

        Map<Integer, Integer> factionCount = new HashMap<>();
        Map<Integer, Integer> attributeCount = new HashMap<>();

        for (Hero hero : team) {
            factionCount.put(hero.getFaction(), factionCount.getOrDefault(hero.getFaction(), 0) + 1);
            attributeCount.put(hero.getAttribute(), attributeCount.getOrDefault(hero.getAttribute(), 0) + 1);
        }

        // Detectar bonos activos
        analysis.activeFactionBonuses = new ArrayList<>();
        analysis.activeAttributeBonuses = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : factionCount.entrySet()) {
            if (entry.getValue() >= HeroConstants.FACTION_BONUS_REQUIREMENT) {
                analysis.activeFactionBonuses.add(HeroConstants.getFactionName(entry.getKey()));
            }
        }

        for (Map.Entry<Integer, Integer> entry : attributeCount.entrySet()) {
            if (entry.getValue() >= HeroConstants.ATTRIBUTE_BONUS_REQUIREMENT) {
                analysis.activeAttributeBonuses.add(HeroConstants.getAttributeName(entry.getKey()));
            }
        }

        analysis.synergyScore = calculateSynergyScore(analysis);

        return analysis;
    }

    /**
     * Calcula puntuación de sinergia
     */
    private float calculateSynergyScore(TeamSynergyAnalysis analysis) {
        float score = 0.0f;

        // +10 puntos por cada bono de facción activo
        score += analysis.activeFactionBonuses.size() * 10;

        // +15 puntos por cada bono de atributo activo
        score += analysis.activeAttributeBonuses.size() * 15;

        // Bonus por diversidad (máximo 20 puntos)
        if (analysis.activeFactionBonuses.size() >= 2) score += 10;
        if (analysis.activeAttributeBonuses.size() >= 2) score += 10;

        return Math.min(score, 100.0f); // Máximo 100 puntos
    }

    // ==================== CLASES INTERNAS ====================

    /**
     * Resultado de subida de nivel para héroes
     */
    public static class LevelUpResult {
        public final boolean leveledUp;
        public final int levelsGained;
        public final int newLevel;
        public final long heroId;

        public LevelUpResult(boolean leveledUp, int levelsGained, int newLevel, long heroId) {
            this.leveledUp = leveledUp;
            this.levelsGained = levelsGained;
            this.newLevel = newLevel;
            this.heroId = heroId;
        }

        @Override
        public String toString() {
            return String.format("HeroLevelUpResult{heroId=%d, leveledUp=%b, levelsGained=%d, newLevel=%d}",
                    heroId, leveledUp, levelsGained, newLevel);
        }
    }

    /**
     * Estadísticas de la colección de héroes
     */
    public static class HeroCollectionStats {
        public int totalHeroes;
        public int favoriteHeroes;
        public int maxLevelHeroes;
        public long totalPower;
        public int[] rarityDistribution;
        public int[] factionDistribution;
        public int[] roleDistribution;

        @Override
        public String toString() {
            return String.format("HeroCollectionStats{total=%d, favorites=%d, maxLevel=%d, totalPower=%d}",
                    totalHeroes, favoriteHeroes, maxLevelHeroes, totalPower);
        }
    }

    /**
     * Análisis de sinergia del equipo
     */
    public static class TeamSynergyAnalysis {
        public List<String> activeFactionBonuses;
        public List<String> activeAttributeBonuses;
        public float synergyScore;

        public TeamSynergyAnalysis() {
            this.activeFactionBonuses = new ArrayList<>();
            this.activeAttributeBonuses = new ArrayList<>();
            this.synergyScore = 0.0f;
        }

        @Override
        public String toString() {
            return String.format("TeamSynergyAnalysis{factionBonuses=%d, attributeBonuses=%d, score=%.1f}",
                    activeFactionBonuses.size(), activeAttributeBonuses.size(), synergyScore);
        }
    }

    /*bonos de equipamiento
     */
    private void applyEquipmentBonuses(HeroStats stats, long heroId) {
        List<Equipment> equipment = equipmentManager.getHeroEquipment(heroId);

        int bonusHp = 0, bonusAtk = 0, bonusDef = 0, bonusSpeed = 0;
        float bonusCritRate = 0, bonusCritDamage = 0;

        for (Equipment item : equipment) {
            // Stats principales
            switch (item.getMainStatType().toLowerCase()) {
                case "hp":
                    bonusHp += item.getMainStatValue();
                    break;
                case "atk":
                    bonusAtk += item.getMainStatValue();
                    break;
                case "def":
                    bonusDef += item.getMainStatValue();
                    break;
                case "speed":
                    bonusSpeed += item.getMainStatValue();
                    break;
                case "crit rate":
                    bonusCritRate += item.getMainStatValue() / 100.0f;
                    break;
            }

            // Stats secundarios
            for (Equipment.SecondaryStat secondaryStat : item.getSecondaryStats()) {
                switch (secondaryStat.type.toLowerCase()) {
                    case "hp%":
                        bonusHp += Math.round(stats.getFinalHp() * secondaryStat.value / 100.0f);
                        break;
                    case "atk%":
                        bonusAtk += Math.round(stats.getFinalAtk() * secondaryStat.value / 100.0f);
                        break;
                    case "def%":
                        bonusDef += Math.round(stats.getFinalDef() * secondaryStat.value / 100.0f);
                        break;
                    case "speed":
                        bonusSpeed += secondaryStat.value;
                        break;
                    case "crit rate%":
                        bonusCritRate += secondaryStat.value / 100.0f;
                        break;
                    case "crit dmg%":
                        bonusCritDamage += secondaryStat.value / 100.0f;
                        break;
                }
            }
        }

        // Aplicar bonos
        stats.setFinalHp(stats.getFinalHp() + bonusHp);
        stats.setFinalAtk(stats.getFinalAtk() + bonusAtk);
        stats.setFinalDef(stats.getFinalDef() + bonusDef);
        stats.setFinalSpeed(stats.getFinalSpeed() + bonusSpeed);
        stats.setFinalCritRate(Math.min(stats.getFinalCritRate() + bonusCritRate, 1.0f));
        stats.setFinalCritDamage(stats.getFinalCritDamage() + bonusCritDamage);

        // Calcular porcentaje total de bonus de equipment
        float equipmentBonus = (float) (bonusHp + bonusAtk + bonusDef + bonusSpeed) /
                (stats.getFinalHp() + stats.getFinalAtk() + stats.getFinalDef() + stats.getFinalSpeed());
        stats.setEquipmentBonus(equipmentBonus);

        // Analizar bonos de sets
        applySetBonuses(stats, heroId);
    }

    /**
     * Aplica bonos de sets de equipamiento
     */
    private void applySetBonuses(HeroStats stats, long heroId) {
        Map<Integer, Integer> setPieces = equipmentManager.analyzeHeroSets(heroId);

        for (Map.Entry<Integer, Integer> entry : setPieces.entrySet()) {
            int setId = entry.getKey();
            int pieceCount = entry.getValue();

            // Aplicar bonos según el set y cantidad de piezas
            applySpecificSetBonus(stats, setId, pieceCount);
        }
    }

    /**
     * Aplica bonos específicos de un set
     */
    private void applySpecificSetBonus(HeroStats stats, int setId, int pieceCount) {
        // Implementar bonos específicos según las constantes
        // Por simplicidad, aplicamos bonos genéricos aquí
        if (pieceCount >= 2) {
            float setBonus = 0.1f * pieceCount; // 10% por pieza (simplificado)
            stats.setFormationBonus(stats.getFormationBonus() + setBonus);
        }
    }

    /**
     * Aplica bonos de formación
     */
    private void applyFormationBonuses(HeroStats stats, long heroId) {
        List<Hero> team = getActiveTeam();

        // Contar facciones y atributos en el equipo
        Map<Integer, Integer> factionCount = new HashMap<>();
        Map<Integer, Integer> attributeCount = new HashMap<>();

        for (Hero teammate : team) {
            factionCount.put(teammate.getFaction(),
                    factionCount.getOrDefault(teammate.getFaction(), 0) + 1);
            attributeCount.put(teammate.getAttribute(),
                    attributeCount.getOrDefault(teammate.getAttribute(), 0) + 1);
        }

        // Aplicar bonos de facción
        for (Map.Entry<Integer, Integer> entry : factionCount.entrySet()) {
            if (HeroConstants.hasFactionBonus(convertMapToArray(factionCount), entry.getKey())) {
                applyFactionBonus(stats, entry.getKey());
            }
        }

        // Aplicar bonos de atributo
        for (Map.Entry<Integer, Integer> entry : attributeCount.entrySet()) {
            if (HeroConstants.hasAttributeBonus(convertMapToArray(attributeCount), entry.getKey())) {
                applyAttributeBonus(stats, entry.getKey());
            }
        }
    }

    /**
     * Aplica bonos de atributo espiritual
     */
    private void applyAttributeBonuses(HeroStats stats, Hero hero) {
        int attribute = hero.getAttribute();

        switch (attribute) {
            case HeroConstants.ATTRIBUTE_POWER:
                stats.setFinalAtk(Math.round(stats.getFinalAtk() * (1.0f + HeroConstants.POWER_ATK_BONUS)));
                stats.setAttributeBonus(HeroConstants.POWER_ATK_BONUS);
                break;

            case HeroConstants.ATTRIBUTE_SOUL:
                stats.setFinalHp(Math.round(stats.getFinalHp() * (1.0f + HeroConstants.SOUL_HP_BONUS)));
                stats.setAttributeBonus(HeroConstants.SOUL_HP_BONUS);
                break;

            case HeroConstants.ATTRIBUTE_CORE:
                float coreBonus = HeroConstants.CORE_BALANCED_BONUS;
                stats.setFinalAtk(Math.round(stats.getFinalAtk() * (1.0f + coreBonus)));
                stats.setFinalDef(Math.round(stats.getFinalDef() * (1.0f + coreBonus)));
                stats.setFinalCritRate(Math.min(stats.getFinalCritRate() + HeroConstants.CORE_CRIT_BONUS, 1.0f));
                stats.setAttributeBonus(coreBonus);
                break;

            case HeroConstants.ATTRIBUTE_MIND:
                // Bonos de Mind se aplicarían en habilidades, aquí solo registramos
                stats.setAttributeBonus(HeroConstants.MIND_SPECIAL_SKILLS_BONUS);
                break;

            case HeroConstants.ATTRIBUTE_HEART:
                // Bonos de Heart se aplicarían en sanación
                stats.setAttributeBonus(HeroConstants.HEART_HEALING_BONUS);
                break;

            case HeroConstants.ATTRIBUTE_VOID:
                // Bonos de Void se aplicarían en penetración
                stats.setAttributeBonus(HeroConstants.VOID_PENETRATION_BONUS);
                break;
        }
    }

    /**
     * Aplica sinergia de facción
     */
    private void applyFactionSynergy(HeroStats stats, Hero hero) {
        // Implementar bonos específicos de facción
        // Por ahora, aplicamos un bono genérico
        stats.setFactionSynergy(0.05f); // 5% genérico
    }

// ==================== OPERACIONES DE EXPERIENCIA Y NIVEL ====================

    /**
     * Añade experiencia a un héroe
     */
    public LevelUpResult addExperienceToHero(long heroId, long expAmount) {
        Hero hero = getHeroById(heroId);
        if (hero == null) {
            return new LevelUpResult(false, 0, 0, 0);
        }

        long currentExp = hero.getExperience();
        int currentLevel = hero.getLevel();
        long newExp = currentExp + expAmount;

        // Calcular level ups
        int newLevel = currentLevel;
        int levelsGained = 0;

        while (newLevel < GameConstants.MAX_HERO_LEVEL) {
            long requiredExp = GameConstants.getExpRequiredForLevel(newLevel + 1);

            if (newExp >= requiredExp) {
                newLevel++;
                levelsGained++;
            } else {
                break;
            }
        }

        // Actualizar héroe
        hero.setExperience(newExp);
        hero.setLevel(newLevel);

        // Actualizar en base de datos
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerHeroes.COLUMN_EXP, newExp);
        values.put(DatabaseContract.PlayerHeroes.COLUMN_LEVEL, newLevel);

        boolean success = dbHelper.updateHeroStats(heroId, values);

        if (success) {
            // Limpiar caches afectados
            heroCache.put(heroId, hero);
            clearStatsCache(heroId);

            Log.i(TAG, String.format("EXP añadida a héroe %d: %d (Nivel: %d → %d)",
                    heroId, expAmount, currentLevel, newLevel));
        }

        return new LevelUpResult(levelsGained > 0, levelsGained, newLevel, hero.getId());
    }

    /**
     * Mejora un héroe (+1 enhancement)
     */
    public boolean enhanceHero(long heroId) {
        Hero hero = getHeroById(heroId);
        if (hero == null) {
            Log.e(TAG, "Héroe no encontrado para mejora: " + heroId);
            return false;
        }

        if (hero.getEnhancement() >= GameConstants.MAX_ENHANCEMENT) {
            Log.w(TAG, "Héroe ya está en nivel máximo: " + heroId);
            return false;
        }

        // Calcular costo
        long cost = GameConstants.getUpgradeCost(hero.getEnhancement(), hero.getRarity());

        // Verificar recursos
        if (!playerDataManager.hasEnoughGold(cost)) {
            Log.w(TAG, "Oro insuficiente para mejorar héroe: " + cost);
            return false;
        }

        // Gastar recursos
        if (!playerDataManager.spendGold(cost)) {
            return false;
        }

        // Aplicar mejora
        int newEnhancement = hero.getEnhancement() + 1;
        hero.setEnhancement(newEnhancement);

        // Recalcular stats base con nueva mejora
        recalculateBaseStats(hero);

        // Actualizar en base de datos
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.PlayerHeroes.COLUMN_ENHANCEMENT, newEnhancement);
        values.put(DatabaseContract.PlayerHeroes.COLUMN_MAX_HP, hero.getBaseHp());
        values.put(DatabaseContract.PlayerHeroes.COLUMN_CURRENT_HP, hero.getBaseHp());
        values.put(DatabaseContract.PlayerHeroes.COLUMN_ATK, hero.getBaseAtk());
        values.put(DatabaseContract.PlayerHeroes.COLUMN_DEF, hero.getBaseDef());
        values.put(DatabaseContract.PlayerHeroes.COLUMN_SPEED, hero.getBaseSpeed());
        values.put(DatabaseContract.PlayerHeroes.COLUMN_POWER_RATING,
                HeroConstants.calculateHeroPower(hero.getBaseHp(), hero.getBaseAtk(),
                        hero.getBaseDef(), hero.getBaseSpeed(),
                        hero.getRarity(), hero.getStars(), newEnhancement));

        boolean success = dbHelper.updateHeroStats(heroId, values);

        if (success) {
            // Actualizar caches
            heroCache.put(heroId, hero);
            clearStatsCache(heroId);

            Log.i(TAG, "Héroe mejorado: " + heroId + " → +" + newEnhancement);
        }

        return success;
    }

    /**
     * Recalcula las stats base después de una mejora
     */
    private void recalculateBaseStats(Hero hero) {
        // Obtener stats base del rol
        int baseHp = HeroConstants.getBaseStat(hero.getRole(), hero.getRarity(), "hp");
        int baseAtk = HeroConstants.getBaseStat(hero.getRole(), hero.getRarity(), "atk");
        int baseDef = HeroConstants.getBaseStat(hero.getRole(), hero.getRarity(), "def");
        int baseSpeed = HeroConstants.getBaseStat(hero.getRole(), hero.getRarity(), "speed");

        // Aplicar multiplicadores
        float totalMult = GameConstants.getRarityMultiplier(hero.getRarity()) *
                GameConstants.getStarMultiplier(hero.getStars()) *
                GameConstants.getEnhancementMultiplier(hero.getEnhancement());

        hero.setBaseHp(Math.round(baseHp * totalMult));
        hero.setBaseAtk(Math.round(baseAtk * totalMult));
        hero.setBaseDef(Math.round(baseDef * totalMult));
        hero.setBaseSpeed(Math.round(baseSpeed * totalMult));
    }

// ==================== OPERACIONES DE FORMACIÓN ====================

    /**
     * Actualiza la posición de un héroe en el equipo
     */
    public boolean updateHeroPosition(long heroId, int position) {
        // Validar posición
        if (position < 0 || position > GameConstants.MAX_TEAM_SIZE) {
            Log.e(TAG, "Posición inválida: " + position);
            return false;
        }

        // Verificar que el slot esté desbloqueado
        int playerLevel = playerDataManager.getPlayerLevel();
        int availableSlots = GameConstants.getAvailableTeamSlots(playerLevel);

        if (position > 0 && position > availableSlots) {
            Log.w(TAG, "Slot no desbloqueado aún: " + position + " (disponibles: " + availableSlots + ")");
            return false;
        }

        boolean success = dbHelper.updateHeroTeamPosition(heroId, position);

        if (success) {
            // Actualizar hero en cache
            Hero hero = heroCache.get(heroId);
            if (hero != null) {
                // Nota: Hero no tiene setTeamPosition, pero podríamos añadirlo
                heroCache.put(heroId, hero);
            }

            // Limpiar cache de stats de formación
            clearStatsCache();

            Log.i(TAG, "Posición de héroe actualizada: " + heroId + " → posición " + position);
        }

        return success;
    }

    /**
     * Obtiene sugerencias de formación óptima
     */
    public List<Hero> suggestOptimalFormation() {
        List<Hero> allHeroes = getAllPlayerHeroes();
        List<Hero> suggestion = new ArrayList<>();

        // Algoritmo simple: ordenar por poder y tomar los mejores
        allHeroes.sort((h1, h2) -> {
            HeroStats stats1 = calculateHeroStats(h1.getId(), true, false);
            HeroStats stats2 = calculateHeroStats(h2.getId(), true, false);

            if (stats1 == null) return 1;
            if (stats2 == null) return -1;

            return Long.compare(stats2.calculateTotalPower(), stats1.calculateTotalPower());
        });

        int availableSlots = GameConstants.getAvailableTeamSlots(playerDataManager.getPlayerLevel());

        for (int i = 0; i < Math.min(availableSlots, allHeroes.size()); i++) {
            suggestion.add(allHeroes.get(i));
        }

        Log.d(TAG, "Formación sugerida: " + suggestion.size() + " héroes");
        return suggestion;
    }

// ==================== UTILIDADES Y HELPERS ====================

    /**
     * Crea un objeto Hero desde un cursor de BD
     */
    private Hero createHeroFromCursor(Cursor cursor) {
        try {
            Hero hero = new Hero();

            // IDs y datos básicos
            hero.setId(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes._ID)));

            // Si el cursor incluye datos del template (join)
            int nameIndex = cursor.getColumnIndex("name");
            if (nameIndex != -1) {
                hero.setName(cursor.getString(nameIndex));

                int factionIndex = cursor.getColumnIndex("faction");
                if (factionIndex != -1) {
                    hero.setFaction(cursor.getInt(factionIndex));
                }

                int attributeIndex = cursor.getColumnIndex("attribute");
                if (attributeIndex != -1) {
                    hero.setAttribute(cursor.getInt(attributeIndex));
                }

                int roleIndex = cursor.getColumnIndex("role");
                if (roleIndex != -1) {
                    hero.setRole(cursor.getInt(roleIndex));
                }

                int rarityIndex = cursor.getColumnIndex("rarity");
                if (rarityIndex != -1) {
                    hero.setRarity(cursor.getInt(rarityIndex));
                }
            }

            // Stats del jugador
            hero.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_LEVEL)));
            hero.setExperience(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_EXP)));
            hero.setStars(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_STARS)));
            hero.setEnhancement(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_ENHANCEMENT)));

            // Stats calculadas
            hero.setBaseHp(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_MAX_HP)));
            hero.setBaseAtk(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_ATK)));
            hero.setBaseDef(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_DEF)));
            hero.setBaseSpeed(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_SPEED)));

            // Stats derivadas
            hero.setCritRate(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_CRIT_RATE)));
            hero.setCritDamage(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_CRIT_DAMAGE)));
            hero.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_ACCURACY)));
            hero.setEvasion(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_EVASION)));

            // Metadatos
            hero.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_OBTAINED_AT)));
            hero.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.PlayerHeroes.COLUMN_IS_FAVORITED)) == 1);

            return hero;

        } catch (Exception e) {
            Log.e(TAG, "Error creando Hero desde cursor", e);
            return null;
        }
    }

    /**
     * Convierte Map a array para compatibilidad con HeroConstants
     */
    private int[] convertMapToArray(Map<Integer, Integer> map) {
        int[] array = new int[10]; // Tamaño suficiente para facciones/atributos
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getKey() < array.length) {
                array[entry.getKey()] = entry.getValue();
            }
        }
        return array;
    }
}
