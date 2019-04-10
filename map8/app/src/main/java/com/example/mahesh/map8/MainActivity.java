package com.example.mahesh.map8;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    EditText start, end;
    Calendar myCalendar = Calendar.getInstance();
    String myFormat = "dd/MM/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    int click = 0;
    boolean permGrant = false;
    database db;
    static boolean sstatus = false;
    FusedLocationProviderClient mfused;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          checkLocPerms();
            return;
        }
        mfused= LocationServices.getFusedLocationProviderClient(this);
        db = new database(MainActivity.this);
        start = (EditText) findViewById(R.id.start);
        end = (EditText) findViewById(R.id.end);
        start.setKeyListener(null);
        end.setKeyListener(null);
        checkLocPerms();


    }

    private void checkLocPerms() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permGrant = true;
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    perms,
                    123);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permGrant = false;

        switch (requestCode) {
            case 123:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    permGrant = true;
                }
        }
    }

    private void updateLabel(EditText e, String text) {
        e.setText(text);
    }

    public void start(View view) {
        click = 1;
        new DatePickerDialog(MainActivity.this, this, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void end(View view) {
        new DatePickerDialog(MainActivity.this, this, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        click = 2;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (click == 1)
            updateLabel(start, sdf.format(myCalendar.getTime()));

        else if (click == 2)
            updateLabel(end, sdf.format(myCalendar.getTime()));
    }

    public void chk(View view) throws ParseException {
        HashMap<Double,Double> g = db.getTableAsString(sdf.parse(start.getText().toString()),sdf.parse(end.getText().toString()));
        Toast.makeText(this, g.toString(), Toast.LENGTH_LONG).show();
    }

    public void chkk(View view) throws ParseException {
        Intent maps = new Intent(MainActivity.this, MapsActivity.class);
        HashMap<Double,Double> g = db.getTableAsString(sdf.parse(start.getText().toString()),sdf.parse(end.getText().toString()));

//        startActivity(maps);

//        for (Map.Entry<Double, Double> entry : g.entrySet())
//        {
//            System.out.println(entry.getKey() + "/" + entry.getValue());
//
//        }
        maps.putExtra("loc",g);
        startActivity(maps);
    }

    public void ss(View view) {
        sstatus = true;
        Intent i1 = new Intent(MainActivity.this, bgservice.class);
        startService(i1);
    }

    public void es(View view) {
        sstatus = false;
        Intent i1 = new Intent(MainActivity.this, bgservice.class);
        stopService(i1);
    }

    public static void showToastMethod(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    Location cL;
    public void showT(String m)
    {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    public void updateloc(View view) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocPerms();
            return;
        }


        Task location=mfused.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()) {
                    cL = (Location) task.getResult();
//                    showT(String.valueOf(lat));
//                    Toast.makeText(MainActivity.this, "jk" + longi, Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.d("as", "onComplete: err");
                }
            }
        });






        Calendar myCal = Calendar.getInstance();
        Geocoder geocoder;
        List<Address> addresses=null;
        geocoder = new Geocoder(this, Locale.getDefault());
if(cL!=null) {
    try {
//            showT(String.valueOf(cL.getLatitude()+" + "+cL.getLongitude()));
        addresses = geocoder.getFromLocation(cL.getLatitude(), cL.getLongitude(), 1);
    } catch (IOException e) {
        e.printStackTrace();
    }
    String address = null;
    if (addresses != null) {

            address = addresses.get(0).getAddressLine(0);
            address+= addresses.get(0).getLocality();
            address+= addresses.get(0).getAdminArea();
            address+= addresses.get(0).getCountryName();
//            showT(String.format(String.valueOf(cL.getLatitude())+String.valueOf(cL.getLongitude())+address+sdf.format(new Date())));
            db.insertData(String.valueOf(cL.getLatitude()),String.valueOf(cL.getLongitude()),address,sdf.format(new Date()));
    }


}
    }
}
