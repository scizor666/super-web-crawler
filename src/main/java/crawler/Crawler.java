package crawler;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.deploy.util.ArrayUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by atepliashin on 5/19/16.
 */
public class Crawler implements Runnable {

    private URI uri;
    private int depth;
    private DataBaseController databaseController;
    private Executor executor;

    public Crawler(URI uri, int depth) {
        this.uri = uri;
        this.depth = depth;
        this.executor = Executor.instance();
        this.databaseController = new DataBaseController();
    }

    public void run() {
        System.out.println("current url: " + uri.toString());
        System.out.println("current depth: " + depth);
        HttpResponse<String> response;
        try {
            response = Unirest.get(uri.toString()).asString();
            HtmlTextExtractor htmlTextExtractor = new HtmlTextExtractor(uri, response.getBody());
            String[] words = htmlTextExtractor.words();
            System.out.println(ArrayUtil.arrayToString(words));
            databaseController.savePage(uri.toString(), words);
            if (depth > 1) {
                addNewLinksToQueue(htmlTextExtractor.uris());
            }
        } catch (UnirestException | URISyntaxException | SQLException e) {
            // replace this by something meaningful, for example remember all failures and try one more time after all
            // ot just give list of failures in the end
            e.printStackTrace();
        } finally {
            databaseController.closeConnection();
            synchronized (executor) {
                executor.notify();
            }
        }
    }

    synchronized private void addNewLinksToQueue(Set<URI> nextUris) {
        nextUris.forEach(uri -> {
            if (!executor.getUriMap().containsKey(uri)) {
                executor.getUriQueue().offer(uri);
                executor.getUriMap().put(uri, depth - 1);
            }
        });
    }

}
