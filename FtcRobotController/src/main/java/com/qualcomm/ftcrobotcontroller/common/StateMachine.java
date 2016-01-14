package com.qualcomm.ftcrobotcontroller.common;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.LinkedList;

/**
 * Wrapper class for easy creation of state machines
 * <p/>
 * This class is used for an easier implementation of state machines than <code>switch</code> statements
 * <br>
 * Call {@link StateMachine#tick()} in your loop function to
 * properly update the state machine's state
 *
 * @param <STATE> The state class to use for this state machine
 * @author Austin
 */
public class StateMachine<STATE extends Enum & StateMachine.State> {

    private LinkedList<STATE> states;
    private OpMode opMode;
    private int currentState = -1;
    private boolean isRunning = false;
    private long startTime = -1;
    private long stateAbortTime;
    private int MAX_STATE_RUN_TIME = 31 * 1000;
    private boolean debug = false;
    private boolean finished = false;

    public StateMachine(Class<? extends STATE> states, OpMode opMode, long delay) {
        this.opMode = opMode;
        this.states = new LinkedList<STATE>(EnumSet.allOf(states));
        this.startTime = System.currentTimeMillis() + delay;
    }

    public StateMachine(STATE states, OpMode opMode, long delay) {
        this(states.getDeclaringClass(), opMode, delay);
    }

    public StateMachine(STATE states, OpMode opMode) {
        this(states, opMode, 0);
    }

