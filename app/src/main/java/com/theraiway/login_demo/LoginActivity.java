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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.theraiway.helper.MySingleton;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends ActionBarActivity {

    String Name;
    String SID;
    String Sex;
    String Mobile;
    Boolean Hosteller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflaste the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void signUpProcess(View v){

        EditText userName = (EditText)this.findViewById(R.id.user_name);
        EditText userSID = (EditText)this.findViewById(R.id.user_sid);
        EditText userCountry = (EditText)this.findViewById(R.id.user_country);
        EditText userMobile = (EditText)this.findViewById(R.id.user_mobile);
        Switch userResidence = (Switch)this.findViewById(R.id.user_residence);

        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioSex);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        Name = userName.getText().toString();
        SID = userSID.getText().toString();
        Mobile = userCountry.getText().toString() + userMobile.getText().toString();
        Hosteller = userResidence.isChecked();
        Sex = "others";

        if(selectedId != -1){
            RadioButton radioButton = (RadioButton)findViewById(selectedId);
            Sex = radioButton.getText().toString();
        }

        if(Name.isEmpty()){
            Toast.makeText(getApplicationContext(),"Empty Name not allowed",Toast.LENGTH_LONG).show();
            return;
        }

        boolean validSID = checkSID(SID);

        if(!validSID){
            Toast.makeText(getApplicationContext(),"SID is not valid",Toast.LENGTH_LONG).show();
            userSID.setText("");
            return;
        }

        if(Mobile.isEmpty()){
            Toast.makeText(getApplicationContext(),"Empty Mobile No. not allowed",Toast.LENGTH_LONG).show();
            return;
        }

        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected){
            Toast.makeText(getApplicationContext(),"Your internet doesn't seem to be working",Toast.LENGTH_LONG).show();
            return;
        }


        RequestQueue requestQueue = MySingleton.getInstance(this).getRequestQueue();
        String url = "http://"+MainActivity.SERVER_IP+"/users";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                SharedPreferences mySharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("jsonObject",response);
                editor.apply();
                Log.d("NewPost", response);
                callStudentActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(),"CHECK YOUR INTERNET",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Name",Name);
                params.put("Sex",Sex);
                params.put("SID",SID);
                params.put("MobileNo",Mobile);
                params.put("Hosteller",Hosteller.toString());
                return params;

            }
        };
        requestQueue.add(stringRequest);


    }

    private void callStudentActivity() {
        Intent intent = new Intent(this,StudentHomeActivity.class);
        startActivity(intent);
    }

    private boolean checkSID(String userSID) {
        if(userSID.isEmpty())
            return false;
        return true;
    }

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
