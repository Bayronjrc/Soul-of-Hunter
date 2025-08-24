package com.by.soh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MainActivity mainActivity;
    private PlayerData playerData;

    // UI Components
    private LinearLayout signInBtn, eventBtn;
    private LinearLayout ghostIntrusionBtn, rukongaiBtn, guildWarBtn;
    private LinearLayout mallBtn, guildBtn, hellBtn, seireiteiBtn, meltBtn, huecoMundoBtn;
    private TextView nextLevelText;
    private LinearLayout levelRewardPreview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            playerData = mainActivity.getPlayerData();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupClickListeners();
        updateUI();
    }

    private void initializeViews(View view) {
        // Daily activities
        signInBtn = view.findViewById(R.id.sign_in_btn);
        eventBtn = view.findViewById(R.id.event_btn);

        // Guild activities
        ghostIntrusionBtn = view.findViewById(R.id.ghost_intrusion_btn);
        rukongaiBtn = view.findViewById(R.id.rukongai_btn);
        guildWarBtn = view.findViewById(R.id.guild_war_btn);

        // Main areas
        mallBtn = view.findViewById(R.id.mall_btn);
        guildBtn = view.findViewById(R.id.guild_btn);
        hellBtn = view.findViewById(R.id.hell_btn);
        seireiteiBtn = view.findViewById(R.id.seireitei_btn);
        meltBtn = view.findViewById(R.id.melt_btn);
        huecoMundoBtn = view.findViewById(R.id.hueco_mundo_btn);

        // Level reward
        nextLevelText = view.findViewById(R.id.next_level_text);
        levelRewardPreview = view.findViewById(R.id.level_reward_preview);
    }

    private void setupClickListeners() {
        // Daily Activities
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignIn();
            }
        });

        eventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEvent();
            }
        });

        // Guild Activities
        ghostIntrusionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGhostIntrusion();
            }
        });

        rukongaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRukongai();
            }
        });

        guildWarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGuildWar();
            }
        });

        // Main Areas
        mallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMall();
            }
        });

        guildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGuild();
            }
        });

        hellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHell();
            }
        });

        seireiteiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSeireitei();
            }
        });

        meltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMelt();
            }
        });

        huecoMundoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHuecoMundo();
            }
        });

        // Level Reward Preview
        levelRewardPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelRewards();
            }
        });
    }

    private void updateUI() {
        if (playerData != null) {
            nextLevelText.setText("Lv." + (playerData.getLevel() + 1));
        }
    }

    // Click handlers for different areas
    private void openSignIn() {
        // TODO: Implement daily sign in rewards
        showToast("Daily Sign In - Coming Soon!");
    }

    private void openEvent() {
        // TODO: Implement events system
        showToast("Events - Coming Soon!");
    }

    private void openGhostIntrusion() {
        // TODO: Implement Ghost Intrusion (Guild Boss)
        showToast("Ghost Intrusion - Coming Soon!");
    }

    private void openRukongai() {
        // TODO: Implement Rukongai (Awakening Materials)
        showToast("Rukongai - Coming Soon!");
    }

    private void openGuildWar() {
        // TODO: Implement Guild War
        showToast("Guild War - Coming Soon!");
    }

    private void openMall() {
        // TODO: Implement Mall/Shop system
        showToast("Mall - Coming Soon!");
    }

    private void openGuild() {
        // TODO: Implement Guild system
        showToast("Guild - Coming Soon!");
    }

    private void openHell() {
        // TODO: Implement Hell Tower
        showToast("Hell Tower - Coming Soon!");
    }

    private void openSeireitei() {
        // TODO: Implement Seireitei (Hero Awakening)
        showToast("Seireitei - Coming Soon!");
    }

    private void openMelt() {
        // TODO: Implement Melt (Equipment Forge)
        showToast("Melt - Coming Soon!");
    }

    private void openHuecoMundo() {
        // TODO: Implement Hueco Mundo (PvP)
        showToast("Hueco Mundo - Coming Soon!");
    }

    private void showLevelRewards() {
        if (playerData != null) {
            String message = "Next Level Rewards:\n" +
                    "Level " + (playerData.getLevel() + 1) + "\n" +
                    "- Gold: " + (playerData.getLevel() * 100) + "\n" +
                    "- Gems: " + (playerData.getLevel() * 10);
            showToast(message);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}