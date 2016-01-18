package com.qualcomm.ftcrobotcontroller.opmodes;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.common.StateMachine;
import com.qualcomm.ftcrobotcontroller.common.Values;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;

public class ResetPlowAndArm extends OpMode {

    DcMotor plow;
    DcMotor arm;

    StateMachine<State> sm;

    @Override
    public void init() {
        plow = this.hardwareMap.dcMotor.get("plow");
        plow.setDirection(DcMotor.Direction.FORWARD);
        plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        arm = this.hardwareMap.dcMotor.get("hangArm");
        arm.setDirection(DcMotor.Direction.FORWARD);
        arm.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
        sm = new StateMachine<State>(State.RESET_ARM, this);
        sm.enableDebug();
    }

    @Override
    public void loop() {
        sm.tick();
    }

    private enum State implements StateMachine.State {
        PLOW_DOWN {
            ResetPlowAndArm parent;

            @Override
            public boolean shouldChangeState() {
                Log.w("plow_pos", parent.plow.getCurrentPosition() + "");
                return Math.abs(parent.plow.getCurrentPosition() - Values.PLOW_DEPLOY) < 10;
            }

            @Override
            public void runState() {
                Log.w("plow_start_pos", parent.plow.getCurrentPosition() + "");
                parent.plow.setTargetPosition(Values.PLOW_DEPLOY);
                parent.plow.setPower(0.5);
            }

            @Override
            public void end() {

            }

            @Override
            public void tick() {

            }
        },
        RESET_ARM {
            ResetPlowAndArm parent;

            @Override
            public boolean shouldChangeState() {
                Log.w("arm_pos", Integer.toString(parent.arm.getCurrentPosition()));
                return Math.abs(parent.arm.getCurrentPosition() - 600) < 10;
            }

            @Override
            public void runState() {
                Log.w("arm_start_pos", parent.arm.getCurrentPosition() + "");
                parent.arm.setTargetPosition(600);
                parent.arm.setPower(1);
            }

            @Override
            public void end() {
                parent.arm.setDirection(DcMotor.Direction.FORWARD);
            }

            @Override
            public void tick() {

            }
        },
        RESET_PLOW {
            @Override
            public boolean shouldChangeState() {
                Log.w("plow_pos", Integer.toString(parent.plow.getCurrentPosition()));
                return Math.abs(parent.plow.getCurrentPosition()) < 10;
            }

            @Override
            public void runState() {
                Log.w("plow_start_pos", parent.plow.getCurrentPosition() + "");
                parent.plow.setMode(DcMotorController.RunMode.RUN_TO_POSITION);
                parent.plow.setTargetPosition(0);
                parent.plow.setPower(0.5);
                parent.plow.setDirection(DcMotor.Direction.REVERSE);
            }

            @Override
            public void end() {
                parent.plow.setDirection(DcMotor.Direction.FORWARD);
            }

            @Override
            public void tick() {

            }

            ResetPlowAndArm parent;
        },
        RESET_ENCODERS {
            ResetPlowAndArm parent;

            @Override
            public boolean shouldChangeState() {
                for (DcMotor m : parent.hardwareMap.dcMotor) {
                    if (m.getCurrentPosition() != 0)
                        return false;
                }
                return true;
            }

            @Override
            public void runState() {
                for (DcMotor m : parent.hardwareMap.dcMotor) {
                    m.setMode(DcMotorController.RunMode.RESET_ENCODERS);
                }
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
