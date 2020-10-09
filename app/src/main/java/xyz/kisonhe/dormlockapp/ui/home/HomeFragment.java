package xyz.kisonhe.dormlockapp.ui.home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import xyz.kisonhe.dormlockapp.MainActivity;
import xyz.kisonhe.dormlockapp.R;

class userInfoClass {
    public String cUserName;
    public String cUserPassword;
    /**
     * cCommand:
     * 0 : Check if auth can pass, without opening
     * 1 : Ask to open door
     */
    public String cCommand;
    public String verifyCode;

}


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    //static boolean onceFlag = false;
    public static final String EXTRA_MESSAGE = "ASK4INTERNET_MESSAGE";
    //
    public SharedPreferences mainSP = null;//= getSharedPreferences("globalSettings", Context.MODE_PRIVATE);
    public Gson mainActGson = null;//= new Gson();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainSP = this.requireActivity().getSharedPreferences("globalSettings", Context.MODE_PRIVATE);
        mainActGson = new Gson();

//        if (getActivity().checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) { //check internet permission
//            Snackbar.make(getActivity().findViewById(R.id.nav_home), R.string.E_NeedNetPermission, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        Button mopenDoorButton = (Button) root.findViewById(R.id.openDoorButton);
        Button msettingsButton = (Button) root.findViewById(R.id.settingsButton);


        mopenDoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userinfoJSON = "";
                if (!mainSP.getBoolean("userInfoIsLegal", false)) {
                    Toast.makeText(getActivity(), R.string.E_LoginNeeded, Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_settings);
                } else {
                    //convert info to string
                    try {
                        userInfoClass tUserInfo = new userInfoClass();
                        tUserInfo.cUserName = mainSP.getString("userName", "");
                        tUserInfo.cUserPassword = mainSP.getString("userPassword", "");
                        tUserInfo.cCommand = "1";
                        int Key = K_AES.GetKey();
                        String FormatedKey = String.format(Locale.US, "%016d", Key);
                        tUserInfo.verifyCode = Base64.encodeToString(FormatedKey.getBytes(), Base64.DEFAULT);
                        userinfoJSON = mainActGson.toJson(tUserInfo);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), R.string.E_Fail2Convert, Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.nav_settings);
                    }
                    //start http request
                    try {
                        // Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(requireActivity());
                        String url = mainSP.getString("serverAddress", "");
                        try {
                            URL tempURL = new URL(url);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), R.string.E_NotAVaildURL, Toast.LENGTH_SHORT).show();
                            NavController navController = Navigation.findNavController(requireView());
                            navController.navigate(R.id.nav_settings);
                        }
                        assert url != null;
                        if (!url.endsWith("?")) {
                            url += "?";
                        }
                        //url+=userinfoJSON;
                        Log.d("JSON:", userinfoJSON);


                        HashMap<String, String> params = new HashMap<>();
                        params.put("info", userinfoJSON);
                        StringBuilder sbParams = new StringBuilder();
                        int i = 0;
                        for (String key : params.keySet()) {
                            try {
                                if (i != 0) {
                                    sbParams.append("&");
                                }
                                sbParams.append(key).append("=")
                                        .append(URLEncoder.encode(params.get(key), "UTF-8"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            i++;
                        }

                        url += sbParams.toString();
                        Log.d("FinalURL", url);

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
//                                        if (response.equals("auth passed")) {
//                                            Toast.makeText(getActivity(), R.string.S_DoorShouldOpen, Toast.LENGTH_SHORT).show();
//                                        }
                                        if (response.contains("auth passed")) {
                                            Toast.makeText(getActivity(), R.string.S_DoorShouldOpen, Toast.LENGTH_SHORT).show();
                                            try {
                                                MainActivity.BatteryLevel = Integer.parseInt(response.replace("auth passed ", ""));
                                            } catch (Exception e) {
                                                Toast.makeText(getActivity(), R.string.E_Fail2GetBatyInfo, Toast.LENGTH_SHORT).show();
                                                MainActivity.BatteryLevel = -1;
                                            }
                                            updateBatteryImg(root);
                                        }
                                        Log.d("log", "Response is: " + response);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                    Toast.makeText(getActivity(), R.string.E_ServerNoResponse, Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireView());
                                    navController.navigate(R.id.nav_settings);
                                } else if (error instanceof AuthFailureError) {
                                    Toast.makeText(getActivity(), R.string.E_WrongPasswordOrUA, Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireView());
                                    navController.navigate(R.id.nav_settings);
                                }
                                Log.d("log", "Something is fucked");
                                Log.d("Reason", error.toString());
                            }
                        });

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);


                    } catch (Exception e) {
                        Toast.makeText(getActivity(), R.string.E_Fail2ConvertRequest, Toast.LENGTH_SHORT).show();
                        NavController navController = Navigation.findNavController(requireView());
                        navController.navigate(R.id.nav_settings);
                    }
                }
            }
        });

        //todo need to fix this shortcut,this is probably needed to be put into mainactivity.java
        Intent checkShortCut = this.getActivity().getIntent();
        String message = checkShortCut.getStringExtra("shortCutOpenDoorEXTRA");
        if (message != null) {
            if (message.equals("value")) {
                mopenDoorButton.performClick();
            }
        }


        msettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * @note Navigation is very good because it hleps to manage the fragment stuff
                 * so we don't have to do the transaction stuff
                 * https://juejin.im/entry/6844903613727260685
                 * https://codelabs.developers.google.com/codelabs/android-navigation/#0
                 * https://stackoverflow.com/questions/51085222/navigate-to-fragment-on-fab-click-navigation-architecture-components
                 *
                 */
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.nav_settings);

                /**
                 * @breif ok this note is no longer a working stuff because we find that Navigation is good
                 * https://juejin.im/entry/6844903613727260685
                 * as google says in their docs,
                 * 由于该 Fragment 是在运行时被添加到 FrameLayout 容器的，而不是利用 <fragment> 元素在 Activity 布局中进行定义的，因此可以从 Activity 中移除该 Fragment，并将其替换为其他 Fragment。
                 * therefore using transaction will change xml which i actually don't want to do
                 * so lets just perform a simple click, lol
                 */

            }
        });

        updateBatteryImg(root);

//        ImageView battImg = root.findViewById(R.id.batteryImg);
//        battImg.setImageResource(R.drawable.battery00);

        return root;
    }

    private int updateBatteryImg(View view) {
        ImageView battImg = view.findViewById(R.id.batteryImg);
        TextView BatteryTextView = view.findViewById(R.id.BatteryTextView);
        //MainActivity.BatteryLevel = 100;
        switch (MainActivity.BatteryLevel) {

            case 100:
                battImg.setImageResource(R.drawable.battery100);
                BatteryTextView.setText(R.string.Battery100TextViewStr);
                break;
            case 75:
                battImg.setImageResource(R.drawable.battery75);
                BatteryTextView.setText(R.string.Battery75TextViewStr);
                break;
            case 50:
                battImg.setImageResource(R.drawable.battery50);
                BatteryTextView.setText(R.string.Battery50TextViewStr);
                break;
            case 25:
                battImg.setImageResource(R.drawable.battery25);
                BatteryTextView.setText(R.string.Battery25TextViewStr);
                break;
            case 0:
                battImg.setImageResource(R.drawable.battery00);
                BatteryTextView.setText(R.string.Battery00TextViewStr);
                break;

            default:
            case -1:
                battImg.setImageResource(R.drawable.ic_battery_unknown);
                BatteryTextView.setText(R.string.BatteryTextViewDefaultString);
                break;


        }
        return 0;
    }

}