package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.ftcrobotcontroller.common.Robot;
import com.qualcomm.ftcrobotcontroller.common.StateMachine;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class ClimberPrepare extends RedBlueOpMode {

    private StateMachine<States> stateMachine;
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(this);
        stateMachine = new StateMachine<States>(States.DEPLOY_SERVO, this);
        stateMachine.enableDebug();
    }

    @Override
    public void loop() {
        stateMachine.tick();
    }

    public void stopDriveMotors() {
        robot.stopDriveMotors();
    }

    private enum States implements StateMachine.State {
        DEPLOY_SERVO{
            ClimberPrepare parent;
            @Override
            public boolean shouldChangeState() {
                return parent.robot.getFrontDeflector().getPosition() == 0;
            }

            @Override
            public void runState() {
                parent.robot.getFrontDeflector().setPosition(0);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        DRIVE_AND_HOPE_FOR_THE_BEST{
            ClimberPrepare parent;
            @Override
            public boolean shouldChangeState() {
                return parent.robot.movementComplete();
            }

            @Override
            public void runState() {
                parent.robot.driveDistanceCm(234, 1);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        MAKE_THE_PLOW_GO_DOWN{
            ClimberPrepare parent;
            @Override
            public boolean shouldChangeState() {
                return Math.abs(parent.robot.getPlow().getCurrentPosition() - Values.PLOW_DEPLOY) < 30;
            }

            @Override
            public void runState() {
                parent.robot.getPlow().setMode(DcMotorController.RunMode.RUN_TO_POSITION);
                parent.robot.getPlow().setTargetPosition(Values.PLOW_DEPLOY);
                parent.robot.getPlow().setPower(1);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        PUT_OUT_ARM{
            ClimberPrepare parent;
            @Override
            public boolean shouldChangeState() {
                return Values.HANGARM_DEPLOY - parent.robot.getHangArmMotor().getCurrentPosition() < 30;
            }

            @Override
            public void runState() {
                parent.robot.getHangArmMotor().setMode(DcMotorController.RunMode.RUN_TO_POSITION);
                parent.robot.getHangArmMotor().setTargetPosition(Values.HANGARM_DEPLOY);
                parent.robot.getHangArmMotor().setPower(1);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        }
    }
}