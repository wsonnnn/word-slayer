package com.project.myapplication.ui.recite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.project.myapplication.R;
import com.project.myapplication.ScrollingActivity;
import com.project.myapplication.loginActivity;

public class ReciteFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recite, container, false);
        final Button button = root.findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(getActivity(), ScrollingActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }
}
