package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedforward.BasicFeedforwardParameters;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.impl.ServoEx;

@Configurable
public class Shooter implements Subsystem {
    public boolean shouldStop = true;
    private Shooter() { }

    public final MotorEx motor1 = new MotorEx("shooter1").reversed();
    public final MotorEx motor2 = new MotorEx("shooter2");
    public final ServoEx hoodServo1 = new ServoEx("hoodServo1");

    public static double shooterGoal = 1550;
    public static BasicFeedforwardParameters feedforwardParameters = new BasicFeedforwardParameters(0.00027, 0.0, 0.0);
    public static PIDCoefficients pidCoefficients = new PIDCoefficients(0.000165, 0, 0.0);

    private final ControlSystem controlSystem = ControlSystem.builder()
            .basicFF(feedforwardParameters)
            .velPid(pidCoefficients)
            .build();

    public Command spinUp = new InstantCommand(() -> {
        controlSystem.setGoal(new KineticState(Double.MAX_VALUE, shooterGoal, Double.MAX_VALUE));
        shouldStop = false;
        //motor.setPower(1);
    });

    public Command spinDown = new InstantCommand(() -> {
        shouldStop = true;
        controlSystem.setGoal(new KineticState(Double.MAX_VALUE, 0, Double.MAX_VALUE));
        //motor.setPower(0);
    });

    public Command hoodTest = new InstantCommand(() -> {
        hoodServo1.setPosition(0.5);
    });

    private TelemetryManager telemetryM;

    // ticksPerSecond = RPM x 28 / 60
    public double ticksToRPM(double ticksPerSecond, double countsPerRevolution) {
        return (ticksPerSecond / countsPerRevolution * 60);
    }

    @Override
    public void initialize() {
        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
    }

    @Override
    public void periodic() {
        double power = controlSystem.calculate(motor1.getState());

        if (shouldStop) {
            motor1.setPower(0);
            motor2.setPower(0);
        } else {
            controlSystem.setGoal(new KineticState(Double.MAX_VALUE, shooterGoal, Double.MAX_VALUE));
            motor1.setPower(power);
            motor2.setPower(power);
        }
        telemetryM.addData("shooter1 ticks/s", motor1.getVelocity());
        telemetryM.addData("shooter1 rpm", ticksToRPM(motor1.getVelocity(), 28));
        telemetryM.addData("shooter2 ticks/s", motor2.getVelocity());
        telemetryM.addData("shooter2 rpm", ticksToRPM(motor2.getVelocity(), 28));
        telemetryM.addData("shooter power", power);
        telemetryM.addData("shouldStop", shouldStop);

        telemetryM.addData("shooterTargetVelocity", controlSystem.getGoal().getVelocity());
        telemetryM.addData("shooterVelocity", motor1.getVelocity());
    }

    public static final Shooter INSTANCE = new Shooter();
}
