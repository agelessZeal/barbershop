package com.odelan.qwork.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDB.db";
    public static final String ALARMS_TABLE_NAME = "alarm";
    public static final String ALARMS_COLUMN_ID = "id";      // primary key : default
    public static final String ALARMS_EVENT_ID = "event_id"; // long-int
    public static final String ALARM_IS_SET = "is_alarm";  // 1- set alarm, 0- unset alarm
    public static final String ALARM_TYPE = "alarm_type"; // 0- 5min, 1- 15min, 2- 30min
    private HashMap hp;

    private static DBHelper _instance;

    public static DBHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new DBHelper(context);
        }
        return _instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        /*db.execSQL(
                "create table " + ALARMS_TABLE_NAME +
                        "(id integer primary key, pID text, type text, name text, amount integer, unitPrice real)"
        );*/
        db.execSQL(
                "create table " + ALARMS_TABLE_NAME + "(" +
                        ALARMS_COLUMN_ID + " integer primary key," +
                        ALARMS_EVENT_ID + " integer," +
                        ALARM_IS_SET + " integer," +
                        ALARM_TYPE + " integer" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS products");
        onCreate(db);
    }

    public boolean insertAlarm  (long eID, int isAlarm, int alarmType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARMS_EVENT_ID, eID);
        contentValues.put(ALARM_IS_SET, isAlarm);
        contentValues.put(ALARM_TYPE, alarmType);
        db.insert(ALARMS_TABLE_NAME, null, contentValues);
        return true;
    }



    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ALARMS_TABLE_NAME);
        return numRows;
    }

    public boolean updateAlarm (int id, long eID, int isAlarm, int alarmType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ALARMS_EVENT_ID, eID);
        contentValues.put(ALARM_IS_SET, isAlarm);
        contentValues.put(ALARM_TYPE, alarmType);
        db.update(ALARMS_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteAlarmWithPrimaryKey (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ALARMS_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public Integer deleteAlarm (int eID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(ALARMS_TABLE_NAME,
                ALARMS_EVENT_ID + " = ? ",
                new String[] { String.valueOf(eID) });
    }

    public void deleteAllAlarm () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ ALARMS_TABLE_NAME);
    }


    /*
    public ArrayList<DBModel> getAllOrders()
    {
        ArrayList<DBModel> array_list = new ArrayList<DBModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ORDERS_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            DBModel dm = new DBModel();
            dm.id = res.getInt(res.getColumnIndex(ORDERS_COLUMN_ID));
            dm.pID = res.getString(res.getColumnIndex(ORDERS_COLUMN_PID));
            dm.type = res.getString(res.getColumnIndex(ORDERS_COLUMN_TYPE));
            dm.name = res.getString(res.getColumnIndex(ORDERS_COLUMN_NAME));
            dm.amount = res.getInt(res.getColumnIndex(ORDERS_COLUMN_AMOUNT));
            dm.unitPrice = res.getFloat(res.getColumnIndex(ORDERS_COLUMN_UNITPRICE));
            array_list.add(dm);
            res.moveToNext();
        }
        return array_list;
    }

    public int getSeatBookID(){
        int result = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + ORDERS_TABLE_NAME + " where type='" + MyApplication.TYPE_SEAT + "'", null);
        if(res.getCount()!=0){
            res.moveToFirst();
            result = res.getInt(res.getColumnIndex(ORDERS_COLUMN_ID));
        }

        return result;
    }

    public int getCertReqID(){
        int result = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ORDERS_TABLE_NAME + " where type='" + MyApplication.TYPE_CERT + "'", null );
        if(res.getCount()!=0){
            res.moveToFirst();
            result = res.getInt(res.getColumnIndex(ORDERS_COLUMN_ID));
        }
        return result;
    }

    public String getSeatBookPID(){
        String result = "-1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + ORDERS_TABLE_NAME + " where type='" + MyApplication.TYPE_SEAT + "'", null);
        if(res.getCount()!=0){
            res.moveToFirst();
            result = res.getString(res.getColumnIndex(ORDERS_COLUMN_PID));
        }

        return result;
    }

    public String getCertReqPID(){
        String result = "-1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ORDERS_TABLE_NAME + " where type='" + MyApplication.TYPE_CERT + "'", null );
        if(res.getCount()!=0){
            res.moveToFirst();
            result = res.getString(res.getColumnIndex(ORDERS_COLUMN_PID));
        }
        return result;
    }

    public ArrayList<String> getProductPIDs()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select pID from " + ORDERS_TABLE_NAME + " where type='" + MyApplication.TYPE_PRODUCTS + "'", null );
        if(res.getCount()!=0) {
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                String pID = res.getString(res.getColumnIndex(ORDERS_COLUMN_PID));
                array_list.add(pID);
                res.moveToNext();
            }
        }
        return array_list;
    }
    */
}