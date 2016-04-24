package mothership.core;

import javaslang.collection.HashSet;
import javaslang.control.Option;
import lombok.val;
import mothership.core.entities.Mission;
import mothership.core.entities.Plateau;
import org.junit.Test;

import static mothership.core.MothershipDataSchema.*;
import static org.junit.Assert.assertEquals;

public class MothershipStateTransitionFunctionTest {

    MothershipStateTransitionFunction function = new MothershipStateTransitionFunction();

    @Test
    public void afterCreated() {

        val mId = new MothershipId("voyager");
        val emptyMothership = MothershipAggregateRoot.builder().build();
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        val event = MothershipCreated.builder().mothershipId(mId)
                .rovers(rovers)
                .build();
        val result = function.apply(event, emptyMothership);
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.none()).status(MothershipStatus.AVALIABLE).build();
        assertEquals(expected, result);
    }

    @Test
    public void afterStarted() {

        val mId = new MothershipId("voyager");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.none()).status(MothershipStatus.AVALIABLE).build();
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();
        val event = MissionStarted.builder().mothershipId(mId).mission(mission).build();
        val result = function.apply(event, avaliableMothership);
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.of(mission)).status(MothershipStatus.ON_MISSION).build();
        assertEquals(expected, result);
    }

    // TODO others state transitions

}