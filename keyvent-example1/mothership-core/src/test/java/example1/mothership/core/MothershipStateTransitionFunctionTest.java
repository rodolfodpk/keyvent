package example1.mothership.core;

import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.control.Option;
import lombok.val;
import org.junit.Test;

import static example1.mothership.core.MothershipDataSchema.*;
import static org.junit.Assert.assertEquals;

public class MothershipStateTransitionFunctionTest {

    MothershipStateTransitionFunction function = new MothershipStateTransitionFunction();

    @Test
    public void after_created_mothership() {

        // given
        val mId = new MothershipId("voyager");
        val emptyMothership = MothershipAggregateRoot.builder().build();
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));

        // when
        val event = MothershipCreated.builder().mothershipId(mId)
                .rovers(rovers)
                .build();
        val result = function.apply(event, emptyMothership);

        // then
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.none()).status(MothershipStatus.AVALIABLE).build();
        assertEquals(expected, result);
    }

    @Test
    public void after_started_mission() {

        // given
        val mId = new MothershipId("voyager");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.none()).status(MothershipStatus.AVALIABLE).build();
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();

        // when
        val event = MissionStarted.builder().mission(mission).build();
        val result = function.apply(event, avaliableMothership);

        // then
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.of(mission)).status(MothershipStatus.ON_MISSION).build();
        assertEquals(expected, result);
    }


    @Test
    public void after_launched_first_rover() {

        // given
        val mId = new MothershipId("voyager");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.of(mission)).status(MothershipStatus.ON_MISSION).build();

        // when
        val event = RoverLaunched.builder().roverId(new RoverId("enio"))
                .roverPosition(new RoverPosition(new PlateauLocation(0,0), RoverDirection.NORTH)).build();
        val result = function.apply(event, avaliableMothership);

        // then
        val newMission = mission.withPlateau(initialPlateau.withLaunchedRovers(HashMap.of(event.getRoverId().getId(), event.getRoverPosition())));
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers)
                .mission(Option.of(newMission)).status(MothershipStatus.ON_MISSION).build();
        assertEquals(expected, result);
    }

    // TODO others state transitions

}