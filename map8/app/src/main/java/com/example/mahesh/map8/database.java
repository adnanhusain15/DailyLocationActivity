package com.example.mahesh.map8;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

public class database extends SQLiteOpenHelper {

    private  static  final String dbname="loc.db";
    private  static  final String tbname="locations";
    private  static  final String id="id";
    private  static  final String lat="lat";
    private  static  final String longi="long";
    private  static  final String addr="addr";
    private  static  final String date="date";
    String TAG = "DbHelper";

    public database(Context context) {
        super(context,dbname,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table if not exists "+tbname+"("
                    +id+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                    +lat+" varchar(30) not null,"
                    +longi+" varchar(30) not null,"
                    +addr+" varchar(30) not null,"
                    +date+" varchar(30) not null)");
//        Toast.makeText(,"created",Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+tbname);
        onCreate(sqLiteDatabase);
    }



    public boolean insertData(String c1,String c2,String c3,String c4)
    {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(lat,c1);
        cv.put(longi,c2);
        cv.put(addr,c3);
        cv.put(date,c4);

        if(db.insert(tbname,null,cv)==-1)
        {
            Log.d("Error","Error while inserting");
            return false;
        }
        else {
            return true;
        }

    }



    public HashMap<Double,Double> getTableAsString(Date from, Date end) {
        HashMap<Double,Double> locs = new HashMap<Double,Double>();

       Date check;
        SQLiteDatabase db= getReadableDatabase();
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Table %s:\n", tbname);
        Cursor allRows  = db.rawQuery("select  date, lat,long from " + tbname , null);
        Log.d(TAG, "getTableAsString: count    "+allRows.getCount());
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    try {
                        check=new SimpleDateFormat("dd/MM/yy", Locale.US).parse(allRows.getString(allRows.getColumnIndex("date")));
//                        Log.d(TAG, "getTableAsString: "+check);
                        Date today=new Date();
                        Log.d(TAG, "Starting date "+ from.toString()+" End: "+end.toString()+" to check: "+check.toString()+" check "+(check.compareTo(from)>0&&check.compareTo(end)<0));
                        if ((check.after(from)&&check.before(end)))
                        {
                            Log.d(TAG, "Lat : "+allRows.getString(allRows.getColumnIndex("lat"))+"Long: "+allRows.getString(allRows.getColumnIndex("long")));
                            locs.put(Double.parseDouble(allRows.getString(allRows.getColumnIndex("lat"))),Double.parseDouble(allRows.getString(allRows.getColumnIndex("long"))));
                        }
                        else {
                            Log.d(TAG, "getTableAsString: kshadkuhadskjhasdkjhkasd");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }


            } while (allRows.moveToNext());
        }

        return locs;
    }

    public void truncate()
    {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("drop TABLE " + tbname);
    }

}
