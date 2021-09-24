package com.umss.rodres.mbp2.model;

public class File {

    private String     name;
    private String     path;
    private long       size;
    private String     extension;
    private String     tempLink;
    private String     shareLink;
    private TypeOfFile type;

    public enum TypeOfFile{
        Audio, Video, Image;
    }

    public File(String name, String path, long size, String ext, TypeOfFile type){
        this.name      = name;
        this.path      = path;
        this.size      = size;
        this.tempLink  = "";
        this.shareLink = "";
        this.extension = ext;
        this.type      = type;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public void setType(TypeOfFile type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getTempLink() {
        return tempLink;
    }

    public void setTempLink(String tempLink) {
        this.tempLink = tempLink;
    }

    public long getSize(){
        return this.size;
    }

    public TypeOfFile getType(){
        return type;
    }
}