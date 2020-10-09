package xyz.kisonhe.dormlockapp.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import xyz.kisonhe.dormlockapp.ChangePasswordActivity;
import xyz.kisonhe.dormlockapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("globalSettings", Context.MODE_PRIVATE);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        View root = inflater.inflate(R.layout.fragment_settings, container, false);


        Button msaveSettingsButton = root.findViewById(R.id.saveSettingsButton);
        Button mChangePasswordButton = root.findViewById(R.id.ChangePasswordButton);
        final EditText mserverAddress = root.findViewById(R.id.serverAddress);
        final EditText muserName = root.findViewById(R.id.userName);
        final EditText muserPassword = root.findViewById(R.id.userPassword);

        if (sharedPreferences.getBoolean("userInfoIsLegal", false)) {
            mserverAddress.setText(sharedPreferences.getString("serverAddress", ""));
            muserName.setText(sharedPreferences.getString("userName", ""));
            muserPassword.setText(sharedPreferences.getString("userPassword", ""));
        }


        msaveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    editor.putString("userName", muserName.getText().toString());
                    editor.putString("userPassword", muserPassword.getText().toString());
                    editor.putString("serverAddress", mserverAddress.getText().toString());
                    editor.putBoolean("userInfoIsLegal", true);
                    editor.apply();

                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.E_Fail2WriteSP, Toast.LENGTH_SHORT).show();
                }


            }
        });

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }
}