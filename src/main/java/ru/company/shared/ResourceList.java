package ru.company.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jago2 on 04.08.2018.
 */
public class ResourceList implements Serializable {

    public static final String CATS_LIST_ID = "CATS_LIST";

    private static final List<ResourceInfo> cats = new ArrayList<ResourceInfo>() {
        {
            add(new ResourceInfo(1, "Васька", "images/cat_1.jpg"));
            add(new ResourceInfo(2, "Рыжик", "images/cat_2.jpg"));
            add(new ResourceInfo(3, "Бегемот", "images/cat_3.jpg"));
            add(new ResourceInfo(4, "Вертолет", "images/cat_4.jpg"));
            add(new ResourceInfo(5, "Гав", "images/cat_5.jpg"));
            add(new ResourceInfo(6, "КотЛета", "images/cat_6.jpg"));
            add(new ResourceInfo(7, "Гром", "images/cat_7.jpg"));
        }
    };


    public static List<ResourceInfo> getResources(String id) {
        if (CATS_LIST_ID.equals(id)) {
            return cats;
        }
        return null;
    }

    public static ResourceInfo getResource(String listId, Integer resId) {
        List<ResourceInfo> resList = getResources(listId);
        if(resList != null) {
            for (ResourceInfo info : resList) {
                if(info.getID().equals(resId)) {
                    return info;
                }
            }
        }
        return null;
    }


    public static List<Integer> getIds(String id) {
        List<Integer> result = new ArrayList<>();
        List<ResourceInfo> resList = getResources(id);
        if (resList != null) {
            for (ResourceInfo info : resList) {
                result.add(info.getID());
            }
        }
        return result;
    }
}
