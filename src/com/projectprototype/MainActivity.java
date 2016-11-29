package com.projectprototype;

import java.util.ArrayList;
import java.util.Calendar;

//import com.firebase.client.DataSnapshot;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//import com.firebase.client.ValueEventListener;
//import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectprototype.lib.WeekView;

//import com.examples.android.calendar.CalendarAdapter;
//import com.examples.android.calendar.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	DatabaseHelper db;
	Button logLeaveButton;
	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items;
	public String nameConverted;
    boolean adminCheck = false;
    boolean dbSyncCheck = false;
    String[] names = new String[1];
    //String EID;
    private FirebaseAuth mAuth;
	private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
	private ProgressBar progressBar;
	//Resource class for Firebase use
	public static class Resource {
 		  int uid;
		  String date;

		  String name;
		  String type;
		  String backup;
		  String status;
		  String checker;
		  String comment;
		  	  
		  public Resource() {
		    // empty default constructor, necessary for Firebase to be able to deserialize blog posts
		  }


		  public String getDate() {
			    return date;
		  }
		  public String getName() {
		    return name;
		  }
		  public String getType() {
		    return type;
		  }
		  public String getBackup() {
				return backup;
			}
		  public String getStatus() {
				return status;
			}
		  public String getChecker() {
			return checker;
		}
		  public String getComment() {
			return comment;
		}
		  
	}

	ValueEventListener valueEventListener = new ValueEventListener() {
		@Override
		public void onDataChange(DataSnapshot snapshot) {
			//System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");

			db.deleteAll();
			for (DataSnapshot personSnapshot: snapshot.getChildren()) {
				Resource person = personSnapshot.getValue(Resource.class);
				nameConverted = person.getName();
				nameConverted = nameConverted.replace("-",".");
				db.createLog(nameConverted,person.getDate(),person.getType(), person.getBackup(), person.getStatus(), person.getChecker(), person.getComment());

				updateCalendar();
//					AsyncTask.execute(calendarUpdater);
				//System.out.println(post.getAuthor() + " - " + post.getTitle());
			}

		}

		@Override
		public void onCancelled(DatabaseError databaseError) {
			//System.out.println("The read failed: " + firebaseError.getMessage());
		}

		//@Override
		//public void onCancelled(FirebaseError firebaseError) {
		//      System.out.println("The read failed: " + firebaseError.getMessage());
		//}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		db = new DatabaseHelper(this);
		mAuth = FirebaseAuth.getInstance();

		FirebaseUser user = mAuth.getCurrentUser();
		if (user != null) {
			// User is not logged in
			Toast.makeText(MainActivity.this, "Welcome " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();
		}


		Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
		setSupportActionBar(myToolbar);



        //Initialize Calendar View with Gridview
		month = Calendar.getInstance();
		setDateToday();

		items = new ArrayList<String>();
		adapter = new CalendarAdapter(this, month);
		   
		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(adapter);

        //final ProgressDialog progressDialog1 = new ProgressDialog(MainActivity.this);
        //final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        //progressDialog1.setIndeterminate(true);
        //progressDialog1.setMessage("Retrieving Data...");
        //progressDialog1.show();

		//Get Firebase Data
		//Firebase.setAndroidContext(this);
		//Firebase ref = new Firebase("https://goschedule-4ffe9.firebaseio.com/dates");
		
		   
		TextView title  = (TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
		   
		TextView previous  = (TextView) findViewById(R.id.previous);
		previous.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) { 
					month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
				}
				refreshCalendar();
			}
		});
		
		TextView next  = (TextView) findViewById(R.id.next);
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) { 
					month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
				} else {
					month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
				}
				refreshCalendar();
			}
		});
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				TextView date = (TextView)v.findViewById(R.id.date);
				if(date instanceof TextView && !date.getText().equals("")) {
				        
				        Intent intent = new Intent(MainActivity.this, ViewLeaveActivity.class);
				        String day = date.getText().toString();
				        if(day.length()==1) {
				        	day = "0"+day;
				        }
				        String years = "" + month.get(Calendar.YEAR);
				        String months = "" + (month.get(Calendar.MONTH)+1);
				        if(months.length()==1) {
				        	months = "0"+months;
				        }
				        // return chosen date as string format 
				        //intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
				        //String[] dateArr = android.text.format.DateFormat.format("yyyy-MM", month).toString().split("-");
				        intent.putExtra("year", years);
				        intent.putExtra("month", months);
				        intent.putExtra("day", day);
				        startActivity(intent);
				}
				       
			}
		});		
		
		Button logLeaveButton = (Button) findViewById(R.id.leavebutton); 
		logLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	//Log.i("network frag list"," test convert view");
            	logLeave(v);
            }
        });
		
		
		  
		//Intent intentHome = getIntent();
		//String message = intentHome.getStringExtra("message");
		//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	public void logLeave(View view) {
        Intent intent1 = new Intent(this, LeaveActivity.class);
        intent1.putExtra("message", "Please Log Details");
        startActivity(intent1);
    }

    public void searchLeave(View view) {
        Intent intent2 = new Intent(this, SearchEIDActivity.class);
        intent2.putExtra("message", "Please Log Resource EID");
        startActivity(intent2);
    }

	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		adapter.refreshDays();
		adapter.notifyDataSetChanged();
		updateCalendar();
