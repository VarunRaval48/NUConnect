package com.nirma.varunraval.nuconnect.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Varun on 10/22/2015.
 */
public class SentMessagesDatabasedbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME = "MessageContract.db" ;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DatabaseContract.SentMessages.TABLE_NAME + " (" +
                    DatabaseContract.SentMessages._ID + " INTEGER PRIMARY KEY," +
                    DatabaseContract.SentMessages.COLUMN_NAME_SUBJECT + TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_DATE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_TIMEFROM+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_TIMETO+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_VENUE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_MSGTYPE+ TEXT_TYPE + COMMA_SEP +
                    DatabaseContract.SentMessages.COLUMN_NAME_MSGTYPE+ TEXT_TYPE + COMMA_SEP +
            ")";

    public SentMessagesDatabasedbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

      @Override
      public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      }
}
