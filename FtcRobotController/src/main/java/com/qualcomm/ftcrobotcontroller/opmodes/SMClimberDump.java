package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.common.GyroWorkerThread;
import com.qualcomm.ftcrobotcontroller.common.RedBlueOpMode;
import com.qualcomm.ftcrobotcontroller.common.StateMachine;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.nio.channels.DatagramChannel;

/**
 * Created by Joshua on 1/13/2016.
 */
public class SMClimberDump extends RedBlueOpMode {

    private StateMachine<States> stateMachine;

    private DcMotor left;
    private DcMotor right;
    private DcMotor hangArm;
    private DcMotor plow;

    GyroSensor gyroSensor;
    GyroWorkerThread gyro;
    double leftPower;
    double rightPower;

    Servo climberServo;


    double power;
    double target;
    int distance;

    double turnTarget;


    @Override
    public void init() {

        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        plow = hardwareMap.dcMotor.get("plow");
        hangArm = hardwareMap.dcMotor.get("hangArm");
        gyroSensor = this.hardwareMap.gyroSensor.get("gyro");
        gyro = new GyroWorkerThread(this, gyroSensor);
        gyro.start();

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

    private void updateGyro() {
        setRunToPosition();
        telemetry.addData("Left", left.getCurrentPosition());
        telemetry.addData("Right", right.getCurrentPosition());
        telemetry.addData("LeftPower", left.getPower());
        telemetry.addData("RightPower", right.getPower());
        if (distance > 0) {
            leftPower = power - (gyro.heading() - target) / 200;
            rightPower = power + (gyro.heading() - target) / 200;

            if (leftPower > 0.6)
                leftPower = 0.6;
            else if (leftPower < 0.1)
                leftPower = 0.1;
            left.setPower(leftPower);

            if (rightPower > 0.6)
                rightPower = 0.6;
            else if (rightPower < 0.1)
                rightPower = 0.1;
            right.setPower(rightPower);
        } else {
            leftPower = power - (gyro.heading() - target) / 200;
            rightPower = power + (gyro.heading() - target) / 200;

            if (leftPower < -0.6)
                leftPower = -0.6;
            else if (leftPower > -0.1)
                leftPower = -0.1;
            left.setPower(leftPower);

            if (rightPower < -0.6)
                rightPower = -0.6;
            else if (rightPower > -0.1)
                rightPower = -0.1;
            right.setPower(rightPower);
        }

    }

    private void driveGyroDistance(int distance, double power, int target) {
        gyro.reset();
        this.distance = distance;
        this.power = power;
        this.target = target;
    }

    private void turnGyroDistance(int target, double power) {
        gyro.reset();
        this.turnTarget = target;
        left.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        right.setMode(DcMotorController.RunMode.RUN_USING_ENCODERS);
        left.setPower(power);
        right.setPower(-power);
    }

    private boolean hasTurnCompleted() {
        return Math.abs(gyro.heading() - this.turnTarget) < 3;
    }


    private void setPos(DcMotor motor, int pos) {
        motor.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        motor.setTargetPosition(pos);
    }

    private void resetEncoders() {
        left.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        right.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    private boolean haveEncodersReset() {
        return left.getCurrentPosition() == 0 && right.getCurrentPosition() == 0;
    }

    private void setRunToPosition() {
        left.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        right.setMode(DcMotorController.RunMode.RUN_TO_POSITION);

        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        hangArm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
    }

    private void resetPlowAndHangArm() {
        plow.setMode(DcMotorController.RunMode.RESET_ENCODERS);
        hangArm.setMode(DcMotorController.RunMode.RESET_ENCODERS);
    }

    public void stopMotors() {
        left.setPower(0);
        right.setPower(0);
    }

    enum States implements StateMachine.State {
        INIT {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                return parent.haveEncodersReset() && (parent.plow.getCurrentPosition() == 0 && parent.hangArm.getCurrentPosition() == 0);
            }

            @Override
            public void runState() {
                parent.resetEncoders();
                parent.resetPlowAndHangArm();
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
        PLOW_DOWN {
            @Override
            public boolean shouldChangeState() {
                return Math.abs(parent.plow.getCurrentPosition() - Values.PLOW_DEPLOY) < 3;
            }

            @Override
            public void runState() {
                parent.plow.setTargetPosition(Values.PLOW_DEPLOY);
                parent.plow.setPower(0.5);
            }

            @Override
            public void end() {
                parent.setRunToPosition();
            }

            @Override
            public void tick() {
                parent.telemetry.addData("Plow", parent.plow.getCurrentPosition());
            }

            SMClimberDump parent;
        },
        DRIVE_BACK_1 {
            SMClimberDump parent;
            @Override
            public boolean shouldChangeState() {
                int leftTarget = Math.abs(parent.left.getCurrentPosition() - parent.distance);
                int rightTarget = Math.abs(parent.right.getCurrentPosition() - parent.distance);
                if ((leftTarget < 3) && rightTarget < 3)
                    return true;
                else
                    return false;

            }

            @Override
            public void runState() {
                parent.driveGyroDistance(-7650, -0.4, 0);
                parent.left.setTargetPosition(parent.distance);
                parent.right.setTargetPosition(parent.distance);
            }

            @Override
            public void end() {
                parent.stopMotors();
            }

            @Override
            public void tick() {
                parent.updateGyro();
            }
        },/*
        GYRO_CALIBRATE {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                return !parent.gyro.isCalibrating();
            }

            @Override
            public void runState() {
                parent.gyro.calibrate();
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },*/
        TURN_1 {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                return parent.hasTurnCompleted();
            }

            @Override
            public void runState() {
                parent.turnGyroDistance(45, 0.3);
            }

            @Override
            public void end() {
                parent.stopMotors();
                parent.setRunToPosition();
                parent.resetEncoders();
            }

            @Override
            public void tick() {

            }


        },
        DRIVE_FORWARD {
            SMClimberDump parent;

            @Override
            public boolean shouldChangeState() {
                if ((Math.abs(parent.left.getCurrentPosition() - parent.distance) < 3) && (Math.abs(parent.right.getCurrentPosition() - parent.distance) < 3))
                    return true;
                else
                    return false;

            }

            @Override
            public void runState() {
                parent.driveGyroDistance(-16130, -0.3, 0);
                parent.left.setTargetPosition(parent.distance);
                parent.right.setTargetPosition(parent.distance);
            }

            @Override
            public void end() {
                parent.stopMotors();
            }

            @Override
            public void tick() {
                parent.updateGyro();
            }
        },
        TURN_2{
            SMClimberDump parent;
            @Override
            public boolean shouldChangeState() {
                return parent.hasTurnCompleted();
            }

            @Override
            public void runState() {
                parent.turnGyroDistance(135, 0.3);
            }

            @Override
            public void end() {
                parent.stopMotors();
                parent.setRunToPosition();
                parent.resetEncoders();
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
