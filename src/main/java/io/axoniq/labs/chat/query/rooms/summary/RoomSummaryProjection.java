package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    @EventHandler
    public void on(RoomCreatedEvent evt) {
        roomSummaryRepository.save(new RoomSummary(evt.getRoomId(), evt.getName()));
    }

    @QueryHandler
    public List<RoomSummary> handle(AllRoomsQuery allRoomsQuery){
        return roomSummaryRepository.findAll();
    }

    @CommandHandler
    public void on(ParticipantJoinedRoomEvent evt) {
        Optional<RoomSummary> rs = roomSummaryRepository.findById(evt.getRoomId());
        rs.ifPresent(RoomSummary::addParticipant);
    }

    @CommandHandler
    public void on(ParticipantLeftRoomEvent evt) {
        Optional<RoomSummary> rs = roomSummaryRepository.findById(evt.getRoomId());
        rs.ifPresent(RoomSummary::removeParticipant);
    }

    // TODO: Create some event handlers that update this model when necessary.

    // TODO: Create the query handler to read data from this model.
}
