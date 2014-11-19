package com.fungineers.enquire;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
 
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 
import com.actionbarsherlock.app.SherlockFragment;
import com.fima.cardsui.views.CardUI;
 
 
public class GetMyQuestions extends SherlockFragment {

    static private String url;
    public final static String GAS_HELLO_WORLD = "url_to_gas_script";
    public static boolean authTokenInvalidated = false;
    public static String authTokenStr;
    public static JSONArray objects; 
    public static String accountName=null;
    String qhead[],qid[],qns[];
    private CardUI mCardView;
    public static TextView textView ;
    private static Button b;
    private class MyTask extends AsyncTask<Void, Void, Void> {
        ArrayList<Item> items = new ArrayList<Item>();
        List<NameValuePair> param = new ArrayList<NameValuePair>();
        @Override
        protected Void doInBackground(Void... params) {
            try {
                /*List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                JSONObject json=jParser.makeHttpRequest(url, authTokenStr, params1);
                TextView textView = (TextView)findViewById(android.R.id.text1);
                textView.setText(authTokenStr);*/
                //....................................//
 
                HttpClient hc = new DefaultHttpClient();
                                   
                HttpGet get = new HttpGet(url);
                get.setHeader("Authorization", "OAuth "+authTokenStr);
                HttpResponse rp = hc.execute(get);
                 
                if(rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    String result = EntityUtils.toString(rp.getEntity());
                    System.out.println(result);
                    objects = new JSONArray(result);
                     
                     
                }
            } catch (Exception e) {
                Log.e("ItemFeed", "Error loading JSON", e);
             
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
             
            qhead=new String[objects.length()];
            qns=new String[objects.length()];
            Toast.makeText(getActivity(), accountName, Toast.LENGTH_LONG).show();
            for (int i = 0; i < objects.length(); i++) {
                JSONObject session;
                try {
                    session = objects.getJSONObject(i);
                 
                    qhead[i]=session.getString("qhead");
                    
                    qns[i]=session.getString("qns");
                Toast.makeText(getActivity(), qhead[i], Toast.LENGTH_LONG).show();
              /*  Toast.makeText(getApplicationContext(), "name:"+session.getString("name"), Toast.LENGTH_LONG).show();*/
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            Toast.makeText(getActivity(), "done", Toast.LENGTH_LONG).show();
          /*   
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("qhead", qhead);
            intent.putExtra("qns", qns);
            intent.putExtra("qid", qid);
            startActivity(intent);*/
            
            
            // init CardView
           
            mCardView.setSwipeable(false);

           
            for(int i=0;i<qhead.length;i++)
            {
            // add one card, and then add another one to the last stack.
                final MyCard androidViewsCard = new MyCard(qhead[i],qns[i]);
                mCardView.addCard(androidViewsCard);
                androidViewsCard.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                            int pos = posret(androidViewsCard.getTitle());
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://www.androidviews.net/"));
                            startActivity(intent);
                            Toast.makeText(getActivity(), ""+androidViewsCard.getDesc() + " " + pos, Toast.LENGTH_SHORT).show();

                    }
            });
            }
           
            // draw cards
            mCardView.refresh();
        }
        public int posret(String s)
        {
            int pos;
            for(pos=0;pos<=qhead.length;pos++)
                if(s.equalsIgnoreCase(qhead[pos]))
                    break;
            return pos;
        }
            
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
                   
                    
                    
                    ////here/////
                    MyTask task = new MyTask();
                    task.execute();
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
            }
        }
    }
    public void getAuthToken() {
        AccountManager accountManager = AccountManager.get(getActivity());
        Bundle options = new Bundle();
        OnTokenAdquired onTokenAdquired = new OnTokenAdquired();
        Handler handler = new Handler();
        Account[] accounts = accountManager.getAccountsByType("com.google");
        String authTokenType = "oauth2:https://www.googleapis.com/auth/drive";
        Toast.makeText(getActivity(), "getting json213", Toast.LENGTH_LONG).show();
        AccountManagerFuture<Bundle> future = accountManager.getAuthToken(accounts[0], 
            authTokenType, options, getActivity(), onTokenAdquired, handler);
    }
    public void invalidateAuthToken(String accountType, String authToken){
        AccountManager accountManager = AccountManager.get(getActivity());
        accountManager.invalidateAuthToken(accountType, authToken);
    }
 
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Activity.RESULT_OK;
        Bundle extras = data.getExtras();
        Set<String> keys = extras.keySet();
        Iterator<String> iter = keys.iterator();
        Log.v("KEY_INTENT","requestCode: "+requestCode+", resultCode: "+resultCode);
        Log.v("KEY_INTENT","EXTRAS KEY_INTENT");
        while (iter.hasNext()){
                Log.v("KEY_INTENT",(String)iter.next());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
    	String s=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("accname", "");
    	Toast.makeText(getActivity(), "getting acc" + s, Toast.LENGTH_LONG).show();
    	View view = inflater.inflate(R.layout.json_activity_main,container, false);
    	Toast.makeText(getActivity(), "getting json", Toast.LENGTH_LONG).show();
    	 mCardView = (CardUI) view.findViewById(R.id.cardsview);
        Log.v("GAS","UploadFileGASActivity called.");
        url = "https://script.google.com/macros/s/AKfycbzjG_OcPqs0BfBcvjBzkeiNymQIApEgL7v78utMH94/dev?qid=naiduuday01@gmail.com";
        		
    getAuthToken();
		return view;
	}
	/*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simplerow);
        
        Toast.makeText(getActivity(), "getting json", Toast.LENGTH_LONG).show();
        
            Log.v("GAS","UploadFileGASActivity called.");
            url = "https://script.google.com/macros/s/AKfycbzjG_OcPqs0BfBcvjBzkeiNymQIApEgL7v78utMH94/dev?cat=science";
        getAuthToken();
    }*/

}
    