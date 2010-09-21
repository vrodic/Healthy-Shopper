package org.vrodic.hshopper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class HShopperDbAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private static String DB_NAME = "hshopper";
	   private static final String TAG = "HShopperDbAdapter";
    
	private final Context mCtx;
	private static final String DATABASE_CREATE1 = 
	"CREATE TABLE Products ("
			  + "_id  varchar(100) PRIMARY KEY,"
			  + "countrycodeorigin varchar(3),"
			  + "countrycodebarcode varchar(3),"
			  + "name  varchar(200),"
			  + "verified int,"
			  + "updated date"
			+ ");";
private static final String DATABASE_CREATE2 = 		
			 "CREATE TABLE ProductsEnumbers (" 
	+ "_id    INTEGER PRIMARY KEY,"	
    + "barcode varchar (100) References Products,"
    + "enumber varchar(4) References Enumbers"
+ ");";
			



 private static final String DATABASE_NAME = "hshopper";
  private static final int DATABASE_VERSION = 2;


 private static class DatabaseHelper extends SQLiteOpenHelper {

     DatabaseHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
     }

     @Override
     public void onCreate(SQLiteDatabase db) {

         db.execSQL(DATABASE_CREATE1);
         db.execSQL(DATABASE_CREATE2);
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w(TAG , "Upgrading database from version " + oldVersion + " to "
                 + newVersion + ", which will destroy all old data");
         db.execSQL("DROP TABLE IF EXISTS Products");
         db.execSQL("DROP TABLE IF EXISTS ProductsEnumbers");
         onCreate(db);
     }
 }
	
	   /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public HShopperDbAdapter (Context ctx) {
        this.mCtx = ctx;
    }

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public HShopperDbAdapter open() throws SQLException {
		 mDbHelper = new DatabaseHelper(mCtx);
	        mDb = mDbHelper.getWritableDatabase();
	        return this;
	}

	public void close() {
		mDbHelper.close();
	}

    public Cursor fetchAllAdditives(String productId) {

        return mDb.rawQuery("select _id, enumber FROM ProductsEnumbers WHERE barcode='" +productId + "'",null);
    }

    public boolean addENumber (String productId, String eNumber) {
    	String sql = "select count(*) FROM ProductsENumbers WHERE barcode='" + 
		productId + "' AND enumber='" + eNumber + "'";
//	System.out.println(sql);
    	Cursor c = mDb.rawQuery(sql, null);
c.moveToFirst();
if (Integer.parseInt(c.getString(0)) == 0) {
    	mDb.execSQL("INSERT INTO ProductsENumbers VALUES(NULL,'" + 
			productId + "','" + eNumber + "')");
    	return true;
}
	return false;
    }
    
    public String getProductName (String productId) {
    
    String sql = "SELECT name FROM Products WHERE _id='" + productId + "'";
     	Cursor c2 = mDb.rawQuery(sql, null);
      	if(c2.getCount() > 0) {
      		// TODO wrap time fix
      		c2.moveToFirst();
      		return c2.getString(0);
      	}
      	return "";
    }
      	
    public void deleteECodeFromProduct(String productId, String code) {
    	mDb.execSQL("DELETE FROM ProductsENumbers WHERE barcode='"+productId+"'  AND enumber ='" + 
				code + "'");
    }
    
public void insertOrUpdateProduct(String productId, String name) {
    	
    	String sql = "select count(*) FROM Products WHERE _id='" + 
    				productId + "'";
    //	System.out.println(sql);
    	Cursor c = mDb.rawQuery(sql, null);
    	c.moveToFirst();
    	if (Integer.parseInt(c.getString(0)) > 0) {
    		//System.out.println("Update Selections");
    		mDb.execSQL("UPDATE Products SET name='"+name+"' WHERE _id='" + 
    				productId + "'");
    	} else {
    		
    		//System.out.println("Insert Selections");
    		mDb.execSQL("INSERT INTO Products (_id,name) VALUES('"+ productId+ 
    				 "','" + name + "')");
    	}
    	c.close();
    }


    
        


}
