package gmail.luronbel.sms.cli;

import gmail.luronbel.sms.component.TradingGateway;
import gmail.luronbel.sms.dto.OrderRequest;
import gmail.luronbel.sms.entity.OrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Scanner;

import static gmail.luronbel.sms.cli.ConsoleInterface.Command.*;
import static gmail.luronbel.sms.entity.OrderType.B;
import static gmail.luronbel.sms.entity.OrderType.S;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsoleInterface {

    enum Command {
        ADD, CANCEL, END, EXIT, INFO, HELP
    }

    private final TradingGateway tradingGateway;

    public void run() {
        final Scanner scanner = new Scanner(System.in);
        System.out.println("<- console mode is on ->");
        System.out.println("Enter /help or /info to see available commands");

        while (true) {
            System.out.print("> ");
            final String line = scanner.nextLine();
            final String[] args = line.strip()
                    .split(" ");

            if (args.length > 0) {
                final String command = args[0];
                if (command.equalsIgnoreCase(ADD.name())) {
                    executeAddCommand(args);
                } else if (command.equalsIgnoreCase(CANCEL.name())) {
                    executeCancelCommand(args);
                } else if (command.equalsIgnoreCase(INFO.name()) || command.equalsIgnoreCase(HELP.name())) {
                    printInfo();
                } else if (command.equalsIgnoreCase(END.name()) || command.equalsIgnoreCase(EXIT.name())) {
                    System.exit(0);
                } else {
                    System.out.println("ERROR. Unknown command " + command);
                }
            }
        }
    }

    private void executeAddCommand(final String[] args) {
        if (args.length != 5) {
            System.out.println("ERROR: Invalid number of arguments.");
            return;
        }

        final String stock = args[1];
        final String operation = args[2];
        final String quantity = args[3];
        final String price = args[4];

        final Optional<OrderType> orderType = parseOperation(operation);
        if (orderType.isEmpty()) {
            return;
        }
        final Optional<Long> quantityLong = parseLong(quantity);
        if (quantityLong.isEmpty()) {
            return;
        }
        final Optional<Long> priceLong = parseLong(price);
        if (priceLong.isEmpty()) {
            return;
        }

        final OrderRequest orderRequest = new OrderRequest(orderType.get(), stock, quantityLong.get(), priceLong.get());
        tradingGateway.addOrder(orderRequest);
    }

    private void executeCancelCommand(final String[] args) {
        if (args.length != 3) {
            System.out.println("ERROR: Invalid number of arguments.");
            return;
        }
        final String orderBookSymbol = args[1];
        final String orderId = args[2];

        final Optional<Long> orderIdLong = parseLong(orderId);
        if (orderIdLong.isEmpty()) {
            return;
        }

        tradingGateway.cancelOrder(orderIdLong.get(), orderBookSymbol);
    }

    private void printInfo() {
        System.out.println("<- Simulator of Stock Market ->");
        System.out.println("  Commands: ");
        System.out.println("- add [STOCK_CODE] [OPERATION: S(sell)/B(buy)] [QUANTITY] [PRICE]");
        System.out.println("- cancel [STOCK_CODE] [ORDER_ID]");
        System.out.println("- info/help");
        System.out.println("- end/exit");
        System.out.println("");
    }

    private static Optional<Long> parseLong(final String arg) {
        try {
            return Optional.of(Long.parseLong(arg));
        } catch (final NumberFormatException ex) {
            System.out.println("ERROR: Invalid argument. Must be a number, but was " + arg + ".");
            return Optional.empty();
        }
    }

    private static Optional<OrderType> parseOperation(final String arg) {
        if ("sell".equalsIgnoreCase(arg) || "s".equalsIgnoreCase(arg)) {
            return Optional.of(S);
        } else if ("buy".equalsIgnoreCase(arg) || "B".equalsIgnoreCase(arg)) {
            return Optional.of(B);
        } else {
            System.out.println("ERROR: Unknown operation - " + arg + ".");
            return Optional.empty();
        }
    }
}
