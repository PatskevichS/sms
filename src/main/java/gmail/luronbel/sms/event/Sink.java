package gmail.luronbel.sms.event;

/**
 * Destination where to publish event.
 */
interface Sink {
    
    /**
     * Send event to destination.
     */
    void publish(String notification);
}
