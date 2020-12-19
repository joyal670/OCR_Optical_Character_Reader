package com.ocr_opticalcharacterreader.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.ocr_opticalcharacterreader.Database.MobUser;
import com.ocr_opticalcharacterreader.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    ImageView main_image;
    FloatingActionButton fab_addImg, fab_scan, fab_clear, fab_clipboard, fab_searchInWeb, fab_save;
    FloatingActionsMenu FloatingMenu;
    Uri mImageUri;
    Bitmap bitmap;
    String resultText = "";
    SharedPreferences sharedPreferences;
    private TextToSpeech tts;
    File MyDir;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final  int STORAGE_REQUEST_CODE = 400;
    private static final  int STORAGE_GALLERY_REQUEST_CODE = 401;
    private static final  int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final  int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    List<MobUser> UserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_image = findViewById(R.id.main_image);
        fab_addImg = findViewById(R.id.fab_addImg);
        fab_scan = findViewById(R.id.fab_scan);
        fab_clear = findViewById(R.id.fab_clear);
        fab_searchInWeb = findViewById(R.id.fab_searchInWeb);
        fab_save = findViewById(R.id.fab_save);
        FloatingMenu = findViewById(R.id.FloatingMenu);
        fab_clipboard = findViewById(R.id.fab_clipboard);

        sharedPreferences = getSharedPreferences("data", 0);

        tts = new TextToSpeech(this, this);

        cameraPermission = new String[]{Manifest.permission.CAMERA};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        fab_addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importImage();
                FloatingMenu.collapse();
            }
        });

        fab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FloatingMenu.collapse();
                if(mImageUri == null)
                {
                    Toast.makeText(MainActivity.this, "No Images found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startRecogize();
                }

            }
        });

        fab_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingMenu.collapse();

                if(mImageUri == null)
                {
                    Toast.makeText(MainActivity.this, "Nothing to Clear", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    bitmap.recycle();
                    mImageUri = null;
                    main_image.setImageResource(R.drawable.noimg);
                }
            }
        });

        fab_searchInWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                FloatingMenu.collapse();
                if(mImageUri == null)
                {
                    Toast.makeText(MainActivity.this, "Nothing to Search", Toast.LENGTH_SHORT).show();
                }
                else
                {
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("ImageUri", String.valueOf(mImageUri));
//                    editor.apply();

                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY,resultText);
                    startActivity(intent);

