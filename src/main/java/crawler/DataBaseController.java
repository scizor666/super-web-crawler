package crawler;

import model.*;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.DBException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by atepliashin on 5/20/16.
 */
public class DataBaseController {

    public DataBaseController() {
        // you should read the properties from database.properties. The new version of lib will do it itself,
        // but it's not under maven central repository yet
//        Base.open("org.sqlite.JDBC", "jdbc:sqlite:src/main/resources/super_web_crawler.db", null, null);
        System.out.println("I am really here!!");
        System.out.println(Base.hasConnection());
    }

    public boolean saveWords(Object pageId, Map<String, Integer> wordsMap) throws SQLException {
        PreparedStatement ps = Base.startBatch("INSERT INTO words(page_id, value, quantity) VALUES(?, ?, ?)");
        for (Map.Entry word : wordsMap.entrySet()) {
            Base.addBatch(ps, pageId, word.getKey(), word.getValue());
        }
        Base.executeBatch(ps);
        ps.close();
        return true;
    }

    public boolean savePage(String url, String[] words) throws SQLException {
        // @TODO this line shouldn't be here. Remove it when connection opening from the constructor works
        Base.open("org.sqlite.JDBC", "jdbc:sqlite:src/main/resources/super_web_crawler.db", null, null);
        System.out.println(Base.hasConnection());
        Base.openTransaction();
        try {
            Page page = new Page();
            page.set("url", url);
            page.saveIt();
            Map<String, Integer> wordsMap = new HashMap<>();
            for (String word : words) {
                wordsMap.put(word, wordsMap.containsKey(word) ? wordsMap.get(word) + 1 : 1);
            }
            saveWords(page.getId(), wordsMap);
            Base.commitTransaction();
            return true;
        } catch (DBException | SQLException e) {
            Base.rollbackTransaction();
            throw e;
        }
    }

    public void closeConnection() {
        Base.close();
    }

}
