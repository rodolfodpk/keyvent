package mothership.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import javax.validation.Valid;

import static mothership.core.MothershipDataSchema.*;

@Value @Builder @Wither @AllArgsConstructor public class Mission {

    MissionId missionId;
    @Valid Plateau plateau;

    public boolean canLaunchRover(RoverId roverId, RoverPosition roverPosition, TemperatureService temperatureService) {
        return plateau.canLaunchRover(roverId, roverPosition, temperatureService);
    }

}
