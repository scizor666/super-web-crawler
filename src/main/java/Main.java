import crawler.Crawler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by atepliashin on 5/16/16.
 */
public class Main {

    final static CommandLineParser commandLineParser = new DefaultParser();
    final static Options commandLineOptions = appOptions();

    static public void main(String[] arguments) throws MalformedURLException, ParseException {
        CommandLine commandLine = commandLineParser.parse(commandLineOptions, arguments);
        Crawler crawler = Crawler.instance();
        crawler.setOriginUrl(new URL(commandLine.getOptionValue('u')));
        if (commandLine.hasOption('d') && !commandLine.getOptionValue('d').isEmpty())
            crawler.setDepth(Integer.valueOf(commandLine.getOptionValue('d')));
        crawler.run();
    }

    static private Options appOptions() {
        Option urlOption = new Option("u", "url", true, "Site URL to crawl");
        urlOption.setRequired(true);
        final Options options = new Options();
        options.addOption(urlOption);
        return options.addOption("d", "depth", true, "Depth of crawling, by default is 1");
    }
}
