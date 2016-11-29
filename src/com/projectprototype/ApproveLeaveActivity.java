package com.projectprototype;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveLeaveActivity extends ListActivity implements AdapterView.OnItemClickListener{

    DatabaseHelper db;
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private int alertDialogView;

    List<String> listForApprovalLeave, listApprovedLeave, listRejectedLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_leave);

        db = new DatabaseHelper(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        Button HomeButton = (Button) findViewById(R.id.faCancel);
        Button Approved = (Button) findViewById(R.id.seeApproved);
        Button ForApproval = (Button) findViewById(R.id.seeForApproval);
        Button Rejected = (Button) findViewById(R.id.seeRejected);

        List<String> listLeave = db.getForApprovalLeaves();
        ImageView approvedLeave = (ImageView) findViewById(R.id.editleave);
        //Log.i("Adap", listLeave.get(0));

        //lv = (ListView) findViewById(android.R.id.list);
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.leave_viewer, R.id.ListMyLeave, listLeave));
        getListView().setOnItemClickListener(this);



        HomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeApproved();
            }
        });

        Rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeRejected();
            }
        });
        ForApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeForApproval();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listApprovedLeave = db.getApprovedLeaves();
        listRejectedLeave = db.getRejectedLeaves();
        listForApprovalLeave = db.getForApprovalLeaves();
    }

    private void seeForApproval() {
        //ImageView approvedLeave = (ImageView) findViewById(R.id.editleave);
        //Log.i("Adap", listLeave.get(0));

        //lv = (ListView) findViewById(android.R.id.list);
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.leave_viewer, R.id.ListMyLeave, listForApprovalLeave));
        getListView().setOnItemClickListener(this);

    }

    private void seeApproved() {
        //ImageView approvedLeave = (ImageView) findViewById(R.id.editleave);
        //Log.i("Adap", listLeave.get(0));

        //lv = (ListView) findViewById(android.R.id.list);
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.approved_leave_viewer, R.id.ListMyLeave, listApprovedLeave));
        getListView().setOnItemClickListener(null);
    }

    private void seeRejected() {
        //ImageView approvedLeave = (ImageView) findViewById(R.id.editleave);
        //Log.i("Adap", listLeave.get(0));

        //lv = (ListView) findViewById(android.R.id.list);
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.approved_leave_viewer, R.id.ListMyLeave, listRejectedLeave));
        getListView().setOnItemClickListener(null);
    }



    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        TextView caption = (TextView) findViewById(R.id.ListMyLeave);
        final DatabaseReference ref = database.getReference();

        String toEdit = (String) parent.getItemAtPosition(position);
        String delim = "[\n]";
        String[] finalLeave = toEdit.split(delim);

        String checker = finalLeave[0];
        checker = checker.replace(".","-");

        String a = "[ ]";
        String splitname = finalLeave[1];
        String [] tempname = splitname.split(a);


        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Actions");
        alert.setMessage("Do you want to approve " + tempname[1].toLowerCase() + "'s filed leave?");

        final String finalChecker = checker;

        alert.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ref.child("dates")
                        .orderByChild("checker")
                        .equalTo(finalChecker)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    String clubkey = childSnapshot.getKey();
                                    ref.child("dates").child(clubkey).child("status").setValue("Approved");
                                }

                                //TODO: update database
                                String approvedEntry = listForApprovalLeave.get(position);
                                listForApprovalLeave.remove(approvedEntry);
                                listApprovedLeave.add(approvedEntry);

                                // refresh list
                                seeForApproval();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                Toast.makeText(ApproveLeaveActivity.this, "Successfully approved leave!", Toast.LENGTH_SHORT).show();
//                finish();
//                startActivity(getIntent());

            }
        });

        /*alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
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
                                String approvedEntry = listForApprovalLeave.get(position);
                                listForApprovalLeave.remove(approvedEntry);

                                // refresh list
                                seeForApproval();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                Toast.makeText(ApproveLeaveActivity.this, "Successfully deleted leave!", Toast.LENGTH_SHORT).show();
//                finish();
//                startActivity(getIntent());

            }
        });*/

        alert.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ref.child("dates")
                        .orderByChild("checker")
                        .equalTo(finalChecker)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    String clubkey = childSnapshot.getKey();
                                    ref.child("dates").child(clubkey).child("status").setValue("Rejected");
                                }

                                //TODO: update database
                                String approvedEntry = listForApprovalLeave.get(position);
                                listForApprovalLeave.remove(approvedEntry);
                                listApprovedLeave.add(approvedEntry);

                                // refresh list
                                seeForApproval();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                Toast.makeText(ApproveLeaveActivity.this, "Leave has been rejected!", Toast.LENGTH_SHORT).show();
//                finish();
//                startActivity(getIntent());

            }
        });

/*
        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ref.child("dates").orderByChild("checker");
                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            String clubkey = childSnapshot.getKey();
                            ref.child("dates").child(clubkey).child("status").removeValue();
                        }

                        //TODO: update database
                        String approvedEntry = listForApprovalLeave.get(position);
                        listForApprovalLeave.remove(approvedEntry);

                        // refresh list
                        seeForApproval();
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });

                Toast.makeText(ApproveLeaveActivity.this, "Successfully deleted leave!", Toast.LENGTH_SHORT).show();
//                finish();
//                startActivity(getIntent());

            }
        });
       */

        alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //So sth here when "cancel" clicked.
            }
        });
        alert.show();

        /*final AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView);
        adb.setTitle("Approve leave?");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        final String finalChecker = checker;
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {*/

                //Toast.makeText(ApproveLeaveActivity.this, toEdit, Toast.LENGTH_SHORT).show();







    }

    /*private void updateEntry(String name, String date, String type, String backup) {
        //DatabaseReference ref = database.getReference("dates").;
        //ref.orderByChild("name").equalTo(name).orderByChild("date").equalTo(date);
            /*Query getname = ref.orderByChild("name").equalTo(name);
            Query getdate = getname.orderByChild("date").equalTo(date);
            Query gettype = getdate.orderByChild("type").equalTo(type);
            Query getbackup = gettype.orderByChild("backup").equalTo(backup);
            ChildEventListener status = getbackup.orderByChild("status").equalTo("For Approval").addListenerForSingleValueEvent (*/

                /*Map<String,Object> dataput = new HashMap<String, Object>();
                dataput.put("name", name);
                dataput.put("date", date);
                dataput.put("type", type);
                dataput.put("backup", backup);
                dataput.put("status", "Approved");
                ref.updateChildren(dataput);*/


    //);*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}