package crawler;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by atepliashin on 5/19/16.
 */
public class Crawler {

    private static Crawler instance;
    private URL originUrl;
    private Integer depth = 1;
    private List<URL> nextUrls = new ArrayList<>();

    private Crawler() {

    }

    public void run() {
        // it runs here and saves everything to db
        // it should start new threads of the crawler for every url from nextUrls link if depth is not reached and list
        // is not empty
        System.out.println("I'm alive!");
    }

    public static Crawler instance() {
        if(instance == null) instance = new Crawler();
        return instance;
    }

    public URL getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(URL originUrl) {
        this.originUrl = originUrl;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

}
