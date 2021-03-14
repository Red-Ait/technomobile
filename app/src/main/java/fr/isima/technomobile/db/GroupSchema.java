package fr.isima.technomobile.db;

import android.provider.BaseColumns;

public class GroupSchema {

    public static final String DB_NAME = "fr.isima.technomobile.db";
    public static final int DB_VERSION = 1;

    public class Group implements BaseColumns {
        public static final String TABLE_NAME = "group_table";
        public static final String COL_TITLE = "title";
        public static final String COL_ID = "id";
    }

}
