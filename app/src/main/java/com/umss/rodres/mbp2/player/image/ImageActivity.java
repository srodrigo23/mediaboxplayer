package com.umss.rodres.mbp2.player.image;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.umss.rodres.mbp2.R;
import com.umss.rodres.mbp2.helper.FileThumbnailRequestHandler;
import com.umss.rodres.mbp2.helper.PicassoClient;
import com.umss.rodres.mbp2.model.File;
import com.umss.rodres.mbp2.model.Library;

public class ImageActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Picasso   mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mImageView = findViewById(R.id.imageToShow);
        mPicasso = PicassoClient.getPicasso();
    }

    private void setImageToShow(int pos){
        File file = Library.mImage.get(pos);
        mPicasso.load(FileThumbnailRequestHandler.buildPicassoUri(file))
                .placeholder(R.drawable.ic_file_image_white_24dp)
                .error(R.drawable.ic_file_image_white_24dp)
                //.fit()
                //.centerCrop()
                .into(mImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int pos = getIntent().getIntExtra("posPressed", 0);
        setImageToShow(pos);
    }
}
