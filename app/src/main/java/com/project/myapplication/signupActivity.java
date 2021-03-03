package com.project.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class signupActivity extends AppCompatActivity {

    private EditText etUserName;
    private EditText etPassword;
    private EditText etValidatePassword;
    private Button btnLogin;
    private TextView twSignUp;
    private String username;
    private String password;
    private String validatePassword;
    final String TAG = "test";

    private void request_signup() {
        OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        FormBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        final Request request = new Request.Builder()
                .url("http://39.102.62.210/api/user/signUp")
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                Log.d(TAG, "onResponse: " + res);
                try {
                    final JSONObject js = new JSONObject(res);
                    final String show_message = js.getString("msg");
                    if (js.getInt("code") != 200 || js.getInt("status") != 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), show_message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), show_message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), loginActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
    }

    private void init(){

        btnLogin = findViewById(R.id.signup_button);
        etUserName = findViewById(R.id.signup_username);
        etPassword = findViewById(R.id.signup_password);
        etValidatePassword = findViewById(R.id.signup_password3);
        twSignUp = findViewById(R.id.signup_textView);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUserName.getText().toString();
                password = etPassword.getText().toString();
                validatePassword = etValidatePassword.getText().toString();
                if(password.length()<4)
                {
                    Toast.makeText(signupActivity.this,"您的密码过短，密码最少为4位",Toast.LENGTH_LONG).show();
                    return;
                }
                if(password.equals(validatePassword))
                    request_signup();
                else
                    Toast.makeText(signupActivity.this,"两次输入密码不一致",Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(signupActivity.this,loginActivity.class);
                startActivity(intent);
            }
        });
    }



}
