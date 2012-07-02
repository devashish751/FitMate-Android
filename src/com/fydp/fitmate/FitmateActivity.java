package com.fydp.fitmate;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Facebook.DialogListener;

public class FitmateActivity extends Activity {
	
public static final String mAPP_ID = "375849315815699";

public Facebook mFacebook = new Facebook(mAPP_ID);

//**********************************************
// onCreate
//**********************************************
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    ((Button)findViewById(R.id.LoginButton)).setOnClickListener( loginButtonListener );
    SessionStore.restore(mFacebook, this);
	}


//***********************************************************
// onActivityResult
//***********************************************************
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    mFacebook.authorizeCallback(requestCode, resultCode, data);
	}


//----------------------------------------------
// loginButtonListener
//----------------------------------------------
private OnClickListener loginButtonListener = new OnClickListener() {
	public void onClick( View v ) {
		if( !mFacebook.isSessionValid() ) {
			Toast.makeText(FitmateActivity.this, "Authorizing", Toast.LENGTH_SHORT).show();
			mFacebook.authorize(FitmateActivity.this, new String[] { "" }, new LoginDialogListener());
			}
		else {
			Toast.makeText( FitmateActivity.this, "Has valid session", Toast.LENGTH_SHORT).show();
			try {
    			JSONObject json = Util.parseJson(mFacebook.request("me"));
    			String facebookID = json.getString("id");
    			String firstName = json.getString("first_name");
    			String lastName = json.getString("last_name");
    			Toast.makeText(FitmateActivity.this, "You already have a valid session, " + firstName + " " + lastName + ". No need to re-authorize.", Toast.LENGTH_SHORT).show();
    			}
			catch( Exception error ) {
				Toast.makeText( FitmateActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
				}
			catch( FacebookError error ) {
				Toast.makeText( FitmateActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	
	
//***********************************************************************
//***********************************************************************
// LoginDialogListener
//***********************************************************************
//***********************************************************************
public final class LoginDialogListener implements DialogListener {
	public void onComplete(Bundle values) {
		try {
			//The user has logged in, so now you can query and use their Facebook info
    		JSONObject json = Util.parseJson(mFacebook.request("me"));
			String facebookID = json.getString("id");
			String firstName = json.getString("first_name");
			String lastName = json.getString("last_name");
			Toast.makeText( FitmateActivity.this, "Thank you for Logging In, " + firstName + " " + lastName + "!", Toast.LENGTH_SHORT).show();
			SessionStore.save(mFacebook, FitmateActivity.this);
			}
		catch( Exception error ) {
			Toast.makeText( FitmateActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
			}
		catch( FacebookError error ) {
			Toast.makeText( FitmateActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
			}
		}
	
	public void onFacebookError(FacebookError error) {
		Toast.makeText( FitmateActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
	    }
	
	public void onError(DialogError error) {
		Toast.makeText( FitmateActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
		}
	 
    public void onCancel() {
    	Toast.makeText( FitmateActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
		}
	}
}