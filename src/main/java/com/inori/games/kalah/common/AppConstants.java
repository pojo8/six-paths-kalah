package com.inori.games.kalah.common;

import java.util.ArrayList;
import java.util.Arrays;

public class AppConstants {

    public static final String COLON = ":";
    public static final String SLASH = "/";
    public static final String GAMES = "games";

    public static final String PLAYER1 = "Player 1";
    public static final String PLAYER2 = "Player 2";

    public final static ArrayList<String> PLAYER_1_PITS =  new ArrayList( new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7")));
    // Fliped P2 array as kalah is to the right of each player
    public final static ArrayList<String> PLAYER_2_PITS =  new ArrayList( new ArrayList<>(Arrays.asList("14", "13", "12", "11", "10", "9", "8")));

    public final static ArrayList<String> PLAYER_1_NO_KALAH =  new ArrayList( new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6")));
    // Fliped P2 array as kalah is to the right of each player
    public final static ArrayList<String> PLAYER_2_NO_KALAH =  new ArrayList( new ArrayList<>(Arrays.asList("13", "12", "11", "10", "9", "8")));

}
