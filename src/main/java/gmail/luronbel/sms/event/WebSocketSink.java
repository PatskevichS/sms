package gmail.luronbel.sms.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import static gmail.luronbel.sms.configuration.WebSocketConfig.TOPIC;

/**
 * Implementation of {@link Sink} that publishes events into WebSocket topic.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSink implements Sink {

    public static final String DESTINATION = "/events";
    private final SimpMessagingTemplate template;

    @Override
    public void publish(final String notification) {
        template.convertAndSend(TOPIC + DESTINATION, notification);
    }
}
