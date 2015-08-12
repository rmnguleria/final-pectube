package com.theraiway.login_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.theraiway.database.User;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    public static String SERVER_IP = "10.0.3.2:1337";

    public static User getUserObject(String jsonString){
        User user = new User();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            ArrayList<String> clubs = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("Clubs");
            for(int i=0;i<jsonArray.length();i++){
                clubs.add(jsonArray.getString(i));
            }
            jsonObject = new JSONObject(jsonString);
            user.setUserId(jsonObject.getString("_id"));
            user.setName(jsonObject.getString("Name"));
            user.setSID(jsonObject.getString("SID"));
            user.setHosteller(jsonObject.getBoolean("Hosteller"));
            user.setSex(jsonObject.getString("Sex"));
            user.setClubs(clubs);
            user.setMobileNo(jsonObject.getString("MobileNo"));
            user.setCategory(jsonObject.getString("Category"));
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            DateTime createdOn = fmt.parseDateTime(jsonObject.getString("CreatedOn"));
            user.setCreatedOn(createdOn.toDate());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String jsonString ;
        SharedPreferences mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        jsonString = mySharedPreferences.getString("jsonObject","");
        if(jsonString.isEmpty()){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this,StudentHomeActivity.class);
            try {
                ArrayList<String> clubs = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("Clubs");
                for(int i=0;i<jsonArray.length();i++){
                    clubs.add(jsonArray.getString(i));
                }
                intent.putExtra("Name",jsonObject.getString("Name"));
                intent.putExtra("SID",jsonObject.getString("SID"));
                intent.putExtra("Hosteller",jsonObject.getBoolean("Hosteller"));
                intent.putExtra("Sex",jsonObject.getString("Sex"));
                intent.putExtra("Clubs",clubs);
                intent.putExtra("MobileNo", jsonObject.getString("MobileNo"));
                intent.putExtra("Category",jsonObject.getString("Category"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            finish();
        }
        //JSONObject jsonObject = new JSONObject(jsonString);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
