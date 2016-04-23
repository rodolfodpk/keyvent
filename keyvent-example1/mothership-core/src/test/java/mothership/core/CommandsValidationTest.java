package mothership.core;

import lombok.val;
import mothership.core.entities.Plateau;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static mothership.core.MothershipDataSchema.*;
import static mothership.core.MothershipDataSchema.MothershipId;
import static mothership.core.MothershipDataSchema.StartsMissionTo;
import static org.junit.Assert.assertEquals;

public class CommandsValidationTest {

    @Test
    public void start_missing_without_plateu() {

        val mothershipId = new MothershipId("voyager");
        val missionId = new MissionId("deep space");
        val cmd = StartsMissionTo.builder().mothershipId(mothershipId).missionId(missionId).build();

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<StartsMissionTo>> constraintViolations =
                validator.validate(cmd);

        assertEquals(constraintViolations.size(), 1);
        assertEquals("plateau", constraintViolations.iterator().next().getPropertyPath().toString());
        assertEquals("may not be null", constraintViolations.iterator().next().getMessage());

    }

    @Test
    public void start_missing_with_invalid_plateu_dimension() {

        val mothershipId = new MothershipId("voyager");
        val missionId = new MissionId("deep space");
        val plateau = Plateau.builder().id(new PlateauId("plateau1")).dimension(new PlateauDimension(1, 3)).build();
        val cmd = StartsMissionTo.builder().mothershipId(mothershipId).missionId(missionId).plateau(plateau).build();

        ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<StartsMissionTo>> constraintViolations = validator.validate(cmd);

        assertEquals(constraintViolations.size(), 1);
        assertEquals("plateau.dimension.height", constraintViolations.iterator().next().getPropertyPath().toString());
        assertEquals("must be greater than or equal to 2", constraintViolations.iterator().next().getMessage());

    }

}
