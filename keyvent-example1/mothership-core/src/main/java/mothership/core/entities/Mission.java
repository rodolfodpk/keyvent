package mothership.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import static mothership.core.MothershipDataSchema.*;

@Value
@Builder
@Wither
@AllArgsConstructor
public class Mission {

    MissionId missionId;
    Plateau plateau;

    public boolean canLaunchRover(RoverId roverId, PlateauLocation location, RoverDirection direction) {
        return plateau.canLaunchRover(roverId, location, direction);
    }

}
