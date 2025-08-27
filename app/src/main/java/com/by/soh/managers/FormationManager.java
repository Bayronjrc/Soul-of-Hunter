package com.by.soh.managers;

import android.content.Context;
import android.util.Log;

import com.by.soh.constants.BattleConstants;
import com.by.soh.constants.GameConstants;
import com.by.soh.constants.HeroConstants;
import com.by.soh.models.Hero;
import com.by.soh.models.HeroStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manager para formaciones de combate y targeting estratégico
 */
public class FormationManager {

    private static final String TAG = "FormationManager";

    // Singleton instance
    private static FormationManager instance;

    // Referencias
    private Context context;
    private HeroManager heroManager;

    // Constructor privado
    private FormationManager(Context context) {
        this.context = context.getApplicationContext();
        this.heroManager = HeroManager.getInstance(context);
    }

    /**
     * Obtiene la instancia singleton del manager
     */
    public static synchronized FormationManager getInstance(Context context) {
        if (instance == null) {
            instance = new FormationManager(context);
        }
        return instance;
    }

    // ==================== SISTEMA DE TARGETING ====================

    /**
     * Selecciona el objetivo óptimo basado en estrategia de IA
     */
    public int selectOptimalTarget(List<Hero> targets, Hero attacker, int preferredStrategy) {
        if (targets == null || targets.isEmpty()) {
            return -1;
        }

        // Convertir a arrays para usar con BattleConstants
        int[] targetIds = new int[targets.size()];
        int[] targetHp = new int[targets.size()];
        int[] targetAtk = new int[targets.size()];

        for (int i = 0; i < targets.size(); i++) {
            targetIds[i] = (int)targets.get(i).getId();
            HeroStats stats = heroManager.calculateHeroStats(targets.get(i).getId(), true, false);
            targetHp[i] = stats != null ? stats.getFinalHp() : 1;
            targetAtk[i] = stats != null ? stats.getFinalAtk() : 1;
        }

        // Usar estrategia inteligente basada en facción y rol del atacante
        int smartStrategy = determineSmartStrategy(attacker, targets);
        int finalStrategy = smartStrategy != BattleConstants.TARGET_RANDOM ? smartStrategy : preferredStrategy;

        int targetIndex = BattleConstants.selectTarget(finalStrategy, targetIds, targetHp, targetAtk);

        // Convertir índice de array a índice de lista
        for (int i = 0; i < targetIds.length; i++) {
            if (targetIds[i] == targetIndex) {
                Log.d(TAG, String.format("Target seleccionado: %s → %s (estrategia: %d)",
                        attacker.getName(), targets.get(i).getName(), finalStrategy));
                return i;
            }
        }

        return 0; // Fallback al primer target
    }

    /**
     * Determina estrategia de targeting inteligente basada en características del atacante
     */
    private int determineSmartStrategy(Hero attacker, List<Hero> targets) {
        int attackerFaction = attacker.getFaction();
        int attackerRole = attacker.getRole();

        // Estrategia por facción
        switch (attackerFaction) {
            case HeroConstants.FACTION_QUINCY:
                // Quincy son efectivos vs Hollow, buscarlos prioritariamente
                if (hasFactionsInTargets(targets, HeroConstants.FACTION_HOLLOW)) {
                    return BattleConstants.TARGET_SAME_TYPE; // Modificar para buscar Hollow específicamente
                }
                return BattleConstants.TARGET_HIGHEST_ATK; // Eliminar amenazas

            case HeroConstants.FACTION_ARRANCAR:
                // Arrancar vs Shinigami, buscar Shinigami o supporters
                if (hasFactionsInTargets(targets, HeroConstants.FACTION_SHINIGAMI)) {
                    return BattleConstants.TARGET_OPPOSITE_TYPE;
                }
                return BattleConstants.TARGET_SUPPORT_FIRST;

            case HeroConstants.FACTION_HOLLOW:
                // Hollow atacan agresivamente, van por el más débil
                return BattleConstants.TARGET_LOWEST_HP;
        }

        // Estrategia por rol
        switch (attackerRole) {
            case HeroConstants.ROLE_ASSASSIN:
                return BattleConstants.TARGET_SUPPORT_FIRST; // Eliminar support/healer primero

            case HeroConstants.ROLE_TANK:
                return BattleConstants.TARGET_HIGHEST_ATK; // Proteger equipo atacando amenazas

            case HeroConstants.ROLE_MAGE:
            case HeroConstants.ROLE_CONTROLLER:
                return BattleConstants.TARGET_BACK_ROW; // Atacar línea trasera

            case HeroConstants.ROLE_BERSERKER:
                return BattleConstants.TARGET_RANDOM; // Berserker ataca sin estrategia

            default:
                return BattleConstants.TARGET_FRONT_ROW; // Estrategia estándar
        }
    }

