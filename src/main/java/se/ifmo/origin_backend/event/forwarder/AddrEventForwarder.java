package se.ifmo.origin_backend.event.forwarder;

import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import se.ifmo.origin_backend.event.AddrEvent;

@Component
@AllArgsConstructor
public class AddrEventForwarder {
    private final SimpMessagingTemplate broker;

    @EventListener
    public void on(AddrEvent e) {
        broker.convertAndSend("/topic/addr-changed", e);
    }
}
