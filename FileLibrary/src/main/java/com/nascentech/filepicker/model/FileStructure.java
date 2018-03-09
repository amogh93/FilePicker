package com.nascentech.filepicker.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Amogh on 13-12-2017.
 */

public class FileStructure implements Comparable<FileStructure>,Serializable
{
    private String name;
    private String creationDate;
    private String path;
    private boolean isDirectory;
    private boolean isSelected;

    public FileStructure(String name,String creationDate,String path,boolean isDirectory)
    {
        this.name=name;
        this.creationDate=creationDate;
        this.path=path;
        this.isDirectory=isDirectory;
        this.isSelected=false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int compareTo(@NonNull FileStructure o)
    {
        if(this.name !=null)
        {
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
