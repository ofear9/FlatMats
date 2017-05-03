package com.example.ranendelman.flatmates.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.Manifest;

import com.example.ranendelman.flatmates.Adapters.receiptsListAdapter;
import com.example.ranendelman.flatmates.Classes.Recepits;
import com.example.ranendelman.flatmates.Holders.ReceiptsListHolder;
import com.example.ranendelman.flatmates.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class ReceiptsActivity extends AppCompatActivity {
    private static final int IMAGE_GALLERY_REQUEST = 2;
    static final int CAM_REQUEST = 1;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String fbID;
    private ListView mListView;
    private ArrayList<Recepits> recList;
    private String tmpUri;
    private final ReceiptsListHolder listHolder = new ReceiptsListHolder(null);
    private int homeID;
    private receiptsListAdapter adapter;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        listHolder.list = new ArrayList<>();
        mProgress = new ProgressDialog(this);
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        extractDataFromBundle();
        setupListView();
        setupNewRecButton();
        setupFromGalleryButton();
        getRecList();
        setWindowProperties();

    }

    private void setupFromGalleryButton() {
        Button b = (Button)findViewById(R.id.addNewRecepitFromGallary);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                    return;
                }
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                    File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String pictureDirectoryPath = pictureDirectory.getPath();

                    Uri data = Uri.parse(pictureDirectoryPath);

                    photoPickerIntent.setDataAndType(data, "image/*");

                    // we will invoke this activity, and get something back from it.
                    startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);

                }
                else{
                    Toast.makeText(ReceiptsActivity.this,"Permissions denied " ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void setupNewRecButton(){
        Button b = (Button)findViewById(R.id.takePhoto);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReceiptsActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                }
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAM_REQUEST);
                }
                else{
                    Toast.makeText(ReceiptsActivity.this,"Permissions denied " ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(ReceiptsActivity.this,"Permissions granted " ,
                            Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ReceiptsActivity.this,"Permissions denied " ,
                            Toast.LENGTH_SHORT).show();

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void setupListView() {
        mListView = (ListView) findViewById(R.id.recepits_list);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiptsActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Do you want to remove this receipt ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<String, String> recepeit = listHolder.list.get(position);
                        Log.v("" + position, "" + position);
                        adapter.remove(recepeit);
                        mDatabase.child("homes").child("" + getHomeID()).child("Receipts-List").setValue(listHolder.list);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    protected void onStart() {
        super.onStart();


        //listHolder.list.add(new Recepits("https://firebasestorage.googleapis.com/v0/b/flatmates-6b9c6.appspot.com/o/Photos%2F10212047684568288%2F1021204768456828820170327_155435?alt=media&token=d47a8a49-cdfa-4cde-a3b2-30e61111b5dc","!"));

    }

    protected void setWindowProperties() {
        // set window to black
        Window window = ReceiptsActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ReceiptsActivity.this, R.color.common_google_signin_btn_text_dark_focused));

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (((requestCode == CAM_REQUEST) || (requestCode==IMAGE_GALLERY_REQUEST)) && resultCode == RESULT_OK && data!=null) {

            Uri uri =  data.getData();
            mProgress.setMessage("Uploading your new receipt...");
            mProgress.show();

            StorageReference filepath = mStorage.child("Photos").child(getUserID()).child(getUserID() + "" + Math.random());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();

                    tmpUri = downloadUri.toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiptsActivity.this);
                    builder.setTitle("Upload done ! Enter a description for the receipt:");
                    final EditText input = new EditText(ReceiptsActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            insertNewRecepit(tmpUri, input.getText().toString());
                        }
                    });

                    builder.show();
                    mProgress.dismiss();
                }
            });
        }
    }

    protected void insertNewRecepit(String url, String description) {
        ArrayList<HashMap> sList;
        sList = listHolder.list;
        if (sList == null)
            sList = new ArrayList<>();

        final HashMap hashMap = new HashMap();
        hashMap.put("imageUrl", url);
        hashMap.put("uid", description);
        sList.add(hashMap);
        mDatabase.child("homes").child("" + getHomeID()).child("Receipts-List").setValue(sList);
        Log.v(" NEW ITEM INSERTED", "NEW ITEM INSERTED TO DATA BASE");
    }

    private void extractDataFromBundle() {
        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(LogInActivity.BUNDLE_KEY);

        setHomeID(bundle.getInt(LogInActivity.BUNDLE_KEY));

        fbID = bundle.getString(UserHomeScreenActivity.BUNDLE_KEY_FB);
    }

    protected String getUserID() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser().getUid();
        else {
            extractDataFromBundle();
            return fbID;
        }
    }

    protected void getRecList() {
        final String user = getUserID();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("homes").child("" + getHomeID()).hasChild("Receipts-List")) {

                    listHolder.list = (ArrayList<HashMap>) dataSnapshot.child("homes").child("" + getHomeID()).child("Receipts-List").getValue();
                    Log.v(" LIST FOUND", "LIST FOUND");


                    adapter = new receiptsListAdapter(ReceiptsActivity.this, R.layout.item_recepit, listHolder.list);

                    mListView.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    protected int getHomeID() {
        return homeID;
    }

    protected void setHomeID(int homeID) {
        this.homeID = homeID;
    }
}
