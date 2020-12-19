package com.ocr_opticalcharacterreader.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.ocr_opticalcharacterreader.Adapter.filesAdapter;
import com.ocr_opticalcharacterreader.Database.MobUser;
import com.ocr_opticalcharacterreader.Interface.RemoveFileInterface;
import com.ocr_opticalcharacterreader.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

public class SavedFilesActivity extends AppCompatActivity implements RemoveFileInterface {

    ListView savedFilesList;
    List<MobUser> UserList;
    filesAdapter filesAdapter;

    public static RemoveFileInterface removeFileInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_files);

        savedFilesList = findViewById(R.id.savedFilesList);

        removeFileInterface = this;

        UserList = SQLite.select().from(MobUser.class).queryList();
        filesAdapter = new filesAdapter(this, UserList);
        savedFilesList.setAdapter(filesAdapter);

        if(UserList.isEmpty())
        {
            Toast.makeText(this, "No files are Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void remove(int position)
    {
        UserList.get(position).delete();
        UserList.remove(position);
        Toast.makeText(this, "Item Removed", Toast.LENGTH_SHORT).show();
        filesAdapter.notifyDataSetChanged();
    }

    @Override
    public void copyText(int position)
    {
        String text = UserList.get(position).getpText();

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        if (clipboard == null || clip == null) return;
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void shareText(int position)
    {
        String text = UserList.get(position).getpText();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Shared From OCR");
        i.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(i, "choose one"));

    }
}