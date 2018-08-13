package ru.company.client;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.company.shared.ResourceInfo;
import ru.company.shared.ResourceList;

import java.util.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Entry implements EntryPoint {
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    private static final String USER_ID = "userID";
    private static final long COOKIES_EXPIRES = 86400*1000L; //24hr

    /**
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

    private String listID = ResourceList.CATS_LIST_ID;

    /**
     * This is the Entry point method.
     */
    public void onModuleLoad() {
        String value = Cookies.getCookie(USER_ID);
        if (value == null) {
            greetingService.getUserId(new Async<String>() {
                @Override
                public void onSuccess(String result) {
                    Cookies.setCookie(USER_ID, result, new Date(System.currentTimeMillis() + COOKIES_EXPIRES));
                    checkUserVoted();
                }
            });
        } else {
            greetingService.setUserId(value, new Async<String>() {
                @Override
                public void onSuccess(String result) {
                    Cookies.setCookie(USER_ID, result, new Date(System.currentTimeMillis() + COOKIES_EXPIRES));
                    checkUserVoted();
                }
            });
        }
    }

    private void checkUserVoted() {
        greetingService.isUserVoted(listID, new Async<Boolean>() {
            @Override
            public void onSuccess(Boolean isVoted) {
                if (isVoted) {
                    getResults();
                } else {
                    getCandidates();
                }
            }
        });
    }

    private void getResults() {
        greetingService.getResults(listID, new Async<Map<Integer, Integer>>() {
            @Override
            public void onSuccess(Map<Integer, Integer> result) {
                if (result != null) {
                    showResults(result);
                }
            }
        });
    }

    private void getCandidates() {
        greetingService.getCandidates(listID, new Async<List<Integer>>() {
            @Override
            public void onSuccess(List<Integer> result) {
                if (result != null && result.size() > 1) {
                    showCandidates(result.get(0), result.get(1));
                } else {
                    showError(SERVER_ERROR);
                }
            }
        });
    }

    private void showResults(Map<Integer, Integer> result) {
        RootPanel.get("header").getElement().setInnerText("Результаты");
        RootPanel.get("mainPanel").clear();

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setWidth("100%");
        RootPanel.get("mainPanel").add(verticalPanel);

        HorizontalPanel panel = new HorizontalPanel();
        Integer curPnt = -1;
        for (Integer point : sortCandidates(result.values())) {
            if (!curPnt.equals(point)) {
                panel = new HorizontalPanel();
                verticalPanel.insert(panel, 0);
                verticalPanel.setCellHorizontalAlignment(panel, HasHorizontalAlignment.ALIGN_CENTER);
                curPnt = point;
                panel.add(getPointWidget(point));
            }

            for (Map.Entry<Integer, Integer> entry : result.entrySet()) {

                if (point.equals(entry.getValue())) {
                    Widget can = getWidget(listID, entry.getKey(), null);
                    panel.add(can);

                    result.remove(entry.getKey());
                    break;
                }
            }
        }

    }

    private List<Integer> sortCandidates(Collection<Integer> points) {
        List<Integer> result = new ArrayList<>();

        for (Integer pnt : points) {
            result.add(pnt);
        }

        Collections.sort(result);

        return result;
    }

    private void showCandidates(final Integer id1, final Integer id2) {
        RootPanel.get("header").getElement().setInnerText("Голосование");
        RootPanel.get("mainPanel").clear();

        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.setWidth("100%");
        RootPanel.get("mainPanel").add(verticalPanel);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        verticalPanel.add(horizontalPanel);
        verticalPanel.setCellHorizontalAlignment(horizontalPanel, HasHorizontalAlignment.ALIGN_CENTER);

        Widget w1 = getWidget(listID, id1, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                vote(listID, id1, id2);
            }
        });
        Widget w2 = getWidget(listID, id2, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                vote(listID, id2, id1);
            }
        });

        horizontalPanel.add(w1);
        horizontalPanel.setCellHorizontalAlignment(w1, HasHorizontalAlignment.ALIGN_CENTER);
        horizontalPanel.setCellVerticalAlignment(w1, HasVerticalAlignment.ALIGN_MIDDLE);
        horizontalPanel.add(w2);
        horizontalPanel.setCellHorizontalAlignment(w2, HasHorizontalAlignment.ALIGN_CENTER);
        horizontalPanel.setCellVerticalAlignment(w2, HasVerticalAlignment.ALIGN_MIDDLE);
    }

    private void vote(String listId, Integer id1, Integer id2) {
        greetingService.vote(listId, id1, id2, new Async<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                checkUserVoted();
            }
        });
    }

    private Widget getWidget(String listId, Integer resId, ClickHandler handler) {
        VerticalPanel vp = new VerticalPanel();
        vp.setStyleName("candidate");
        ResourceInfo resource = ResourceList.getResource(listId, resId);
        if (resource != null) {
            Image image = new Image(resource.getFileName());
            vp.add(image);

            if (handler != null) {
                image.addStyleName("selectable");
                image.addClickHandler(handler);
            }

            Label label = new Label(resource.getName());
            vp.add(label);
            vp.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_CENTER);
        }

        return vp;
    }

    private Widget getPointWidget(Integer point) {
        VerticalPanel vp = new VerticalPanel();
        vp.setStyleName("pointWidget");

        Label textLabel = new Label("голосов");
        vp.add(textLabel);
        vp.setCellHorizontalAlignment(textLabel, HasHorizontalAlignment.ALIGN_CENTER);
        vp.setCellVerticalAlignment(textLabel, HasVerticalAlignment.ALIGN_MIDDLE);

        Label pntLabel = new Label("" + point);
        pntLabel.setStyleName("pointLabel");

        vp.add(pntLabel);
        vp.setCellHorizontalAlignment(pntLabel, HasHorizontalAlignment.ALIGN_CENTER);
        vp.setCellVerticalAlignment(pntLabel, HasVerticalAlignment.ALIGN_MIDDLE);

        return vp;
    }

    private void showError(String text) {
        RootPanel.get("header").getElement().setInnerText("");
        RootPanel.get("mainPanel").clear();
        Label label = new Label(text);
        RootPanel.get("mainPanel").add(label);
        label.setStyleName("serverResponseLabelError");
    }

    abstract class Async<T> implements AsyncCallback<T> {
        @Override
        public void onFailure(Throwable caught) {
            showError(SERVER_ERROR);
        }
    }
}
