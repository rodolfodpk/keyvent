package example1.mothership.core;

import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import example1.mothership.core.entities.Rover;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.HashSet;
import javaslang.collection.Map;
import javaslang.control.Option;
import lombok.val;
import org.junit.Test;

import static example1.mothership.core.MothershipDataSchema.*;
import static example1.mothership.core.MothershipDataSchema.RoverDirection.*;
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
        val expected = MothershipAggregateRoot.builder().id(mId)
                .rovers(HashMap.ofEntries(event.getRovers().map(rover -> Tuple.of(rover.getId().getId(), rover))))
                .mission(Option.none()).status(MothershipStatus.AVALIABLE).build();
        assertEquals(expected, result);
    }

    @Test
    public void after_started_mission() {

        // given
        val mId = new MothershipId("voyager");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        Map<String, Rover> mapOfRovers = HashMap.ofEntries(rovers.map(rover -> Tuple.of(rover.getId().getId(), rover)));
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId)
                .rovers(mapOfRovers)
                .mission(Option.none()).status(MothershipStatus.AVALIABLE).build();
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();

        // when
        val event = MissionStarted.builder().mission(mission).build();
        val result = function.apply(event, avaliableMothership);

        // then
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(mapOfRovers).mission(Option.of(mission))
                .status(MothershipStatus.ON_MISSION).build();
        assertEquals(expected, result);
    }


    @Test
    public void after_launched_first_rover() {

        // given
        val mId = new MothershipId("voyager");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        Map<String, Rover> mapOfRovers = HashMap.ofEntries(rovers.map(rover -> Tuple.of(rover.getId().getId(), rover)));
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(mapOfRovers)
                .mission(Option.of(mission)).status(MothershipStatus.ON_MISSION).build();

        // when
        val event = RoverLaunched.builder().roverId(new RoverId("enio"))
                .plateauLocation(new PlateauLocation(0,0)).build();
        val result = function.apply(event, avaliableMothership);

        // then
        val newMission = mission.withPlateau(initialPlateau.withLandedRovers(HashMap.of(event.getRoverId().getId(), event.getPlateauLocation())));
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(mapOfRovers)
                .mission(Option.of(newMission)).status(MothershipStatus.ON_MISSION).build();
        assertEquals(expected, result);
    }

    @Test
    public void after_change_rover_direction() {

        // given
        val mId = new MothershipId("voyager");
        val rovers = HashSet.of(new Rover(new RoverId("enio")), new Rover(new RoverId("beto")));
        Map<String, Rover> mapOfRovers = HashMap.ofEntries(rovers.map(rover -> Tuple.of(rover.getId().getId(), rover)));
        val initialPlateau = new Plateau(new PlateauId("death's cave"), new PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MissionId("kamikaze")).plateau(initialPlateau).build();
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(mapOfRovers)
                .mission(Option.of(mission)).status(MothershipStatus.ON_MISSION).build();

        // when
        val event = new RoverDirectionChanged(new RoverId("enio"), SOUTH);
        val result = function.apply(event, avaliableMothership);

        // then
        val expected = avaliableMothership.withRovers(avaliableMothership.getRovers().put("enio", new Rover(new RoverId("enio"), SOUTH)));
        assertEquals(expected, result);
    }

    @Test
    public void after_move_rover_forward() {
    }

    @Test
    public void after_finish_mission() {
    }

    // TODO others state transitions

}