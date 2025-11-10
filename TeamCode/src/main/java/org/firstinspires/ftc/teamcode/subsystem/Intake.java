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

    public Command spinUp = new InstantCommand(() -> {
        motor1.setPower(1);
    });

    public Command spinDown = new InstantCommand(() -> {
        motor1.setPower(0);
    });
}
