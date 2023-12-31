package main.csp;

import java.time.LocalDate;
import java.util.*;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 * 
 * [!] Note: this class provides an implementation that do not guarantee
 *     completeness but are suited for large CSPs that traditional
 *     backtracking approaches would take too long to solve.
 */
public class CSPLocalSolver extends CSPSolver {
    
    // Local Search Constants
    // --------------------------------------------------------------------------------------------------------------
    
    public static final int MAX_STEPS = 250,
                            MAX_RESTARTS = 50;
    

    // Backtracking CSP Solver
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * 
     * [!] Is a local search implementation that is not guaranteed to be complete.
     * 
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
        throw new UnsupportedOperationException();
    }
    
}
