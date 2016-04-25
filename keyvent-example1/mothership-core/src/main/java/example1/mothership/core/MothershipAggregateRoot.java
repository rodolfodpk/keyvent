package example1.mothership.core;

import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import example1.mothership.core.entities.Rover;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.Objects;

import static example1.mothership.core.MothershipDataSchema.*;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.AVALIABLE;
import static example1.mothership.core.MothershipDataSchema.MothershipStatus.ON_MISSION;
import static example1.mothership.core.MothershipExceptions.*;

@Value
@AllArgsConstructor
@Wither
@Builder
public class MothershipAggregateRoot {

    MothershipId id;
    Map<String, Rover> rovers;

    // mutable data
    MothershipStatus status;
    Option<Mission> mission;

    // service
    transient TemperatureService temperatureService;

    // events emitters

    public List<? super MothershipEvent> create(MothershipId id, Set<Rover> avaliableRovers) {
        isNew();
        hasAtLeastOneRover(avaliableRovers.size());
        return List.of(new MothershipCreated(id, avaliableRovers));
    }

    public List<? super MothershipEvent> startMission(MissionId missionId, Plateau plateau) {
        isNotNew();
        isFirstMission();
        statusIs(AVALIABLE);
        return List.of(new MissionStarted(new Mission(missionId, plateau)));
    }

    public List<? super MothershipEvent> landRover(RoverId roverId, PlateauLocation plateauLocation) {
        isNotNew();
        statusIs(ON_MISSION);
        hasRover(roverId);
        mission.get().canLaunchRover(roverId, plateauLocation, temperatureService);
        return List.of(new RoverLaunched(roverId, plateauLocation));
    }

    public List<? super MothershipEvent> changeRoverDirection(RoverId roverId, RoverDirection newDirection) {
        isNotNew();
        statusIs(ON_MISSION);
        hasRover(roverId);
        // TODO could also check if rover is already landed
        return List.of(new RoverDirectionChanged(roverId, newDirection));
    }

    private void hasRover(RoverId roverId) {
        if (!rovers.containsKey(roverId.getId())){
            throw new CantLandUnknownRover();
        }
    }

    // guards

    void statusIs(MothershipStatus requiredStatus) {
        if (!requiredStatus.equals(status)) {
            throw new MothershipStatusConflict();
        }
    }

    void isNew() {
        Objects.isNull(id);
    }

    void hasAtLeastOneRover(int howManyRovers) {
         if (howManyRovers == 0) {
             throw new MothershipMustHaveAtLeastOneRover();
         }
    }

    void isNotNew() {
        Objects.requireNonNull(id);
    }

    void isFirstMission() {
        if (mission.isDefined()) {
            throw new MothershipCanHaveJustOneMission();
        }
    }

}
