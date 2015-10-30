package com.nirma.varunraval.nuconnect.Database;

import android.provider.BaseColumns;
import android.provider.ContactsContract;

/**
 * Created by Varun on 10/22/2015.
 */
public final class DatabaseContract {

    public DatabaseContract(){}

    public static abstract class SentMessages implements BaseColumns{
        public static final String TABLE_NAME = "table_SentMessages";
        public static final String COLUMN_NAME_MSGID = "msg_id";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIMEFROM = "time_from";
        public static final String COLUMN_NAME_TIMETO = "time_to";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_VENUE = "venue";
        public static final String COLUMN_NAME_MSGTYPE = "msg_type";
        public static final String COLUMN_NAME_DATESENTON = "date_sent_on";
    }

    public static abstract class AllMessages implements BaseColumns{
        public static final String TABLE_NAME = "table_AllMessages";
        public static final String COLUMN_NAME_MSGID = "msg_id";
        public static final String COLUMN_NAME_FROM_NAME = "from_name";
        public static final String COLUMN_NAME_FROM_ID = "from_id";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIMEFROM = "time_from";
        public static final String COLUMN_NAME_TIMETO = "time_to";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_VENUE = "venue";
        public static final String COLUMN_NAME_MSGTYPE = "msg_type";
        public static final String COLUMN_NAME_DATESENTON = "date_sent_on";
    }

}