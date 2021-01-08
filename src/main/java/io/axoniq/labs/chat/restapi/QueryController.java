package io.axoniq.labs.chat.restapi;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.RoomMessagesQuery;
import io.axoniq.labs.chat.coreapi.RoomParticipantsQuery;
import io.axoniq.labs.chat.query.rooms.messages.ChatMessage;
import io.axoniq.labs.chat.query.rooms.participants.RoomParticipant;
import io.axoniq.labs.chat.query.rooms.summary.RoomSummary;
import org.axonframework.messaging.responsetypes.InstanceResponseType;
import org.axonframework.messaging.responsetypes.MultipleInstancesResponseType;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Future;

@RestController
public class QueryController {

    private final QueryGateway queryGateway;

    public QueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("rooms")
    public Future<List<RoomSummary>> listRooms() {
        return queryGateway.query(new AllRoomsQuery(), new MultipleInstancesResponseType<>(RoomSummary.class));
    }

    @GetMapping("/rooms/{roomId}/participants")
    public Future<List<RoomParticipant>> participantsInRoom(@PathVariable String roomId) {
        // TODO:: Test and Ask someone. This seems wrong. RoomParticipantsQuery returns a List of RoomParticipants.
        // Yup. String gave an error. Changed return type to RoomParticipant
        // How to map it to string?
        // THIS IS BROKEN
        return queryGateway.query(new RoomParticipantsQuery(roomId),
                new MultipleInstancesResponseType<>(RoomParticipant.class));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public Future<List<ChatMessage>> roomMessages(@PathVariable String roomId) {
        return queryGateway.query(new RoomMessagesQuery(roomId),
                new MultipleInstancesResponseType<>(ChatMessage.class));
    }

    @GetMapping(value = "/rooms/{roomId}/messages/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatMessage> subscribeRoomMessages(@PathVariable String roomId) {
        // source:: https://github.com/mohosseini/axon-chatroom/blob/904057ebf29ae967e750905e79fa92955d68c570/src/main/java/io/axoniq/labs/chat/restapi/QueryController.java
        SubscriptionQueryResult<List<ChatMessage>, ChatMessage> res =
                queryGateway.subscriptionQuery(new RoomMessagesQuery(roomId),
                new MultipleInstancesResponseType<>(ChatMessage.class),
                new InstanceResponseType<>(ChatMessage.class));

        Flux<ChatMessage> initialResult = res.initialResult().flatMapMany(Flux::fromIterable);
        return Flux.concat(initialResult, res.updates());

        // TODO:: Cant test this? Does this even work?
    }
}
