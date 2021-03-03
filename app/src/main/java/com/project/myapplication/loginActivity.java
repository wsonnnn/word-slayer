package com.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class loginActivity extends AppCompatActivity {

    private EditText etUserName;
    private EditText etPassword;
    private Button btnLogin;
    private TextView twSignUp;
    private String username;
    private String password;
    final String TAG = "debug";
    private void request_login() {
        OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        final Request request = new Request.Builder()
                .url("http://39.102.62.210/api/user/signIn")
                .post(formBody)
                .build();
        final StringBuilder sb = new StringBuilder();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                final Headers headers =response.headers();
                try {
                    final JSONObject js = new JSONObject(res);
                    final String show_message = js.getString("msg");
                    if(js.getInt("code")!=200||js.getInt("status")!=1)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                    Toast.makeText(getApplicationContext(), show_message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), show_message, Toast.LENGTH_SHORT).show();
                                for(int i = 0;i<headers.size();i++)
                                {
                                    System.out.println(headers.value(i));
                                }
                                List<String> cookies = headers.values("Set-Cookie");
                                String session = cookies.get(0);
                                String sessionid = session.substring(0,session.indexOf(";"));
                                SharedPreferences share = getSharedPreferences("login",MODE_PRIVATE);
                                SharedPreferences.Editor edit = share.edit();//编辑文件
                                edit.putString("sessionid",sessionid);
                                edit.apply();
                                Log.d(TAG, "onResponse: " + res);
                                Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init(){

        btnLogin = findViewById(R.id.login_button);
        etUserName = findViewById(R.id.login_username);
        etPassword = findViewById(R.id.login_password);
        twSignUp = findViewById(R.id.login_textView);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUserName.getText().toString();
                password = etPassword.getText().toString();
                request_login();
            }
        });

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                android.util.Log.d("edittext",s.toString());
            }
        });

        twSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this,signupActivity.class);
                startActivity(intent);
            }
        });
    }
}