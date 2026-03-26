package com.main.untitledantgame.utility;

public class UUID_Int {
    private static int inc_uuid = -1;

    private UUID_Int() { }

    public static int getUUIDInt()
    {
        return ++inc_uuid;
    }
}
