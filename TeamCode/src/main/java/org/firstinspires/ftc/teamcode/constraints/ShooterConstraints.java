package org.firstinspires.ftc.teamcode.constraints;

import com.bylazar.configurables.annotations.Configurable;

import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.control.feedforward.BasicFeedforwardParameters;

@Configurable
public class ShooterConstraints {
    public static double shooterGoal = 2500;
    public static BasicFeedforwardParameters feedforwardParameters = new BasicFeedforwardParameters(0.001,0,0);
    public static PIDCoefficients pidCoefficients = new PIDCoefficients(0.0005, 0, 0);
}
