package ddwu.mobile.finalproject.ma02_20181022;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MyTravelDBManager {
    MyTravelDBHelper travelDBHelper = null;
    Cursor cursor = null;

    public  MyTravelDBManager(Context context){
        travelDBHelper = new MyTravelDBHelper(context);
    }

    public ArrayList<TravelDto> getAllTravel(){
        ArrayList addressList = new ArrayList();
        SQLiteDatabase db = travelDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ MyTravelDBHelper.TABLE_NAME, null);

        while(cursor.moveToNext()){
            long id = cursor.getInt(cursor.getColumnIndex(MyTravelDBHelper.COL_ID));
            String title = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_TITLE));
            String address = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_ADDR));
            String tel = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_TEL));
            String x = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_X));
            String y = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_Y));
            String file = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_FILE));
            String memo = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_MEMO));
            String date = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_DATE));
            String photo = cursor.getString(cursor.getColumnIndex(MyTravelDBHelper.COL_PHOTO));

            addressList.add(new TravelDto(id,  title,  address,  tel, x, y,  file,  memo, date, photo));
        }

        cursor.close();
        travelDBHelper.close();
        return addressList;
    }

    public boolean addNewTravel(TravelDto newTravel){
        SQLiteDatabase db = travelDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();

        value.put(MyTravelDBHelper.COL_TITLE, newTravel.getTitle());
        value.put(MyTravelDBHelper.COL_ADDR, newTravel.getAddress());
        value.put(MyTravelDBHelper.COL_TEL, newTravel.getTel());
        value.put(MyTravelDBHelper.COL_X, newTravel.getMapx());
        value.put(MyTravelDBHelper.COL_Y, newTravel.getMapy());
        value.put(MyTravelDBHelper.COL_FILE, newTravel.getImageFileName());
        value.put(MyTravelDBHelper.COL_MEMO, newTravel.getMemo());
        value.put(MyTravelDBHelper.COL_PHOTO, newTravel.getPhotoFile());

        long count = db.insert(MyTravelDBHelper.TABLE_NAME, null, value);
        travelDBHelper.close();
        if(count > 0) return true;
        return false;
    }

    public boolean removeTravel(long id){
        SQLiteDatabase db = travelDBHelper.getWritableDatabase();
        String whereClause = MyTravelDBHelper.COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };
        int result = db.delete(MyTravelDBHelper.TABLE_NAME, whereClause,whereArgs);
        travelDBHelper.close();
        if (result > 0) return true;
        return false;
    }

    public boolean modifyTravel(TravelDto travel){
        SQLiteDatabase db = travelDBHelper.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put(MyTravelDBHelper.COL_TITLE, travel.getTitle());
        value.put(MyTravelDBHelper.COL_ADDR, travel.getAddress());
        value.put(MyTravelDBHelper.COL_TEL, travel.getTel());
        value.put(MyTravelDBHelper.COL_X, travel.getMapx());
        value.put(MyTravelDBHelper.COL_Y, travel.getMapy());
        value.put(MyTravelDBHelper.COL_FILE, travel.getImageFileName());
        value.put(MyTravelDBHelper.COL_MEMO, travel.getMemo());
        value.put(MyTravelDBHelper.COL_DATE, travel.getDate());
        value.put(MyTravelDBHelper.COL_PHOTO, travel.getPhotoFile());

        String whereClause = MyTravelDBHelper.COL_ID+"=?";
        String[] whereArgs = new String[] {String.valueOf(travel.get_id())};

        int result = db.update(MyTravelDBHelper.TABLE_NAME, value, whereClause, whereArgs);

        travelDBHelper.close();
        if (result > 0) return true;
        return false;
    }

    public void close() {
        if (travelDBHelper != null) travelDBHelper.close();
        if (cursor != null) cursor.close();
    };
}
