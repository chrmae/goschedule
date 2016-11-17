package com.projectprototype;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ReportsActivity extends ListActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinYear;
    Spinner spinMonth;
    DatabaseHelper db;
    List<String> listEIDs;
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        spinYear = (Spinner) findViewById(R.id.reports_year);
        spinMonth = (Spinner) findViewById(R.id.reports_month);

        spinYear.setOnItemSelectedListener(this);
        spinMonth.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.select_month, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth.setAdapter(adapter1);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = 2010; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        spinYear.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
       int intYear = Calendar.getInstance().get(Calendar.YEAR);
       String year = String.valueOf(intYear);
       int intMonth = Calendar.getInstance().get(Calendar.MONTH);
        String month = String.valueOf(intMonth);
        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.reports_year)
        {
            year = parent.getItemAtPosition(position).toString();
        }
        else if(spinner.getId() == R.id.reports_month)
        {
            month = parent.getItemAtPosition(position).toString();

        }

        //showReport();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void showReport(){

        listEIDs = db.getAllEID();
        //Log.i("Adap", listLeave.get(0));

        lv = (ListView) findViewById(R.id.listReport);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(ReportsActivity.this, R.layout.reports_viewer, R.id.eid_report, listEIDs);
       // getListView().setOnItemClickListener(this);
        lv.setAdapter(myAdapter);


    }



}