package ddwu.mobile.finalproject.ma02_20181022;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyTravelDBHelper extends SQLiteOpenHelper {
    final static String TAG="AddressDBHelper";

    final static String DB_NAME = "travel_table";

    public final static String TABLE_NAME = "MyTravel_table";

    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_ADDR = "address";
    public final static String COL_TEL = "tel";
    public final static String COL_X = "mapx";
    public final static String COL_Y = "mapy";
    public final static String COL_FILE = "imageFileName";
    public final static String COL_MEMO = "memo";
    public final static String COL_DATE = "date";
    public final static String COL_PHOTO = "photo";

    public MyTravelDBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME +" ("+ COL_ID +" integer primary key autoincrement, " +
                COL_TITLE + " TEXT," + COL_ADDR + " TEXT," + COL_TEL + " TEXT,"
                + COL_X + " TEXT," + COL_Y + " TEXT," + COL_FILE + " TEXT," + COL_MEMO + " TEXT," + COL_DATE + " TEXT," + COL_PHOTO + " TEXT)";

        Log.d(TAG, sql);
        db.execSQL(sql);

//        db.execSQL("insert into " + TABLE_NAME + " values (null, '육성재', '010-1234-1234','yook@naver.com',  '남','26', '1995-05-02', 'R.mipmap.yook');");
//        db.execSQL("insert into " + TABLE_NAME + " values (null, '동덕여대', '서울 성북구 화월곡동','02-123-1234', '37.6063200', '127.0418080',null, null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
