package io.axoniq.labs.chat.query.rooms.participants;

import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomParticipantsQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class RoomParticipantsProjection {

    private final RoomParticipantsRepository repository;

    @QueryHandler
    public List<String> handle(RoomParticipantsQuery roomParticipantsQuery){
        return repository.findRoomParticipantsByRoomId(roomParticipantsQuery.getRoomId())
                .stream().map(RoomParticipant::getParticipant).sorted().collect(toList());
    }

    @CommandHandler
    public void on(ParticipantJoinedRoomEvent evt){
        repository.save(new RoomParticipant(evt.getRoomId(), evt.getParticipant()));
    }

    @CommandHandler
    public void on(ParticipantLeftRoomEvent evt){
        repository.deleteByParticipantAndRoomId(evt.getParticipant(), evt.getRoomId());
    }

    // TODO: Create some event handlers that update this model when necessary.

    // TODO: Create the query handler to read data from this model.
}
