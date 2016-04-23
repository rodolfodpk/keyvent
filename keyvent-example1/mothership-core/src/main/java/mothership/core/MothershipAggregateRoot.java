package mothership.core;

import javaslang.Tuple2;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.collection.Set;
import javaslang.control.Option;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;
import mothership.core.entities.Mission;
import mothership.core.entities.Plateau;

import java.util.Objects;

import static mothership.core.MothershipDataSchema.*;
import static mothership.core.MothershipDataSchema.MothershipStatus.AVALIABLE;
import static mothership.core.MothershipDataSchema.MothershipStatus.ON_MISSION;

@Value
@AllArgsConstructor
@Wither
@Builder
public class MothershipAggregateRoot {

    MothershipId id;
    Set<Rover> rovers;

    MothershipStatus status;
    Option<Mission> mission;

    transient TemperatureService temperatureService;

    Map<Tuple2<PlateauLocation, RoverDirection>, RoverId> landedRovers;

    // events emitters

    public List<? super MothershipEvent> create(MothershipId id, Set<Rover> rovers) {
        isNew();
        return List.of(new MothershipCreated(id, rovers));
    }

    public List<? super MothershipEvent> startMission(MissionId missionId, Plateau plateau) {
        isNotNew();
        isFirstMission();
        statusIs(AVALIABLE);
        return List.of(new MissionStarted(id, new Mission(missionId, plateau)));
    }

    public List<? super MothershipEvent> landRover(RoverId roverId, PlateauLocation location, RoverDirection direction) {
        isNotNew();
        statusIs(ON_MISSION);
        mission.get().canLaunchRover(roverId, location, direction, temperatureService, landedRovers);
        return List.of(new RoverLaunched(id, roverId, location, direction));
    }

    // guards

    void statusIs(MothershipStatus requiredStatus) {
        if (!requiredStatus.equals(status)) {
            throw new IllegalStateException(String.format("status must be = %s", requiredStatus));
        }
    }

    void isNew() {
        Objects.isNull(id);
    }

    void isNotNew() {
        Objects.requireNonNull(id);
    }

    void isFirstMission() {
        if (mission.isDefined()) {
            throw new IllegalStateException("this mothership can have only 1 mission");
        }
    }

}
