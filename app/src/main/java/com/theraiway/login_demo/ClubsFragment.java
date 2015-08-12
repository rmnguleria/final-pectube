package com.theraiway.login_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theraiway.database.Club;
import com.theraiway.helper.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ClubsFragment extends Fragment {

    ArrayAdapter<String> arrayAdapter;
    List<Club> clubs ;
    ArrayList<String> clubsName ;

    public ClubsFragment() {
        clubs = new ArrayList<>();
        clubsName = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_club_list, container, false);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_club);

        RequestQueue requestQueue = MySingleton.getInstance(getActivity()).getRequestQueue();

        String urlString = "http://"+MainActivity.SERVER_IP+"/clubs/fetchInfo";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray clubsJsonArray = new JSONArray(response);
                    for(int i=0;i<clubsJsonArray.length();i++){
                        JSONObject jsonObject = new JSONObject(clubsJsonArray.get(i).toString());
                        Club club = new Club();
                        club.set_id(jsonObject.getString("_id"));
                        club.setName(jsonObject.getString("clubName"));
                        club.setDescription(jsonObject.getString("description"));
                        JSONArray activities = jsonObject.getJSONArray("activities");
                        ArrayList<String> activitiesList = new ArrayList<>();
                        for(int j=0;j<activities.length();j++){
                            activitiesList.add(activities.get(j).toString());
                        }
                        club.setActivities(activitiesList);

                        JSONArray achievements = jsonObject.getJSONArray("achievements");
                        ArrayList<String> achievementsList = new ArrayList<>();
                        for(int j=0;j<achievements.length();j++){
                            achievementsList.add(achievements.get(j).toString());
                        }
                        club.setAchievements(achievementsList);
                        clubs.add(club);
                        clubsName.add(club.getName());
                    }
                    arrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(),volleyError.toString(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(stringRequest);

        arrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item_club, R.id.list_item_club_textview,clubsName);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),ClubDetailActivity.class);
                intent.putExtra("CLUB_ID",clubs.get(i).get_id());
                intent.putExtra("CLUB_NAME",clubs.get(i).getName());
                intent.putExtra("CLUB_DESCRIPTION", clubs.get(i).getDescription());
                intent.putStringArrayListExtra("CLUB_ACTIVITIES", clubs.get(i).getActivities());
                intent.putStringArrayListExtra("CLUB_ACHIEVEMENTS",clubs.get(i).getAchievements());
                intent.putStringArrayListExtra("CLUB_HEADS",clubs.get(i).getHeads());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
