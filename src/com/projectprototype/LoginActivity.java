package com.projectprototype;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectprototype.lib.WeekView;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{
	
	TextView loginE;
	String email = "";
	String password = "";
	TextView loginPass;
	Button loginButt;



	private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_authentication);

		mAuth = FirebaseAuth.getInstance();

		if (mAuth.getCurrentUser() != null) {
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			finish();
		}

		loginE = (TextView) findViewById(R.id.loginEmail);
		loginPass = (TextView) findViewById(R.id.loginPassword);
		loginButt = (Button) findViewById(R.id.loginButton);
		
		loginButt.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            login(v);
	        }
	    });

		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					//Toast.makeText(LoginActivity.this, "Log-in Success!"
					//		,Toast.LENGTH_SHORT).show();
                    //Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    //mainIntent.putExtra("eid", loginE.getText().toString());
                    LoginActivity.this.startActivity(mainIntent);
                    LoginActivity.this.finish();

                    //int count = names[0].length() - names[0].replace(",","").length();
                    //Toast.makeText(LoginActivity.this, names[0], Toast.LENGTH_SHORT).show();

					// User is signed in
					//Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
				}
				// ........
			}
		};
	}

	@Override
	public void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}
	
	public void login(View view) {
		final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
		view.startAnimation(buttonClick);

            email = loginE.getText().toString();
            password = loginPass.getText().toString();
            if (email.equals("") || password.equals("")){
                Toast.makeText(LoginActivity.this, "Please provide necessary information!",
                        Toast.LENGTH_SHORT).show();
                email = "error@accenture.com";
                password = "errorpass";
                return;
            }

            email = email + "@accenture.com";

            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Authenticating...");
            progressDialog.show();

		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						//Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

						// If sign in fails, display a message to the user. If sign in succeeds
						// the auth state listener will be notified and logic to handle the
						// signed in user can be handled in the listener.
						if (!task.isSuccessful()) {
							//Log.w(TAG, "signInWithEmail:failed", task.getException());
							Toast.makeText(LoginActivity.this, "Log in Failed!", Toast.LENGTH_SHORT).show();
						}else {

						}

						progressDialog.cancel();
						Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
						// ...
					}
				});

		//Firebase.setAndroidContext(this);
		//Firebase ref = new Firebase("https://goschedule-12ee6.firebaseio.com/");
		
		/**ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
		    @Override
		    public void onAuthenticated(AuthData authData) {
		    	Toast.makeText(getApplicationContext(), "Log-in Success!", Toast.LENGTH_LONG).show();
		    	//Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                //startActivity(mainIntent);

		    }
		    @Override
		    public void onAuthenticationError(FirebaseError firebaseError) {
		        // there was an error
		    	Toast.makeText(getApplicationContext(), firebaseError.getMessage(), Toast.LENGTH_LONG).show();
		    }
		});			**/



	}


}
