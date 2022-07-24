package gmail.luronbel.sms.schedule;

import gmail.luronbel.sms.component.MatchingEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler that invokes {@link MatchingEngine Matching Engine} in certain interval.
 */
@Component
@RequiredArgsConstructor
public class SyncSimpleScheduler {

    private final MatchingEngine matchingEngine;

    @Scheduled(fixedDelayString = "${scheduler.fixed-delay-milliseconds}", initialDelayString = "${scheduler" +
            ".initial-delay-milliseconds}")
    public void scheduleBalancing() {
        matchingEngine.balanceOrderBooks();
    }
}
