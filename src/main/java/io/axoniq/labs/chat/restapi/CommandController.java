package io.axoniq.labs.chat.restapi;

import io.axoniq.labs.chat.coreapi.CreateRoomCommand;
import io.axoniq.labs.chat.coreapi.JoinRoomCommand;
import io.axoniq.labs.chat.coreapi.LeaveRoomCommand;
import io.axoniq.labs.chat.coreapi.PostMessageCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.Future;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CommandController {

    private final CommandGateway commandGateway;

    @PostMapping("/rooms")
    public Future<String> createChatRoom(@RequestBody @Valid Room room) {
        log.debug("Create chat room");
        return commandGateway.send(new CreateRoomCommand(room.getRoomId() == null ?
                UUID.randomUUID().toString() : room.getRoomId(), room.getName()));
    }

    @PostMapping("/rooms/{roomId}/participants")
    public Future<Void> joinChatRoom(@PathVariable String roomId, @RequestBody @Valid Participant participant) {
        log.debug("Join chat room");

        return  commandGateway.send(new JoinRoomCommand(roomId == null ?
                UUID.randomUUID().toString() : roomId, participant.getName()));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public Future<Void> postMessage(@PathVariable String roomId, @RequestBody @Valid PostMessageRequest message) {
        return commandGateway.send(new PostMessageCommand(roomId == null ?
                UUID.randomUUID().toString() : roomId, message.getParticipant(), message.getMessage()));
    }

    @DeleteMapping("/rooms/{roomId}/participants")
    public Future<Void> leaveChatRoom(@PathVariable String roomId, @RequestBody @Valid Participant participant) {
        return commandGateway.send(new LeaveRoomCommand(roomId == null ?
                UUID.randomUUID().toString() : roomId, participant.getName()));
    }

    public static class PostMessageRequest {

        @NotEmpty
        private String participant;
        @NotEmpty
        private String message;

        public String getParticipant() {
            return participant;
        }

        public void setParticipant(String participant) {
            this.participant = participant;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Participant {

        @NotEmpty
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Room {

        private String roomId;
        @NotEmpty
        private String name;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
