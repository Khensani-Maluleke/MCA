package com.example.mcpro.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.mcpro.R;
import com.example.mcpro.utils.CounsellorData;
import com.example.mcpro.utils.SessionManager;

import java.util.ArrayList;

public class CounsellorPreferencesFragment extends Fragment {

    public CounsellorPreferencesFragment() {}
    CheckBox Anxiety;
    CheckBox Addiction;
    CheckBox Abuse;
    CheckBox Academics;
    CheckBox Depression;
    CheckBox General;
    CheckBox GriefLoss;
    ArrayList<String> issues;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_counsellor_preferences, container, false);

        //FINDING ID
        issues = new ArrayList<>();
        Anxiety=view.findViewById(R.id.AnxietyID);
        Addiction=view.findViewById(R.id.AddictionID);
        Abuse=view.findViewById(R.id.AbuseID);
        Academics=view.findViewById(R.id.AcademicID);
        Depression=view.findViewById(R.id.DeprissionID);
        General=view.findViewById(R.id.GeneralID);
        GriefLoss=view.findViewById(R.id.Grief_LossID);

        Button nextButton = view.findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                issues.clear();
                if (Anxiety.isChecked()) issues.add("Anxiety");
                if (Addiction.isChecked()) issues.add("Addiction");
                if (Abuse.isChecked()) issues.add("Abuse");
                if (Academics.isChecked()) issues.add("Academics");
                if (Depression.isChecked()) issues.add("Depression");
                if (General.isChecked()) issues.add("General");
                if (GriefLoss.isChecked()) issues.add("Grief & Loss");

                if (issues.isEmpty()) {//if(issues.size() == 0)
                    Toast.makeText(getContext(), "Please select at least one issue to specialize on", Toast.LENGTH_SHORT).show();
                    return;
                }
                 Toast.makeText(getContext(), issues.toString(), Toast.LENGTH_SHORT).show();
                Log.d("SELECTED_ISSUES", issues.toString());

                CounsellorData data = CounsellorData.getInstance();
                data.setIssues(issues);
                //Save session
                SessionManager sessionManager = new SessionManager(getContext());
                sessionManager.setIssuesDone(true);
                // Move to next fragment
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CounsellorProfileFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
