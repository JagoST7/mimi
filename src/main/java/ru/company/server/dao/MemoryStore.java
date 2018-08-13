package ru.company.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jago2 on 04.08.2018.
 */
public class MemoryStore implements DataStoreInt {

    private final Map<String, List<Integer>> votes;
    private final Map<Integer, Integer> results;

    public MemoryStore() {
        votes = new ConcurrentHashMap<>();
        results = new ConcurrentHashMap<>();
    }

    @Override
    public List<Integer> getUserVotes(String userId) {
        return votes.get(userId);
    }

    @Override
    public boolean vote(String userId, Integer id1, Integer id2) {

        List<Integer> userVotes = votes.get(userId);
        if (userVotes == null) {
            userVotes = new ArrayList<>();
            votes.put(userId, userVotes);
        }

        if (userVotes.contains(id1) || userVotes.contains(id2)) {
            return false;
        }

        userVotes.add(id1);
        userVotes.add(id2);

        ///

        Integer cand = results.get(id1);
        if (cand == null) {
            results.put(id1, 1);
        } else {
            results.put(id1, ++cand);
        }

        return true;
    }

    @Override
    public Map<Integer, Integer> getResults() {
        return new HashMap<>(results);
    }

    @Override
    public void replaceUserId(String oldId, String newId) {
        List<Integer> userVotes = votes.get(oldId);
        if(userVotes != null) {
            votes.remove(oldId);
            votes.put(newId, userVotes);
        }
    }
}
