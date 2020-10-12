package xyz.kisonhe.dormlockapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

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

import xyz.kisonhe.dormlockapp.ui.home.K_AES;


class CP_info {
    public String cUserName = "";
    public String cUserPassword = "";
    public String NewPassword = "";
    public String verifyCode = "";

}


public class ChangePasswordActivity extends AppCompatActivity {
    static public final String FRAG_TO_START_MESSAGE = "xyz.kisonhe.dormlockapp.FRAG_TO_START_MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //final SharedPreferences CP_SP = getSharedPreferences("globalSettings", Context.MODE_PRIVATE);
        final SharedPreferences CP_SP = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor CP_SP_editor = CP_SP.edit();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final EditText mCP_UserName = findViewById(R.id.CP_EnterUserName);
        final EditText mCP_OldPassword = findViewById(R.id.CP_EnterOldPassword);
        final EditText mCP_NewPassword = findViewById(R.id.CP_EnterNewPassword);
        final EditText mCP_ConfirmPassword = findViewById(R.id.CP_ConfirmPassword);

        Button mCP_ChangePasswordButton = findViewById(R.id.CP_ChangePasswordButton);

        mCP_ChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                final Gson CPActGson = new Gson();
                String CPinfoJSON = "";
                //(A & 0xff) << 24 | (R & 0xff) << 16 | (G & 0xff) << 8 | (B & 0xff);
                //mCP_UserName.setBackgroundColor(Color.parseColor("#00ffff"));
                int NormalBGC = ContextCompat.getColor(ChangePasswordActivity.this, R.color.activityBGC);
                int warningRed = ContextCompat.getColor(ChangePasswordActivity.this, R.color.warningRed);
                if (!TextUtils.isEmpty(mCP_UserName.getText().toString())) {
                    mCP_UserName.setBackgroundColor(NormalBGC);
                    if (!TextUtils.isEmpty(mCP_OldPassword.getText().toString())) {
                        mCP_OldPassword.setBackgroundColor(NormalBGC);
                        if (!TextUtils.isEmpty(mCP_NewPassword.getText().toString())) {
                            mCP_NewPassword.setBackgroundColor(NormalBGC);
                            if (!TextUtils.isEmpty(mCP_ConfirmPassword.getText().toString())) {
                                if (mCP_ConfirmPassword.getText().toString().equals(mCP_NewPassword.getText().toString())) {
                                    mCP_ConfirmPassword.setBackgroundColor(NormalBGC);

                                    try {
                                        CP_info CPInfo = new CP_info();
                                        CPInfo.cUserName = mCP_UserName.getText().toString();
                                        CPInfo.cUserPassword = mCP_OldPassword.getText().toString();
                                        CPInfo.NewPassword = mCP_NewPassword.getText().toString();
                                        int Key = K_AES.GetKey();
                                        String FormatedKey = String.format(Locale.US, "%016d", Key);
                                        CPInfo.verifyCode = Base64.encodeToString(FormatedKey.getBytes(), Base64.DEFAULT);
                                        CPinfoJSON = CPActGson.toJson(CPInfo);
                                    } catch (Exception e) {
                                        Toast.makeText(ChangePasswordActivity.this, R.string.E_Fail2Convert, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                        intent.putExtra(FRAG_TO_START_MESSAGE, "Settings");
                                        startActivity(intent);
                                    }


                                    //Server Requests and other things
                                    try {
                                        // Instantiate the RequestQueue.
                                        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
                                        String url = CP_SP.getString("serverAddress", "");
                                        try {
                                            URL tempURL = new URL(url);
                                        } catch (Exception e) {
                                            Toast.makeText(ChangePasswordActivity.this, R.string.E_NotAVaildURL, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                            intent.putExtra(FRAG_TO_START_MESSAGE, "Settings");
                                            startActivity(intent);
                                        }
                                        assert url != null;
                                        url += "/changepassword";//nodemcu should handle this in another function
                                        if (!url.endsWith("?")) {
                                            url += "?";
                                        }


                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("cp", CPinfoJSON);
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
                                                        if (response.equals("password changed")) {
                                                            Toast.makeText(ChangePasswordActivity.this, R.string.S_PasswordChangedAndSaved, Toast.LENGTH_SHORT).show();
                                                            CP_SP_editor.putString("userName", mCP_UserName.getText().toString());
                                                            CP_SP_editor.putString("userPassword", mCP_NewPassword.getText().toString());
                                                            CP_SP_editor.apply();
                                                        }
                                                        Log.d("log", "Response is: " + response);
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                                    Toast.makeText(ChangePasswordActivity.this, R.string.E_ServerNoResponse, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                                    intent.putExtra(FRAG_TO_START_MESSAGE, "Settings");
                                                    startActivity(intent);
                                                } else if (error instanceof AuthFailureError) {
                                                    Toast.makeText(ChangePasswordActivity.this, R.string.E_WrongPasswordOrUA, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                                                    intent.putExtra(FRAG_TO_START_MESSAGE, "Settings");
                                                    startActivity(intent);

                                                }
                                                Log.d("log", "Something is fucked");
                                                Log.d("Reason", error.toString());
                                            }
                                        });

                                        // Add the request to the RequestQueue.
                                        queue.add(stringRequest);


                                    } catch (Exception e) {
                                        Toast.makeText(ChangePasswordActivity.this, R.string.E_Fail2ConvertRequest, Toast.LENGTH_SHORT).show();
                                        //Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                                        //startActivity(intent);
                                    }


                                } else {
                                    mCP_NewPassword.setBackgroundColor(warningRed);
                                    mCP_ConfirmPassword.setBackgroundColor(warningRed);
                                    Toast.makeText(ChangePasswordActivity.this, R.string.E_PasswordNoMatch, Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                mCP_ConfirmPassword.setBackgroundColor(warningRed);
                            }

                        } else {
                            mCP_NewPassword.setBackgroundColor(warningRed);
                        }

                    } else {
                        mCP_OldPassword.setBackgroundColor(warningRed);
                    }

                } else {
                    mCP_UserName.setBackgroundColor(warningRed);
                }
            }
        });

    }
}