//                    Intent webViewIntent = new Intent(MainActivity.this, WebViewActivity.class);
//                    webViewIntent.putExtra("Query",resultText);
//                    startActivity(webViewIntent);
                }
            }
        });

        fab_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                FloatingMenu.collapse();
                if(mImageUri == null)
                {
                    Toast.makeText(MainActivity.this, "Please Add Some Images", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveData();
                }
            }
        });

        fab_clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingMenu.collapse();

                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View layout = li.inflate(R.layout.clipboard_layout, null);
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder.setView(layout);
                alertBuilder.setCancelable(true);

                TextView clipbordEtx;
                clipbordEtx = layout.findViewById(R.id.clipbordEtx);

                final AlertDialog alertDialog = alertBuilder.create();

                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                try {
                    CharSequence text = clipboard.getPrimaryClip().getItemAt(0).getText();
                    if(text.equals(""))
                    {
                        clipbordEtx.setText("Nothing in Clipboard");
                    }
                    else
                    {
                        clipbordEtx.setText(text);
                    }

                } catch (Exception e) {
                    return;
                }

                alertDialog.show();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveData()
    {

        if(!checkStoragePermission())
        {
            requestStoragePermission();
        }
        else
        {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Would you like to Save");

            dialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    MobUser mobUser = new MobUser();
                    mobUser.InsertData(String.valueOf(mImageUri), resultText);
                    boolean checkSave =  mobUser.save();
                    if(checkSave)
                    {
                        Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Unable to Save", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            dialog.create().show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission()
    {
        requestPermissions(storagePermission,STORAGE_GALLERY_REQUEST_CODE);
    }

    private boolean checkStoragePermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void importImage()
    {
        String[] items = {" Camera", " Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickCamera();
                    }
                }
                if (which == 1)
                {
                    pickGallery();
                }

            }
        });
        dialog.create().show();
    }

    private boolean checkCameraPermission()
    {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission()
    {
        requestPermissions(cameraPermission,CAMERA_REQUEST_CODE);
    }

    private void pickCamera()
    {

        CropImage.activity().start(this);
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case  CAMERA_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!cameraAccepted)
                    {
                        Toast.makeText(this,"Permissions Denied",Toast.LENGTH_SHORT).show();
                    }else{
                        pickCamera();
                    }
                }
                break;

            case STORAGE_GALLERY_REQUEST_CODE:
                if (grantResults.length > 0)
                {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!writeStorageAccepted)
                    {
                        Toast.makeText(this,"Permissions Denied",Toast.LENGTH_SHORT).show();
                    }else{
                       saveData();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                if(resultCode == RESULT_OK)
                {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMultiTouchEnabled(true)
                            .start(this);
                }


//                Bitmap photo = (Bitmap) data.getExtras().get("data");
//                main_image.setImageBitmap(photo);

            }

            if(requestCode == IMAGE_PICK_GALLERY_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMultiTouchEnabled(true)
                        .start(this);

//                Uri tem2 = data.getData();
//                Picasso.get().load(tem2).into(main_image);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK)
                {
                    mImageUri = result.getUri();
                    main_image.setImageURI(result.getUri());
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) main_image.getDrawable();
                    bitmap = bitmapDrawable.getBitmap();
                    startRecogize();
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Exception error = result.getError();
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRecogize()
    {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText)
                    {
                        resultText = firebaseVisionText.getText();

                        LayoutInflater li = LayoutInflater.from(MainActivity.this);
                        View layout = li.inflate(R.layout.result_layout, null);
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertBuilder.setView(layout);

                        final EditText main_result;
                        final Button result_edit, result_cancel, result_copyTxt, result_audio;

                        main_result = layout.findViewById(R.id.main_result);
                        result_edit = layout.findViewById(R.id.result_edit);
                        result_cancel = layout.findViewById(R.id.result_cancel);
                        result_copyTxt = layout.findViewById(R.id.result_copyTxt);
                        result_audio = layout.findViewById(R.id.result_audio);

                        final AlertDialog alertDialog = alertBuilder.create();

                        if(resultText.isEmpty() || resultText.equals(""))
                        {
                            main_result.setText("Nothing to Show");
                        }
                        else
                        {
                            main_result.setText(resultText);
                        }

                        result_edit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if(resultText.isEmpty() || resultText.equals(""))
                                {
                                    Toast.makeText(MainActivity.this, "Nothing to Edit", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    main_result.setFocusable(true);
                                    main_result.setEnabled(true);
                                    main_result.setFocusableInTouchMode(true);
                                    result_edit.setText("Editing Mode");
                                }

                            }
                        });

                        result_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if(tts != null)
                                {
                                    tts.stop();

                                }
                                alertDialog.cancel();
                            }
                        });

                        result_copyTxt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if(resultText.isEmpty() || resultText.equals(""))
                                {
                                    Toast.makeText(MainActivity.this, "No Text to Copy", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("label", main_result.getText().toString());
                                    if (clipboard == null || clip == null) return;
                                    clipboard.setPrimaryClip(clip);

                                    Toast.makeText(MainActivity.this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        result_audio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                if(resultText.isEmpty() || resultText.equals(""))
                                {
                                    Toast.makeText(MainActivity.this, "No Text", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    voiceOutput();
                                }

                            }
                        });

                        alertDialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to Recognise Text", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void voiceOutput()
    {
        CharSequence t1 = resultText;

        tts.speak(t1, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @Override
    public void onInit(int status)
    {
        if(status == TextToSpeech.SUCCESS)
        {
            int result=tts.setLanguage(Locale.US);
            float i = 50;
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Toast.makeText(this, "Langauge not Supported", Toast.LENGTH_SHORT).show();
            }
            else
            {
                voiceOutput();
            }
        }
        else
        {
            Toast.makeText(this, "Initilization Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy()
    {
        if(tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure want to Exit?");


        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_saved:
                Intent intent = new Intent(MainActivity.this, SavedFilesActivity.class);
                startActivity(intent);
                return true;

            case R.id.item_about:
                Intent intent1 = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}