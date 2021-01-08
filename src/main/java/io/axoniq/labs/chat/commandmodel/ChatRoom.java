package io.axoniq.labs.chat.commandmodel;


import io.axoniq.labs.chat.coreapi.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.ArrayList;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
@Slf4j
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private List<String> participants = new ArrayList<>();

    @CommandHandler
    public ChatRoom(CreateRoomCommand cmd) {
        log.debug("Handling {}", cmd);
        apply(new RoomCreatedEvent(cmd.getRoomId(), cmd.getName()));
    }

    @CommandHandler
    public void handle(JoinRoomCommand cmd) {
        log.debug("Handling {}", cmd);

        if (!participants.contains(cmd.getParticipant()))
            apply(new ParticipantJoinedRoomEvent(cmd.getRoomId(), cmd.getParticipant()));
//        else
//            throw new IllegalStateException("Cant join the room twice");
    }


    @CommandHandler
    public void handle(PostMessageCommand cmd) {
        log.debug("Handling {}", cmd);

        if (participants.contains(cmd.getParticipant()))
            apply(new MessagePostedEvent(cmd.getRoomId(), cmd.getParticipant(), cmd.getMessage()));
        else
            throw new IllegalStateException("Cant post without joining");
    }

    @CommandHandler
    public void handle(LeaveRoomCommand cmd) {
        log.debug("Handling {}", cmd);

        if (participants.contains(cmd.getParticipant()))
            apply(new ParticipantLeftRoomEvent(cmd.getRoomId(), cmd.getParticipant()));

    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent evt) {
        log.debug("Handling {}", evt);
        this.roomId = evt.getRoomId();
    }

    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent evt) {
        log.debug("Handling {}", evt);
        this.participants.add(evt.getParticipant());
    }

    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent evt) {
        log.debug("Handling {}", evt);
        this.participants.remove(evt.getParticipant());
    }


    // TODO: This class has just been created to make the test compile. It's missing, well, everything...
}
