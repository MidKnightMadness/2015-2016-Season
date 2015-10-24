package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.MotorGroup;
import com.qualcomm.ftcrobotcontroller.common.TKAOpmode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class TKAOpmodeExample extends TKAOpmode{

    // Group 'front_left' into the 'drivetrain_left' group
    @MotorGroup("drivetrain_left")
    DcMotor front_left;
    // Group 'front_right' into the 'drivetrain_right' group
    @MotorGroup("drivetrain_right")
    DcMotor front_right;
    // Group 'back_left' into the 'drivetrain_left' group
    @MotorGroup("drivetrain_left")
    DcMotor back_left;
    // Group 'back_right' into the 'drivetrain_right' group
    @MotorGroup("drivetrain_right")
    DcMotor back_right;
    

    @Override
    public void initialize() {

    }

    @Override
    public void loop() {
        // Run motor group 'drivetrain_left' at the specified power
        runMotorGroup("drivetrain_left", 0.5F);
        // Run motor group 'drivetrain_right' at the specified power
        runMotorGroup("drivetrain_right", 0.5F);
    }
}
