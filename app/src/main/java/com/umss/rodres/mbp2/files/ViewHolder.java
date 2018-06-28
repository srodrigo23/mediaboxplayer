package com.umss.rodres.mbp2.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.util.Conversor;
import com.umss.rodres.mbp2.util.StringProcess;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ViewHolder extends RecyclerView.ViewHolder {

    protected TextView  fileName, filePath, fileSize, fileChecked;
    protected ImageView fileIcon, menuPopUp;

    public ViewHolder(View itemView) {
        super(itemView);
        this.fileName    = itemView.findViewById(R.id.idFileName);
        this.fileIcon    = itemView.findViewById(R.id.idIconFileExtention);
        this.filePath    = itemView.findViewById(R.id.idFilePath);
        this.fileSize    = itemView.findViewById(R.id.idFileSize);
        this.fileChecked = itemView.findViewById(R.id.idChecked);
    }

    private void setPopUpMenuMusicFile(final View item) {
        menuPopUp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(item.getContext(), item);
                popup.getMenuInflater().inflate(R.menu.menu_music_item, popup.getMenu());
                letShowIconsPopUpMenu(popup);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.downloadPopUpmenu:

                                break;
                            case R.id.deletePopUpMenU:

                                break;
                            case R.id.shareFilePopUpMenu:

                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    protected void letShowIconsPopUpMenu(PopupMenu popup){
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
