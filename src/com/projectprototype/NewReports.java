package com.projectprototype;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class NewReports extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spinYear;
    Spinner spinMonth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reports);

        spinYear = (Spinner) findViewById(R.id.reports_year);
        spinMonth = (Spinner) findViewById(R.id.reports_month);

        spinYear.setOnItemSelectedListener(this);
        spinMonth.setOnItemSelectedListener(this);

        Toast.makeText(NewReports.this, spinMonth.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
        /*ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.select_month, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth.setAdapter(adapter1);*/

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int i = 2010; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        spinYear.setAdapter(adapter);

        Toast.makeText(NewReports.this, spinYear.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
