package com.example.smartchili;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView txtSuhu, txtKelembaban, txtHeater, txtFan, txtTarget;
    Button btnSystem;
    SeekBar seekTarget;

    boolean systemOn = true;
    int targetSuhu = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSuhu = findViewById(R.id.txtSuhu);
        txtKelembaban = findViewById(R.id.txtKelembaban);
        txtHeater = findViewById(R.id.txtHeater);
        txtFan = findViewById(R.id.txtFan);
        txtTarget = findViewById(R.id.txtTarget);
        btnSystem = findViewById(R.id.btnSystem);
        seekTarget = findViewById(R.id.seekTarget);

        btnSystem.setOnClickListener(v -> {
            systemOn = !systemOn;

            if (systemOn) {
                btnSystem.setText("SYSTEM ON");
                txtHeater.setText("🔥\nHEATER\nON\nMode: Otomatis");
                txtFan.setText("🌬\nFAN\nON\nMode: Otomatis");
            } else {
                btnSystem.setText("SYSTEM OFF");
                txtHeater.setText("🔥\nHEATER\nOFF\nMode: Otomatis");
                txtFan.setText("🌬\nFAN\nOFF\nMode: Otomatis");
            }
        });

        seekTarget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetSuhu = progress + 30;
                txtTarget.setText("Target Suhu: " + targetSuhu + " °C");
                txtSuhu.setText("🌡\nSUHU\n47.2 °C\nTarget: " + targetSuhu + " °C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}