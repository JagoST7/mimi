package ru.company.server;

import ru.company.client.GreetingService;
import ru.company.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import ru.company.shared.ResourceInfo;
import ru.company.shared.ResourceList;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
        GreetingService {

    private Random random = new Random();

    public String getUserId() {
        return getThreadLocalRequest().getSession().getId();
    }

    public String setUserId(String userId) {
        DataManager.getInstance().replaceUserId(userId, getUserId());
        return getUserId();
    }

    public Boolean isUserVoted(String resId) throws IllegalArgumentException {
        List<Integer> votes = DataManager.getInstance().getUserVotes(getUserId(), resId);

        List<ResourceInfo> resList = ResourceList.getResources(resId);

        if (resList == null) {
            throw new IllegalArgumentException("there is no such resource");
        }

        return (votes != null && (resList.size() - votes.size() < 2));
    }

    public Map<Integer, Integer> getResults(String listId) {
        List<ResourceInfo> resList = ResourceList.getResources(listId);

        if (resList == null) {
            throw new IllegalArgumentException("there is no such resource");
        }
        Map<Integer, Integer> result = DataManager.getInstance().getResults(listId);
        for (ResourceInfo info : resList) {
            if (!result.containsKey(info.getID())) {
                result.put(info.getID(), 0);
            }
        }

        return result;
    }

    public List<Integer> getCandidates(String resId) throws IllegalArgumentException {
        List<Integer> votes = DataManager.getInstance().getUserVotes(getUserId(), resId);

        List<Integer> resIds = ResourceList.getIds(resId);

        if (resIds == null) {
            throw new IllegalArgumentException("there is no such resource");
        }

        if (votes != null && (resIds.size() - votes.size() < 2)) {
            return null;
        }

        if (votes != null) {
            for (Integer id : votes) {
                resIds.remove(id);
            }
        }

        while (resIds.size() > 2) {
            resIds.remove(random.nextInt(resIds.size()));
        }

        return resIds;
    }

    public Boolean vote(String listId, Integer id1, Integer id2) {
        return DataManager.getInstance().vote(getUserId(), listId, id1, id2);
    }

    public String greetServer(String input) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (!FieldVerifier.isValidName(input)) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException(
                    "Name must be at least 4 characters long");
        }

        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");

        System.out.println(getThreadLocalRequest().getSession().getId());

        // Escape data from the client to avoid cross-site script vulnerabilities.
        input = escapeHtml(input);
        userAgent = escapeHtml(userAgent);

        return "Hello, " + input + "!<br><br>I am running " + serverInfo
                + ".<br><br>It looks like you are using:<br>" + userAgent;
    }

    /**
     * Escape an html string. Escaping data received from the client helps to
     * prevent cross-site script vulnerabilities.
     *
     * @param html the html string to escape
     * @return the escaped string
     */
    private String escapeHtml(String html) {
        if (html == null) {
            return null;
        }
        return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
                ">", "&gt;");
    }
}
