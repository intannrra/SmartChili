package com.example.smartchili;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    TextView btnHome, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHome = findViewById(R.id.btnHome);
        btnHistory = findViewById(R.id.btnHistory);

        if (savedInstanceState == null) {
            bukaFragment(new HomeFragment());
        }

        btnHome.setOnClickListener(v -> {
            bukaFragment(new HomeFragment());

            btnHome.setBackgroundResource(R.drawable.bg_nav_active);
            btnHome.setTextColor(getColor(R.color.primary_red));

            btnHistory.setBackground(null);
            btnHistory.setTextColor(getColor(R.color.white));
        });

        btnHistory.setOnClickListener(v -> {
            bukaFragment(new HistoryFragment());

            btnHistory.setBackgroundResource(R.drawable.bg_nav_active);
            btnHistory.setTextColor(getColor(R.color.primary_red));

            btnHome.setBackground(null);
            btnHome.setTextColor(getColor(R.color.white));
        });
    }

    private void bukaFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}