package com.fungineers.enquire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class Launcher extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher);
		String s=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("accname", "");
		Boolean reg=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("registered", false);
		if(s.equalsIgnoreCase("") || !reg )
		{
			Intent intent = new Intent(getApplicationContext(),SelectEmail.class);
			startActivity(intent);
		}
		else
		{
			Intent intent = new Intent(getApplicationContext(),Home.class);
			startActivity(intent);

		}
	}
	

	
}
