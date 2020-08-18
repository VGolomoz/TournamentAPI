package me.sports.task.utils;

/**
 * Enum class to set match status. Active - match is active, Finished - match is finished,
 * TBD - match waiting for winners of previous matches
 *
 * @author Vadym
 */
public enum MatchStatus {

    ACTIVE, FINISHED, TBD;

    MatchStatus() {
    }
}
