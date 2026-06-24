package com.example.smartchili;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    RecyclerView recyclerHistory;
    ArrayList<HistoryModel> historyList;
    HistoryAdapter adapter;
    DatabaseReference databaseReference;

    public HistoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.history, container, false);

        recyclerHistory = view.findViewById(R.id.recyclerHistory);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(historyList);
        recyclerHistory.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("riwayat");

        bacaRiwayat();

        return view;
    }

    private void bacaRiwayat() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    HistoryModel history = data.getValue(HistoryModel.class);

                    if (history != null) {
                        historyList.add(0, history);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}