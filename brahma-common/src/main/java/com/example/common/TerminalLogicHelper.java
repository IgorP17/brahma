package com.example.common;

public class TerminalLogicHelper {

    public static boolean isLocationMoscow(String location) {
        if (location == null) return false;
        String lowerLoc = location.toLowerCase();
        return lowerLoc.contains("moscow") || lowerLoc.contains("москва");
    }

    public static TerminalStatus determineStatus(String location) {
        return isLocationMoscow(location) ? TerminalStatus.REGISTERED : TerminalStatus.REJECTED;
    }
}