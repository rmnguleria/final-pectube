package com.theraiway.login_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theraiway.database.MyDBHelper;
import com.theraiway.database.Post;
import com.theraiway.database.User;
import com.theraiway.helper.MySingleton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewPost extends ActionBarActivity {

    EditText title;
    EditText content;
    String clubId;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        clubId = intent.getStringExtra("CLUB_ID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        String jsonString;
        SharedPreferences mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        jsonString = mySharedPreferences.getString("jsonObject", "");

        user = MainActivity.getUserObject(jsonString);
    }

    public void createNewPost(View v) {
        title = (EditText) this.findViewById(R.id.post_title);
        content = (EditText) this.findViewById(R.id.Content);

        final String titleString = title.getText().toString();
        final String contentString = content.getText().toString();

        if (titleString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty Title not allowed", Toast.LENGTH_LONG).show();
            return;
        }

        if (contentString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Empty Content not allowed", Toast.LENGTH_LONG).show();
            return;
        }

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getApplicationContext(), "Your internet doesn't seem to be working", Toast.LENGTH_LONG).show();
            return;
        }

        RequestQueue requestQueue = MySingleton.getInstance(this).getRequestQueue();
        String url = "http://" + MainActivity.SERVER_IP + "/posts";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Log.d("NewPost", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Post post = getPost(jsonObject);
                    MyDBHelper dbHelper = MyDBHelper.getInstance(NewPost.this);
                    dbHelper.createNewPost(post);

                    callPostsList();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "CHECK YOUR INTERNET", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("userPostedId", user.getUserId());
                params.put("postTitle", titleString);
                params.put("postContent", contentString);
                params.put("club_id", clubId);
                return params;

            }

            @Override
            protected void deliverResponse(String response) {

                super.deliverResponse(response);
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                Map<String, String> headers = response.headers;

                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(stringRequest);
    }

    private void callPostsList() {
        Intent intent = new Intent(this, PostsList.class);
        startActivity(intent);
    }

    public static Post getPost(JSONObject postObject) {
        Post post = new Post();
        try {
            String postId = postObject.getString("_id");
            post.setPostid(postId);

            String title = postObject.getString("title");
            post.setTitle(title);

            String content = postObject.getString("content");
            post.setContent(content);

            String clubId = postObject.getString("club");
            post.setClub(clubId);

            String createdBy = postObject.getString("createdBy");
            post.setCreatedBy(createdBy);

            post.setEdited(postObject.getBoolean("edited"));

            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            DateTime createdOn = fmt.parseDateTime(postObject.getString("createdOn"));
            post.setCreatedOn(createdOn.toDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
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
