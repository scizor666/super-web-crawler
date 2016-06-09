import crawler.Executor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Created by atepliashin on 5/16/16.
 */
public class Main {

    final static private CommandLineParser commandLineParser = new DefaultParser();
    static private Options commandLineOptions = appOptions();
    static private int depth;
    static private int threadsNumber;
    static private URI uri;

    static public void main(String[] arguments) throws URISyntaxException, ParseException {
        extractParameters(arguments);
        if (uri != null) {
            Executor executor = Executor.instance(uri, depth, threadsNumber);
            executor.start();
        } else {
            commandLineOptions.getOption("u").setRequired(true);
            handleTasks();
        }

    }

    static private void extractParameters(String[] args) throws ParseException, URISyntaxException {
        CommandLine commandLine = commandLineParser.parse(commandLineOptions, args);
        depth = Executor.DEFAULT_DEPTH;
        if (commandLine.hasOption('d') && !commandLine.getOptionValue('d').isEmpty()) {
            depth = Integer.valueOf(commandLine.getOptionValue('d'));
        }
        threadsNumber = Executor.DEFAULT_THREADS_NUMBER;
        if (commandLine.hasOption('t') && !commandLine.getOptionValue('t').isEmpty()) {
            threadsNumber = Integer.valueOf(commandLine.getOptionValue('t'));
        }
        if (commandLine.hasOption('u') && !commandLine.getOptionValue('u').isEmpty()) {
            uri = new URI(commandLine.getOptionValue('u'));
        } else {
            uri = null;
        }
    }

    static private void handleTasks() {
        printWait();
        Scanner scanner = new Scanner(System.in);
        String nextCommand;
        while (true) {
            nextCommand = scanner.nextLine().trim().toLowerCase();
            switch (nextCommand) {
                case "help":
                    printHelp();
                    break;
                case "exit":
                    System.exit(0);
                case "start":
                //@todo add continue, stop
                    printWait();
                    start(scanner);
                    printWait();
                    break;
                default:
                    System.out.println("Unrecognized command! ");
                    printHelp();
            }

        }

    }



    static private void start(Scanner scanner) {
        try {
            extractParameters(scanner.nextLine().split("\\s+"));
            Runnable task = () -> {
                Executor executor = Executor.instance(uri, depth, threadsNumber);
                executor.start();
            };
            Thread thread = new Thread(task);
            thread.start();
        } catch (ParseException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Something went wrong");
            printWait();
        }
    }

    static private void printHelp() {
        // @TODO help
        System.out.println("It must be helpful");
    }

    static private void printWait() {
        System.out.println("Waiting for input...");
    }

    static private Options appOptions() {
        final Options options = new Options();
        return options.addOption("u", "url", true, "Site URL to crawl")
                .addOption("d", "depth", true, "Depth of crawling, by default is 1")
                .addOption("t", "threads", true, "Number of threads to crawl, by default is 4");
    }
}
