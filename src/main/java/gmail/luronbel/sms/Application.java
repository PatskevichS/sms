package gmail.luronbel.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import static gmail.luronbel.sms.ConsoleApplication.CLI;

@EnableScheduling
@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        if (args.length > 0 && CLI.equalsIgnoreCase(args[0])) {
            SpringApplication.run(ConsoleApplication.class, args);
        } else {
            SpringApplication.run(Application.class, args);
        }
    }

}
