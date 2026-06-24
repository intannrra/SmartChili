package com.example.smartchili;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

public class MainActivity extends AppCompatActivity {

    TextView txtSuhu, txtKelembaban, txtHeater, txtTarget;
    Button btnSystem;
    TextView btnHistory;
    SeekBar seekTarget;
    Switch switchAuto;

    DatabaseReference databaseReference;

    boolean systemOn = false;
    boolean autoOn = false;
    boolean sedangUpdateDariFirebase = false;
    int targetSuhu = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSuhu = findViewById(R.id.txtSuhu);
        txtKelembaban = findViewById(R.id.txtKelembaban);
        txtHeater = findViewById(R.id.txtHeater);
        txtTarget = findViewById(R.id.txtTarget);
        btnSystem = findViewById(R.id.btnSystem);
        btnHistory = findViewById(R.id.btnHistory);
        seekTarget = findViewById(R.id.seekTarget);
        switchAuto = findViewById(R.id.switchAuto);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        seekTarget.setMax(100);
        seekTarget.setProgress(targetSuhu);
        txtTarget.setText("Target Suhu : " + targetSuhu + " °C");

        bacaDataFirebase();

        btnSystem.setOnClickListener(v -> {
            if (!autoOn) {
                systemOn = !systemOn;

                String status = systemOn ? "ON" : "OFF";

                databaseReference.child("kontrol").child("system").setValue(status);
                databaseReference.child("kontrol").child("manualHeater").setValue(status);
                databaseReference.child("kontrol").child("heater").setValue(status);

                btnSystem.setText(status);
                txtHeater.setText(systemOn ? "Heater menyala." : "Heater mati.");
            }
        });

        switchAuto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!sedangUpdateDariFirebase) {
                autoOn = isChecked;
                databaseReference.child("kontrol").child("auto").setValue(autoOn ? "ON" : "OFF");

                if (autoOn) {
                    databaseReference.child("kontrol").child("manualHeater").setValue("OFF");
                }
            }
        });

        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        seekTarget.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetSuhu = progress;
                txtTarget.setText("Target Suhu : " + targetSuhu + " °C");

                if (fromUser) {
                    databaseReference.child("sensor").child("target").setValue(targetSuhu);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                databaseReference.child("sensor").child("target").setValue(targetSuhu);
            }
        });
    }

    private void bacaDataFirebase() {

        databaseReference.child("sensor").child("suhu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();

                if (value != null) {
                    double suhu = Double.parseDouble(value.toString());
                    txtSuhu.setText(String.format("%.1f °C", suhu));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        databaseReference.child("sensor").child("kelembaban").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();

                if (value != null) {
                    double kelembaban = Double.parseDouble(value.toString());
                    txtKelembaban.setText(String.format("%.1f %%", kelembaban));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        databaseReference.child("sensor").child("target").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object value = snapshot.getValue();

                if (value != null) {
                    targetSuhu = (int) Double.parseDouble(value.toString());
                    txtTarget.setText("Target Suhu : " + targetSuhu + " °C");

                    if (seekTarget.getProgress() != targetSuhu) {
                        seekTarget.setProgress(targetSuhu);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        databaseReference.child("kontrol").child("heater").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String heater = snapshot.getValue(String.class);

                if (heater != null) {
                    txtHeater.setText(heater.equalsIgnoreCase("ON") ? "Heater menyala." : "Heater mati.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        databaseReference.child("kontrol").child("system").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String system = snapshot.getValue(String.class);

                if (system != null) {
                    systemOn = system.equalsIgnoreCase("ON");

                    if (!autoOn) {
                        btnSystem.setText(systemOn ? "ON" : "OFF");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        databaseReference.child("kontrol").child("auto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String auto = snapshot.getValue(String.class);

                if (auto != null) {
                    autoOn = auto.equalsIgnoreCase("ON");

                    sedangUpdateDariFirebase = true;
                    switchAuto.setChecked(autoOn);
                    sedangUpdateDariFirebase = false;

                    if (autoOn) {
                        btnSystem.setEnabled(false);
                        btnSystem.setAlpha(0.5f);
                        btnSystem.setText("AUTO");
                    } else {
                        btnSystem.setEnabled(true);
                        btnSystem.setAlpha(1f);
                        btnSystem.setText(systemOn ? "ON" : "OFF");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}