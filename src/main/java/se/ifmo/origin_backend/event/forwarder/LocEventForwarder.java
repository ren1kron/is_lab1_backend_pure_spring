package se.ifmo.origin_backend.event.forwarder;


import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.event.LocEvent;

@Component
@AllArgsConstructor
public class LocEventForwarder {
    private final SimpMessagingTemplate broker;

    @EventListener
    public void on(LocEvent e) {
        broker.convertAndSend("/topic/loc-changed", e);
    }
}
