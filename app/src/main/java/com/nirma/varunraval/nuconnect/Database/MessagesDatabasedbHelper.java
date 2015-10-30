package com.nirma.varunraval.nuconnect.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Varun on 10/22/2015.
 */
public class MessagesDatabasedbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME = "MessageContract.db" ;

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATE";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES_SENT =
            "CREATE TABLE " + DatabaseContract.SentMessages.TABLE_NAME + " (" +
                    DatabaseContract.SentMessages.COLUMN_NAME_MSGID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_SUBJECT + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_DATE+ DATE_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_TIMEFROM+ DATE_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_TIMETO+ DATE_TYPE+ COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_VENUE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_MSGTYPE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_MESSAGE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_DATESENTON+ DATE_TYPE +
            ")";

    private static final String SQL_CREATE_ENTRIES_ALL =

            "CREATE TABLE " + DatabaseContract.AllMessages.TABLE_NAME + " (" +
                    DatabaseContract.AllMessages.COLUMN_NAME_MSGID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_FROM_ID + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_FROM_NAME + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_SUBJECT + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_DATE+ DATE_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_TIMEFROM+ DATE_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_TIMETO+ DATE_TYPE+ COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_VENUE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_MSGTYPE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_MESSAGE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.AllMessages.COLUMN_NAME_DATESENTON+ DATE_TYPE +
                    ")";

    private static final String SQL_DELETE_ENTRIES_SENT = "DROP TABLE if EXISTS "+DatabaseContract.SentMessages.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_ALL = "DROP TABLE if EXISTS "+DatabaseContract.AllMessages.TABLE_NAME;

    public MessagesDatabasedbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ENTRIES_SENT);
        db.execSQL(SQL_CREATE_ENTRIES_ALL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_SENT);
        db.execSQL(SQL_CREATE_ENTRIES_ALL);
        onCreate(db);
    }

    public boolean insertRowSent(Integer msg_id, String s, String d, String t_f, String t_t, String v,
                             String msg_type, String msg_optional, String date_sent_on){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_MSGID , msg_id);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_SUBJECT, s);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_DATE, d);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_TIMEFROM, t_f);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_TIMETO, t_t);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_VENUE, v);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_MSGTYPE, msg_type);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_MESSAGE, msg_optional);
        contentValues.put(DatabaseContract.SentMessages.COLUMN_NAME_DATESENTON, date_sent_on);

        db.insert(DatabaseContract.SentMessages.TABLE_NAME, null, contentValues);
        Log.i("SentMessagedb", "Inserted");
        return true;
    }

    public ArrayList getRowsSent(ArrayList msgids){

        ArrayList<String> rows = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if(msgids == null) {
            cursor = db.rawQuery("SELECT * from " + DatabaseContract.SentMessages.TABLE_NAME, null);
    //        }
    //        else{
    //            cursor = db.rawQuery();
    //        }
            cursor.moveToFirst();
            JSONObject jsonObject_cover, data;

            try {
                while (!cursor.isAfterLast()) {
                    data = new JSONObject();
                    jsonObject_cover = new JSONObject();
                    data.put("s", cursor.getString(cursor.getColumnIndex("subject")));
                    data.put("v", cursor.getString(cursor.getColumnIndex("venue")));
                    data.put("t_f", cursor.getString(cursor.getColumnIndex("time_from")));
                    data.put("t_t", cursor.getString(cursor.getColumnIndex("time_to")));
                    data.put("d", cursor.getString(cursor.getColumnIndex("date")));
                    data.put("msg_optional", cursor.getString(cursor.getColumnIndex("message")));
                    jsonObject_cover.put("msg_id", cursor.getString(cursor.getColumnIndex("msg_id")));
                    jsonObject_cover.put("msg_type", cursor.getString(cursor.getColumnIndex("msg_type")));
                    jsonObject_cover.put("date_sent_on", cursor.getString(cursor.getColumnIndex("date_sent_on")));
                    jsonObject_cover.put("data", data);

                    Log.i("SentMsgDB", jsonObject_cover.toString());

                    rows.add(jsonObject_cover.toString());
                    cursor.moveToNext();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i("SentMessageDatabasedb", rows.toString());
        return rows;
    }

    public boolean insertRowAll(Integer msg_id, String from_id, String from_name, String s, String d, String t_f, String t_t, String v,
                             String msg_type, String msg_optional, String date_sent_on){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_MSGID , msg_id);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_FROM_ID, from_id);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_FROM_NAME, from_name);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_SUBJECT, s);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_DATE, d);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_TIMEFROM, t_f);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_TIMETO, t_t);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_VENUE, v);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_MSGTYPE, msg_type);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_MESSAGE, msg_optional);
        contentValues.put(DatabaseContract.AllMessages.COLUMN_NAME_DATESENTON, date_sent_on);

        db.insert(DatabaseContract.AllMessages.TABLE_NAME, null, contentValues);
        Log.i("AllMessagedb", "Inserted");
        return true;
    }

    public ArrayList getRowsAll(ArrayList msgids){

        ArrayList<String> rows = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        if(msgids == null) {
            cursor = db.rawQuery("SELECT * from " + DatabaseContract.AllMessages.TABLE_NAME, null);
            //        }
            //        else{
            //            cursor = db.rawQuery();
            //        }
            cursor.moveToFirst();
            JSONObject jsonObject_cover, data;

            try {
                while (!cursor.isAfterLast()) {
                    data = new JSONObject();
                    jsonObject_cover = new JSONObject();
                    data.put("s", cursor.getString(cursor.getColumnIndex("subject")));
                    data.put("v", cursor.getString(cursor.getColumnIndex("venue")));
                    data.put("t_f", cursor.getString(cursor.getColumnIndex("time_from")));
                    data.put("t_t", cursor.getString(cursor.getColumnIndex("time_to")));
                    data.put("d", cursor.getString(cursor.getColumnIndex("date")));
                    data.put("msg_optional", cursor.getString(cursor.getColumnIndex("message")));

                    jsonObject_cover.put("msg_id", cursor.getString(cursor.getColumnIndex("msg_id")));
                    jsonObject_cover.put("msg_type", cursor.getString(cursor.getColumnIndex("msg_type")));
                    jsonObject_cover.put("date_sent_on", cursor.getString(cursor.getColumnIndex("date_sent_on")));
                    jsonObject_cover.put("from_id", cursor.getString(cursor.getColumnIndex("from_id")));
                    jsonObject_cover.put("from_name", cursor.getString(cursor.getColumnIndex("from_name")));
                    jsonObject_cover.put("data", data);

                    Log.i("SentMsgDB", jsonObject_cover.toString());

                    rows.add(jsonObject_cover.toString());
                    cursor.moveToNext();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i("AllMessageDatabasedb", rows.toString());
        return rows;
    }
}
