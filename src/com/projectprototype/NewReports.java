package com.projectprototype;

import android.app.Activity;
import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewReports extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinYear;
    Spinner spinMonth;
    DatabaseHelper db;
    List<String> listLeave, listVLcount, listSLcount, listELcount;
    Button backHome;
    String year;
    String month;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reports);

        spinYear = (Spinner) findViewById(R.id.reports_year);
        spinMonth = (Spinner) findViewById(R.id.reports_month);

        spinYear.setOnItemSelectedListener(this);
        spinMonth.setOnItemSelectedListener(this);


        /*ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.select_month, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth.setAdapter(adapter1);*/

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR) + 5;
        for(int i = 2010; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        spinYear.setAdapter(adapter);


        backHome = (Button) findViewById(R.id.reports_backhome);

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backHome(v);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        year = spinYear.getSelectedItem().toString();
        month = spinMonth.getSelectedItem().toString();

        switch (month) {
            case "January":
                month = "01";
                break;
            case "February":
                month = "02";
                break;
            case "March":
                month = "03";
                break;
            case "April":
                month = "04";
                break;
            case "May":
                month = "05";
                break;
            case "June":
                month = "06";
                break;
            case "July":
                month = "07";
                break;
            case "August":
                month = "08";
                break;
            case "September":
                month = "09";
                break;
            case "October":
                month = "10";
                break;
            case "November":
                month = "11";
                break;
            case "December":
                month = "12";
                break;
        }


        Toast.makeText(NewReports.this, year + " " + month, Toast.LENGTH_SHORT).show();

        showReports(year, month);

    }

    public void backHome(View view) {
        finish();
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void showReports(String year, String month){
        db = new DatabaseHelper(this);

        listLeave = db.getAllEID(year, month);
        ArrayAdapter myAdapter = new ArrayAdapter<String>(this, R.layout.reports_viewer, R.id.eid_report, listLeave);
        ListView listview = (ListView) findViewById(R.id.listReportEID);
        listview.setAdapter(myAdapter);

        listVLcount = db.getTotalCount(year, month);
        ArrayAdapter VLAdapter = new ArrayAdapter<String>(this, R.layout.reports_viewer, R.id.vl_report, listVLcount);
        ListView VLlistview = (ListView) findViewById(R.id.listReportVL);
        VLlistview.setAdapter(VLAdapter);

//        listVLcount = db.getVLcount(year, month);
//        ArrayAdapter VLAdapter = new ArrayAdapter<String>(this, R.layout.reports_viewer, R.id.vl_report, listVLcount);
//        ListView VLlistview = (ListView) findViewById(R.id.listReportVL);
//        VLlistview.setAdapter(VLAdapter);
//
//        listSLcount = db.getSLcount(year, month);
//        ArrayAdapter SLAdapter = new ArrayAdapter<String>(this, R.layout.reports_viewer, R.id.sl_report, listSLcount);
//        ListView SLlistview = (ListView) findViewById(R.id.listReportSL);
//        SLlistview.setAdapter(SLAdapter);
//
//        listELcount = db.getELcount(year, month);
//        ArrayAdapter ELAdapter = new ArrayAdapter<String>(this, R.layout.reports_viewer, R.id.el_report, listELcount);
//        ListView ELlistview = (ListView) findViewById(R.id.listReportEL);
//        ELlistview.setAdapter(ELAdapter);
    }

}
