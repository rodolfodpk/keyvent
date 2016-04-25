package example1.mothership.core.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javaslang.collection.HashMap;
import javaslang.collection.Map;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

import javax.validation.Valid;

import static example1.mothership.core.MothershipDataSchema.*;
import static example1.mothership.core.MothershipExceptions.*;

@Value @Wither @AllArgsConstructor public class Plateau {

    PlateauId id;
    @Valid PlateauDimension dimension;
    @JsonIgnore Map<String, RoverPosition> landedRovers;

    public Plateau(PlateauId id, PlateauDimension dimension) {
        this.id = id;
        this.dimension = dimension;
        this.landedRovers = HashMap.empty();
    }

    public void canLaunchRover(RoverId roverId, RoverPosition roverPosition, TemperatureService temperatureService) {

        if (temperatureService.currentTemperatureInCelsius() > 100 /*celsius*/) {
            throw new CantLandOverATooHotPlateau();
        }
        if (landedRovers.containsValue(roverPosition)) {
            throw new CantLandToAnAldreadyOccupedPosition();
        }
        if (landedRovers.containsKey(roverId.getId())) {
            throw new CantLandAlreadyLandedRover();
        }
        if (roverPosition.getPlateauLocation().getX() >= dimension.getWidth() ||
            roverPosition.getPlateauLocation().getY() >= dimension.getHeight()) {
            throw new CantLandOutsidePlateau();
        }
    }

    @JsonIgnore public Map<String, RoverPosition> landedRovers() { return landedRovers; }

}
