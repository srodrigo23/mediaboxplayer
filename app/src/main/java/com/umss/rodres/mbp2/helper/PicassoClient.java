package com.umss.rodres.mbp2.helper;

import android.content.Context;

import com.dropbox.core.v2.DbxClientV2;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class PicassoClient {

    private static Picasso sPicasso;

    public static void init(Context context, DbxClientV2 dbxClient) {
        sPicasso = new Picasso.Builder(context)
                .downloader(new OkHttpDownloader(context))
                .addRequestHandler(new FileThumbnailRequestHandler(dbxClient))
                .build();
    }

    public static Picasso getPicasso() {
        return sPicasso;
    }
}
