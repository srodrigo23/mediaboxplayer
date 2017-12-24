package com.umss.rodres.mbp.media;

public interface MediaContract {

    interface View{

        //puede ser una lista de elementos recogidos del servidor
        void showMedia();

    }

    interface UserActionListener{

        void connectAccount();

        void loadMedia(boolean forceUpdate);

        void reproducir();

    }
}
