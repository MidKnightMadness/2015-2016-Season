package com.qualcomm.ftcrobotcontroller.common;

public class Values {

    public static final int PLOW_DEPLOY = 3000;
    public static final int PLOW_RETRACT = 0;

    public static final int HANGARM_DEPLOY = -16000;
    public static final int HAMGARM_HANG = -3900;


    //Nathan B adjusted CLIMBER_OPEN to 1 for better climber release. Original Value: 0.29803923
    public static final double CLIMBER_OPEN = 1;
    public static final double CLIMBER_CLOSE = 0;

    public static final double TRIGGER_LEFT_DEPLOY = 0.105882354;
    public static final double TRIGGER_LEFT_RETRACT = 1;

    public static final double TRIGGER_RIGHT_DEPLOY = 1;
    public static final double TRIGGER_RIGHT_RETRACT = 0.23137255;
}