//		AsyncTask.execute(calendarUpdater); // generate some random calendar items
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	//public void onNewIntent(Intent intent) {
	//String date = intent.getStringExtra("date");
	//String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
	//test date today
	//month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	//} 
	public void setDateToday() {
		Calendar calendar = Calendar.getInstance();
		int thisYear = calendar.get(Calendar.YEAR);
		int thisMonth = calendar.get(Calendar.MONTH);
		int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
		month.set(thisYear, thisMonth, thisDay);
	} 
	
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
	// Inflate the menu; this adds items to the action bar if it is present.
		final FirebaseUser user = mAuth.getCurrentUser();

		if (user != null) {
			if (adminCheck) {
				getMenuInflater().inflate(R.menu.admin, menu);

			}
			Query adminQuery = database.getReference().child("admin").limitToFirst(20);

			//adminQuery.addValueEventListener(new ValueEventListener() {
			adminQuery.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					for (DataSnapshot adminSnapshot: dataSnapshot.getChildren()) {
						String value = (String) adminSnapshot.getValue();
						names[0] = value;
						names[0] = names[0].replaceAll("\\s","");
						//Toast.makeText(LoginActivity.this, names[0], Toast.LENGTH_SHORT).show();

						String[] admins = names[0].split(",");

						for (int i = 0; i < admins.length ; i++){
							if (admins[i].equals(user.getEmail())){
								//Toast.makeText(LoginActivity.this, "admin", Toast.LENGTH_SHORT).show();
								adminCheck = true;
							}
						}

						if (adminCheck) {
							getMenuInflater().inflate(R.menu.admin, menu);
						}
						else{
							getMenuInflater().inflate(R.menu.main, menu);
						}
						// do your stuff here with value
					}
				}
				@Override
				public void onCancelled(DatabaseError databaseError) {
				}
			});
		}else {
			getMenuInflater().inflate(R.menu.main, menu);
		}


	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	// Handle action bar item clicks here. The action bar will
	// automatically handle clicks on the Home/Up button, so long
	// as you specify a parent activity in AndroidManifest.xml.
	int id = item.getItemId();

	/*if (id == R.id.action_settings) {
	    return true;
	}*/
    if (id == R.id.weekview){
		Intent intent = new Intent(this, WeekViewActivity.class);

		this.startActivity(intent);
		//this.finish();
    }

    if (id == R.id.signout) {

        //mAuth = FirebaseAuth.getInstance();
		final ProgressDialog progressDialog2 = new ProgressDialog(MainActivity.this);
		progressDialog2.setIndeterminate(true);
		progressDialog2.setMessage("Signing out...");
		progressDialog2.show();


		/*Intent intent = new Intent(this, LoginActivity.class);

		MainActivity.this.finish();
		MainActivity.this.startActivity(intent);*/
		FirebaseAuth.getInstance().signOut();

// this listener will be called when there is change in firebase user session
		//auth.signOut();

// this listener will be called when there is change in firebase user session
		mAuthListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user == null) {
					// user auth state is changed - user is null
					// launch login activity
					Intent i = getBaseContext().getPackageManager()
							.getLaunchIntentForPackage( getBaseContext().getPackageName() );
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			}
		};
		mAuth.addAuthStateListener(mAuthListener);
		MainActivity.this.finish();

	}

		if (id == R.id.myleaves) {

		Intent mainIntent = new Intent(MainActivity.this, MyLeavesActivity.class);
		MainActivity.this.startActivity(mainIntent);
		}

		if (id == R.id.reports) {

			Intent mainIntent = new Intent(MainActivity.this, NewReports.class);
			MainActivity.this.startActivity(mainIntent);
		}

		if (id == R.id.search) {

			Intent mainIntent = new Intent(MainActivity.this, SearchEIDActivity.class);
			MainActivity.this.startActivity(mainIntent);
		}

		if (id == R.id.leaves) {

			Intent mainIntent = new Intent(MainActivity.this, ApproveLeaveActivity.class);
			MainActivity.this.startActivity(mainIntent);



		}

		if (id == R.id.reset) {

			FirebaseUser user = mAuth.getCurrentUser();

			final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setIndeterminate(true);
			progressDialog.setMessage("Sending email...");
			progressDialog.show();
			mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
					if (task.isSuccessful()) {
						Toast.makeText(MainActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
					}

					progressDialog.cancel();
				}
			});

		}

	return super.onOptionsItemSelected(item);
	}

	public Runnable calendarUpdater = new Runnable() {
		
		@Override
		public void run() {
			items.clear();
			String toPass;
			String years = "" + month.get(Calendar.YEAR);
	        String months = "" + (month.get(Calendar.MONTH)+1);
	        if(months.length()==1) {
	        	months = "0"+months;
	        }
	        Log.i("TestMain",months+years);
	        
			// format random values. You can implement a dedicated class to provide real values
			for(int i=0;i<=31;i++) {
				
				toPass = Integer.toString(i);
				if (toPass.length() == 1){
					toPass = "0" + toPass;
				}
				
				if (db.dateHit(toPass, months+years)){
					items.add(Integer.toString(i));
				}
				
			}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};

    public void onStart() {
		super.onStart();

		//mAuth.addAuthStateListener(mAuthListener);
        /*if (adminCheck) {
            Toast.makeText(MainActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(MainActivity.this, AdminActivity.class);
            MainActivity.this.startActivity(mainIntent);
            MainActivity.this.finish();
        }

        final ProgressDialog progressDialog2 = new ProgressDialog(MainActivity.this);
        progressDialog2.setIndeterminate(true);
        progressDialog2.setMessage("Checking "+EID+" role...");
        progressDialog2.show();

        //DatabaseReference mDatabase;
        //mDatabase = FirebaseDatabase.getInstance().getReference();
        Query adminQuery = database.getReference().child("admin").limitToFirst(20);

        //adminQuery.addValueEventListener(new ValueEventListener() {
        adminQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot adminSnapshot: dataSnapshot.getChildren()) {
                    String value = (String) adminSnapshot.getValue();
                    names[0] = value;
                    names[0] = names[0].replaceAll("\\s","");
                    //Toast.makeText(LoginActivity.this, names[0], Toast.LENGTH_SHORT).show();

                    String[] admins = names[0].split(",");

                    for (int i = 0; i < admins.length ; i++){
                        if (admins[i].equals(EID)){
                            //Toast.makeText(LoginActivity.this, "admin", Toast.LENGTH_SHORT).show();
                            adminCheck = true;
                        }
                    }

                    if (names[0].length() > 1) {
                        dbSyncCheck = true;
                    }

                    if (adminCheck) {
                        Toast.makeText(MainActivity.this, "Welcome Admin!", Toast.LENGTH_SHORT).show();
                        progressDialog2.cancel();
                        Intent mainIntent = new Intent(MainActivity.this, AdminActivity.class);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                    }
                    else{
                        progressDialog2.cancel();
                    }
                    // do your stuff here with value
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    @Override
	public void onStop() {
		super.onStop();
		/*if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}*/
	}

	@Override
	protected void onResume() {
		super.onResume();
		DatabaseReference ref = database.getReference("dates");
		ref.addValueEventListener(valueEventListener);
	}

	@Override
	protected void onPause() {
		super.onPause();
		DatabaseReference ref = database.getReference("dates");
		ref.removeEventListener(valueEventListener);
		if(updateCalendarTask != null &&
				updateCalendarTask.getStatus() != AsyncTask.Status.FINISHED) {
			updateCalendarTask.cancel(true);
		}
	}

	@Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

	AsyncTask updateCalendarTask;
	private void updateCalendar() {
		if(updateCalendarTask != null &&
				updateCalendarTask.getStatus() != AsyncTask.Status.FINISHED) {
			updateCalendarTask.cancel(true);
		}
		updateCalendarTask = new AsyncTask() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				items.clear();
			}

			@Override
			protected void onPostExecute(Object o) {
				super.onPostExecute(o);

				adapter.setItems(items);
				adapter.notifyDataSetChanged();
			}

			@Override
			protected Object doInBackground(Object[] params) {
				String toPass;
				String years = "" + month.get(Calendar.YEAR);
				String months = "" + (month.get(Calendar.MONTH)+1);
				if(months.length()==1) {
					months = "0"+months;
				}

				// format random values. You can implement a dedicated class to provide real values
				for(int i=0;i<=31;i++) {

					toPass = Integer.toString(i);
					if (toPass.length() == 1){
						toPass = "0" + toPass;
					}

					if (db.dateHit(toPass, months+years)){
						items.add(Integer.toString(i));
					}

				}
				return null;
			}
		};

		updateCalendarTask.execute();
	}
	
}
