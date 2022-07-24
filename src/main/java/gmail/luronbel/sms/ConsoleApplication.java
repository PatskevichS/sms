package gmail.luronbel.sms;

import gmail.luronbel.sms.cli.ConsoleInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {

    public static final String CLI = "cli";

    @Autowired
    private ConsoleInterface consoleInterface;

    @Override
    public void run(final String... args) {
        if (hasCliFlag(args)) {
            consoleInterface.run();
        }
    }

    private static boolean hasCliFlag(final String... args) {
        return Arrays.stream(args)
                .anyMatch(CLI::equalsIgnoreCase);
    }
}