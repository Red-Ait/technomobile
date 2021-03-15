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

    public class Member implements BaseColumns {
        public static final String TABLE_NAME = "member_table";
        public static final String COL_PHONE_NO = "phone_no";
        public static final String COL_CONTACT_NAME = "contact_name";
        public static final String COL_GROUP_ID = "group_id";
        public static final String COL_ID = "id";
    }

}
