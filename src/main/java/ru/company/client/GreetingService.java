package ru.company.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.List;
import java.util.Map;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
    String greetServer(String name) throws IllegalArgumentException;

    Boolean isUserVoted(String listId);

    List<Integer> getCandidates(String listId);

    Boolean vote(String listId, Integer id1, Integer id2);

    Map<Integer, Integer> getResults(String listId);

    String getUserId();

    String setUserId(String userId);
}
