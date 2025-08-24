package com.by.soh


import android.R
import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.String
import kotlin.Int

class MainActivity : AppCompatActivity() {
    // UI Components
    private var playerName: TextView? = null
    private var playerLevel: TextView? = null
    private var vipStatus: TextView? = null
    private var totalPower: TextView? = null
    private var gemsCount: TextView? = null
    private var goldCount: TextView? = null
    private var profileImage: ImageView? = null
    private var addGemsBtn: ImageView? = null
    private var addGoldBtn: ImageView? = null
    private var navHome: LinearLayout? = null
    private var navFight: LinearLayout? = null
    private var navRole: LinearLayout? = null
    private var navBook: LinearLayout? = null
    private var navBag: LinearLayout? = null
    private var navConfig: LinearLayout? = null

    // Navigation
    private var fragmentManager: FragmentManager? = null
    private var currentFragment: Fragment? = null

    // Game Data
    private var playerData: PlayerData? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeData()
        setupNavigation()
        updateUI()


        // Load Home fragment by default
        loadFragment(HomeFragment())
    }

    private fun initializeViews() {
        // Top status bar components
        profileImage = findViewById(R.id.profile_image)
        playerName = findViewById(R.id.player_name)
        playerLevel = findViewById(R.id.player_level)
        vipStatus = findViewById(R.id.vip_status)
        totalPower = findViewById(R.id.total_power)
        gemsCount = findViewById(R.id.gems_count)
        goldCount = findViewById(R.id.gold_count)
        addGemsBtn = findViewById(R.id.add_gems_btn)
        addGoldBtn = findViewById(R.id.add_gold_btn)


        // Bottom navigation
        navHome = findViewById(R.id.nav_home)
        navFight = findViewById(R.id.nav_fight)
        navRole = findViewById(R.id.nav_role)
        navBook = findViewById(R.id.nav_book)
        navBag = findViewById(R.id.nav_bag)
        navConfig = findViewById(R.id.nav_config)

        fragmentManager = getSupportFragmentManager()
    }

    private fun initializeData() {
        // Initialize player data (later this will come from database/backend)
        playerData = PlayerData()
        playerData.setPlayerName("Player")
        playerData.setLevel(2)
        playerData.setVipLevel(0)
        playerData.setTotalPower(657)
        playerData.setGems(50)
        playerData.setGold(40015)
        playerData.setCurrentExp(75) // For progress bar
    }

    private fun setupNavigation() {
        // Profile section click listener
        findViewById(R.id.profile_section).setOnClickListener(View.OnClickListener { openPlayerProfile() })


        // Resource buttons
        addGemsBtn!!.setOnClickListener(View.OnClickListener { // TODO: Open gems purchase dialog or debug add gems
            debugAddGems(100)
        })

        addGoldBtn!!.setOnClickListener(View.OnClickListener { // TODO: Open gold purchase dialog or debug add gold
            debugAddGold(10000)
        })


        // Bottom navigation click listeners
        navHome!!.setOnClickListener(View.OnClickListener {
            loadFragment(HomeFragment())
            updateNavigationSelection(navHome!!)
        })

        navFight!!.setOnClickListener(View.OnClickListener {
            loadFragment(FightFragment())
            updateNavigationSelection(navFight!!)
        })

        navRole!!.setOnClickListener(View.OnClickListener {
            loadFragment(RoleFragment())
            updateNavigationSelection(navRole!!)
        })

        navBook!!.setOnClickListener(View.OnClickListener {
            loadFragment(BookFragment())
            updateNavigationSelection(navBook!!)
        })

        navBag!!.setOnClickListener(View.OnClickListener {
            loadFragment(BagFragment())
            updateNavigationSelection(navBag!!)
        })

        navConfig!!.setOnClickListener(View.OnClickListener {
            loadFragment(ConfigFragment())
            updateNavigationSelection(navConfig!!)
        })


        // Set Home as selected by default
        updateNavigationSelection(navHome!!)
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
        currentFragment = fragment
    }

    private fun updateNavigationSelection(selectedNav: LinearLayout) {
        // Reset all navigation items
        resetNavigationSelection()


        // Highlight selected navigation item
        selectedNav.isSelected = true
        selectedNav.alpha = 1.0f
    }

    private fun resetNavigationSelection() {
        navHome!!.isSelected = false
        navFight!!.isSelected = false
        navRole!!.isSelected = false
        navBook!!.isSelected = false
        navBag!!.isSelected = false
        navConfig!!.isSelected = false

        navHome!!.alpha = 0.6f
        navFight!!.alpha = 0.6f
        navRole!!.alpha = 0.6f
        navBook!!.alpha = 0.6f
        navBag!!.alpha = 0.6f
        navConfig!!.alpha = 0.6f
    }

    private fun updateUI() {
        playerName.setText(playerData.getPlayerName())
        playerLevel!!.text = "LV." + playerData.getLevel()
        vipStatus!!.text = "VIP" + playerData.getVipLevel()
        totalPower.setText(String.valueOf(playerData.getTotalPower()))
        gemsCount.setText(String.valueOf(playerData.getGems()))
        goldCount.setText(String.valueOf(playerData.getGold()))


        // Update experience progress bar
        val expProgressBar: ProgressBar = findViewById(R.id.exp_progress_bar)
        expProgressBar.progress = playerData.getCurrentExp()
    }

    private fun openPlayerProfile() {
        // TODO: Open player profile dialog or activity
        // For now, just show a toast or debug info
        Toast.makeText(
            this, "Player Profile - " + playerData.getPlayerName(),
            Toast.LENGTH_SHORT
        ).show()
    }

    // Debug methods for testing
    private fun debugAddGems(amount: Int) {
        playerData.setGems(playerData.getGems() + amount)
        updateUI()
        Toast.makeText(
            this, "Added $amount gems!",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun debugAddGold(amount: Int) {
        playerData.setGold(playerData.getGold() + amount)
        updateUI()
        Toast.makeText(
            this, "Added $amount gold!",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Getter for player data (for use in fragments)
    fun getPlayerData(): PlayerData? {
        return playerData
    }

    // Method to update UI from fragments
    fun refreshUI() {
        updateUI()
    }
}