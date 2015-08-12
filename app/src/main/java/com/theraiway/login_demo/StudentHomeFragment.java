package com.theraiway.login_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.theraiway.database.User;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class StudentHomeFragment extends Fragment {

    Button clubs;

    public StudentHomeFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_student_home, container, false);
        String jsonString ;
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        jsonString = mySharedPreferences.getString("jsonObject","");
        Toast.makeText(getActivity(),jsonString,Toast.LENGTH_LONG).show();
        final User user = MainActivity.getUserObject(jsonString);
        TextView Name = (TextView)rootView.findViewById(R.id.Name);
        Name.setText(user.getName());

        Boolean Hosteller = user.getHosteller();
        //// TODO: 20/6/15 Disable laundry button for non-hostellers

        clubs = (Button)rootView.findViewById(R.id.clubs);
        clubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> clubs = user.getClubs();
                if(clubs.isEmpty()){
                    Intent intent1 = new Intent(getActivity(),ClubListActivity.class);
                    startActivity(intent1);
                }else{
                    Intent intent1 = new Intent(getActivity(),PostsList.class);
                    startActivity(intent1);
                }
            }
        });
        return rootView;
    }
}
