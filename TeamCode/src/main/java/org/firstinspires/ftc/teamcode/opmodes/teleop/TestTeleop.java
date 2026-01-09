package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.subsystem.Intake;
import org.firstinspires.ftc.teamcode.subsystem.Shooter;
import org.firstinspires.ftc.teamcode.subsystem.Transfer;
import org.firstinspires.ftc.teamcode.subsystem.Webcam;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.FieldCentric;
import dev.nextftc.hardware.driving.MecanumDriverControlled;
import dev.nextftc.hardware.impl.Direction;
import dev.nextftc.hardware.impl.IMUEx;
import dev.nextftc.hardware.impl.MotorEx;

@TeleOp(name = "NextFTC TeleOp Program Java")
public class TestTeleop extends NextFTCOpMode {
    public TestTeleop() {
        addComponents(
                new SubsystemComponent(Shooter.INSTANCE, Intake.INSTANCE, Transfer.INSTANCE, Webcam.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    // change the names and directions to suit your robot
    private final MotorEx frontLeftMotor = new MotorEx("front_left");
    private final MotorEx frontRightMotor = new MotorEx("front_right").reversed();
    private final MotorEx backLeftMotor = new MotorEx("back_left");
    private final MotorEx backRightMotor = new MotorEx("back_right").reversed();
    private final IMUEx imu = new IMUEx("imu", Direction.LEFT, Direction.UP).zeroed();

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    private boolean slowMode = false;
    private double slowModeMultiplier = 0.25;

    @Override
    public void onStartButtonPressed() {
        Command driverControlled = new MecanumDriverControlled(
                frontLeftMotor,
                frontRightMotor,
                backLeftMotor,
                backRightMotor, // .map(x -> slowMode ? Math.copySign(Math.pow(Math.abs(x), 1.5), x) * slowModeMultiplier : x)
                Gamepads.gamepad1().leftStickY().negate().map(y -> slowMode ? y * slowModeMultiplier : y),
                Gamepads.gamepad1().leftStickX().map(x -> slowMode ? x * slowModeMultiplier : x),
                Gamepads.gamepad1().rightStickX().map(x -> slowMode ? x * slowModeMultiplier : x),
                new FieldCentric(imu)
        );
        driverControlled.schedule();

        Gamepads.gamepad1().cross().toggleOnBecomesTrue()
                .whenBecomesTrue(() -> Shooter.INSTANCE.spinUp.schedule())
                .whenBecomesFalse(() -> Shooter.INSTANCE.spinDown.schedule());

        Gamepads.gamepad1().triangle().toggleOnBecomesTrue()
                .whenBecomesTrue(() -> slowMode = true)
                .whenBecomesFalse(() -> slowMode = false);

        Gamepads.gamepad1().dpadUp()
                        .whenBecomesTrue(() ->
                                slowModeMultiplier = Math.min(1, slowModeMultiplier+0.05));

        Gamepads.gamepad1().dpadDown()
                        .whenBecomesTrue(() ->
                                slowModeMultiplier = Math.max(0.05, slowModeMultiplier-0.05));

        Gamepads.gamepad1().dpadLeft()
                .whenBecomesTrue(imu::zero);

        Gamepads.gamepad1().leftBumper().toggleOnBecomesTrue()
                .whenBecomesTrue(() -> Intake.INSTANCE.spinUp.schedule())
                .whenBecomesFalse(() -> Intake.INSTANCE.spinDown.schedule());

        Gamepads.gamepad1().rightBumper()
                .whenBecomesTrue(() -> Transfer.INSTANCE.cycleOverrideState.schedule());

        Gamepads.gamepad1().start().toggleOnBecomesTrue()
                .whenBecomesTrue(() -> Transfer.INSTANCE.overrideOn.schedule())
                .whenBecomesFalse(() -> Transfer.INSTANCE.overrideOff.schedule());

        Gamepads.gamepad1().leftTrigger().greaterThan(0.2)
                .whenBecomesTrue(() -> Transfer.INSTANCE.shootModeOn.schedule())
                .whenBecomesFalse(() -> Transfer.INSTANCE.shootModeOff.schedule());
    }

    @Override
    public void onUpdate() {
        ActiveOpMode.telemetry().addData("slowMode toggle", slowMode);
        ActiveOpMode.telemetry().addData("slowMode multiplier", slowModeMultiplier);
        ActiveOpMode.telemetry().update();

        RobotLog.d("Motor Amp: Left Front Drive: " + frontLeftMotor.getMotor().getCurrent(CurrentUnit.AMPS));
        RobotLog.d("Motor Amp: Left Back Drive: " + backLeftMotor.getMotor().getCurrent(CurrentUnit.AMPS));
        RobotLog.d("Motor Amp: Right Front Drive: " + frontRightMotor.getMotor().getCurrent(CurrentUnit.AMPS));
        RobotLog.d("Motor Amp: Right Back Drive: " + backRightMotor.getMotor().getCurrent(CurrentUnit.AMPS));

        RobotLog.d("Motor Amp: Intake Motor: " + Intake.INSTANCE.motor1.getMotor().getCurrent(CurrentUnit.AMPS));
        RobotLog.d("Motor Amp: Shooter Motor 1: " + Shooter.INSTANCE.motor1.getMotor().getCurrent(CurrentUnit.AMPS));
        RobotLog.d("Motor Amp: Shooter Motor 2: " + Shooter.INSTANCE.motor2.getMotor().getCurrent(CurrentUnit.AMPS));

        panelsTelemetry.getTelemetry().update(telemetry);
    }
}