    /**
     * Verifica si hay héroes de ciertas facciones en la lista de targets
     */
    private boolean hasFactionsInTargets(List<Hero> targets, int targetFaction) {
        for (Hero target : targets) {
            if (target.getFaction() == targetFaction) {
                return true;
            }
        }
        return false;
    }

    // ==================== ANÁLISIS DE FORMACIÓN ====================

    /**
     * Analiza una formación y sugiere mejoras
     */
    public FormationAnalysis analyzeFormation(List<Hero> formation) {
        if (formation == null || formation.isEmpty()) {
            return new FormationAnalysis(false, "Formación vacía", new ArrayList<>());
        }

        FormationAnalysis analysis = new FormationAnalysis();
        analysis.isOptimal = true;
        analysis.suggestions = new ArrayList<>();

        // Verificar balance de roles
        Map<Integer, Integer> roleCount = countRoles(formation);

        // Verificar que haya al menos un tank o healer
        if (!roleCount.containsKey(HeroConstants.ROLE_TANK) &&
                !roleCount.containsKey(HeroConstants.ROLE_HEALER)) {
            analysis.isOptimal = false;
            analysis.suggestions.add("Considera añadir un Tank o Healer para supervivencia");
        }

        // Verificar que no haya demasiados de un rol
        for (Map.Entry<Integer, Integer> entry : roleCount.entrySet()) {
            if (entry.getValue() > 2) {
                analysis.isOptimal = false;
                analysis.suggestions.add("Demasiados " + HeroConstants.getRoleName(entry.getKey()) +
                        " - considera diversificar roles");
            }
        }

        // Verificar sinergia de facciones
        TeamSynergyScore synergyScore = calculateFormationSynergy(formation);
        analysis.synergyScore = synergyScore.totalScore;

        if (synergyScore.totalScore < 30) {
            analysis.isOptimal = false;
            analysis.suggestions.add("Baja sinergia de facción - busca más héroes de la misma facción");
        }

        // Verificar distribución de poder
        PowerDistribution powerDist = analyzePowerDistribution(formation);
        if (powerDist.hasWeakLinks) {
            analysis.isOptimal = false;
            analysis.suggestions.add("Algunos héroes están significativamente más débiles - considera mejorarlos");
        }

        // Análisis de posicionamiento
        analysis.positioningAdvice = analyzePositioning(formation);

        return analysis;
    }

    /**
     * Cuenta roles en una formación
     */
    private Map<Integer, Integer> countRoles(List<Hero> formation) {
        Map<Integer, Integer> roleCount = new HashMap<>();

        for (Hero hero : formation) {
            int role = hero.getRole();
            roleCount.put(role, roleCount.getOrDefault(role, 0) + 1);
        }

        return roleCount;
    }

