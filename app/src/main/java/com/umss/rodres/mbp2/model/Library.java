package com.umss.rodres.mbp2.model;

import com.dropbox.core.v2.files.FileMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Library {

    public static List<File> mAudio;
    public static List<File> mImage;
    public static List<File> mVideo;

    public static List<File> mLibrary;

    private static String name, path, ext;
    private static FileMetadata m;

    public static void initAudioLib(List<FileMetadata> lib) {
        mAudio = new ArrayList<>();
        if(!lib.equals(null)){
            Iterator<FileMetadata> lista = lib.iterator();
            while(lista.hasNext()){
                m     = lista.next();
                name  = m.getName();
                ext   = detExtension(name);
                path  = m.getPathLower();
                File file = new File(name, path, m.getSize(), ext, null);
                if (isAudio(ext)){
                    file.setType(File.TypeOfFile.Audio);
                    mAudio.add(file);
                }
            }
        }
        sortFiles((ArrayList<File>) mAudio);
    }

    public static void initVideoLib(List<FileMetadata> lib) {
        mVideo = new ArrayList<>();
        if(!lib.equals(null)) {
            Iterator<FileMetadata> lista = lib.iterator();
            while(lista.hasNext()) {
                m     = lista.next();
                name  = m.getName();
                ext   = detExtension(name);
                path  = m.getPathLower();
                File file = new File(name, path, m.getSize(), ext, null);
                if(isVideo(ext)) {
                    file.setType(File.TypeOfFile.Video);
                    mVideo.add(file);
                }
            }
        }
        sortFiles((ArrayList<File>) mVideo);
    }

    public static void initImageLib(List<FileMetadata> lib) {
        mImage = new ArrayList<>();
        if(!lib.equals(null)){
            Iterator<FileMetadata> lista = lib.iterator();
            while(lista.hasNext()){
                m     = lista.next();
                name  = m.getName();
                ext   = detExtension(name);
                path  = m.getPathLower();
                File file = new File(name, path, m.getSize(), ext, null);
                if(isImag(ext)){
                    file.setType(File.TypeOfFile.Image);
                    mImage.add(file);
                }
            }
        }
        sortFiles((ArrayList<File>) mImage);
    }

    public static void mergeList(){
        mLibrary = new ArrayList<>();
        mLibrary.addAll(mImage);
        mLibrary.addAll(mAudio);
        mLibrary.addAll(mVideo);

        sortFiles((ArrayList<File>) mLibrary);
    }

    private static void sortFiles(ArrayList<File> list){
        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String nameFile1, nameFile2;
                nameFile1 = o1.getName();
                nameFile2 = o2.getName();
                return nameFile1.compareTo(nameFile2);
            }
        };
        Collections.sort(list, comparator);
    }

    private static boolean isAudio(String ext){
        return verifica(ext, FormatsSuported.audio);
    }
    private static boolean isVideo(String ext){
        return verifica(ext, FormatsSuported.video);
    }
    private static boolean isImag(String ext){
        return verifica(ext, FormatsSuported.imag);
    }

    private static boolean verifica(String ext, String[] formats) {
        boolean ans = false;
        for(int i=0; i<formats.length; i++){
           if(formats[i].equals(ext)){
               ans = true;
               i = formats.length;
           }
        }
        return ans;
    }
    private static String detExtension(String name) {
        return name.substring(name.lastIndexOf("."));
    }
}