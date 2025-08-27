package com.by.soh.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.by.soh.R;
import com.by.soh.managers.HeroManager;
import com.by.soh.managers.PlayerDataManager;
import com.by.soh.managers.SaveGameManager;
import com.by.soh.models.HeroStats;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.by.soh.managers.EquipmentManager;
import com.by.soh.models.Equipment;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // UI Components
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView currentPowerText;
    private TextView inventoryCapacityText;

    // Managers
    private EquipmentManager equipmentManager;
    private PlayerDataManager playerDataManager;
    private SaveGameManager saveManager;
    private HeroManager heroManager;
    // Data
    private List<Equipment> allEquipment = new ArrayList<>();
    private List<Equipment> testEquipment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bag);

        // Inicializar managers
        equipmentManager = EquipmentManager.getInstance(this);
        playerDataManager = PlayerDataManager.getInstance(this);
        saveManager = SaveGameManager.getInstance(this);
        heroManager = HeroManager.getInstance(this);

        // Inicializar UI
        initializeViews();

        // ¡AQUÍ ESTÁ TU PRUEBA!
        testEquipmentSystem();

        // Configurar UI
        setupUI();

        // Cargar datos
        loadEquipmentData();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        currentPowerText = findViewById(R.id.current_power);
        inventoryCapacityText = findViewById(R.id.inventory_capacity);

        Log.d(TAG, "Views inicializados");
    }

    /**
     * ¡AQUÍ ESTÁ LA PRUEBA DE TU SISTEMA!
     * Exactamente como me dijiste que querías probarlo
     */
    private void testEquipmentSystem() {
        Log.d(TAG, "=== INICIANDO PRUEBA DE EQUIPMENT SYSTEM ===");

        try {
            // Tu código de prueba exacto
            long itemId = equipmentManager.createRandomEquipment(3, 5, 25);
            Equipment item = equipmentManager.getEquipmentById(itemId);
            playerDataManager.addGold(5000);
            playerDataManager.addExperience(1000);

            SaveGameManager.SaveResult result = saveManager.saveGame("test_save", true);
            Log.d("TEST", result.toString());

            long newHeroId = heroManager.createHeroFromTemplate("byakuya_kuchiki", 3, 0);
            HeroStats stats = heroManager.calculateHeroStats(newHeroId, true, true);
            Log.d("POWER", "Poder total: " + stats.calculateTotalPower());

            boolean success = heroManager.enhanceHero(newHeroId);

// Añadir experiencia
            HeroManager.LevelUpResult result1 = heroManager.addExperienceToHero(newHeroId, 1000);
            if (result1.leveledUp) {
                Log.i("LEVELUP", "¡Subió " + result1.levelsGained + " niveles!");
            }
            String stat = heroManager.getHeroById(newHeroId).toString();
            Log.d("GOL", stat);
            HeroManager.HeroCollectionStats stats1 = heroManager.getCollectionStats();

// Análisis de equipo
            HeroManager.TeamSynergyAnalysis synergy = heroManager.analyzeTeamSynergy();
            Log.d("SYNERGY", "Puntuación: " + synergy.synergyScore);
            Log.d("MAMADA", stat);

            if (item != null) {
                Log.d("TEST", item.getDescription());

                // Mostrar también en Toast para que lo veas en pantalla
                Toast.makeText(this, "Equipment creado! Ver logs para detalles", Toast.LENGTH_LONG).show();

                // Crear algunos equipos más para la prueba
                createMoreTestEquipment();

            } else {
                Log.e("TEST", "Error: No se pudo crear el equipment");
                Toast.makeText(this, "Error creando equipment", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("TEST", "Exception en prueba de equipment", e);
            Toast.makeText(this, "Error en prueba: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Log.d(TAG, "=== PRUEBA DE EQUIPMENT SYSTEM COMPLETADA ===");
    }

    /**
     * Crea más equipment para pruebas más completas
     */
    private void createMoreTestEquipment() {
        Log.d(TAG, "Creando equipment adicional para pruebas...");

        // Crear equipment de diferentes raridades
        for (int i = 1; i <= 6; i++) {  // Diferentes rarezas
            long equipId = equipmentManager.createRandomEquipment(i, i, 25);
            Equipment equipment = equipmentManager.getEquipmentById(equipId);

            if (equipment != null) {
                testEquipment.add(equipment);
                Log.d("TEST_EXTRA", "Equipment " + i + ": " + equipment.toString());
                Log.d("TEST_EXTRA", "Descripción: " + equipment.getDescription());
                Log.d("TEST_EXTRA", "---");
            }
        }

        // Probar también el sistema de loot
        Log.d("TEST_LOOT", "Probando generación de loot...");
        List<Long> lootIds = equipmentManager.generateLoot(30, 5, true);

        for (Long lootId : lootIds) {
            Equipment lootItem = equipmentManager.getEquipmentById(lootId);
            if (lootItem != null) {
                testEquipment.add(lootItem);
                Log.d("TEST_LOOT", "Loot generado: " + lootItem.toString());
            }
        }

        Log.d(TAG, "Equipment total creado para pruebas: " + testEquipment.size());
    }

    private void setupUI() {
        // Configurar TabLayout
        String[] tabTitles = {"Equipment", "Item", "Shard"};

        // Configurar ViewPager con adapter básico
        EquipmentPagerAdapter adapter = new EquipmentPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Conectar TabLayout con ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        Log.d(TAG, "UI configurado");
    }

    private void loadEquipmentData() {
        // Cargar todo el equipment (incluyendo los de prueba)
        allEquipment = equipmentManager.getAllEquipment();

        // Calcular poder total
        int totalPower = 0;
        for (Equipment equipment : allEquipment) {
            totalPower += equipment.getPowerRating();
        }

        // Actualizar UI
        currentPowerText.setText(String.valueOf(totalPower));
        inventoryCapacityText.setText(allEquipment.size() + "/60");

        Log.d(TAG, "Datos cargados - Total items: " + allEquipment.size() + ", Poder total: " + totalPower);

        // Mostrar estadísticas del inventario
        EquipmentManager.EquipmentInventoryStats stats = equipmentManager.getInventoryStats();
        Log.d("STATS", stats.toString());

        // Log de algunos items para verificar
        Log.d(TAG, "=== PRIMEROS 3 EQUIPMENT EN INVENTARIO ===");
        for (int i = 0; i < Math.min(3, allEquipment.size()); i++) {
            Equipment eq = allEquipment.get(i);
            Log.d("INVENTORY", "Item " + (i+1) + ": " + eq.toString());
            Log.d("INVENTORY", "Descripción: " + eq.getDescription());
            Log.d("INVENTORY", "---");
        }
    }

    /**
     * Adapter simple para ViewPager2 - básico para la prueba
     */
    private static class EquipmentPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

        public EquipmentPagerAdapter(androidx.fragment.app.FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public androidx.fragment.app.Fragment createFragment(int position) {
            // Por ahora retornar fragments básicos
            switch (position) {
                case 0: // Equipment tab
                    return EquipmentTabFragment.newInstance();
                case 1: // Item tab
                    return ItemTabFragment.newInstance();
                case 2: // Shard tab
                    return ShardTabFragment.newInstance();
                default:
                    return EquipmentTabFragment.newInstance();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Equipment, Item, Shard
        }
    }

    /**
     * Fragment básico para tab de Equipment
     */
    public static class EquipmentTabFragment extends androidx.fragment.app.Fragment {

        public static EquipmentTabFragment newInstance() {
            return new EquipmentTabFragment();
        }

        @Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                              android.view.ViewGroup container, Bundle savedInstanceState) {

            // Usar tu layout tap_equipment.xml
            android.view.View view = inflater.inflate(R.layout.tap_equipment, container, false);

            // Configurar RecyclerView
            RecyclerView recyclerView = view.findViewById(R.id.equipment_recycler_view);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5));

                // Aquí pondrías tu adapter de equipment cuando lo tengas
                Log.d("EquipmentTab", "RecyclerView configurado para equipment");
            }

            return view;
        }
    }

    /**
     * Fragment básico para tab de Items
     */
    public static class ItemTabFragment extends androidx.fragment.app.Fragment {

        public static ItemTabFragment newInstance() {
            return new ItemTabFragment();
        }

        @Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                              android.view.ViewGroup container, Bundle savedInstanceState) {

            // Usar tu layout tap_items.xml
            android.view.View view = inflater.inflate(R.layout.tap_items, container, false);

            RecyclerView recyclerView = view.findViewById(R.id.items_recycler_view);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                Log.d("ItemTab", "RecyclerView configurado para items");
            }

            return view;
        }
    }

    /**
     * Fragment básico para tab de Shards
     */
    public static class ShardTabFragment extends androidx.fragment.app.Fragment {

        public static ShardTabFragment newInstance() {
            return new ShardTabFragment();
        }

        @Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater,
                                              android.view.ViewGroup container, Bundle savedInstanceState) {

            // Usar tu layout tab_shards.xml
            android.view.View view = inflater.inflate(R.layout.tab_shards, container, false);

            RecyclerView recyclerView = view.findViewById(R.id.shards_recycler_view);
            if (recyclerView != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                Log.d("ShardTab", "RecyclerView configurado para shards");
            }

            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar cache si es necesario
        if (equipmentManager != null) {
            equipmentManager.clearCache();
        }
    }
}