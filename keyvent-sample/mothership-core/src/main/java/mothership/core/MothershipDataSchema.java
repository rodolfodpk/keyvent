package mothership.core;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javaslang.collection.List;
import javaslang.collection.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import mothership.core.entities.Mission;
import mothership.core.entities.Plateau;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.UUID;

public class MothershipDataSchema {

    @Value public static class MothershipId { String id; }

    public enum MothershipStatus { AVALIABLE, ON_MISSION, FINISHED_MISSION}

    @Value public static class MissionId { String id; }

    @Value public static class PlateauDimension { @Min(2) int height; @Min(2) int width; }

    @Value public static class PlateauLocation { @Min(0) int x; @Min(0) int y; }

    @Value public static class PlateauId { String id; }

    @Value public static class RoverId { String id; }

    @Value public static class Rover { RoverId id; }

    public enum RoverDirection { NORTH, SOUTH, EAST, WEST;}

    @Value public static class CommandId { UUID uuid; }

    @Value public static class UnitOfWorkId { UUID uuid; }

    // services

    public interface TemperatureService { float currentTemperatureInCelsius(); }

    // commands

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "cmdType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = CreateMothership.class, name = "CreateMothership"),
            @JsonSubTypes.Type(value = StartsMissionTo.class, name = "StartsMissionTo"),
            @JsonSubTypes.Type(value = LaunchRoverTo.class, name = "LaunchRoverTo"),
            @JsonSubTypes.Type(value = ChangeRoverDirection.class, name = "ChangeRoverDirection"),
            @JsonSubTypes.Type(value = MoveRover.class, name = "MoveRover"),
            @JsonSubTypes.Type(value = ComeBackRover.class, name = "ComeBackRover"),
            @JsonSubTypes.Type(value = FinishCurrentMission.class, name = "FinishCurrentMission")

    })
    public interface MothershipCommand {
        CommandId getCommandId();
        MothershipId getMothershipId();
    }

    @Value @Builder public static class CreateMothership { String cmdType; CommandId commandId; MothershipId id; Set<Rover> rovers; }

    @Value @Builder public static class StartsMissionTo { String cmdType; CommandId commandId; MothershipId mothershipId; MissionId missionId; Plateau plateau; }

    @Value @Builder public static class LaunchRoverTo { String cmdType; CommandId commandId; MothershipId mothershipId; RoverId roverId; PlateauLocation location; RoverDirection direction; }

    @Value @Builder public static class ChangeRoverDirection { String cmdType; CommandId commandId; MothershipId mothershipId; RoverId roverId; RoverDirection direction; }

    @Value @Builder public static class MoveRover { String cmdType; CommandId commandId; MothershipId mothershipId; RoverId roverId; int steps; }

    @Value @Builder public static class ComeBackRover { String cmdType; CommandId commandId; MothershipId mothershipId; RoverId roverId; }

    @Value @Builder public static class FinishCurrentMission { String cmdType; CommandId commandId; MothershipId mothershipId; }

    // events

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "evtType")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = MothershipCreated.class, name = "MothershipCreated"),
            @JsonSubTypes.Type(value = MissionStarted.class, name = "MissionStarted"),
            @JsonSubTypes.Type(value = RoverLaunched.class, name = "RoverLaunched"),
            @JsonSubTypes.Type(value = RoverDirectionChanged.class, name = "RoverDirectionChanged"),
            @JsonSubTypes.Type(value = RoverMoved.class, name = "RoverMoved"),
            @JsonSubTypes.Type(value = RoverIsBack.class, name = "RoverIsBack"),
            @JsonSubTypes.Type(value = MissionFinished.class, name = "MissionFinished")

    })
    public interface MothershipEvent {
        MothershipId getMothershipId();
    }

    @Value @Builder public static class MothershipCreated { String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; Set<Rover> rovers; }

    @Value @Builder public static class MissionStarted {String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; Mission mission; }

    @Value @Builder public static class RoverLaunched {String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; RoverId roverId; PlateauLocation location; RoverDirection direction; }

    @Value @Builder public static class RoverDirectionChanged {String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; MissionId missionId; RoverId roverId; RoverDirection newDirection; }

    @Value @Builder public static class RoverMoved {String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; MissionId missionId; RoverId roverId; int steps; }

    @Value @Builder public static class RoverIsBack {String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; MissionId missionId; RoverId roverId; }

    @Value @Builder public static class MissionFinished {String evtType = this.getClass().getSimpleName(); MothershipId mothershipId; MissionId missionId; }

    // unitofwork

    @Value
    @Builder
    @AllArgsConstructor
    public static class MothershipUnitOfWork {
        UnitOfWorkId id;
        MothershipCommand command;
        Long originalVersion;
        List<MothershipEvent> events;
        LocalDateTime localDateTime;
        Long resultingVersion;
    }

}
