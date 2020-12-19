package com.ocr_opticalcharacterreader.Database;

import com.ocr_opticalcharacterreader.Database.dbflow.MyDatabse;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = MyDatabse.class)
public class MobUser extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    int id;

    @Column
    String pImage;

    @Column
    String pText;



    public void InsertData(String pImage, String pText)
    {
        this.pImage = pImage;
        this.pText = pText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpText() {
        return pText;
    }

    public void setpText(String pText) {
        this.pText = pText;
    }
}
