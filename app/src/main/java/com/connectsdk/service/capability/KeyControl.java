package com.connectsdk.service.capability;

import com.connectsdk.service.capability.listeners.ResponseListener;

public interface KeyControl extends CapabilityMethods {
    public static final String Any = "KeyControl.Any";
    public static final String Back = "KeyControl.Back";
    //  public static final String[] Capabilities = new String[]{Up, Down, Left, Right, OK, Back, Home, KeyCode};
    public static final String Down = "KeyControl.Down";
    public static final String Home = "KeyControl.Home";
    public static final String KeyCode = "KeyControl.KeyCode";
    public static final String Left = "KeyControl.Left";
    public static final String OK = "KeyControl.OK";
    public static final String Right = "KeyControl.Right";
    public static final String Send_Key = "KeyControl.SendKey";
    public static final String Up = "KeyControl.Up";

    public final static String[] Capabilities = {
            Up,
            Down,
            Left,
            Right,
            OK,
            Back,
            Home,
            KeyCode,
    };

    void back(ResponseListener<Object> responseListener);

    void down(ResponseListener<Object> responseListener);

    KeyControl getKeyControl();

    CapabilityPriorityLevel getKeyControlCapabilityLevel();

    void home(ResponseListener<Object> responseListener);

    void left(ResponseListener<Object> responseListener);

    void ok(ResponseListener<Object> responseListener);

    void right(ResponseListener<Object> responseListener);

    void sendKeyCode(KeyCode keyCode, ResponseListener<Object> responseListener);

    void up(ResponseListener<Object> responseListener);

    enum KeyCode {

        NUM_0(0),
        NUM_1(1),
        NUM_2(2),
        NUM_3(3),
        NUM_4(4),
        NUM_5(5),
        NUM_6(6),
        NUM_7(7),
        NUM_8(8),
        NUM_9(9),
        DASH(10),
        ENTER(11);

        private static final KeyCode[] codes;
        private final int code;

        static {
            KeyCode keyCode = null;
            KeyCode keyCode2 = null;
            KeyCode keyCode3 = null;
            KeyCode keyCode4 = null;
            KeyCode keyCode5 = null;
            KeyCode keyCode6 = null;
            KeyCode keyCode7 = null;
            KeyCode keyCode8 = null;
            KeyCode keyCode9 = null;
            KeyCode keyCode10 = null;
            KeyCode keyCode11 = null;
            KeyCode keyCode12 = null;
            codes = new KeyCode[]{keyCode, keyCode2, keyCode3, keyCode4, keyCode5, keyCode6, keyCode7, keyCode8, keyCode9, keyCode10, keyCode11, keyCode12};
        }

        private KeyCode(int i) {
            this.code = i;
        }

        public int getCode() {
            return this.code;
        }

        public static KeyCode createFromInteger(int i) {


            if (i < 0) {
                return null;
            }
            KeyCode[] keyCodeArr = codes;
            if (i < keyCodeArr.length) {
                return keyCodeArr[i];
            }
            return null;
        }
    }
}


//
//import com.connectsdk.service.capability.listeners.ResponseListener;
//
//public interface KeyControl extends CapabilityMethods {
//    public final static String Any = "KeyControl.Any";
//
//    public final static String Up = "KeyControl.Up";
//    public final static String Down = "KeyControl.Down";
//    public final static String Left = "KeyControl.Left";
//    public final static String Right = "KeyControl.Right";
//    public final static String OK = "KeyControl.OK";
//    public final static String Back = "KeyControl.Back";
//    public final static String Home = "KeyControl.Home";
//    public final static String Send_Key = "KeyControl.SendKey";
//    public final static String KeyCode = "KeyControl.KeyCode";
//
//    public enum KeyCode {
//        NUM_0 (0),
//        NUM_1 (1),
//        NUM_2 (2),
//        NUM_3 (3),
//        NUM_4 (4),
//        NUM_5 (5),
//        NUM_6 (6),
//        NUM_7 (7),
//        NUM_8 (8),
//        NUM_9 (9),
//
//        DASH (10),
//        ENTER (11);
//
//        private final int code;
//
//        public static final KeyCode[] codes = {
//            NUM_0, NUM_1, NUM_2, NUM_3, NUM_4, NUM_5, NUM_6, NUM_7, NUM_8, NUM_9, DASH, ENTER
//        };
//
//        KeyCode(int code) {
//            this.code = code;
//        }
//
//        public int getCode() {
//            return code;
//        }
//
//        public static KeyCode createFromInteger(int keyCode) {
//            if (keyCode >= 0 && keyCode < codes.length) {
//                return codes[keyCode];
//            }
//            return null;
//        }
//    }
//
//    public final static String[] Capabilities = {
//        Up,
//        Down,
//        Left,
//        Right,
//        OK,
//        Back,
//        Home,
//        KeyCode,
//    };
//
//    public KeyControl getKeyControl();
//    public CapabilityPriorityLevel getKeyControlCapabilityLevel();
//
//    public void up(ResponseListener<Object> listener);
//    public void down(ResponseListener<Object> listener);
//    public void left(ResponseListener<Object> listener);
//    public void right(ResponseListener<Object> listener);
//    public void ok(ResponseListener<Object> listener);
//    public void back(ResponseListener<Object> listener);
//    public void home(ResponseListener<Object> listener);
//    public void sendKeyCode(KeyCode keycode, ResponseListener<Object> listener);
//}
