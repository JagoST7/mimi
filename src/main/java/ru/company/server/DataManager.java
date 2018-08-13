package ru.company.server;

import ru.company.server.dao.DataStoreInt;
import ru.company.server.dao.MemoryStore;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jago2 on 04.08.2018.
 */
public class DataManager {
    private final static DataManager instance = new DataManager();

    private final Map<String, DataStoreInt> dataStore;

    private DataManager() {
        dataStore = new ConcurrentHashMap<>();
    }

    public static DataManager getInstance() {
        return instance;
    }

    public List<Integer> getUserVotes(String userId, String resId) {
        return getStore(resId).getUserVotes(userId);
    }

    public boolean vote(String userId, String listId, Integer id1, Integer id2) {
        return getStore(listId).vote(userId, id1, id2);
    }

    public Map<Integer, Integer> getResults(String listId) {
        return getStore(listId).getResults();
    }


    private DataStoreInt getStore(String resId) {
        DataStoreInt result = dataStore.get(resId);
        if (result == null) {
            result = new MemoryStore();
            dataStore.put(resId, result);
        }
        return result;
    }

    public void replaceUserId(String oldId, String newId) {
        for (DataStoreInt store : dataStore.values()) {
            store.replaceUserId(oldId, newId);
        }
    }
}
