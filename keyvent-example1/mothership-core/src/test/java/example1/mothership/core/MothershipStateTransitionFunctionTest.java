package example1.mothership.core;

import example1.mothership.core.entities.Mission;
import example1.mothership.core.entities.Plateau;
import javaslang.collection.HashSet;
import javaslang.control.Option;
import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MothershipStateTransitionFunctionTest {

    MothershipStateTransitionFunction function = new MothershipStateTransitionFunction();

    @Test
    public void afterCreated() {

        val mId = new MothershipDataSchema.MothershipId("voyager");
        val emptyMothership = MothershipAggregateRoot.builder().build();
        val rovers = HashSet.of(new MothershipDataSchema.Rover(new MothershipDataSchema.RoverId("enio")), new MothershipDataSchema.Rover(new MothershipDataSchema.RoverId("beto")));
        val event = MothershipDataSchema.MothershipCreated.builder().mothershipId(mId)
                .rovers(rovers)
                .build();
        val result = function.apply(event, emptyMothership);
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.none()).status(MothershipDataSchema.MothershipStatus.AVALIABLE).build();
        assertEquals(expected, result);
    }

    @Test
    public void afterStarted() {

        val mId = new MothershipDataSchema.MothershipId("voyager");
        val rovers = HashSet.of(new MothershipDataSchema.Rover(new MothershipDataSchema.RoverId("enio")), new MothershipDataSchema.Rover(new MothershipDataSchema.RoverId("beto")));
        val avaliableMothership = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.none()).status(MothershipDataSchema.MothershipStatus.AVALIABLE).build();
        val initialPlateau = new Plateau(new MothershipDataSchema.PlateauId("death's cave"), new MothershipDataSchema.PlateauDimension(2, 2));
        val mission = Mission.builder().missionId(new MothershipDataSchema.MissionId("kamikaze")).plateau(initialPlateau).build();
        val event = MothershipDataSchema.MissionStarted.builder().mission(mission).build();
        val result = function.apply(event, avaliableMothership);
        val expected = MothershipAggregateRoot.builder().id(mId).rovers(rovers).mission(Option.of(mission)).status(MothershipDataSchema.MothershipStatus.ON_MISSION).build();
        assertEquals(expected, result);
    }

    // TODO others state transitions

}