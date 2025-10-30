package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.CRServoEx;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.PowerableGroup;

public class Intake implements Subsystem {
    public static final Intake INSTANCE = new Intake();
    private Intake() {}

    private final MotorEx motor1 = new MotorEx("intake").reversed();
    private final CRServoEx servo1 = new CRServoEx("servo1");
    private final PowerableGroup crservogroup = new PowerableGroup(servo1);

    public Command spinUp = new InstantCommand(() -> {
        motor1.setPower(1);
    });

    public Command spinDown = new InstantCommand(() -> {
        motor1.setPower(0);
    });

    public Command transferUp = new InstantCommand(() -> {
        crservogroup.setPower(1);
    });

    public Command transferDown = new InstantCommand(() -> {
        crservogroup.setPower(0);
    });
}
