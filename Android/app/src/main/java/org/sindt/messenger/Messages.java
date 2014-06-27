package org.sindt.messenger;


        import java.text.DateFormat;
        import java.util.ArrayList;

        import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.DatabaseUtils;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.database.sqlite.SQLiteDatabase;
        import android.util.Log;

public class Messages extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "messenger.db";
    public static final String MESSAGES_TABLE_NAME = "messages";
    public static final String MESSAGES_COLUMN_ID = "id";
    public static final String MESSAGES_COLUMN_MESSAGE = "message";
    public static final String MESSAGES_COLUMN_MESSAGETYPE = "messagetype";
    public static final String MESSAGES_COLUMN_MESSAGETITLE = "messagetitle";
    public static final String MESSAGES_COLUMN_DATETIME = "datetime";

    static final String TAG = "MSGR";

    Context context;


    public Messages(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table " + MESSAGES_TABLE_NAME +
                        " (" + MESSAGES_COLUMN_ID + " integer primary key, " + MESSAGES_COLUMN_MESSAGE + " text," + MESSAGES_COLUMN_MESSAGETYPE + " text," + MESSAGES_COLUMN_MESSAGETITLE + " text," + MESSAGES_COLUMN_DATETIME + " text)"
        );
        Log.i(TAG, "onCreate - SQL");
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Todo --- upragde code, if needed....
        //db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
        //onCreate(db);
    }

    public boolean insertMessage(String message, String messagetype, String messagetitle, Long datetime)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MESSAGES_COLUMN_MESSAGE, message);
        contentValues.put(MESSAGES_COLUMN_MESSAGETITLE, messagetitle);
        contentValues.put(MESSAGES_COLUMN_MESSAGETYPE, messagetype);
        contentValues.put(MESSAGES_COLUMN_DATETIME, datetime);


        db.insert(MESSAGES_TABLE_NAME, null, contentValues);
        Log.i(TAG, "insertEvent - SQL " + numberOfMessages());
        db.close();

        return true;
    }
    public Cursor getMessage(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + MESSAGES_TABLE_NAME + " where " + MESSAGES_COLUMN_ID + "="+id+"", null );
        db.close();
        return res;
    }
    public int numberOfMessages(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MESSAGES_TABLE_NAME);
        db.close();
        return numRows;
    }


    public Integer deleteMessage (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(MESSAGES_TABLE_NAME,
                MESSAGES_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }
    public ArrayList getAllMessages()
    {
        ArrayList array_list = new ArrayList();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + MESSAGES_TABLE_NAME + " ORDER BY " + MESSAGES_COLUMN_ID + " DESC", null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            array_list.add(DateFormat.getDateTimeInstance().format(Long.parseLong(res.getString(res.getColumnIndex(MESSAGES_COLUMN_DATETIME)))).toString() + ": " +res.getString(res.getColumnIndex(MESSAGES_COLUMN_MESSAGE)));
            //Log.i(TAG, "getAllMessages - SQL " + res.getString(res.getColumnIndex(EVENTS_COLUMN_MESSAGE)));
            res.moveToNext();
        }
        db.close();
        return array_list;
    }
}