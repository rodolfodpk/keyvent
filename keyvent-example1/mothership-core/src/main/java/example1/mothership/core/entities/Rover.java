package example1.mothership.core.entities;

import lombok.AllArgsConstructor;
import lombok.Value;

import static example1.mothership.core.MothershipDataSchema.RoverDirection;
import static example1.mothership.core.MothershipDataSchema.RoverDirection.NORTH;
import static example1.mothership.core.MothershipDataSchema.RoverId;

@Value @AllArgsConstructor public class Rover {

    RoverId id;
    RoverDirection roverDirection;

    public Rover(RoverId id) {
        this.id = id;
        this.roverDirection = NORTH;
    }

}
