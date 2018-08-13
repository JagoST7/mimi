package ru.company.server.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by jago2 on 04.08.2018.
 */
public interface DataStoreInt {

    List<Integer> getUserVotes(String userId);

    boolean vote(String userId, Integer id1, Integer id2);

    Map<Integer,Integer> getResults();

    void replaceUserId(String oldId, String newId);

}
