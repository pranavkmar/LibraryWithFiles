package com.pranav.librarywithfiles;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    FileOutputStream outputStream;
    FileInputStream inputStream;
    String filename = "temp.txt";

    File myExternalFile;
    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button issueBtn, returnBtn;
        issueBtn = (Button) findViewById(R.id.BtnIssue);
        returnBtn = (Button) findViewById(R.id.BtnReturn);
        TextView msgtv = (TextView) findViewById(R.id.tvmsg);

        spinner = (Spinner) findViewById(R.id.spinner_BookList);
        adapter = ArrayAdapter.createFromResource(this, R.array.bookNames, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + "is selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // if not able to write to external storage, disable buttons
        if (!isExternalStorageWritable() && isExternalStorageReadable()) {
            issueBtn.setEnabled(false);
            returnBtn.setEnabled(false);

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    12);
        }

        myExternalFile = getDocumentDir("temp.txt");


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 12: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do something
                } else {
                    // not granted
                    Toast.makeText(this, "We require Storage permission to write on a Text File", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public void ReadExternalOnClick(View view) {
        try {
            InputStream instream = new FileInputStream(myExternalFile);
            ReadData(instream);

        } catch (java.io.FileNotFoundException e) {
            Toast.makeText(this, "File NOT FOUND", Toast.LENGTH_SHORT).show();
            // do something if the filename does not exits
        }
    }

    public void DeleteExternalOnClick(View view) {
        myExternalFile.delete();
    }



    /* Helper Methods */

    public File getDocumentDir(String name) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), name);

        return file;
    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void ReadData(InputStream instream) {
        try {
            InputStreamReader inputreader = new InputStreamReader(instream);
            TextView msgtvRD = (TextView) findViewById(R.id.tvmsg);
            BufferedReader buffreader = new BufferedReader(inputreader);

            String line = new String();
            String allLines = new String();

            // read every line of the file into the line-variable, on line at the time

            while ((line = buffreader.readLine()) != null) {
                allLines += line;
            }

            msgtvRD.setText(allLines);

            // close the file again
            instream.close();

        } catch (Exception e) {

            // do something if the filename does not exits
        }
    }
}
