package com.theraiway.login_demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theraiway.database.MyDBHelper;
import com.theraiway.database.Post;
import com.theraiway.database.PostComment;
import com.theraiway.helper.MySingleton;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class PostsListFragment extends Fragment {
    long timeStamp = 0;
    ArrayList<String> clubs;
    ArrayAdapter<String> arrayAdapter;
    List<Post> posts;
    List<String> postTitles;

    public PostsListFragment() {
        clubs = new ArrayList<>();
        posts = new ArrayList<>();
        postTitles = new ArrayList<>();
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_posts_list, container, false);

        String jsonString;
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        jsonString = mySharedPreferences.getString("jsonObject", "");

        Log.v("jsonString", jsonString);

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray clubsJsonArray = jsonObject.getJSONArray("Clubs");
            for (int i = 0; i < clubsJsonArray.length(); i++) {
                clubs.add(clubsJsonArray.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button newPost = (Button) rootView.findViewById(R.id.createPost);

        Log.v("PostsList", newPost.toString() + R.id.createPost);

        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewPost.class);
                intent.putExtra("CLUB_ID", clubs.get(0));
                startActivity(intent);
            }
        });

        MyDBHelper dbHelper = MyDBHelper.getInstance(getActivity());
        try {
            posts = dbHelper.getPosts();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("PostsListFragment", "posts list fetched. Size = " + posts.size());
        for (Post post : posts) {
            postTitles.add(post.getTitle());
            Date createdOn = post.getCreatedOn();
            timeStamp = createdOn.getTime();
        }

        final ListView postListView = (ListView) rootView.findViewById(R.id.listview_posts);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_post,
                R.id.list_item_post_textview, postTitles);

        postListView.setAdapter(arrayAdapter);

        String clubId = clubs.get(0);

        String urlString = "http://" + MainActivity.SERVER_IP + "/posts/newPosts/club/" + clubId + "/timestamp/" + timeStamp;

        RequestQueue requestQueue = MySingleton.getInstance(getActivity()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.v("response",response);
                try {
                    JSONArray postsArray = new JSONArray(response);
                    for (int i = 0; i < postsArray.length(); i++) {

                        JSONObject postObject = postsArray.getJSONObject(i);

                        Post post = NewPost.getPost(postObject);

                        if (post.getCreatedOn().getTime() > timeStamp) {
                            timeStamp = post.getCreatedOn().getTime();
                        }
                        posts.add(post);
                        postTitles.add(post.getTitle());
                        arrayAdapter.notifyDataSetChanged();
                        //// TODO: 21/6/15 Add this data to Database -> Done

                        JSONArray commentsArray = postObject.getJSONArray("comments");
                        ArrayList<PostComment> comments = new ArrayList<>();
                        for (int j = 0; j < commentsArray.length(); j++) {
                            PostComment comment = new PostComment();
                            JSONObject commentObject = commentsArray.getJSONObject(j);
                            comment.setPostId(post.getPostid());
                            comment.setCreatedBy(commentObject.getString("createdBy"));
                            comment.setText(commentObject.getString("text"));
                            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                            DateTime createdOn = fmt.parseDateTime(commentObject.getString("posted"));
                            comment.setCreatedOn(createdOn.toDate());
                            comments.add(comment);
                        }

                        MyDBHelper myDBHelper = MyDBHelper.getInstance(getActivity());
                        myDBHelper.createNewPost(post);
                        myDBHelper.saveComments(comments);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(stringRequest);


        postListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), PostActivity.class);

                Post post = posts.get(i);

                intent.putExtra("postId", post.getPostid());
                intent.putExtra("title", post.getTitle());
                intent.putExtra("content", post.getContent());
                intent.putExtra("clubId", post.getClub());

                startActivity(intent);
            }
        });

        return rootView;
    }
}
