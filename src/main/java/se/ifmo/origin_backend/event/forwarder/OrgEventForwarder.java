package se.ifmo.origin_backend.event.forwarder;


import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.event.OrgEvent;

@Component
@AllArgsConstructor
public class OrgEventForwarder {
    private final SimpMessagingTemplate broker;

    @EventListener
    public void on(OrgEvent e) {
        broker.convertAndSend("/topic/org-changed", e);
    }
}
