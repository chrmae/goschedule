package com.projectprototype;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

//import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.MutableInt;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LeaveActivity extends AppCompatActivity implements OnItemSelectedListener {
    DatabaseHelper db;
    Button submitButton;
    Button cancelButton;
    EditText name;
    EditText date;
    EditText date2;
    EditText backup;
    EditText status;
    String checker;
    EditText comment;
    Spinner type;
    String item;
    boolean adminCheck;
    boolean dbSyncCheck = false;
    String[] names = new String[1];
    //String EID;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private KeyListener listener;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Calendar myCalendar = Calendar.getInstance();
    Calendar myCalendar2 = Calendar.getInstance();
    private FirebaseAuth mAuth;

    //SimpleDateFormat sdf1 = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
    //		Locale.ENGLISH);
    String myFormat = "MM/dd/yyyy"; //In which you need put here
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
    SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
    //DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    Date today = new Date();
    Date dateFirst = new Date();
    Date dateSecond = new Date();


    DatePickerDialog.OnDateSetListener datepicker1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }


    };

    DatePickerDialog.OnDateSetListener datepicker2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar2.set(Calendar.YEAR, year);
            myCalendar2.set(Calendar.MONTH, monthOfYear);
            myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_leave);

        db = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        Intent intentReceived = getIntent();

        //Firebase.setAndroidContext(this);
        //Firebase f = new Firebase("https://goschedule-50998.firebaseio.com/");
        //f.setValue("Hello World! version 2.0");

        submitButton = (Button) findViewById(R.id.leaveSubmit);
        cancelButton = (Button) findViewById(R.id.leaveCancel);

        name = (EditText) findViewById(R.id.leaveName);
        date = (EditText) findViewById(R.id.leaveDate);
        date2 = (EditText) findViewById(R.id.leaveDate2);
        type = (Spinner) findViewById(R.id.leaveType);
        backup = (EditText) findViewById(R.id.leaveBackUp);
        status = (EditText) findViewById(R.id.leaveStatus);
        comment = (EditText) findViewById(R.id.leaveComment);

        status.setVisibility(View.GONE);

        String c = "[@]";
        final String[] nameEID = user.getEmail().split(c);


        name.setText(nameEID[0], TextView.BufferType.EDITABLE);
        ;
        listener = name.getKeyListener();
        final String forCheck = nameEID[0] + "@accenture.com";


        if (adminCheck) {
            name.setKeyListener(listener);
        }
        Query adminQuery = database.getReference().child("admin").limitToFirst(20);

        //adminQuery.addValueEventListener(new ValueEventListener() {
        adminQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                    String value = (String) adminSnapshot.getValue();
                    names[0] = value;
                    names[0] = names[0].replaceAll("\\s", "");
                    //Toast.makeText(LoginActivity.this, names[0], Toast.LENGTH_SHORT).show();

                    String[] admins = names[0].split(",");

                    for (int i = 0; i < admins.length; i++) {
                        if (admins[i].equals(forCheck)) {
                            //Toast.makeText(LoginActivity.this, "admin", Toast.LENGTH_SHORT).show();
                            adminCheck = true;
                        }
                    }

                    if (adminCheck) {
                        name.setKeyListener(listener);
                    } else {
                        name.setKeyListener(null);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        type.setOnItemSelectedListener(this);
        final Calendar myCalendar = Calendar.getInstance();
        final Calendar myCalendar2 = Calendar.getInstance();

        date.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LeaveActivity.this, datepicker1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LeaveActivity.this, datepicker2, myCalendar2
                        .get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH),
                        myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLeave(v);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelView(v);
            }
        });


    }


    private void updateLabel() {
        date.setText(sdf.format(myCalendar.getTime()));

        date2.setText(sdf.format(myCalendar2.getTime()));
    }


    public void submitLeave(View view) {
        try {
            String firstDate = date.getText().toString();
            String secondDate = date2.getText().toString();

            dateFirst = sdf2.parse(firstDate);
            int date1Result = today.compareTo(dateFirst);
            dateSecond = sdf2.parse(secondDate);
            int date2Result = dateFirst.compareTo(dateSecond);


            if (name.getText().toString().length() > 0 && date.getText().toString().length() > 0) {


                if ((dateFirst.after(today) || sdf2.format(dateFirst).equals(sdf2.format(today))) && (dateSecond.after(dateFirst) || sdf2.format(dateSecond).equals(sdf2.format(dateFirst)))) {



                    boolean logStatus = false;

                    Calendar c = Calendar.getInstance();
                    c.setTime(dateFirst);
                    int dateDiff = dateSecond.getDate() - dateFirst.getDate() + 1;

                    if (comment.getText().equals(null)) {
                        comment.setText("None");
                    }
                        for (int i = 1; i <= dateDiff; i++) {

                            Log.d("daterange", "DateFirst:" + dateFirst);


                            //Toast.makeText(getApplicationContext(), "Date:" + dateFirst + " " + sdf2.format(dateFirst), Toast.LENGTH_SHORT).show();
                            logStatus = createLogFB(name.getText().toString(), sdf2.format(dateFirst), item, backup.getText().toString(), status.getText().toString(), checker, comment.getText().toString());
                            //Intent back = new Intent(this, MainActivity.class);
                            c.add(Calendar.DATE, 1);
                            dateFirst = c.getTime();
                        }

                        if (logStatus) {
                            Toast.makeText(getApplicationContext(), "Added Leave!", Toast.LENGTH_LONG).show();

                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed, please try again.", Toast.LENGTH_LONG).show();
                        }

                } else {
                    Toast.makeText(getApplicationContext(), "End date is invalid.", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Invalid date. Choose date today or later." + dateFirst + dateSecond + today, Toast.LENGTH_LONG).show();
            }


            if (name.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(), "EID is required.", Toast.LENGTH_LONG).show();
            } else if (date.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(), "Date is required.", Toast.LENGTH_LONG).show();
            }else if (backup.getText().toString().length() ==  0){
                Toast.makeText(getApplicationContext(), "Backup is required.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception er) {

        }

    }

    public void cancelView(View view) {
        //Intent intent1 = new Intent(this, MainActivity.class);
        //intent1.putExtra("message", "Canceled.");
        //startActivity(intent1);
        finish();
    }


    public boolean createLogFB(String name, String date, String type, String backup, String status, String checker, String comment) {


        String[] startDate = date.split("/");
        String monthyear = startDate[0] + startDate[2];
        date = startDate[0] + "-" + startDate[1] + "-" + startDate[2];
        String dateforchecker = startDate[0] + startDate[1] + startDate[2];
        name = name.replace(".", "-");
        backup = backup.replace(".", "-");

        checker = name.toUpperCase() + dateforchecker;

        //Firebase.setAndroidContext(this);
        //Firebase ref = new Firebase("https://goschedule-4ffe9.firebaseio.com/");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dateRef = database.getReference("dates");


        //Firebase dateRef = ref.child("dates");
        //DatabaseReference dateRef = ref.child("dates");
        /*dateRef.runTransaction(new Transaction.Handler() {
			@Override
			public Transaction.Result doTransaction(MutableData uid) {
				if (uid.getValue() == null){
					uid.setValue(1);
				}else{
					uid.setValue((Long) uid.getValue() + 1);
				}
				return Transaction.success(uid);
			}



			@Override
			public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

			}
		});*/


        Map<String, Object> dataput = new HashMap<String, Object>();

        dataput.put("name", name);
        dataput.put("date", date);
        dataput.put("type", type);
        dataput.put("backup", backup);
        dataput.put("status", status);
        dataput.put("checker", checker);
        dataput.put("comment", comment);
        dateRef.push().setValue(dataput);

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

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
                    for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                        String value = (String) adminSnapshot.getValue();
                        names[0] = value;
                        names[0] = names[0].replaceAll("\\s", "");
                        //Toast.makeText(LoginActivity.this, names[0], Toast.LENGTH_SHORT).show();

                        String[] admins = names[0].split(",");

                        for (int i = 0; i < admins.length; i++) {
                            if (admins[i].equals(user.getEmail())) {
                                //Toast.makeText(LoginActivity.this, "admin", Toast.LENGTH_SHORT).show();
                                adminCheck = true;
                            }
                        }

                        if (adminCheck) {
                            getMenuInflater().inflate(R.menu.admin, menu);
                        } else {
                            getMenuInflater().inflate(R.menu.main, menu);
                        }
                        // do your stuff here with value
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
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
        if (id == R.id.weekview) {
            Intent intent = new Intent(this, MainActivity.class);

            this.startActivity(intent);
            this.finish();
        }

        if (id == R.id.search) {

            Intent mainIntent = new Intent(LeaveActivity.this, SearchEIDActivity.class);
            LeaveActivity.this.startActivity(mainIntent);
        }

        if (id == R.id.signout) {

            //mAuth = FirebaseAuth.getInstance();
            final ProgressDialog progressDialog2 = new ProgressDialog(LeaveActivity.this);
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
                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
            };
            mAuth.addAuthStateListener(mAuthListener);
            LeaveActivity.this.finish();

        }

        if (id == R.id.myleaves) {

            Intent mainIntent = new Intent(LeaveActivity.this, MyLeavesActivity.class);
            LeaveActivity.this.startActivity(mainIntent);

        }

        if (id == R.id.leaves) {

            Intent mainIntent = new Intent(this, ApproveLeaveActivity.class);
            this.startActivity(mainIntent);


        }

        if (id == R.id.reports) {

            Intent mainIntent = new Intent(LeaveActivity.this, NewReports.class);
            LeaveActivity.this.startActivity(mainIntent);
        }


        if (id == R.id.reset) {

            FirebaseUser user = mAuth.getCurrentUser();

            final ProgressDialog progressDialog = new ProgressDialog(LeaveActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending email...");
            progressDialog.show();
            mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(LeaveActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LeaveActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                    }

                    progressDialog.cancel();
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}


	