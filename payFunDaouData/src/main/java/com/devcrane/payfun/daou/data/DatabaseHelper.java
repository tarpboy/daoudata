package com.devcrane.payfun.daou.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.devcrane.payfun.daou.utility.BHelper;
import com.devcrane.payfun.daou.utility.FileHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	// The Android's default system path of your application database.
	public static final String DB_PATH = "/data/data/com.devcrane.payfun.daou/databases/";

	public static final String DB_NAME = "payfun";

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context) {

		super(context, DB_NAME + ".db", null, 3);
		this.myContext = context;
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			BHelper.db("DB Existed");
			// copyDataBase();
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into the default system path
			// of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();
			// set write permission

			BHelper.db("DB Created");
			try {
				BHelper.db("DB is started copy");
				copyDataBase();
				BHelper.db("DB is finished copy");
				this.close();

			} catch (Exception e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		File f = new File(DB_PATH + DB_NAME + ".db");
		return f.exists();
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the system folder, from where it can be accessed and handled. This is done by transfering bytestream.
	 * */
	public void deleteDatabase(){
		String outFileName = DB_PATH + DB_NAME + ".db";
		try{
			File f = new File(outFileName);
			if (f.exists())
				f.delete();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	private void copyDataBase() {

		try {
			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME + ".db";
			File f = new File(outFileName);
			FileHelper.chmod(f, 0777);
			if (f.exists())
				BHelper.db("File existed");
			else {
				BHelper.db("Can not access file");
			}
			// Open your local db as the input stream
			// InputStream myInput = myContext.getAssets().open(DB_NAME+".sqlite");
			// split file to multi parts
			// FileHelper.SplitFile("/sdcard/Barcode/Barcode",".sqlite");

			// SplitFileInputStream myInput = new SplitFileInputStream(DB_NAME,".sqlite",19,myContext.getAssets());

			// Open the empty db as the output stream
			FileOutputStream myOutput = new FileOutputStream(outFileName);
			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1024];
			int length;
			InputStream myInput = myContext.getAssets().open(DB_NAME + "1.db");
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
				BHelper.db("writing file with size buffer:" + String.valueOf(length));
			}
			myInput.close();

			// Close the streams
			myOutput.flush();
			myOutput.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			BHelper.db("Error Copying: " + ex.getMessage());
		}

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME + ".db";
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();
		BHelper.db("DB is closed");
		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	// Add your public helper methods to access and get content from the database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
	// to you to create adapters for your views.

}
