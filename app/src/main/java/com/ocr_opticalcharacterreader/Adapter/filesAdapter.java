package com.ocr_opticalcharacterreader.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ocr_opticalcharacterreader.Activity.SavedFilesActivity;
import com.ocr_opticalcharacterreader.Database.MobUser;
import com.ocr_opticalcharacterreader.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class filesAdapter extends BaseAdapter
{
    private Context context;
    List<MobUser> count;

    public filesAdapter(Context context, List<MobUser> count) {
        this.context = context;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.saved_listview_items, null);

        ImageView list_Image;
        TextView list_name;
        ImageButton list_removebtn, list_copybtn, list_sharebtn;

        list_Image = convertView.findViewById(R.id.list_Image);
        list_name = convertView.findViewById(R.id.list_name);
        list_removebtn = convertView.findViewById(R.id.list_removebtn);
        list_copybtn = convertView.findViewById(R.id.list_copybtn);
        list_sharebtn = convertView.findViewById(R.id.list_sharebtn);

        String DBimage,DBname;
        DBimage = count.get(position).getpImage();
        DBname = count.get(position).getpText();

        Picasso.get().load(DBimage).into(list_Image);
        list_name.setText(DBname);

        list_removebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedFilesActivity.removeFileInterface.remove(position);
            }
        });

        list_copybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedFilesActivity.removeFileInterface.copyText(position);
            }
        });

        list_sharebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavedFilesActivity.removeFileInterface.shareText(position);
            }
        });

        return convertView;
    }
}
