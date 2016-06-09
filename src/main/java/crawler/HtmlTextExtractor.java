package crawler;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by atepliashin on 5/25/16.
 */
public class HtmlTextExtractor {

    private String htmlText;
    private URI uri;

    public HtmlTextExtractor(URI uri, String htmlText) {
        this.uri = uri;
        this.htmlText = htmlText;
    }

    public String text() {
        return cleanNonLetters(unescape(removeScripts(htmlText)));
    }

    public String[] words() {
        String[] words = text().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toLowerCase();
        }
        return words;
    }

    public Set<URI> uris() throws URISyntaxException {
        Document doc = Jsoup.parse(htmlText);
        Element html = doc.select("html").first();
        Elements linkElements = html.select("a");
        Set<URI> uris  = new HashSet<>();
        for (Element linkElement : linkElements) {
            String link = linkElement.attr("href");
            URI uri = new URI(link);
            if (!uri.isAbsolute()) {
                uri = new URI(this.uri.getScheme() + "://" + this.uri.getAuthority() + link);
            }
            uris.add(uri);
        }
        return uris;
    }

    private String removeScripts(String text) {
        Document doc = Jsoup.parse(text);
        Element html = doc.select("html").first();
        html.select("script").remove();
        return html.html();
    }

    private String unescape(String text) {
        return StringEscapeUtils.unescapeHtml4(text).replaceAll("&nbsp;", " ");
    }

    private String cleanHtmlTags(String text) {
        return text.replaceAll("\\<.*?>", " ");
    }

    private String cleanNonLetters(String text) {
        return cleanHtmlTags(text).replaceAll("[^\\p{IsAlphabetic}-]", " ")
                .replaceAll("(\\s-|\\s-\\s|-\\s)", " ").replaceAll("\\s+", " ").trim();
    }

}
