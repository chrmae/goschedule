package com.projectprototype;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.projectprototype.lib.DateTimeInterpreter;
import com.projectprototype.lib.MonthLoader;
import com.projectprototype.lib.WeekView;
import com.projectprototype.lib.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
public abstract class BaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_WEEK_VIEW;
    private WeekView mWeekView;
    private FirebaseAuth mAuth;
    boolean adminCheck = false;
    boolean dbSyncCheck = false;
    String[] names = new String[1];

    DatabaseHelper db;

    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_base);

        db = new DatabaseHelper(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();



        /*if (user != null) {
            // User is not logged in

            Toast.makeText(BaseActivity.this, "Welcome " + user.getEmail() + "!", Toast.LENGTH_SHORT).show();
        }*/

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        /*mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);*/

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);



        Button logLeaveButton = (Button) findViewById(R.id.leavebutton);
        logLeaveButton.setVisibility(View.GONE);
        logLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i("network frag list"," test convert view");
                logLeave(v);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

	/*if (id == R.id.action_settings) {
	    return true;
	}*/
        if (id == R.id.weekview){
            Intent intent = new Intent(this, MainActivity.class);

            this.startActivity(intent);
            this.finish();
        }

        if (id == R.id.search) {

            Intent mainIntent = new Intent(this, SearchEIDActivity.class);
            this.startActivity(mainIntent);
        }


        if (id == R.id.myleaves) {

            Intent mainIntent = new Intent(this, MyLeavesActivity.class);

            this.startActivity(mainIntent);


        }

        if (id == R.id.reports) {

            Intent mainIntent = new Intent(this, NewReports.class);
            this.startActivity(mainIntent);
        }

        if (id == R.id.leaves) {

            Intent mainIntent = new Intent(this, ApproveLeaveActivity.class);
            this.startActivity(mainIntent);



        }

        if (id == R.id.signout) {

            //mAuth = FirebaseAuth.getInstance();
            final ProgressDialog progressDialog2 = new ProgressDialog(BaseActivity.this);
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
            BaseActivity.this.finish();

        }

        if (id == R.id.myleaves) {

            Intent mainIntent = new Intent(BaseActivity.this, MyLeavesActivity.class);
            BaseActivity.this.startActivity(mainIntent);



        }

        if (id == R.id.search) {

            Intent mainIntent = new Intent(BaseActivity.this, SearchEIDActivity.class);
            BaseActivity.this.startActivity(mainIntent);
        }



        if (id == R.id.reset) {

            FirebaseUser user = mAuth.getCurrentUser();

            final ProgressDialog progressDialog = new ProgressDialog(BaseActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending email...");
            progressDialog.show();
            mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(BaseActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BaseActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.cancel();
                }
            });


        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    //public static String newline = System.getProperty("line.separator");

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                String newLine = System.getProperty("line.separator");
                SimpleDateFormat format = new SimpleDateFormat(newLine + "M/d/yy", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }


    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.MONTH), time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.YEAR));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    public WeekView getWeekView() {
        return mWeekView;
    }



}
