package com.example.mcpro.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mcpro.R;
import com.example.mcpro.utils.ApiManager;
import com.example.mcpro.utils.CounsellorData;
import com.example.mcpro.utils.SessionManager;

public class CounsellorProfileFragment extends Fragment {

    public CounsellorProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_counsellor_profile, container, false);

        EditText bioText = view.findViewById(R.id.bio);
        Button saveProfile = view.findViewById(R.id.saveProfile);

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bioS = bioText.getText().toString();
                if (bioS.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill your bio", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Save to CounsellorData Singleton
                CounsellorData data = CounsellorData.getInstance();
                SessionManager sessionManager = new SessionManager(requireContext());
                data.setBio(bioS);
                //data.setAvatar(avatar);

                // ✅ Make API call
                //ApiManager.submitCounsellorData(requireActivity());

            }
        });



        return view;
    }

}
