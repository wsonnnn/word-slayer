package com.project.myapplication.ui.upload;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.myapplication.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class UploadFragment extends Fragment {

    private Button up;
    private EditText txt;
    String getstr;
    public void postup(String s){
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login",MODE_PRIVATE);
        String cookie = sharedPreferences.getString("sessionid","");
        OkHttpClient okHttpClient=new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        FormBody formBody = new FormBody.Builder()
                .add("text", s)
                .build();
        final Request request = new Request.Builder()
                .header("Cookie",cookie)
                .url("http://39.102.62.210/api/upload")//请求的url
                .post(formBody)
                .build();
        //创建/Call
        Call call = okHttpClient.newCall(request);
        //加入队列 异步操作
        call.enqueue(new Callback() {
            //请求错误回调方法
            public void onFailure(Call call, IOException e) {
                System.out.println("连接失败");
            }
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200) {
                    System.out.println(response.body().string());
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        up=root.findViewById(R.id.upload_button);
        txt=root.findViewById(R.id.word);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getstr=txt.getText().toString();
                //testHttpUrlCon();
                postup(getstr);
                Toast.makeText(getActivity(),"上传成功",Toast.LENGTH_LONG).show();
            }
        });
        txt.addTextChangedListener(new TextWatcher() {
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
        return root;
    }
}