    /**
     * Calcula sinergia de formación
     */
    public TeamSynergyScore calculateFormationSynergy(List<Hero> formation) {
        TeamSynergyScore score = new TeamSynergyScore();

        Map<Integer, Integer> factionCount = new HashMap<>();
        Map<Integer, Integer> attributeCount = new HashMap<>();

        // Contar facciones y atributos
        for (Hero hero : formation) {
            factionCount.put(hero.getFaction(), factionCount.getOrDefault(hero.getFaction(), 0) + 1);
            attributeCount.put(hero.getAttribute(), attributeCount.getOrDefault(hero.getAttribute(), 0) + 1);
        }

        // Calcular puntos por bonos de facción
        for (Map.Entry<Integer, Integer> entry : factionCount.entrySet()) {
            if (entry.getValue() >= HeroConstants.FACTION_BONUS_REQUIREMENT) {
                score.factionBonusPoints += 20;
                score.activeFactionBonuses.add(HeroConstants.getFactionName(entry.getKey()));
            }
        }

        // Calcular puntos por bonos de atributo
        for (Map.Entry<Integer, Integer> entry : attributeCount.entrySet()) {
            if (entry.getValue() >= HeroConstants.ATTRIBUTE_BONUS_REQUIREMENT) {
                score.attributeBonusPoints += 25;
                score.activeAttributeBonuses.add(HeroConstants.getAttributeName(entry.getKey()));
            }
        }

        // Detectar combinaciones épicas
        score.epicComboBonuses = detectEpicCombinations(attributeCount);
        score.epicComboPoints = score.epicComboBonuses.size() * 30;

        // Calcular score total
        score.totalScore = score.factionBonusPoints + score.attributeBonusPoints + score.epicComboPoints;

        // Bonus por diversidad (no solo stack de una facción)
        if (factionCount.size() >= 3) {
            score.diversityBonus = 10;
            score.totalScore += score.diversityBonus;
        }

        return score;
    }

