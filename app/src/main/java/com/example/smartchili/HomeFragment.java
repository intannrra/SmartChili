package com.example.smartchili;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.*;

public class HomeFragment extends Fragment {

    TextView txtSuhu, txtKelembaban, txtHeater, txtTarget;
    Button btnSystem;
    SeekBar seekTarget;
    Switch switchAuto;

    DatabaseReference databaseReference;

    boolean systemOn = false;
    boolean autoOn = false;
    boolean sedangUpdateDariFirebase = false;
    int targetSuhu = 60;

    String idRiwayatAktif = null;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home, container, false);

        txtSuhu = view.findViewById(R.id.txtSuhu);
        txtKelembaban = view.findViewById(R.id.txtKelembaban);
        txtHeater = view.findViewById(R.id.txtHeater);
        txtTarget = view.findViewById(R.id.txtTarget);
        btnSystem = view.findViewById(R.id.btnSystem);
        seekTarget = view.findViewById(R.id.seekTarget);
        switchAuto = view.findViewById(R.id.switchAuto);

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

                if (systemOn) {
                    mulaiRiwayat();
                } else {
                    selesaiRiwayat();
                }
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
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                databaseReference.child("sensor").child("target").setValue(targetSuhu);
            }
        });

        return view;
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        databaseReference.child("kontrol").child("heater").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String heater = snapshot.getValue(String.class);

                if (heater != null) {
                    txtHeater.setText(heater.equalsIgnoreCase("ON")
                            ? "Heater menyala."
                            : "Heater mati.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void mulaiRiwayat() {
        DatabaseReference riwayatRef = databaseReference.child("riwayat").push();
        idRiwayatAktif = riwayatRef.getKey();

        riwayatRef.child("mulai").setValue(ServerValue.TIMESTAMP);
        riwayatRef.child("targetSuhu").setValue(targetSuhu);
        riwayatRef.child("status").setValue("Berjalan");
    }

    private void selesaiRiwayat() {
        if (idRiwayatAktif != null) {
            databaseReference.child("riwayat")
                    .child(idRiwayatAktif)
                    .child("selesai")
                    .setValue(ServerValue.TIMESTAMP);

            databaseReference.child("riwayat")
                    .child(idRiwayatAktif)
                    .child("status")
                    .setValue("Selesai");

            idRiwayatAktif = null;
        }
    }
}