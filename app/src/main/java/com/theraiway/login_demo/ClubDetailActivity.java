package com.theraiway.login_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theraiway.database.User;
import com.theraiway.helper.MySingleton;

import java.util.ArrayList;

public class ClubDetailActivity extends ActionBarActivity {

    String jsonString ;
    TextView title ;
    ImageView imageIcon;
    TextView description ;
    TextView activities ;
    TextView achievements ;
    TextView heads ;
    String club_id;
    SharedPreferences mySharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_detail);

        jsonString = mySharedPreferences.getString("jsonObject","");


        title = (TextView) findViewById(R.id.post_title);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        description = (TextView) findViewById(R.id.description);
        activities = (TextView) findViewById(R.id.activities);
        achievements = (TextView) findViewById(R.id.achievements);
        heads = (TextView) findViewById(R.id.heads);

        Intent intent = getIntent();

        title.setText(intent.getStringExtra("CLUB_NAME"));
        title.setTextColor(Color.MAGENTA);
        imageIcon.setImageResource(intent.getIntExtra("CLUB_IMAGE", R.drawable.ic_launcher));
        description.setText(intent.getStringExtra("CLUB_DESCRIPTION"));
        description.setTextColor(Color.GREEN);
        activities.setText(getConcatenatedList(intent.getStringArrayListExtra("CLUB_ACTIVITIES")));
        activities.setTextColor(Color.RED);
        achievements.setText(getConcatenatedList(intent.getStringArrayListExtra("CLUB_ACHIEVEMENTS")));
        achievements.setTextColor(Color.BLUE);
        heads.setText(getConcatenatedList(intent.getStringArrayListExtra("CLUB_HEADS")));

        club_id = intent.getStringExtra("CLUB_ID");

    }

    private String getConcatenatedList(ArrayList<String> arrayList) {
        StringBuilder stringBuilder = new StringBuilder("");
        for(String string : arrayList){
            stringBuilder.append(string + "\n");
        }
        return stringBuilder.toString();
    }

    public void joinClub(View v){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Toast.makeText(this, "Your internet doesn't seem to be working", Toast.LENGTH_LONG).show();
            return;
        }

        User user = MainActivity.getUserObject(jsonString);

        String urlString = "http://"+MainActivity.SERVER_IP+"/users/"+user.getUserId()+"/joinclub/"+club_id;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.remove("jsonObject");
                editor.putString("jsonObject",response);
                editor.apply();
                Log.v("ClubDetail","Response  " + response);
                Log.v("clubDetail","sharedPredereneces " + mySharedPreferences.getString("jsonObject",""));
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                callPostsList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        RequestQueue requestQueue = MySingleton.getInstance(this).getRequestQueue();
        requestQueue.add(stringRequest);
    }

    private void callPostsList() {
        Intent intent = new Intent(this,PostsList.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_club_detail, menu);
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
