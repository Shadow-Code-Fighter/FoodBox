package com.example.foodbox.AdminFile.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.foodbox.AdminFile.Admin.A_Dhaba;
import com.example.foodbox.AdminFile.Tiffin.A_Tiffin;
import com.example.foodbox.R;

public class A_HomeFragment extends Fragment {

    CardView dhaba_btn,tiffin_btn,Dhaba,Tiffin;;
    CheckBox acc_switch,acc_switch1;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.a_fragment_home, container, false);

        dhaba_btn = v.findViewById(R.id.Dhaba_btn);
        tiffin_btn = v.findViewById(R.id.Tiffin_btn);
//        acc_switch=v.findViewById(R.id.ac_switch);



        dhaba_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), A_Dhaba.class);
                startActivity(intent);
//                TapTargetView.showFor(this, TapTarget.forView(dhaba_btn, "Button 2", "Click this button").targetRadius(60));


            }
        });

        tiffin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), A_Tiffin.class);
                startActivity(intent);
            }
        });
        return v;

    }
}
