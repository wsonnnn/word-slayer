package com.project.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
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

public class ScrollingActivity extends AppCompatActivity {
    public int know=0;
    public int i=0;
    public int numtsk=0;
    public String []s;
    public CollapsingToolbarLayout ctobar;
    public TextView txview;
    public Button btno;
    public Toolbar toolbar;
    public Button btyes;
    public String[] searchword(String s){
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        final Request request = new Request.Builder()
                .url("http://39.102.62.210/api/getoneword?keyword="+s)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            String raw_result = response.body().string();
            JSONObject jsonObj = new JSONObject(raw_result);
            JSONObject data = jsonObj.getJSONArray("data").getJSONObject(0);
            String translation = data.getString("translation");
            String[] res=new String[5];
            res[0]=data.getString("word");
            res[1]=data.getString("phonetic");
            res[2]=translation.replace("\\n","\n");
            res[3]=data.getString("definition").replace("\\n","\n");
            res[4]=data.getString("exchange");
            return res;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    public void recite(String s,String know){
        final SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        String cookie = sharedPreferences.getString("sessionid","");
        OkHttpClient okHttpClient=new okhttp3.OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
        FormBody formBody = new FormBody.Builder()
                .add("word", s)
                .add("operator",know)
                .build();
        final Request request = new Request.Builder()
                .header("Cookie",cookie)
                .url("http://39.102.62.210/api/recite")//请求的url
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
    public String[] gettask(){
        final SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);
        String cookie = sharedPreferences.getString("sessionid","");
        OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
        final Request request = new Request.Builder()
                .header("Cookie",cookie)
                .url("http://39.102.62.210/api/getonetask?num="+"20")
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        int j=20;
        System.out.println("inget");
        try {
            System.out.println("yes");
            Response response = call.execute();
            String raw_result = response.body().string();
            JSONObject jsonObj = new JSONObject(raw_result);
            JSONArray data = jsonObj.getJSONArray("data");
            System.out.println(data);
            if(data.length()<20){
                j=data.length();
            }
            String[] datastr=new String[j];
            for(int i=0;i<j;i++){
                datastr[i]=data.getJSONObject(i).getString("word");
            }
            return datastr;
        }
        catch (JSONException e)
        {
            System.out.println("np1");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            System.out.println("np2");
            e.printStackTrace();
        }

        return null;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        super.onCreate(savedInstanceState);
        s=gettask();

        setContentView(R.layout.activity_scrolling);
        txview=(TextView) findViewById(R.id.wordtrans);
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        ctobar=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        btno=(Button)findViewById(R.id.button);
        btyes=(Button)findViewById(R.id.button3);
        setSupportActionBar(toolbar);
        if(s==null){
            know=-1;
            btno.setText("BACK");
            btyes.setText("BACK");
            ctobar.setTitle("无计划");
            txview.setText("请上传单词");
        }
        else{
            numtsk = s.length;
            i = 0;
            txview.setText("");
            String p = "no more word";
            if (numtsk > 0) {
                p = s[0];
            }
            know = 0;
            ctobar.setTitle(p);
        }
        btno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(know);
                if(know==-1){
                    Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                    startActivity(intent);
                }
                else if(numtsk==0){
                    ctobar.setTitle("no more word");
                }
                else if(know==0){
                    recite(s[i],"0");
                    know=1;
                    String[] res = searchword(s[i]);
                    String out=res[0]+"\n";
                    if(res[1].length()>0){
                        out+="音标:\n"+res[1]+"\n\n";
                    }
                    if(res[2].length()>0){
                        out+="释义:\n"+res[2]+"\n\n";
                    }
                    if(res[3].length()>0){
                        out+="definition:\n"+res[3]+"\n\n";
                    }
                    if(res[4].length()>0){
                        out+="变换:\n"+res[4]+"\n\n";
                    }
                    String st=s[i];
                    ctobar.setTitle(st);
                    btno.setText("NEXT");
                    btyes.setText("NEXT");
                    txview.setText(out);
                }
                else if(know==1){
                    i+=1;
                    if(i>=numtsk){
                        btno.setText("NEW");
                        btyes.setText("DONE");
                        ctobar.setTitle("NO MORE");
                        txview.setText("计划已完成");
                        know=2;
                    }
                    else {
                        know = 0;
                        btno.setText("NO");
                        btyes.setText("YES");
                        txview.setText("");
                        ctobar.setTitle(s[i]);
                    }
                }
                else{
                    know=0;
                    i=0;
                    s=gettask();
                    numtsk=s.length;
                    btno.setText("NO");
                    btyes.setText("YES");
                    ctobar.setTitle(s[i]);
                    txview.setText("");
                }
            }
        });

        btyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(know==-1){
                    Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                    startActivity(intent);
                }
                else if(numtsk==0){
                    ctobar.setTitle("no more word");
                }
                else if(know==1){
                    i+=1;
                    if(i>=numtsk){
                        btno.setText("NEW");
                        btyes.setText("DONE");
                        ctobar.setTitle("NO MORE");
                        txview.setText("计划已完成");
                        know=2;
                    }
                    else {
                        know = 0;
                        btno.setText("NO");
                        btyes.setText("YES");
                        txview.setText("");
                        ctobar.setTitle(s[i]);
                    }
                }
                else if(know==0){
                    recite(s[i],"1");
                    i+=1;
                    if(i>=numtsk){
                        btno.setText("NEW");
                        btyes.setText("DONE");
                        txview.setText("计划已完成");
                        ctobar.setTitle("NO MORE");
                        know=2;
                    }
                    else {
                        txview.setText("");
                        ctobar.setTitle(s[i]);
                        know=0;
                    }
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),NavigationActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
