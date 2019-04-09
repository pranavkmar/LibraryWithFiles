package com.pranav.mobileApps.library;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String filename = "library.txt";
    EditText etuname = null;
    Button issue, returnb;
    Spinner spinner = null;
    FileWriter fw = null;
    FileInputStream fis = null;
    FileOutputStream fos = null;
    File myExternalFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etuname = (EditText) findViewById(R.id.et);
        spinner = (Spinner) findViewById(R.id.spinner1);
        issue = (Button) findViewById(R.id.is);
        returnb = (Button) findViewById(R.id.re);

        ArrayAdapter<CharSequence> aa = ArrayAdapter.createFromResource(this, R.array.books, android.R.layout.simple_spinner_item);

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);
        spinner.setOnItemSelectedListener(this);
// if not able to write to external storage, disable buttons
        if (!isExternalStorageWritable() && isExternalStorageReadable()) {
            issue.setEnabled(false);
            returnb.setEnabled(false);


        }

      /* try {
           File f = new File(filename);
           f.createNewFile();
           fw = new FileWriter(f,true);
           fw.write("USERNAME /t BOOKNAME /t STATUS \n");
       }catch(FileNotFoundException e){
        e.printStackTrace();
       }catch (IOException e){
           e.printStackTrace();
       }*/

        issue.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String bookname = (String) spinner.getSelectedItem().toString();
                String[] state = getStatus(bookname);

                String edittextStr = etuname.getText().toString();
//                if (state[2].compareTo("I")==0) {
                if (edittextStr.equals("")){
                    Toast.makeText(getApplicationContext(), "BOOK CANNOT BE ISSUED TO UNKNOWN, Enter Name " , Toast.LENGTH_LONG).show();


                }
                else if (Arrays.asList(state).contains("I")) {
                    Toast.makeText(getApplicationContext(), "BOOK ALREADY ISSUED TO " + " \"" + state[0] + "\" ", Toast.LENGTH_LONG).show();
                }
                else{
                    writeRecord(etuname.getText().toString(), bookname, "I");
                }
            }
        });

        returnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bookname = (String) spinner.getSelectedItem();
                String[] state = getStatus(bookname);
                if ((Arrays.asList(state).contains("I")) && (Arrays.asList(state).contains(etuname.getText().toString()))) {
                    writeRecord(etuname.getText().toString(), bookname, "R");
                    Toast.makeText(getApplicationContext(), "BOOK \"" + state[1] + " IS RETURNED BY \"" + state[0] + "\" AND Log Updated at \"" + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Documents" + File.separator + filename + " \"", Toast.LENGTH_LONG).show();
                }

                else if( ((Arrays.asList(state).contains("I")) && ( Arrays.asList(state).contains(etuname.getText().toString())))== false){
                    Toast retToas = Toast.makeText(getApplicationContext(), "BOOK IS NOT POSSIBLE TO RETURN ,AS IT IS ISSUED TO SOMEONE ELSE ", Toast.LENGTH_LONG);
                    View retToastView = retToas.getView();
                    retToastView.setPadding(8, 2, 2, 8);
                    retToastView.setBackgroundResource(R.color.red);

                    retToas.show();

                }

                    else {
                    Toast retToast = Toast.makeText(getApplicationContext(), "BOOK IS NOT POSSIBLE TO RETURN ,AS IT IS NOT YET ISSUED ", Toast.LENGTH_LONG);
                    View retToastView = retToast.getView();
                    retToastView.setPadding(8, 2, 2, 8);
                    retToastView.setBackgroundResource(R.color.red);

                    retToast.show();
                }
            }


        });

        myExternalFile = getDocumentDir(filename);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // String bookname = parent.getItemAtPosition(position).toString();


    }

    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void writeRecord(String user, String book, String status) {

//        FileOutputStream fos = null;
        myExternalFile = getDocumentDir(filename);

        try {
            String comma = ",";
            String newline = "\n";

            fos = new FileOutputStream(myExternalFile, true);
//InternalStorage /data/app/<package>/.txt
            //   fos = openFileOutput(filename, MODE_APPEND);
            //fos=new FileOutputStream(filename,true);
            //  fos.write(user.getBytes()+ "\t" +book.getBytes()+ "\t" +status.getBytes()+ "\n");
            fos.write(user.getBytes());
            fos.write(comma.getBytes());
            fos.write(book.getBytes());
            fos.write(comma.getBytes());
            fos.write(status.getBytes());
            fos.write(newline.getBytes());
/* fw=new FileWriter(filename,true);
fw.write(user.getBytes()+ "\t" +book.getBytes()+ "\t" +status.getBytes()+ "\n");*/

            Toast.makeText(this, "Book \" " + book + " is ISSUED to " + user + " and Log is Saved at \"" + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Documents" + File.separator + filename + "\"", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /* Helper Methods */
    @TargetApi(19)
    public File getDocumentDir(String name) {
        // Get the directory for the user's public pictures directory.
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String myDirectory = "Documents";

        File myDir = new File(externalStorage + File.separator + myDirectory);
        if (!myDir.exists()) {
            myDir.mkdirs();
            Toast t1 = Toast.makeText(this, "Documents Directory is created at \"" + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Documents" + File.separator + "\"", Toast.LENGTH_LONG);
            View tvt1 = t1.getView();
            tvt1.setBackgroundResource(R.color.red);
            t1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
            t1.show();
        }
        File outfile = new File(externalStorage + File.separator + myDirectory + File.separator + name);
        /*if directory doesn't exist, create it*/
        if (!outfile.exists()) {

            try {
                outfile.createNewFile();
                Toast toast = Toast.makeText(getApplicationContext(), "Library.txt Log file is created at \"" + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Documents" + File.separator + filename + "\"", Toast.LENGTH_LONG);
                View ToastView = toast.getView();
                ToastView.setBackgroundResource(R.color.green);
                ToastView.setPadding(8, 2, 8, 2);

                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outfile;


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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 12: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do something

//                    requestGranted=true;
                } else {
                    // not granted
                    Toast.makeText(this, "We require Storage permission to write on a Text File", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public String[] getStatus(String book) {

        String[] BookStatus = new String[3];
        String statusFlag = "R";
        // FileReader fr=null;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    12);
        }


        try {
            InputStream is = new FileInputStream(myExternalFile);
            BookStatus = readData(is, statusFlag, book);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return BookStatus;

    }

    public String[] readData(InputStream is, String BStatus, String book
    ) {
        String details[] = new String[3];

        try {
//            fis = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            // StringBuilder sb = new StringBuilder();

           /*  fr=new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);*/


            String line;
            while ((line = br.readLine()) != null) {
                //  String arr[]=line.split("\t");
                String arr[] = line.split(",");


                if (arr[1].equals(book)) {
                    BStatus = arr[2];
                    // username in details[0] and BookName in details[1]
                    details[0] = arr[0];
                    details[1] = arr[1];
                    details[2] = BStatus;
                }
            }
            br.close();
            is.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    is.close();
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return details;
    }


}



