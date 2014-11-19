package com.fungineers.enquire;

import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;


import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SelectEmail extends SherlockActivity{
	public static String[] acc;
	public static ListView lv;
	public static String accountName=null;
	public static String authTokenStr;
	public static boolean authTokenInvalidated = false;
	public static int cat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectemail);
		AccountManager accountManager = AccountManager.get(this);
		Account[] accounts = accountManager.getAccountsByType("com.google");
		acc=new String[accounts.length];
		for(int i=0;i<acc.length;i++)
		{acc[i]=accounts[i].name;
	    }
		lv= (ListView)findViewById(R.id.accountlist);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, acc));
        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        Button b=(Button)findViewById(R.id.next);
         b.setOnClickListener(buttonAddOnClickListener);
       
        
        
	}
        Button.OnClickListener buttonAddOnClickListener
        = new Button.OnClickListener(){
        	

		@Override
		public void onClick(View arg0) {
			
			try{
             			cat=lv.getCheckedItemPosition();
             			Toast.makeText(getApplicationContext(), acc[cat], Toast.LENGTH_LONG).show();
            			getAuthToken();
            			PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("accname",acc[cat] ).commit();
            			Intent intent = new Intent(getApplicationContext(),Registration.class);
            			startActivity(intent);
			}
			catch(ArrayIndexOutOfBoundsException e){
				Toast.makeText(getApplicationContext(), "select atlest one emailid", Toast.LENGTH_LONG).show();	
			}
			
			////...............................................
			//here u will get position of email id selected which can be replaced by account[0] from others json calling methods  
		}
    };
    public void getAuthToken() {
        AccountManager accountManager = AccountManager.get(this);
        Bundle options = new Bundle();
        OnTokenAdquired onTokenAdquired = new OnTokenAdquired();
        Handler handler = new Handler();
        Account[] accounts = accountManager.getAccountsByType("com.google");
        String authTokenType = "oauth2:https://www.googleapis.com/auth/drive";
        AccountManagerFuture<Bundle> future = accountManager.getAuthToken(accounts[cat], 
            authTokenType, options, this, onTokenAdquired, handler);
    }
    public class OnTokenAdquired implements AccountManagerCallback<Bundle>{
        public void run(AccountManagerFuture<Bundle> future){
            Log.v("CALLBACK","post-getAuthToken");
            try {
                Bundle authTokenBundle = future.getResult();

                if ( authTokenBundle.containsKey(AccountManager.KEY_AUTHTOKEN)){
                    authTokenStr = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN);
                    accountName = authTokenBundle.getString(AccountManager.KEY_ACCOUNT_NAME);
                    String accountType = authTokenBundle.getString(AccountManager.KEY_ACCOUNT_TYPE);
                    System.out.print(accountType);
                    Log.v("ACCOUNT NAME",accountName);
                    Log.v("ACCOUNT TYPE",accountType);
                    Log.v("TOKEN",authTokenStr);
                    if (!authTokenInvalidated) {
                        invalidateAuthToken("com.google",authTokenStr);
                        authTokenInvalidated = true;
                        Log.v("CALLBACK","authTokenInvalidated == true");
                        getAuthToken();
                        return;
                    }
                    else {
                        authTokenInvalidated = false;
                        Log.v("CALLBACK","authTokenInvalidated == false");
                    }
                   
                   
                }
                else {
                    Log.v("KEY_AUTHTOKEN","non hai auth token, normal cando non hai conexion");
                }
                if (authTokenBundle.containsKey(AccountManager.KEY_INTENT)) {
                    Log.e("KEY_INTENT","temos KEY_INTENT");
                    Intent launch = (Intent)authTokenBundle.get(AccountManager.KEY_INTENT);
                    startActivityForResult(launch,0);
                    return;
                }
            }
            catch (AuthenticatorException e){
                Log.e("OnTokenAdquired.run","AuthenticatorException");
            }
            catch (IOException e){
                Log.e("OnTokenAdquired.run","IOException");
            }
            catch (OperationCanceledException e){
                Log.e("OnTokenAdquired.run","OperationCanceledException");
                Toast.makeText(getApplicationContext(), " user authentication required", Toast.LENGTH_LONG).show();	
            }
        }
    }
    public void invalidateAuthToken(String accountType, String authToken){
        AccountManager accountManager = AccountManager.get(this);
        accountManager.invalidateAuthToken(accountType, authToken);
    }       
}
