package com.example.customerinterface;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String res, SID, Password, Table;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference("SID");
        scancode();
    }

    private void scancode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barlauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null){
            res = String.valueOf(result.getContents());
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(res);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Map<String, String> map = new HashMap<>();

            Iterator<String> keys = jsonObject.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                String value = null;
                try {
                    value = jsonObject.getString(key);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                map.put(key, value);
            }
            SID = String.valueOf(map.get("SID"));
            Password = String.valueOf(map.get("Password"));
            Table = String.valueOf(map.get("Table"));

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Iterate through each child node of the SID node
                    for (DataSnapshot sidSnapshot : dataSnapshot.getChildren()) {
                        String username = sidSnapshot.child("username").getValue(String.class);
                        String password = sidSnapshot.child("password").getValue(String.class);
                        if (SID.equals(username) && Password.equals(password)){
                            Intent intent = new Intent(MainActivity.this, AddItemToCartActivity.class);
                            intent.putExtra("username",username);
                            intent.putExtra("table",Table);
                            startActivity(intent);
                            finish();
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
            });


}