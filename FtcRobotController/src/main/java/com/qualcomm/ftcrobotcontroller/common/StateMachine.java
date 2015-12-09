package com.qualcomm.ftcrobotcontroller.common;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.robocol.Telemetry;

import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.LinkedList;

public class StateMachine<STATE extends Enum & StateMachine.State, T extends OpMode> {

    private LinkedList<STATE> states;
    private T opMode;
    private int currentState = -1;
    private boolean isRunning = false;

    public StateMachine(Class<? extends STATE> states, T opMode){
        this.opMode = opMode;
        this.states = new LinkedList<STATE>(EnumSet.allOf(states));
    }

    public StateMachine(STATE states, T opMode){
        this(states.getDeclaringClass(), opMode);
    }

    public STATE getCurrentState(){
        return states.get(currentState);
    }

    public void executeNext(){
        int nextState = ++currentState;
        STATE next = states.get(nextState);
        Log.w("Switching to state", next.toString());
        inject(next, "hardwareMap", HardwareMap.class, opMode.hardwareMap);
        inject(next, "gamepad1", Gamepad.class, opMode.gamepad1);
        inject(next, "gamepad2", Gamepad.class, opMode.gamepad2);
        inject(next, "telemetry", Telemetry.class, opMode.telemetry);
        inject(next, "parent", opMode.getClass(), opMode);
        next.runState();
        if(next.shouldChangeState()){
            next.postState();
            executeNext();
        }
    }

    public void tick(){
        if(getCurrentState().shouldChangeState()) {
            getCurrentState().postState();
            executeNext();
        }
    }

    public void start(){
        isRunning = true;
        executeNext();
    }

    public void setState(STATE state){
        for(int i = 0; i < states.size(); i++){
            STATE curr = states.get(i);
            if(curr.equals(state)){
                this.currentState = i;
                getCurrentState().runState();
            }
        }
    }


    private void inject(STATE state, String fieldName, Class type, Object value){
        try{
            Field field = state.getClass().getDeclaredField(fieldName);
            if(!field.getType().equals(type))
                return;
            boolean wasAccessible;
            if(!(wasAccessible = field.isAccessible()))
                field.setAccessible(true);
            if(value != null)
                field.set(state, value);
            if(!wasAccessible)
                field.setAccessible(false);
        } catch (NoSuchFieldException ignored){

        } catch (IllegalAccessException ignored) {

        }
    }

    public interface State{
        boolean shouldChangeState();
        void runState();
        void postState();
    }
}
