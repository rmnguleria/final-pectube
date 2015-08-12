package com.theraiway.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;


public class MyDBHelper extends SQLiteOpenHelper {

    private static MyDBHelper myDBHelper = null;
    private static final String DB_NAME = "app.db";
    private static final int DB_VERSION = 1;
    private static final String COMMENT_TABLE_NAME = "comments";
    private static final String POST_TABLE_NAME = "posts";
    private static final String CLUB_TABLE_NAME = "clubs";
    private Context context;
    private SQLiteDatabase db;

    private MyDBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
        this.context = context;
    }

    public static MyDBHelper getInstance(Context ctx){
        if(myDBHelper == null){
            myDBHelper = new MyDBHelper(ctx.getApplicationContext());
        }
        return myDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CLUB_TB = "CREATE TABLE " + CLUB_TABLE_NAME +
                " (\"_id\" TEXT PRIMARY KEY NOT NULL  UNIQUE , " +
                "\"name\" TEXT NOT NULL  UNIQUE , " +
                "\"description\" TEXT, " +
                "\"activities\" TEXT, " +
                "\"achievements\" TEXT, " +
                "\"heads\" TEXT)";

        final String POST_TB = "CREATE TABLE \""+ POST_TABLE_NAME +
                "\" (\"createdBy\" TEXT NOT NULL , " +
                "\"createdOn\" DATETIME NOT NULL , " +
                "\"title\" TEXT NOT NULL , " +
                "\"content\" TEXT NOT NULL , " +
                "\"club\" TEXT NOT NULL , " +
                "\"edited\" BOOL NOT NULL  DEFAULT false, " +
                "\"postid\" TEXT PRIMARY KEY  NOT NULL  UNIQUE )";

        final String COMMENT_TB = "CREATE TABLE " + COMMENT_TABLE_NAME + "(" +
                " text TEXT NOT NULL," +
                " createdBy TEXT NOT NULL," +
                " posted DATETIME NOT NULL," +
                " postid TEXT NOT NULL," +
                " FOREIGN KEY(postid) REFERENCES posts(postid)," +
                " PRIMARY KEY(postid,posted)" +
                ")";

        sqLiteDatabase.execSQL(CLUB_TB);
        sqLiteDatabase.execSQL(POST_TB);
        sqLiteDatabase.execSQL(COMMENT_TB);
    }

    //Open the database
    public boolean open() {
        try {
            db = myDBHelper.getWritableDatabase();
            return true;
        } catch(SQLException sql) {
            db = null;
            return false;
        }
    }

    @Override
    public synchronized void close() {
        if(db != null)
            db.close();
        super.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int previousVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CLUB_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + POST_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + COMMENT_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public List<PostComment> getComments(String postId) throws ParseException{
        List<PostComment> comments = new ArrayList<>();
        String query = "SELECT * from " + COMMENT_TABLE_NAME + " where postid = \"" + postId + "\"";
        boolean openDB = open();
        if(openDB){
            Log.d("getComments", "Database Opened");
            Cursor cursor = db.rawQuery(query,null);
            Log.d("MyDbHelper","Cursor rows :- " + cursor.getCount());
            if(cursor.moveToFirst()){
                do{
                    PostComment comment = new PostComment();
                    comment.setPostId(postId);
                    comment.setText(cursor.getString(0));
                    comment.setCreatedBy(cursor.getString(1));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    comment.setCreatedOn(dateFormat.parse(cursor.getString(2)));
                    Log.d("Comment Object", comments.toString());
                    comments.add(comment);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }else{
            Log.e("getComments","Database could not be opened");
            return null;
        }
        return comments;
    }

    public void saveComments(ArrayList<PostComment> comments){
        boolean openDB = open();
        if(openDB){
            for(PostComment comment:comments){
                ContentValues contentValues = new ContentValues();
                contentValues.put("postid",comment.getPostId());
                contentValues.put("createdBy",comment.getCreatedBy());
                contentValues.put("text",comment.getText());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                contentValues.put("posted",dateFormat.format(comment.getCreatedOn()));

                db.insert("comments",null,contentValues);
            }
        }else{
            Log.e("MyDBHelper","Could not open database");
        }
    }

    public void createNewPost(Post post){
        ContentValues contentValues = new ContentValues();

        contentValues.put("postid",post.getPostid());
        contentValues.put("club",post.getClub());
        contentValues.put("title",post.getTitle());
        contentValues.put("content",post.getContent());
        contentValues.put("createdBy",post.getCreatedBy());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        contentValues.put("createdOn",dateFormat.format(post.getCreatedOn()));
        contentValues.put("edited",post.getEdited());

        boolean openDB = open();
        if(openDB){
            long val = db.insert("posts",null,contentValues);
        }else{
            Log.e("MyDBHelper","Could not open Database");
        }
    }

    public List<Post> getPosts() throws ParseException{
        List<Post> posts = new ArrayList<>();
        String query = "SELECT * from " + POST_TABLE_NAME ;
        boolean openDB = open();
        if(openDB){
            Log.d("getPosts", "Database Opened");
            Cursor cursor = db.rawQuery(query,null);
            Log.d("MyDbHelper","Cursor rows :- " + cursor.getCount());
            if(cursor.moveToFirst()){
                do{
                    Post post = new Post();
                    post.setPostid(cursor.getString(6));
                    post.setTitle(cursor.getString(2));
                    post.setContent(cursor.getString(3));
                    post.setClub(cursor.getString(4));
                    post.setCreatedBy(cursor.getString(0));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    post.setCreatedOn(dateFormat.parse(cursor.getString(1)));
                    post.setEdited(Boolean.parseBoolean(cursor.getString(5)));
                    Log.d("Post Object", post.toString());
                    posts.add(post);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }else{
            Log.e("getPosts","Database could not be opened");
            return null;
        }
        return posts;
    }

    public List<Club> getClubs(){
        List<Club> clubs = new ArrayList<>();
            String query = "SELECT * from " + CLUB_TABLE_NAME;
            boolean openDB = open();
            if(openDB){
                Log.d("getClubs", "Database Opened");
                Cursor cursor = db.rawQuery(query,null);
                Log.d("MyDbHelper","Cursor rows :- " + cursor.getCount());
                if(cursor.moveToFirst()){
                    do{
                        Club club = new Club();
                        club.set_id(cursor.getString(0));
                        club.setName(cursor.getString(1));
                        club.setDescription(cursor.getString(2));
                        club.setActivities(getList(cursor.getString(3)));
                        club.setAchievements(getList(cursor.getString(4)));
                        club.setHeads(getList(cursor.getString(5)));
                        Log.d("Club Object", club.toString());
                        clubs.add(club);
                    }while (cursor.moveToNext());
                }
                cursor.close();
            }else{
                Log.e("getClubs","Database could not be opened");
                return null;
            }

        return clubs;
    }

    private ArrayList<String> getList(String data) {
        ArrayList<String> list = null;
        String[] splitIt = data.split(Pattern.quote(":)"));
        list = new ArrayList<>(Arrays.asList(splitIt));
        return  list;
    }
}
