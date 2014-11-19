package com.fungineers.enquire;

import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;

public class Registration extends SherlockActivity {
	final String myTag = "DocsUpload";
	String Name,Plannedcourse,Phone,Email;
	EditText name,plannedcourse,phone,email;
	Spinner branch,year,division,event;
	TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.credila_registration);
		name=(EditText)findViewById(R.id.name);
		plannedcourse=(EditText)findViewById(R.id.plannedcourse);
		phone=(EditText)findViewById(R.id.phone);
		email=(EditText)findViewById(R.id.email);
		String s = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("accname","" );
		Toast.makeText(getApplicationContext(), "Account Name is "+ s ,Toast.LENGTH_LONG ).show();
	
		
		Log.i(myTag, "OnCreate()");
		
	}

	public void register(View v)
	{
		
		Name=name.getText().toString();
		Plannedcourse=plannedcourse.getText().toString();
		Phone=phone.getText().toString();
		Email=email.getText().toString();
		
		
		
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				postData(Name,Plannedcourse, Phone,Email);	
			}
		});
		t.start();
		
		if(checkInternetConnection())
	      {
	    	 Toast.makeText(getApplicationContext(), "Thank You For Registration!",Toast.LENGTH_LONG ).show();
	    	 PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("registered",true).commit();
	      }
	       else
	    	  {
	    	  Toast.makeText(getApplicationContext(), "Registration Failed! Please Check Your Internet Connection.", Toast.LENGTH_LONG).show();
	    	  }
		
		Intent intent = new Intent(getApplicationContext(), Home.class);
  	    startActivity(intent);
	}

	public void postData(String Name,String Plannedcourse,String Phone,String Email) {

		String fullUrl = "https://docs.google.com/forms/d/1kEKDnRIiT8qfsAU7Tg13oauN67C26tIpxhdjcdYUofw/formResponse";
		HttpRequest mReq = new HttpRequest();
		
		String data =   "entry.766287907=" + URLEncoder.encode(Name) + "&" + 
					    "entry.529931769=" + URLEncoder.encode(Plannedcourse) + "&" +
						"entry.644217772=" + URLEncoder.encode(Phone) + "&" +
						"entry.741923390=" + URLEncoder.encode(Email) ;
		
		String response = mReq.sendPost(fullUrl, data);
		Log.i(myTag, response);
	} 
	
	 public boolean checkInternetConnection() {

	    	ConnectivityManager conMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE);

	    	// ARE WE CONNECTED TO THE NET

	    	if (conMgr.getActiveNetworkInfo() != null

	    	&& conMgr.getActiveNetworkInfo().isAvailable()

	    	&& conMgr.getActiveNetworkInfo().isConnected()) {

	    	return true;

	    	} else {


	    	return false;

	    	}

	    	}

}
