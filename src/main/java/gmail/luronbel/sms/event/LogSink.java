package gmail.luronbel.sms.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation of {@link Sink} that publishes events into application logs.
 */
@Slf4j
@Component
class LogSink implements Sink {

    @Override
    public void publish(final String notification) {
        log.info(notification);
    }
}
