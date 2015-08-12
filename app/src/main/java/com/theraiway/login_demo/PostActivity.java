package com.theraiway.login_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theraiway.database.MyDBHelper;
import com.theraiway.database.PostComment;
import com.theraiway.database.User;
import com.theraiway.helper.MySingleton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PostActivity extends ActionBarActivity {

    TextView title;
    TextView content;
    LinearLayout commentsLayout;
    EditText comment;
    long timestamp = 0;
    StringRequest stringRequest;
    String postId ;
    String userId;
    Timer timer;
    SharedPreferences mySharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        String jsonString;
        mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        jsonString = mySharedPreferences.getString("jsonObject", "");

        User user = MainActivity.getUserObject(jsonString);
        userId = user.getUserId();

        title = (TextView) this.findViewById(R.id.post_title);
        content = (TextView) this.findViewById(R.id.content);
        commentsLayout = (LinearLayout) this.findViewById(R.id.comments);

        final Intent intent = getIntent();
        String titleText = intent.getStringExtra("title");
        Log.v("titleText",titleText);
        title.setText(titleText);
        content.setText(intent.getStringExtra("content"));

        postId = intent.getStringExtra("postId");

        final MyDBHelper myDBHelper = MyDBHelper.getInstance(this);
        try {
            List<PostComment> commentList = myDBHelper.getComments(postId);
            for (PostComment comment : commentList) {

                addComment(comment.getText());

                if (comment.getCreatedOn().getTime() > timestamp) {
                    timestamp = comment.getCreatedOn().getTime();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }




        timer = new Timer();
        PrivateTask privateTask = new PrivateTask();
        timer.scheduleAtFixedRate(privateTask,0,10000);


    }

    public void createNewComment(View v){
        comment = (EditText) this.findViewById(R.id.textComment);
        final String commentText = comment.getText().toString();
        comment.setText("");

        String urlString = "http://"+MainActivity.SERVER_IP+"/posts/"+postId+"/addcomment/"+userId;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.v("onResponse",s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("text",commentText);
                return params;
            }

            @Override
            public Priority getPriority() {
                return Priority.HIGH;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void addComment(String text) {
        TextView commentText = new TextView(PostActivity.this);
        commentText.setText(text);
        commentText.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        commentText.setTextColor(Color.MAGENTA);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        layoutParams.setMargins(px*2,px,px,px);

        commentsLayout.addView(commentText,layoutParams);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
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

    @Override
    protected void onStop() {
        super.onStop();

        MySingleton.getInstance(this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });

        timer.cancel();
    }

    public class PrivateTask extends TimerTask{

        @Override
        public void run() {
            String urlString = "http://" + MainActivity.SERVER_IP + "/posts/" + postId + "/fetchcomments/" + timestamp;

            Log.v("timestamp value",String.valueOf(timestamp));

            stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ArrayList<PostComment> comments = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            PostComment postComment = new PostComment();
                            JSONObject comment = jsonArray.getJSONObject(i);
                            postComment.setPostId(postId);
                            postComment.setText(comment.getString("text"));
                            postComment.setCreatedBy(comment.getString("createdBy"));

                            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                            DateTime createdOn = fmt.parseDateTime(comment.getString("posted"));
                            postComment.setCreatedOn(createdOn.toDate());
                            comments.add(postComment);
                            addComment(comment.getString("text"));

                            if(postComment.getCreatedOn().getTime() > timestamp){
                                timestamp = postComment.getCreatedOn().getTime();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(!comments.isEmpty())
                        MyDBHelper.getInstance(PostActivity.this).saveComments(comments);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            });
            MySingleton.getInstance(PostActivity.this).addToRequestQueue(stringRequest);
        }
    }
}
