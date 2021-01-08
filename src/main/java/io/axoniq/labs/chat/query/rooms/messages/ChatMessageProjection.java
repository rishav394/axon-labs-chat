package io.axoniq.labs.chat.query.rooms.messages;

import io.axoniq.labs.chat.coreapi.MessagePostedEvent;
import io.axoniq.labs.chat.coreapi.RoomMessagesQuery;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMessageProjection {

    private final ChatMessageRepository repository;
    private final QueryUpdateEmitter updateEmitter;

    @EventHandler
    public void on(MessagePostedEvent evt, @Timestamp Instant timestamp) {
        ChatMessage cm = repository.save(new ChatMessage(evt.getParticipant(), evt.getRoomId(),
                evt.getMessage(), timestamp.toEpochMilli()));

        updateEmitter.emit(RoomMessagesQuery.class, roomMessagesQuery ->
                roomMessagesQuery.getRoomId().equals(evt.getRoomId()), cm);
    }

    @QueryHandler
    public List<ChatMessage> handle(RoomMessagesQuery roomMessagesQuery) {
        return repository.findAllByRoomIdOrderByTimestamp(roomMessagesQuery.getRoomId());
    }

    // TODO: Create some event handlers that update this model when necessary.

    // TODO: Create the query handler to read data from this model.

    // TODO: Emit updates when new message arrive to notify subscription query by modifying the event handler.
}