    /**
     * Gets the current state of the state machine.
     *
     * @return the current state of the state machine
     */
    public STATE getCurrentState() {
        try {
            return states.get(currentState);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Executes the next statement in the state machine's list
     * <p/>
     * By default, states are executed in top-to-bottom (top of class down) order. This order can be
     * changed by using {@link #setNextState(Enum)}
     */
    public void executeNext() {
        if(finished)
            return;
        int nextState = ++currentState;
        STATE next;
        try {
            next = states.get(nextState);
        } catch (IndexOutOfBoundsException e){
            finished = true;
            return;
        }
        if (next == null) {
            isRunning = true;
            return;
        }
        Log.i("Switching to state", next.toString());
        inject(next, "hardwareMap", HardwareMap.class, opMode.hardwareMap);
        inject(next, "gamepad1", Gamepad.class, opMode.gamepad1);
        inject(next, "gamepad2", Gamepad.class, opMode.gamepad2);
        inject(next, "telemetry", Telemetry.class, opMode.telemetry);
        inject(next, "parent", opMode.getClass(), opMode);
        try {
            if (opMode instanceof RedBlueOpMode) {
                Field tcField = opMode.getClass().getField("teamColor");
                if (tcField != null) {
                    tcField.setAccessible(true);
                    Object teamColorObj = tcField.get(opMode);
                    if (teamColorObj instanceof RedBlueOpMode.TeamColor) {
                        inject(next, "teamColor", RedBlueOpMode.TeamColor.class, teamColorObj);
                    }
                }
            }
        } catch (Exception ignored){}
        next.runState();
        resetHangTimer();
        if (next.shouldChangeState()) {
            next.end();
            executeNext();
        }
    }

    /**
     * Updates the state machine
     * <p/>
     * It is crucial to the correct functioning of the state machine that this method to be called
     * as quickly as it can. States will only advance when this method is called. Also, state
     * timeouts are also calculated and states are correctly aborted only when this method is called.
     */
    public void tick() {
        if(finished) {
            opMode.telemetry.addData("Status", "State machine finished");
            return;
        }
        if (startTime != -1 && !isRunning) {
            if (System.currentTimeMillis() > this.startTime)
                start();
        }
        if(startTime != -1 && !isRunning){
            opMode.telemetry.addData("Starting in", (this.startTime - System.currentTimeMillis()) / 1000D);
        }
        if (isRunning) {
            // Check if the state is hung
            if (stateHung()) {
                System.err.println("**********");
                System.err.println();
                System.err.println(String.format("The state %s has reached its max execution time", this.getCurrentState().toString()));
                System.err.println("If you were not expecting this, increase the max execution time using increaseMaxRunTime()");
                System.err.println("Aborting state and halting state machine!");
                System.err.println();
                System.err.println("**********");
                finished = true;
                return;
            }
            getCurrentState().tick();
            if (getCurrentState().shouldChangeState()) {
                getCurrentState().end();
                executeNext();
            }
            if (debug) {
                Telemetry telemetry = this.opMode.telemetry;
                if(getCurrentState() != null)
                    telemetry.addData("Current State", getCurrentState().toString());
                telemetry.addData("Execution time left (s)", (this.stateAbortTime - System.currentTimeMillis()) / 1000);
            }
        } else {
            if(startTime == -1){
                Log.e("INFO", "STARTED MACHINE!");
                start();
            }
        }
    }

    /**
     * Starts the state machine
     * <p/>
     * The state machine is normally automatically started from the constructor. This method can be
     * used to start the state machine earlier than expected.
     */
    public void start() {
        isRunning = true;
        stateAbortTime = System.currentTimeMillis() + MAX_STATE_RUN_TIME;
        executeNext();
    }

    /**
     * Sets the current state of the state machine
     * <p/>
     * After execution of this method, the state machine will abort the current state that it is
     * running/waiting for and will execute the provided state immediatly.
     *
     * @param state The state to execute
     */
    public void setState(STATE state) {
        this.setNextState(state);
        this.executeNext();
    }

    /**
     * Sets the next state of the state machine
     * <p/>
     * After execution, the state machine will "queue" up the provided state and execute that after
     * the currently running state has exited.
     * <br>
     * <b>Note:</b> The state machine will continue normal linear operation after the provided state
     * is run
     *
     * @param state The state to execute next
     */
    public void setNextState(STATE state) {
        Log.i("Setting next state", state.toString());
        this.currentState = (getStateIndex(state) == -1) ? -1 : getStateIndex(state) - 1;
    }

    /**
     * Increases the maximum runtime of a state in the state machine.
     * <p/>
     * By default, a state in the state machine has a maximum execution time of about 30 seconds.
     * After this point, the state is aborted and the state machine shut down. If a state is to run
     * longer than about 30 seconds, this method will add the provided number of miliseconds to the
     * maximum execution time
     *
     * @param millis The time in milliseconds to add to the maximum execution time.
     */
    public void increaseMaxRunTime(int millis) {
        this.MAX_STATE_RUN_TIME += millis;
    }

    /**
     * Gets the state index of the provided state
     *
     * @param state The state
     * @return The index in the {@link #states} that the given state is in.
     */
    private int getStateIndex(STATE state) {
        for (int i = 0; i < states.size(); i++) {
            STATE curr = states.get(i);
            if (curr.equals(state)) {
                this.currentState = i;
                getCurrentState().runState();
            }
        }
        return -1;
    }

    /**
     * Checks if the currently executing state has reached its maximum execution time
     *
     * @return True if the current state has reached its maximum execution time
     */
    public boolean stateHung() {
        return System.currentTimeMillis() > stateAbortTime;
    }

    /**
     * Enables debug telemetry to be printed (Such as the current state and time left) to the
     * telemetry data
     */
    public void enableDebug() {
        this.debug = true;
    }

    /**
     * Injects a given field into the given state
     * <p/>
     * If the field does not exist or is not of the provided type, this method will fail silently.
     *
     * @param state     The state to inject the object into
     * @param fieldName The name of the field in thhe state
     * @param type      The type of field
     * @param value     The value to set the field to
     */
    private void inject(STATE state, String fieldName, Class type, Object value) {
        try {
            Field field = state.getClass().getDeclaredField(fieldName);
            if (!field.getType().equals(type))
                return;
            boolean wasAccessible;
            if (!(wasAccessible = field.isAccessible()))
                field.setAccessible(true);
            if (value != null)
                field.set(state, value);
            if (!wasAccessible)
                field.setAccessible(false);
        } catch (NoSuchFieldException ignored) {

        } catch (IllegalAccessException ignored) {

        }
    }

    private void resetHangTimer(){
        this.stateAbortTime = System.currentTimeMillis() + MAX_STATE_RUN_TIME;
    }

    public interface State {
        boolean shouldChangeState();

        void runState();

        void end();

        void tick();
    }
}
