package com.projectprototype;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.projectprototype.lib.WeekViewEvent;

public class DatabaseHelper extends SQLiteOpenHelper{
	// database version
	private static final int database_VERSION = 1;
	// database name
	private static final String database_NAME = "GoScheduleLeaves";
	private static final String table_LEAVES = "FiledLeaves";
	private static final String leave_ID = "id";
	private static final String leave_NAME = "name";
	private static final String leave_DATE = "date";
	private static final String leave_TYPE = "type";
	private static final String leave_BACKUP = "backup";
	private static final String leave_STATUS = "status";
	private static final String leave_CHECKER = "checker";
	private static final String leave_MONTHYEAR = "MMYYYY";

	SQLiteDatabase db;
	Cursor cursor;

	public DatabaseHelper(Context context) {
		super(context, database_NAME, null, database_VERSION);
		db = this.getWritableDatabase();
	}



	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_BOOK_TABLE = "CREATE TABLE FiledLeaves ( " + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, " + "date TEXT, " + "type TEXT, " + "backup TEXT, " + "status TEXT, " + "checker TEXT, " + "MMYYYY TEXT )";
		db.execSQL(CREATE_BOOK_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS FiledLeaves");
		this.onCreate(db);

		db.execSQL("DROP TABLE IF EXISTS FiledResources");
		this.onCreate(db);
	}
	
	public boolean createLog(String name, String date, String type, String backup, String status, String checker) {
		// get reference of the BookDB database
		
		String[] dateArr = date.split("-");
		String monthyear = dateArr[0] + dateArr[2];
		//date = dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2];
		
		// make values to be inserted
		ContentValues values = new ContentValues();

		values.put(leave_NAME, name);
		values.put(leave_DATE, date);
		values.put(leave_TYPE, type);
		values.put(leave_BACKUP, backup);
		values.put(leave_STATUS, status);
		values.put(leave_CHECKER, checker);
		values.put(leave_MONTHYEAR, monthyear);

		// insert book
		long result = db.insert(table_LEAVES, null, values);
		
		if (result == -1){
			return false;
		}
		else{
			return true;
		}		
	}



	public void deleteAll() {
		db.delete(table_LEAVES,null,null);
	}
	
	public List<String> getAllInDate(String date) {
		List<String> output = new ArrayList<String>();
		Integer id;
		String name;
		String type;
		String backup;
		String status;
		
		date = date.replace("/","-");
		
		//Log.i("myApp", date);
		// select book query
		//String query = "SELECT  * FROM " + table_LEAVES;
		String query = "SELECT  * FROM FiledLeaves WHERE date = '" + date + "'";

		// get reference of the BookDB database
		cursor = db.rawQuery(query, null);

		// parse all results
		if (cursor.moveToFirst()) {
			do {
				id = Integer.parseInt(cursor.getString(0));
				name = cursor.getString(1).toUpperCase();
				type = cursor.getString(3);
				backup = cursor.getString(4);
				backup = backup.replace("-",".");

				// Add book to books
				//Log.i("myApp", Integer.toString(id));
				//Log.i("myApp", name + " ( " + type + " ) ");
				output.add(name + "\nType: " + type + "\nBack up: " + backup);
				//Log.i("myApp", cursor.getString(2));
				//Log.i("myapp", output.get(id-1));
			} while (cursor.moveToNext());
		}

		cursor.close();
		return output;
	}

	public List<String> getAllEID(){
		List<String> eid = new ArrayList<String>();

		Integer id;
		String name;
		String type;
		String backup;
		String status;
		String eid_query = "SELECT * FROM FiledLeaves";
		cursor = db.rawQuery(eid_query, null);

		// parse all results
		if (cursor.moveToFirst()) {
			do {
				name = cursor.getString(1).toUpperCase();

				// Add book to books
				//Log.i("myApp", Integer.toString(id));
				//Log.i("myApp", name + " ( " + type + " ) ");
				eid.add(name);
				//Log.i("myApp", cursor.getString(2));
				//Log.i("myapp", output.get(id-1));
			} while (cursor.moveToNext());
		}

		cursor.close();

		return eid;


	}


	public List<String> getAllInName(String name) {
		List<String> output = new ArrayList<String>();
		List<String> outputs = new ArrayList<String>();
		Integer id;
		String date;
		String type;
		String backup;
		String checker;
		String status;

		//Log.i("myApp", date);
		// select book query
		//String query = "SELECT  * FROM " + table_LEAVES;
		String query = "SELECT * FROM FiledLeaves WHERE name = '" + name + "' ORDER BY date DESC";

		cursor = db.rawQuery(query, null);

		// parse all results
		if (cursor.moveToFirst()) {
			do {
				id = Integer.parseInt(cursor.getString(0));
				date = cursor.getString(2);
				type = cursor.getString(3);
				backup = cursor.getString(4);
				backup = backup.replace("-",".");
				status = cursor.getString(5);
				checker = cursor.getString(6);

				// Add book to books
				//Log.i("myApp", Integer.toString(id));
				//Log.i("myApp", name + " ( " + type + " ) ");

				output.add(checker +"\nDate: " + date + "\nType: " + type + "\nBack up: " + backup + "\nStatus: " + status);
				//Log.i("myApp", cursor.getString(2));
				//Log.i("myapp", output.get(id-1));
			} while (cursor.moveToNext());
		}

		cursor.close();

		return output;
	}
	
	public boolean dateHit(String day, String monthyear){
		String date;
		String query = "SELECT  * FROM FiledLeaves WHERE MMYYYY = '" + monthyear + "'";
		cursor = db.rawQuery(query, null);

		boolean result = false;

		// parse all results
		if (cursor.moveToFirst()) {
			do {
				date = cursor.getString(2);
				String[] dateArr = date.split("-");

				if (day.equals(dateArr[1])){
					result = true;
				}
			} while (cursor.moveToNext());
		}

		// very important to close cursors, it causes memory leak
		cursor.close();

		return result;
	}

	public List<WeekViewEvent> getAll() {

		List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

		Integer id;
		String name;
		String date;
		String type;
		String backup;
		int hour = 1;
		Integer tempDay = 0;
		Integer tempMonth = 0;




		String query = "SELECT * FROM FiledLeaves ORDER BY date";
		cursor = db.rawQuery(query, null);

		// parse all results
		if (cursor.moveToFirst()) {
			do {

				id = Integer.parseInt(cursor.getString(0));
				name = cursor.getString(1);
				type = cursor.getString(3);
				date = cursor.getString(2);
				backup = cursor.getString(4);
				backup = backup.replace("-",".");

				//Context context = null;
				String[] dateParts = date.split("-");
				int dateMonth = Integer.parseInt(dateParts[0]);
				int dateDay = Integer.parseInt(dateParts[1]);
				int dateYear = Integer.parseInt(dateParts[2]);

				dateMonth = dateMonth + 1;


				if(dateDay != tempDay || dateMonth != tempMonth){
					hour = 1;

				}

				Calendar startTime = Calendar.getInstance();
				startTime.set(Calendar.HOUR_OF_DAY, hour - 1);
				startTime.set(Calendar.MINUTE, 0);
				startTime.set(Calendar.MONTH, dateMonth - 2);
				startTime.set(Calendar.DAY_OF_MONTH, dateDay);
				startTime.set(Calendar.YEAR, dateYear);
				Calendar endTime = (Calendar) startTime.clone();
				endTime.set(Calendar.HOUR_OF_DAY, (int) hour);
				endTime.set(Calendar.MINUTE, 0);
				WeekViewEvent event = new WeekViewEvent(id, name + "\nType: " + type + "\nBack up: " + backup, startTime, endTime);
				//event.setColor(000000);
				//event.getColor();
				events.add(event);


				hour = hour + 1;
				tempDay = dateDay;
				tempMonth = dateMonth;

			} while (cursor.moveToNext());
		}

		cursor.close();

		return events;
	}

	public List<String> getForApprovalLeaves() {
		List<String> output = new ArrayList<String>();
		Integer id;
		String date;
		String type;
		String name;
		String backup;
		String checker;

		//Log.i("myApp", date);
		// select book query
		//String query = "SELECT  * FROM " + table_LEAVES;
		String query = "SELECT * FROM FiledLeaves WHERE status = 'For Approval' ORDER BY date DESC";

		// get reference of the BookDB database
		cursor = db.rawQuery(query, null);

		// parse all results
		if (cursor.moveToFirst()) {
			do {
				id = Integer.parseInt(cursor.getString(0));
				date = cursor.getString(2);
				type = cursor.getString(3);
				name = cursor.getString(1);
				backup = cursor.getString(4);
				checker = cursor.getString(6);

				// Add book to books
				//Log.i("myApp", Integer.toString(id));
				//Log.i("myApp", name + " ( " + type + " ) ");
				output.add(checker +"\nName: " + name + "\nDate: " + date + "\nType: " + type + "\nBack up: " + backup);
				//Log.i("myApp", cursor.getString(2));
				//Log.i("myapp", output.get(id-1));
			} while (cursor.moveToNext());
		}

		cursor.close();

		return output;
	}

	public List<String> getApprovedLeaves() {
		List<String> output = new ArrayList<String>();
		Integer id;
		String date;
		String type;
		String name;
		String backup;
		String checker;

		//Log.i("myApp", date);
		// select book query
		//String query = "SELECT  * FROM " + table_LEAVES;
		String query = "SELECT * FROM FiledLeaves WHERE status = 'Approved' ORDER BY date DESC";

		// get reference of the BookDB database
		cursor = db.rawQuery(query, null);

		// parse all results
		if (cursor.moveToFirst()) {
			do {
				id = Integer.parseInt(cursor.getString(0));
				date = cursor.getString(2);
				type = cursor.getString(3);
				name = cursor.getString(1);
				backup = cursor.getString(4);
				checker = cursor.getString(6);

				// Add book to books
				//Log.i("myApp", Integer.toString(id));
				//Log.i("myApp", name + " ( " + type + " ) ");
				output.add(checker +"\nName: " + name + "\nDate: " + date + "\nType: " + type + "\nBack up: " + backup);
				//Log.i("myApp", cursor.getString(2));
				//Log.i("myapp", output.get(id-1));
			} while (cursor.moveToNext());
		}

		cursor.close();

		return output;
	}
}
