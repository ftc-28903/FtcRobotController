package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.CRServoEx;
import dev.nextftc.hardware.powerable.PowerableGroup;

public class Transfer implements Subsystem {
    public static final Transfer INSTANCE = new Transfer();
    private Transfer() {}

    private final CRServoEx servo1 = new CRServoEx("servo1"); // bottom transfer
    private final CRServoEx servo2 = new CRServoEx("servo2"); // top transfer (to shooter)
    private final PowerableGroup crServoGroup = new PowerableGroup(servo1, servo2);
    private RevColorSensorV3 revColorSensor;
    private boolean manualOverride = false;
    // 0-2; 0 = -1; 1 = 0; 2 = 1
    private int manualOverrideState = 0;
    // in cm
    private double targetDistance = 8;

    public Command overrideOn = new InstantCommand(() -> manualOverride = true);
    public Command overrideOff = new InstantCommand(() -> manualOverride = false);

    public Command shootModeOn = new InstantCommand(() -> targetDistance = 5);
    public Command shootModeOff = new InstantCommand(() -> targetDistance = 8);

    public Command cycleOverrideState = new InstantCommand(() -> {
        manualOverrideState = (manualOverrideState == 2) ? 0 : manualOverrideState + 1;
    });

    private int stateToSpeed(int state) {
        // made this way to be clearer
        switch (state) {
            case 0:
                return -1;
            case 1:
                return 0;
            case 2:
                return 1;
        }

        return 0;
    }

    @Override
    public void initialize() {
        revColorSensor = ActiveOpMode.hardwareMap().get(RevColorSensorV3.class, "color_sensor");
        servo1.getServo().setDirection(DcMotorSimple.Direction.REVERSE);
    }

    @Override
    public void periodic() {
        if (!manualOverride) {
            crServoGroup.setPower(revColorSensor.getDistance(DistanceUnit.CM) < targetDistance ? 1 : 0);
        } else {
            crServoGroup.setPower(stateToSpeed(manualOverrideState));
        }

        ActiveOpMode.telemetry().addData("Transfer manualOverride", manualOverride);
        ActiveOpMode.telemetry().addData("Transfer manualOverride speed", stateToSpeed(manualOverrideState));
        // somethings gonna go wrong anyways
        ActiveOpMode.telemetry().addData("Transfer manualOverrideState", manualOverrideState);
        ActiveOpMode.telemetry().addData("Transfer targetDistance", targetDistance);
        ActiveOpMode.telemetry().addData("Transfer revColorSensor distance (CM)", revColorSensor.getDistance(DistanceUnit.CM));
    }
}