    /**
     * Detecta combinaciones épicas de atributos
     */
    private List<String> detectEpicCombinations(Map<Integer, Integer> attributeCount) {
        List<String> epicCombos = new ArrayList<>();

        // 3 Power + 2 Void = "Fuerza Destructiva"
        if (attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_POWER, 0) >= 3 &&
                attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_VOID, 0) >= 2) {
            epicCombos.add("Fuerza Destructiva (3 Power + 2 Void)");
        }

        // 4 Soul + 1 Heart = "Barrera Espiritual"
        if (attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_SOUL, 0) >= 4 &&
                attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_HEART, 0) >= 1) {
            epicCombos.add("Barrera Espiritual (4 Soul + 1 Heart)");
        }

        // 5 Core = "Equilibrio Perfecto"
        if (attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_CORE, 0) >= 5) {
            epicCombos.add("Equilibrio Perfecto (5 Core)");
        }

        // 3 Mind + 2 Heart = "Sabiduría Compasiva"
        if (attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_MIND, 0) >= 3 &&
                attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_HEART, 0) >= 2) {
            epicCombos.add("Sabiduría Compasiva (3 Mind + 2 Heart)");
        }

        // 2 Power + 2 Soul + 1 Void = "Trinidad Destructora"
        if (attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_POWER, 0) >= 2 &&
                attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_SOUL, 0) >= 2 &&
                attributeCount.getOrDefault(HeroConstants.ATTRIBUTE_VOID, 0) >= 1) {
            epicCombos.add("Trinidad Destructora (2 Power + 2 Soul + 1 Void)");
        }

        return epicCombos;
    }

    /**
     * Analiza distribución de poder en la formación
     */
    private PowerDistribution analyzePowerDistribution(List<Hero> formation) {
        PowerDistribution distribution = new PowerDistribution();
        List<Long> powerLevels = new ArrayList<>();

        for (Hero hero : formation) {
            HeroStats stats = heroManager.calculateHeroStats(hero.getId(), true, true);
            if (stats != null) {
                powerLevels.add(stats.calculateTotalPower());
            }
        }

        if (powerLevels.isEmpty()) {
            distribution.hasWeakLinks = false;
            return distribution;
        }

        // Calcular estadísticas
        long totalPower = 0;
        long maxPower = 0;
        long minPower = Long.MAX_VALUE;

        for (Long power : powerLevels) {
            totalPower += power;
            maxPower = Math.max(maxPower, power);
            minPower = Math.min(minPower, power);
        }

        distribution.totalPower = totalPower;
        distribution.averagePower = totalPower / powerLevels.size();
        distribution.maxPower = maxPower;
        distribution.minPower = minPower;

        // Detectar weak links (50% menos poder que el promedio)
        distribution.hasWeakLinks = minPower < (distribution.averagePower * 0.5);
        distribution.powerGap = maxPower - minPower;

        return distribution;
    }

    /**
     * Analiza posicionamiento de héroes
     */
    private List<String> analyzePositioning(List<Hero> formation) {
        List<String> advice = new ArrayList<>();

        if (formation.size() < 2) {
            return advice;
        }

        // Analizar posiciones frontales (1, 2, 3) vs traseras (4, 5)
        Map<Integer, Integer> positionRoles = new HashMap<>();

        for (int i = 0; i < formation.size(); i++) {
            Hero hero = formation.get(i);
            int position = i + 1; // Posición 1-5

            // Verificar si tanks están en posiciones frontales
            if (hero.getRole() == HeroConstants.ROLE_TANK && position > BattleConstants.POSITION_FRONT_RIGHT) {
                advice.add("Tank " + hero.getName() + " debería estar en posición frontal");
            }

            // Verificar si healers están en posiciones traseras
            if (hero.getRole() == HeroConstants.ROLE_HEALER && position <= BattleConstants.POSITION_FRONT_RIGHT) {
                advice.add("Healer " + hero.getName() + " estaría más seguro en posición trasera");
            }

            // Verificar si assassins están en posiciones laterales
            if (hero.getRole() == HeroConstants.ROLE_ASSASSIN && position == BattleConstants.POSITION_FRONT_CENTER) {
                advice.add("Assassin " + hero.getName() + " evita el centro frontal (muy expuesto)");
            }

            // Verificar si mages están protegidos
            if (hero.getRole() == HeroConstants.ROLE_MAGE && position <= BattleConstants.POSITION_FRONT_RIGHT) {
                advice.add("Mage " + hero.getName() + " necesita protección en la retaguardia");
            }
        }

        return advice;
    }

    // ==================== OPTIMIZACIÓN DE FORMACIONES ====================

    /**
     * Sugiere la formación óptima automáticamente
     */
    public FormationSuggestion suggestOptimalFormation(List<Hero> availableHeroes, int maxSlots) {
        FormationSuggestion suggestion = new FormationSuggestion();

        if (availableHeroes == null || availableHeroes.isEmpty()) {
            suggestion.success = false;
            suggestion.reason = "No hay héroes disponibles";
            return suggestion;
        }

        // Ordenar héroes por poder
        List<Hero> sortedHeroes = new ArrayList<>(availableHeroes);
        sortedHeroes.sort((h1, h2) -> {
            HeroStats s1 = heroManager.calculateHeroStats(h1.getId(), true, false);
            HeroStats s2 = heroManager.calculateHeroStats(h2.getId(), true, false);

            if (s1 == null) return 1;
            if (s2 == null) return -1;

            return Long.compare(s2.calculateTotalPower(), s1.calculateTotalPower());
        });

        // Algoritmo de selección inteligente
        List<Hero> optimalFormation = selectOptimalTeam(sortedHeroes, maxSlots);

        // Optimizar posiciones
        List<Hero> positionedFormation = optimizePositions(optimalFormation);

        suggestion.suggestedFormation = positionedFormation;
        suggestion.success = true;

        // Calcular métricas de la sugerencia
        TeamSynergyScore synergyScore = calculateFormationSynergy(positionedFormation);
        suggestion.expectedSynergyScore = synergyScore.totalScore;

        PowerDistribution powerDist = analyzePowerDistribution(positionedFormation);
        suggestion.totalPower = powerDist.totalPower;

        return suggestion;
    }

    /**
     * Selecciona el equipo óptimo considerando sinergia
     */
    private List<Hero> selectOptimalTeam(List<Hero> sortedHeroes, int maxSlots) {
        List<Hero> team = new ArrayList<>();

        // Estrategia: Priorizar balance de roles y sinergia sobre poder bruto

        // 1. Buscar un tank o héroe tanky
        Hero tank = findBestHeroOfRole(sortedHeroes, HeroConstants.ROLE_TANK);
        if (tank == null) {
            tank = findTankiestHero(sortedHeroes);
        }
        if (tank != null) {
            team.add(tank);
            sortedHeroes.remove(tank);
        }

        // 2. Buscar un healer si hay espacio
        if (team.size() < maxSlots) {
            Hero healer = findBestHeroOfRole(sortedHeroes, HeroConstants.ROLE_HEALER);
            if (healer != null) {
                team.add(healer);
                sortedHeroes.remove(healer);
            }
        }

        // 3. Llenar con DPS priorizando sinergia
        while (team.size() < maxSlots && !sortedHeroes.isEmpty()) {
            Hero bestSynergyHero = findBestSynergyHero(team, sortedHeroes);
            if (bestSynergyHero != null) {
                team.add(bestSynergyHero);
                sortedHeroes.remove(bestSynergyHero);
            } else {
                // Fallback: agregar el más poderoso disponible
                team.add(sortedHeroes.get(0));
                sortedHeroes.remove(0);
            }
        }

        return team;
    }

    /**
     * Encuentra el mejor héroe de un rol específico
     */
    private Hero findBestHeroOfRole(List<Hero> heroes, int role) {
        Hero best = null;
        long bestPower = 0;

        for (Hero hero : heroes) {
            if (hero.getRole() == role) {
                HeroStats stats = heroManager.calculateHeroStats(hero.getId(), true, false);
                if (stats != null) {
                    long power = stats.calculateTotalPower();
                    if (power > bestPower) {
                        best = hero;
                        bestPower = power;
                    }
                }
            }
        }

        return best;
    }

    /**
     * Encuentra el héroe más tanky si no hay tank dedicado
     */
    private Hero findTankiestHero(List<Hero> heroes) {
        Hero tankiest = null;
        int bestTankiness = 0;

        for (Hero hero : heroes) {
            HeroStats stats = heroManager.calculateHeroStats(hero.getId(), true, false);
            if (stats != null) {
                // Tankiness = HP + DEF
                int tankiness = stats.getFinalHp() / 10 + stats.getFinalDef();
                if (tankiness > bestTankiness) {
                    tankiest = hero;
                    bestTankiness = tankiness;
                }
            }
        }

        return tankiest;
    }

    /**
     * Encuentra el héroe que mejor sinergia aporte al equipo actual
     */
    private Hero findBestSynergyHero(List<Hero> currentTeam, List<Hero> candidates) {
        Hero bestSynergyHero = null;
        float bestSynergyImprovement = 0;

        // Calcular sinergia actual
        TeamSynergyScore currentSynergy = calculateFormationSynergy(currentTeam);

        for (Hero candidate : candidates) {
            // Crear equipo temporal
            List<Hero> tempTeam = new ArrayList<>(currentTeam);
            tempTeam.add(candidate);

            // Calcular nueva sinergia
            TeamSynergyScore newSynergy = calculateFormationSynergy(tempTeam);

            float improvement = newSynergy.totalScore - currentSynergy.totalScore;

            if (improvement > bestSynergyImprovement) {
                bestSynergyHero = candidate;
                bestSynergyImprovement = improvement;
            }
        }

        return bestSynergyHero;
    }

    /**
     * Optimiza las posiciones de los héroes en la formación
     */
    private List<Hero> optimizePositions(List<Hero> formation) {
        if (formation.size() <= 1) {
            return formation;
        }

        List<Hero> optimized = new ArrayList<>(formation);

        // Algoritmo simple: ordenar por rol y tankiness
        optimized.sort((h1, h2) -> {
            int role1 = h1.getRole();
            int role2 = h2.getRole();

            // Prioridad de posicionamiento por rol
            int priority1 = getRolePositionPriority(role1);
            int priority2 = getRolePositionPriority(role2);

            if (priority1 != priority2) {
                return Integer.compare(priority1, priority2);
            }

            // Si mismo rol, ordenar por tankiness
            HeroStats s1 = heroManager.calculateHeroStats(h1.getId(), true, false);
            HeroStats s2 = heroManager.calculateHeroStats(h2.getId(), true, false);

            if (s1 != null && s2 != null) {
                int tankiness1 = s1.getFinalHp() / 10 + s1.getFinalDef();
                int tankiness2 = s2.getFinalHp() / 10 + s2.getFinalDef();
                return Integer.compare(tankiness2, tankiness1); // Más tanky primero
            }

            return 0;
        });

        return optimized;
    }

    /**
     * Obtiene prioridad de posicionamiento por rol (menor = más frontal)
     */
    private int getRolePositionPriority(int role) {
        switch (role) {
            case HeroConstants.ROLE_TANK: return 1;        // Posición frontal
            case HeroConstants.ROLE_BERSERKER: return 2;   // Frontal-media
            case HeroConstants.ROLE_ASSASSIN: return 3;    // Media
            case HeroConstants.ROLE_RANGE: return 4;       // Media-trasera
            case HeroConstants.ROLE_CONTROLLER: return 5;  // Trasera
            case HeroConstants.ROLE_MAGE: return 6;        // Trasera
            case HeroConstants.ROLE_SUPPORT: return 7;     // Muy trasera
            case HeroConstants.ROLE_HEALER: return 8;      // Máxima protección
            default: return 5;
        }
    }

    // ==================== CÁLCULOS DE COMBATE ====================

    /**
     * Calcula la efectividad de un atacante contra un defensor
     */
    public float calculateMatchupEffectiveness(Hero attacker, Hero defender) {
        float effectiveness = 1.0f;

        // Ventaja de tipo (facción)
        effectiveness *= BattleConstants.getTypeAdvantage(attacker.getFaction(), defender.getFaction());

        // Ventaja de rol
        effectiveness *= calculateRoleAdvantage(attacker.getRole(), defender.getRole());

        // Ventaja de atributo
        effectiveness *= calculateAttributeAdvantage(attacker.getAttribute(), defender.getAttribute());

        return effectiveness;
    }

    /**
     * Calcula ventaja entre roles
     */
    private float calculateRoleAdvantage(int attackerRole, int defenderRole) {
        // Assassin vs Support/Healer
        if (attackerRole == HeroConstants.ROLE_ASSASSIN &&
                (defenderRole == HeroConstants.ROLE_SUPPORT || defenderRole == HeroConstants.ROLE_HEALER)) {
            return 1.3f; // +30% efectividad
        }

        // Mage vs grupos (múltiples targets)
        if (attackerRole == HeroConstants.ROLE_MAGE) {
            return 1.1f; // +10% efectividad general
        }

        // Tank vs Assassin (resistencia)
        if (attackerRole == HeroConstants.ROLE_TANK && defenderRole == HeroConstants.ROLE_ASSASSIN) {
            return 0.8f; // -20% efectividad (tank resiste assassin)
        }

        // Range vs Mage (outrange)
        if (attackerRole == HeroConstants.ROLE_RANGE && defenderRole == HeroConstants.ROLE_MAGE) {
            return 1.2f; // +20% efectividad
        }

        return 1.0f; // Sin ventaja especial
    }

    /**
     * Calcula ventaja entre atributos
     */
    private float calculateAttributeAdvantage(int attackerAttribute, int defenderAttribute) {
        // Power vs Soul (ataque vs defensa)
        if (attackerAttribute == HeroConstants.ATTRIBUTE_POWER &&
                defenderAttribute == HeroConstants.ATTRIBUTE_SOUL) {
            return 0.9f; // -10% efectividad (Soul resiste Power)
        }

        // Void vs Core (penetración vs balance)
        if (attackerAttribute == HeroConstants.ATTRIBUTE_VOID &&
                defenderAttribute == HeroConstants.ATTRIBUTE_CORE) {
            return 1.15f; // +15% efectividad
        }

        // Mind vs Heart (control vs soporte)
        if (attackerAttribute == HeroConstants.ATTRIBUTE_MIND &&
                defenderAttribute == HeroConstants.ATTRIBUTE_HEART) {
            return 1.1f; // +10% efectividad
        }

        return 1.0f; // Sin ventaja especial
    }

    /**
     * Calcula el orden de turnos basado en velocidad
     */
    public List<Hero> calculateTurnOrder(List<Hero> team1, List<Hero> team2) {
        List<TurnOrderEntry> allParticipants = new ArrayList<>();

        // Agregar equipo 1
        for (Hero hero : team1) {
            HeroStats stats = heroManager.calculateHeroStats(hero.getId(), true, true);
            if (stats != null) {
                float effectiveSpeed = BattleConstants.calculateEffectiveSpeed(stats.getFinalSpeed());
                allParticipants.add(new TurnOrderEntry(hero, effectiveSpeed, 1));
            }
        }

        // Agregar equipo 2
        for (Hero hero : team2) {
            HeroStats stats = heroManager.calculateHeroStats(hero.getId(), true, true);
            if (stats != null) {
                float effectiveSpeed = BattleConstants.calculateEffectiveSpeed(stats.getFinalSpeed());
                allParticipants.add(new TurnOrderEntry(hero, effectiveSpeed, 2));
            }
        }

        // Ordenar por velocidad efectiva (mayor a menor)
        allParticipants.sort((e1, e2) -> Float.compare(e2.effectiveSpeed, e1.effectiveSpeed));

        // Convertir a lista de héroes
        List<Hero> turnOrder = new ArrayList<>();
        for (TurnOrderEntry entry : allParticipants) {
            turnOrder.add(entry.hero);
        }

        return turnOrder;
    }

    /**
     * Calcula protección recibida por posición
     */
    public float calculatePositionProtection(int position, List<Hero> team) {
        float baseProtection = BattleConstants.getPositionProtection(position);

        // Bonus adicional si hay tanks en formación
        boolean hasTankProtection = false;
        for (Hero teammate : team) {
            if (teammate.getRole() == HeroConstants.ROLE_TANK) {
                hasTankProtection = true;
                break;
            }
        }

        if (hasTankProtection && position > BattleConstants.POSITION_FRONT_RIGHT) {
            baseProtection *= 1.2f; // +20% protección adicional
        }

        return baseProtection;
    }

    /**
     * Encuentra el héroe que mejor sinergia aporte al equipo actual
     */

    // ==================== VALIDACIONES DE FORMACIÓN ====================

    /**
     * Valida si una formación es legal
     */
    public FormationValidation validateFormation(List<Hero> formation) {
        FormationValidation validation = new FormationValidation();
        validation.isValid = true;
        validation.errors = new ArrayList<>();
        validation.warnings = new ArrayList<>();

        if (formation == null || formation.isEmpty()) {
            validation.isValid = false;
            validation.errors.add("La formación no puede estar vacía");
            return validation;
        }

        // Verificar límites de slots
        int playerLevel = PlayerDataManager.getInstance(context).getPlayerLevel();
        int maxSlots = GameConstants.getAvailableTeamSlots(playerLevel);

        if (formation.size() > maxSlots) {
            validation.isValid = false;
            validation.errors.add("Formación excede slots disponibles: " + formation.size() + "/" + maxSlots);
        }

        // Verificar héroes duplicados
        Map<Long, Integer> heroCount = new HashMap<>();
        for (Hero hero : formation) {
            heroCount.put(hero.getId(), heroCount.getOrDefault(hero.getId(), 0) + 1);
        }

        for (Map.Entry<Long, Integer> entry : heroCount.entrySet()) {
            if (entry.getValue() > 1) {
                validation.isValid = false;
                validation.errors.add("Héroe duplicado en formación: " + entry.getKey());
            }
        }

        // Verificar poder mínimo recomendado
        PowerDistribution powerDist = analyzePowerDistribution(formation);
        if (powerDist.hasWeakLinks) {
            validation.warnings.add("Algunos héroes están significativamente más débiles");
        }

        // Verificar balance de roles
        Map<Integer, Integer> roleCount = countRoles(formation);
        if (!roleCount.containsKey(HeroConstants.ROLE_TANK) &&
                !roleCount.containsKey(HeroConstants.ROLE_HEALER) && formation.size() > 2) {
            validation.warnings.add("Formación sin Tank ni Healer - alta probabilidad de derrota");
        }

        return validation;
    }

    // ==================== CLASES INTERNAS ====================

    /**
     * Análisis completo de una formación
     */
    public static class FormationAnalysis {
        public boolean isOptimal;
        public List<String> suggestions;
        public List<String> positioningAdvice;
        public float synergyScore;

        public FormationAnalysis() {
            this.suggestions = new ArrayList<>();
            this.positioningAdvice = new ArrayList<>();
        }

        public FormationAnalysis(boolean isOptimal, String reason, List<String> suggestions) {
            this();
            this.isOptimal = isOptimal;
            if (!isOptimal && reason != null) {
                this.suggestions.add(reason);
            }
            if (suggestions != null) {
                this.suggestions.addAll(suggestions);
            }
        }
    }

    /**
     * Puntuación de sinergia del equipo
     */
    public static class TeamSynergyScore {
        public int factionBonusPoints;
        public int attributeBonusPoints;
        public int epicComboPoints;
        public int diversityBonus;
        public float totalScore;
        public List<String> activeFactionBonuses;
        public List<String> activeAttributeBonuses;
        public List<String> epicComboBonuses;

        public TeamSynergyScore() {
            this.activeFactionBonuses = new ArrayList<>();
            this.activeAttributeBonuses = new ArrayList<>();
            this.epicComboBonuses = new ArrayList<>();
        }
    }

    /**
     * Distribución de poder en el equipo
     */
    public static class PowerDistribution {
        public long totalPower;
        public long averagePower;
        public long maxPower;
        public long minPower;
        public long powerGap;
        public boolean hasWeakLinks;
    }

    /**
     * Sugerencia de formación óptima
     */
    public static class FormationSuggestion {
        public boolean success;
        public String reason;
        public List<Hero> suggestedFormation;
        public float expectedSynergyScore;
        public long totalPower;

        public FormationSuggestion() {
            this.suggestedFormation = new ArrayList<>();
        }
    }

    /**
     * Validación de formación
     */
    public static class FormationValidation {
        public boolean isValid;
        public List<String> errors;
        public List<String> warnings;

        public FormationValidation() {
            this.errors = new ArrayList<>();
            this.warnings = new ArrayList<>();
        }
    }

    /**
     * Entrada para orden de turnos
     */
    private static class TurnOrderEntry {
        public Hero hero;
        public float effectiveSpeed;
        public int teamId;

        public TurnOrderEntry(Hero hero, float effectiveSpeed, int teamId) {
            this.hero = hero;
            this.effectiveSpeed = effectiveSpeed;
            this.teamId = teamId;
        }
    }
}