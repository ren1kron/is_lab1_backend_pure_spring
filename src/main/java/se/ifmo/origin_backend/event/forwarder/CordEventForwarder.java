package se.ifmo.origin_backend.event.forwarder;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.event.CordEvent;

@Component
@AllArgsConstructor
public class CordEventForwarder {
    private final SimpMessagingTemplate broker;

    @EventListener
    public void on(CordEvent e) {
        broker.convertAndSend("/topic/cord-changed", e);
    }
}
