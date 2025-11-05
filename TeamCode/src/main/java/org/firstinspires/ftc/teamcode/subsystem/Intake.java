package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotorSimple;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.CRServoEx;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.PowerableGroup;

public class Intake implements Subsystem {
    public static final Intake INSTANCE = new Intake();
    private Intake() {}

    public final MotorEx motor1 = new MotorEx("intake").reversed();
    private final CRServoEx servo1 = new CRServoEx("servo1"); // bottom transfer
    private final CRServoEx servo2 = new CRServoEx("servo2"); // top transfer (to shooter)
    private final PowerableGroup crservogroup = new PowerableGroup(servo1, servo2);

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

    @Override
    public void initialize() {
        servo1.getServo().setDirection(DcMotorSimple.Direction.REVERSE);
    }
}
