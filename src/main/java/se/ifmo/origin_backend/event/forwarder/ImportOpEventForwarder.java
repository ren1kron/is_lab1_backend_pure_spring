package se.ifmo.origin_backend.event.forwarder;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import se.ifmo.origin_backend.event.ImportOpEvent;

@Component
@RequiredArgsConstructor
public class ImportOpEventForwarder {
    private final SimpMessagingTemplate broker;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(ImportOpEvent e) {
        broker.convertAndSend("/topic/imports", e);
    }
}
