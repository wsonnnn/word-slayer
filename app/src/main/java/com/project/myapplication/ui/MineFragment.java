package com.project.myapplication.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.myapplication.NavigationActivity;
import com.project.myapplication.R;
import com.project.myapplication.ScrollingActivity;
import com.project.myapplication.loginActivity;

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

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MineFragment extends Fragment {
    final String TAG = "debug";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mine, container, false);
        final Button button = root.findViewById(R.id.button4);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login",MODE_PRIVATE);
                String cookie = sharedPreferences.getString("sessionid","");
                OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS).build();
                final Request request = new Request.Builder()
                        .url("http://39.102.62.210/api/user/logout")
                        .header("Cookie",cookie)
                        .get()
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
                            final int status = js.getInt("status");
                            System.out.println(show_message);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(status==1)
                                    {
                                        SharedPreferences.Editor edit = sharedPreferences.edit();//编辑文件
                                        edit.clear();
                                        edit.apply();
                                        Intent intent = new Intent(getActivity(),loginActivity.class);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(),show_message,Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        return root;
    }
}

