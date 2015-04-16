package com.sondsara.v_scan;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

class animalIngredient implements Parcelable {
    String name = "name";
    String derivation = "derivation";
    String status = "status";

    public animalIngredient (Parcel source){
        name = source.readString();
        derivation = source.readString();
        status = source.readString();
    }

    public animalIngredient (String name, String derivation, String status){
        this.name = name;
        this.derivation = derivation;
        this.status = status;
    }

    public int describeContents(){
        return this.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(derivation);
        dest.writeString(status);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public animalIngredient createFromParcel (Parcel in){
            return new animalIngredient(in);
        }
        public animalIngredient[] newArray(int size){
            return new animalIngredient[size];
        }
    };
}

class veganProduct implements Parcelable {
    String name = "name";
    String company = "company";

    public veganProduct (Parcel source){
        name = source.readString();
        company = source.readString();
    }

    public veganProduct (String name, String company){
        this.name = name;
        this.company = company;
    }

    public int describeContents(){
        return this.hashCode();
    }

    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(company);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public veganProduct createFromParcel (Parcel in){
            return new veganProduct(in);
        }
        public veganProduct[] newArray(int size){
            return new veganProduct[size];
        }
    };
}

public class MainActivity extends ActionBarActivity {

    public String upc = "";

    private static final int ZBAR_SCANNER_REQUEST = 0;
    private Cursor c;
    private Cursor vegC;
    private Database db;
    public ArrayList<animalIngredient> animalProducts = new ArrayList<>();
    public ArrayList<veganProduct> veganProducts = new ArrayList<>();
    public final static String EXTRA_MESSAGE = "com.Veganizer.main.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        db= new Database(this);

        try {
            c = db.getIngredients();

            while (c.moveToNext()) {
                String name = c.getString(c.getColumnIndexOrThrow("NAME"));
                String derivation = c.getString(c.getColumnIndexOrThrow("DERIVATION"));
                String status = c.getString(c.getColumnIndexOrThrow("STATUS"));
                animalIngredient temp = new animalIngredient(name, derivation, status);
                animalProducts.add(temp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            vegC = db.getVeggies();

            while (vegC.moveToNext()) {
                String name = vegC.getString(vegC.getColumnIndexOrThrow("NAME"));
                String company = vegC.getString(vegC.getColumnIndexOrThrow("COMPANY"));
                veganProduct temp = new veganProduct(name, company);
                veganProducts.add(temp);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        LaunchCamera();
    }

    void ScanButton() {
        ImageButton start = (ImageButton) findViewById(R.id.imageButton);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick (View vw) {
                LaunchCamera();
            }
        });
    }

    void LaunchCamera(){
        if (isCameraAvailable()){
            Intent intent = new Intent (this, ZBarScannerActivity.class);
            startActivityForResult (intent, ZBAR_SCANNER_REQUEST);
        }else{
            Toast.makeText(this, "YOU AIN'T GOT A CAMERA BOAH", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isCameraAvailable(){
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            upc = data.getStringExtra(ZBarConstants.SCAN_RESULT);
            if (upc.length() == 13) {
                upc = upc.substring(1, 13);
            }
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            intent.putExtra(EXTRA_MESSAGE, upc);
            intent.putParcelableArrayListExtra("key", animalProducts);
            intent.putParcelableArrayListExtra("key2", veganProducts);
            this.startActivity(intent);

        }else if (resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy (){
        super.onDestroy();
        c.close();
        db.close();
    }

}