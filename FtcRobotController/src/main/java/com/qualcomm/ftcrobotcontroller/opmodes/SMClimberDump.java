package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.ftcrobotcontroller.common.StateMachine;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by Joshua on 1/13/2016.
 */
public class SMClimberDump extends RedBlueOpMode {

    private StateMachine<States> stateMachine;

    private DcMotor left;
    private DcMotor right;
    private DcMotor hangArm;
    private DcMotor plow;

    Servo climberServo;


    @Override
    public void init() {

        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        plow = hardwareMap.dcMotor.get("plow");
        hangArm = hardwareMap.dcMotor.get("hangArm");

        climberServo = hardwareMap.servo.get("climber");

        left.setDirection(DcMotor.Direction.FORWARD);
        right.setDirection(DcMotor.Direction.REVERSE);
        hangArm.setDirection(DcMotor.Direction.FORWARD);
        plow.setDirection(DcMotor.Direction.FORWARD);

        stateMachine = new StateMachine<States>(States.INIT, this);
        stateMachine.enableDebug();
    }

    @Override
    public void loop() {
        stateMachine.tick();

    }

    private void resetEncoders() {
        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    private boolean haveEncodersReset() {
        if (left.getCurrentPosition() == 0 && right.getCurrentPosition() == 0)
            return true;
        else
            return false;
    }

    private void setRunToPosition() {
        left.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    enum States implements StateMachine.State {
        INIT {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                return parent.haveEncodersReset();
            }

            @Override
            public void runState() {
                parent.resetEncoders();
                parent.climberServo.setPosition(Values.CLIMBER_CLOSE);
            }

            @Override
            public void end() {
                parent.setRunToPosition();
            }

            @Override
            public void tick() {

            }
        },
        DRIVE_FORWARD {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                if (Math.abs(parent.left.getCurrentPosition() - 1000) < 3 && Math.abs(parent.right.getCurrentPosition() - 1000) < 3)
                    return true;
                else
                    return false;

            }

            @Override
            public void runState() {
                parent.left.setTargetPosition(1000);
                parent.right.setTargetPosition(1000);
                parent.left.setPower(0.5);
                parent.right.setPower(0.5);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        HANGARM_DEPLOY {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                if (Math.abs(parent.hangArm.getCurrentPosition() - Values.HANGARM_DEPLOY) < 3)
                    return true; //True too soon? Advancing too soon?
                else
                    return false;
            }

            @Override
            public void runState() {
                parent.hangArm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
                parent.hangArm.setTargetPosition(Values.HANGARM_DEPLOY);
                parent.hangArm.setPower(0.75);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        SERVO_RELEASE {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                return true; //maybe??
            }

            @Override
            public void runState() {
                parent.climberServo.setPosition(Values.CLIMBER_OPEN);
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
