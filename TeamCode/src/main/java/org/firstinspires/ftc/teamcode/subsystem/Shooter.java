package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;

public class Shooter implements Subsystem {
    public static final Shooter INSTANCE = new Shooter();
    private Shooter() { }

    private final MotorEx motor = new MotorEx("shooter1");

    private final ControlSystem controlSystem = ControlSystem.builder()
            .velPid(0.005)
            .build();

    public Command spinUp = new InstantCommand(() -> {
        controlSystem.setGoal(new KineticState(Double.MAX_VALUE, 2000, Double.MAX_VALUE));
        //motor.setPower(1);
    });

    public Command spinDown = new InstantCommand(() -> {
        controlSystem.setGoal(new KineticState(Double.MAX_VALUE, 0, Double.MAX_VALUE));
        //motor.setPower(0);
    });

    public double ticksToRPM(double ticksPerSecond, double countsPerRevolution) {
        return (ticksPerSecond / countsPerRevolution * 60);
    }

    @Override
    public void periodic() {
        motor.setPower(controlSystem.calculate(motor.getState()));
        ActiveOpMode.telemetry().addData("shooter1 ticks/s", motor.getVelocity());
        ActiveOpMode.telemetry().addData("shooter1 rpm", ticksToRPM(motor.getVelocity(), 28));
        ActiveOpMode.telemetry().addData("cs power", motor.getPower());
        ActiveOpMode.telemetry().addData("shooter1 target", controlSystem.getGoal());
    }
}
