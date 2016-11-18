package com.projectprototype;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyLeavesActivity extends ListActivity implements AdapterView.OnItemClickListener{

    Button backHome;
    DatabaseHelper db;
    String extraString;
    List<String> listLeave;

    ArrayAdapter<String> myAdapter;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String nameConverted;
    public Handler handler;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    Calendar myCalendar = Calendar.getInstance();
    private FirebaseAuth mAuth;
    private KeyListener listener;
    boolean adminCheck = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_leaves);

        db = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        backHome = (Button) findViewById(R.id.backhome);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome(v);
            }
        });

        DatabaseReference ref = database.getReference("dates");

        String c = "[@]";
        String[] nameEID = user.getEmail().split(c);
        listLeave = db.getAllInName(nameEID[0]);

        //Log.i("Adap", listLeave.get(0));
        myAdapter = new ArrayAdapter<String>(this, R.layout.leave_viewer, R.id.ListMyLeave, listLeave);

        //lv = (ListView) findViewById(android.R.id.list);
        seeMyList();



    }

    private void seeMyList(){
        this.setListAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        getListView().setOnItemClickListener(this);
    }



    public void backHome(View view) {
        finish();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        TextView caption = (TextView) findViewById(R.id.ListMyLeave);
        FirebaseUser user = mAuth.getCurrentUser();
        final DatabaseReference ref = database.getReference();


        String toEdit = (String) parent.getItemAtPosition(position);
        //Toast.makeText(ApproveLeaveActivity.this, toEdit, Toast.LENGTH_SHORT).show();
        String delim = "[\n]";
        String[] finalLeave = toEdit.split(delim);

        String c = "[@]";
        String[] tempname = user.getEmail().split(c);
        String name = tempname[0].toLowerCase();

        String a = "[ ]";
        String splitdate = finalLeave[1];
        String[] tempdate = splitdate.split(a);
        String date = tempdate[1];

        String b = ": ";
        String splittype = finalLeave[2];
        String[] temptype = splittype.split(b);
        String type = temptype[1];

        String d = ": ";
        String splitbackup = finalLeave[3];
        String[] tempbackup = splitbackup.split(d);
        String backup = tempbackup[1];

        String e = ": ";
        String splitstatus = finalLeave[4];
        String[] tempstatus = splitstatus.split(d);
        String status = tempstatus[1];

        String checker = finalLeave[0];



        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Actions");
        alert.setMessage("What do you want to do?");

        final String finalName = name;
        final String finalDate = date;
        final String finalType = type;
        final String finalBackup = backup;
        final String finalChecker = checker;
        final String finalStatus = status;
        final Context context = this;




        alert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(MyLeavesActivity.this, EditLeaveActivity.class);


                //Toast.makeText(ApproveLeaveActivity.this, "Successfully approved leave!", Toast.LENGTH_SHORT).show();
                intent.putExtra("name", finalName);
                intent.putExtra("date", finalDate);
                intent.putExtra("type", finalType);
                intent.putExtra("backup", finalBackup);
                intent.putExtra("checker", finalChecker);
                intent.putExtra("status", finalStatus);
                intent.putExtra("extraString", extraString);
                //startActivity(intent);

                startActivityForResult(intent, 1);

                seeMyList();

                /*LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.activity_edit_leave, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText name = (EditText) findViewById(R.id.editleaveName);
                EditText date = (EditText) findViewById(R.id.editleaveDate);
                Spinner type = (Spinner) findViewById(R.id.editleaveType);
                EditText backup = (EditText) findViewById(R.id.editleaveBackUp);
                EditText status = (EditText) findViewById(R.id.editleaveStatus);
                TextView checker = (TextView) findViewById(R.id.editleaveChecker);
                listener = name.getKeyListener();

                status.setVisibility(View.GONE);


                FirebaseUser user = mAuth.getCurrentUser();
                final String[] names = new String[1];

                String c = "[@]";
                final String[] nameEID = user.getEmail().split(c);

                status.setText(finalStatus);
                name.setText(nameEID[0]);


                if (user != null) {

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
                                    if (admins[i].equals(nameEID)) {
                                        //Toast.makeText(LoginActivity.this, "admin", Toast.LENGTH_SHORT).show();
                                        adminCheck = true;
                                    }
                                }

                                if (adminCheck) {
                                    name.setKeyListener(listener);
                                } else {
                                    name.setKeyListener(null);
                                }
                                // do your stuff here with value
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

                type.setOnItemSelectedListener(this);
                final Calendar myCalendar = Calendar.getInstance();

                date.setText(neweditDate);
                date.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(EditLeaveActivity.this, date2, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                String compareValue = neweditType;
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.leave_arrays, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                type.setAdapter(adapter);
                if (!compareValue.equals(null)) {
                    int spinnerPosition = adapter.getPosition(compareValue);
                    type.setSelection(spinnerPosition);
                }
                backup.setText(neweditBackup);
                checker.setVisibility(View.GONE);
                checker.setText(neweditChecker);


                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);



                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Update",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText(userInput.getText());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();*/



            }
        });

        alert.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ref.child("dates")
                        .orderByChild("checker")
                        .equalTo(finalChecker)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    String clubkey = childSnapshot.getKey();
                                    ref.child("dates").child(clubkey).setValue(null);
                                }

                                //TODO: update database
                                String approvedEntry = listLeave.get(position);
                                listLeave.remove(approvedEntry);

                                // refresh list
                                seeMyList();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                Toast.makeText(MyLeavesActivity.this, "Successfully deleted leave!", Toast.LENGTH_SHORT).show();
//                finish();
//                startActivity(getIntent());

            }
        });


        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //So sth here when "cancel" clicked.
            }
        });
        alert.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                //Update List
                //updateData();
                myAdapter.notifyDataSetChanged();
            }
            if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }
        }
    }

    protected void onResume()
    {
        super.onResume();


        myAdapter.notifyDataSetChanged();


    }

    private void updateData()
    {
        myAdapter = new ArrayAdapter<String>(this, R.layout.leave_viewer, R.id.ListMyLeave, listLeave);


        myAdapter.notifyDataSetChanged();
        setListAdapter(myAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //seeMyList();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }




}
