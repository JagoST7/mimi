package ru.company.shared;

import java.io.Serializable;

/**
 * Created by jago2 on 04.08.2018.
 */
public class ResourceInfo implements Serializable {

    private Integer id;
    private String name;
    private String fileName;

    public ResourceInfo() {

    }

    public ResourceInfo(int id, String name, String fileName) {
        this.id = id;
        this.name = name;
        this.fileName = fileName;
    }

//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public Integer getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getFileName(){
        return fileName;
    }
}
