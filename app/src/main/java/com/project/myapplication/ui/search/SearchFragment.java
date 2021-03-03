package com.project.myapplication.ui.search;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment {


    private Button bsearch;
    private EditText myword;
    private TextView mtv;
    String getstr;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        super.onCreate(savedInstanceState);

        mtv = root.findViewById(R.id.wordres);
        bsearch =  root.findViewById(R.id.btn_search);
        bsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getstr = myword.getText().toString();
                int flag=0;
                String[] res = searchword(getstr);
                String out="";
                if(res!=null) {
                    flag=1;
                    out = res[0] + "\n";
                    if (res[1].length() > 0) {
                        out += "音标\n" + res[1] + "\n";
                    }
                    if (res[2].length() > 0) {
                        out += "释义\n" + res[2] + "\n";
                    }
                    if (res[3].length() > 0) {
                        out += "definition:\n" + res[3] + "\n";
                    }
                    System.out.println(out);
                }
                mtv.setText("未找到");
                if(flag==1&&res[0].length()>0)
                    mtv.setText(out);
                Toast.makeText(getActivity(), "输入成功", Toast.LENGTH_LONG).show();
            }
        });
        myword =  root.findViewById(R.id.word);
        myword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                android.util.Log.d("edittext", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return root;
    }


}
