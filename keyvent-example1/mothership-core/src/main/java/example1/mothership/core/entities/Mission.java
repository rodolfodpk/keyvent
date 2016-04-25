package example1.mothership.core.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import javax.validation.Valid;

import static example1.mothership.core.MothershipDataSchema.*;

@Value @Builder @Wither @AllArgsConstructor public class Mission {

    MissionId missionId;
    @Valid Plateau plateau;

    // TODO service as param ? really ?
    public void canLaunchRover(RoverId roverId, PlateauLocation plateauLocation, TemperatureService temperatureService) {
        plateau.canLaunchRover(roverId, plateauLocation, temperatureService);
    }

}
