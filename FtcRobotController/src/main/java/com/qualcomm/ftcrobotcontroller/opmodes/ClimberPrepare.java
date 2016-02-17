package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.ftcrobotcontroller.common.Robot;
import com.qualcomm.ftcrobotcontroller.common.StateMachine;
import com.qualcomm.ftcrobotcontroller.common.Values;

public class ClimberPrepare extends RedBlueOpMode {

    private StateMachine<States> stateMachine;
    private Robot robot;

    @Override
    public void init() {
        robot = new Robot(this);
        stateMachine = new StateMachine<States>(States.DEPLOY_PLOW, this);
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
        DEPLOY_PLOW {
            ClimberPrepare parent;

            @Override
            public boolean shouldChangeState() {
                return Math.abs(Values.PLOW_DEPLOY - parent.robot.getPlow().getCurrentPosition()) < 20;
            }

            @Override
            public void runState() {
                parent.robot.deployPlow();
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        DRIVE_BACK {
            ClimberPrepare parent;

            @Override
            public boolean shouldChangeState() {
                return parent.robot.movementComplete();
            }

            @Override
            public void runState() {
//                parent.robot.driveDistanceTicks(-7000, -1);
                parent.robot.driveDistanceCm(-60, -1);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        TURN_TOWARDS_GOAL {
            ClimberPrepare parent;

            @Override
            public boolean shouldChangeState() {
                return parent.robot.movementComplete();
            }

            @Override
            public void runState() {
                if (parent.teamColor == TeamColor.BLUE)
                    parent.robot.turn(45, 0.5);
                else
                    parent.robot.turn(-45, 0.5);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        DRIVE_TO_GOAL{
            ClimberPrepare parent;
            @Override
            public boolean shouldChangeState() {
                return parent.robot.movementComplete();
            }

            @Override
            public void runState() {
//                parent.robot.driveDistanceTicks(-26000, -1);
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