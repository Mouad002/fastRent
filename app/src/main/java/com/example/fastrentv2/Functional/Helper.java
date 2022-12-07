package com.example.fastrentv2.Functional;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Helper extends SQLiteOpenHelper
{
    private static final String CITY = "CITY";
    private static final String CITYNAME = "CITYNAME";
    private static final String CATEGORIY = "CATEGORIY";
    private static final String CATEGORIYNAME = "CATEGORIYNAME";

    public Helper(@Nullable Context context) {
        super(context,"CITIES",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query1 = "CREATE TABLE "+CITY+" ( "+CITYNAME+" TEXT PRIMARY KEY )";
        String query2 = "CREATE TABLE "+CATEGORIY+" ( "+CATEGORIYNAME+" TEXT PRIMARY KEY )";
        String query3 = "INSERT INTO "+CITY+"("+CITYNAME+") VALUES('Agadir'),('Tarodant'),('Casablanca'),('Rabat'),('Taoujdat'),('Dakhla');";
        String query4 = "INSERT INTO "+CATEGORIY+"("+CATEGORIYNAME+") VALUES('Bike'),('House'),('Car');";
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.execSQL(query4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<String> getCities()
    {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT "+CITYNAME+" FROM "+CITY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do
            {
                list.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<String> getCategories()
    {
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT "+CATEGORIYNAME+" FROM "+CATEGORIY;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do
            {
                list.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

}
