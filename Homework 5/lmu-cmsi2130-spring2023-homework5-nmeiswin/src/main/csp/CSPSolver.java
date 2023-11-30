package main.csp;

import java.time.LocalDate;
import java.util.*;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSPSolver {

    // Backtracking CSP Solver
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) 
    {
        List<MeetingDomain> domains = new ArrayList<>();
        for (int i = 0; i < nMeetings; i++) 
        {
            domains.add(new MeetingDomain(rangeStart, rangeEnd));
        }
        nodeConsistency(domains, constraints);
        arcConsistency(domains, constraints);
        List<LocalDate> assignments = new ArrayList<>(Collections.nCopies(nMeetings, null));
        if (recursiveBackTracking(assignments, domains,constraints,0))
        {
            return assignments;
        }
        else
        {
            return null;
        }
    }

    //recursive backtracking helper function
    private static boolean recursiveBackTracking(List<LocalDate> assignments, List<MeetingDomain> domains, Set<DateConstraint> constraints,int assignmentIndex) 
    {
        if (assignments.size() == assignmentIndex) 
        {
            return true;
        }
        MeetingDomain domain = domains.get(assignmentIndex);
        for (LocalDate date : domain.domainValues) {
            assignments.set(assignmentIndex, date);
            if (isConsistant(assignments,constraints,assignmentIndex)) {
                if (recursiveBackTracking(assignments, domains,constraints, assignmentIndex + 1)) {
                    return true;
                }
            }
        }
        assignments.set(assignmentIndex, null);
        return false;
    }

    //checks if an assignment is consistant
    private static boolean isConsistant(List<LocalDate> assignments, Set<DateConstraint> constraints, int assignmentIndex) {
        LocalDate assignedDate = assignments.get(assignmentIndex);
        for(DateConstraint constraint : constraints){
            if(constraint.ARITY == 1 && constraint.L_VAL == assignmentIndex){
                UnaryDateConstraint unaryDateConstraint = (UnaryDateConstraint) constraint;
                if(!unaryDateConstraint.isSatisfiedBy(assignedDate, unaryDateConstraint.R_VAL)){
                    return false;
                }
            }
            else if (constraint.ARITY == 2) {
                BinaryDateConstraint binaryDateConstraint = (BinaryDateConstraint) constraint;
                if (binaryDateConstraint.L_VAL == assignmentIndex || binaryDateConstraint.R_VAL == assignmentIndex) {
                    LocalDate constraintDate = assignments.get(binaryDateConstraint.L_VAL == assignmentIndex ? binaryDateConstraint.R_VAL : binaryDateConstraint.L_VAL);
                    if(constraintDate != null && !binaryDateConstraint.isSatisfiedBy(assignments.get(binaryDateConstraint.L_VAL), assignments.get(binaryDateConstraint.R_VAL))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    
    
    // Filtering Operations
    // --------------------------------------------------------------------------------------------------------------
    
    /**
     * Enforces node consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *unary* constraints! 
     */
    public static void nodeConsistency (List<MeetingDomain> domains, Set<DateConstraint> constraints) 
    {
        for (DateConstraint constraint : constraints) 
        {
            if (constraint.ARITY == 1) 
            {
                UnaryDateConstraint unaryDateConstraint = (UnaryDateConstraint) constraint;
                MeetingDomain meetingDomain = domains.get(unaryDateConstraint.L_VAL);
                Set<LocalDate> filterdDomain = new HashSet<>();
                for (LocalDate date : meetingDomain.domainValues) 
                {
                    if (unaryDateConstraint.isSatisfiedBy(date, unaryDateConstraint.R_VAL)) 
                    {
                        filterdDomain.add(date);
                    }
                }
                meetingDomain.domainValues = filterdDomain;
            }
        }
    }
    
    /**
     * Enforces arc consistency for all variables' domains given in varDomains based on
     * the given constraints. Meetings' domains correspond to their index in the varDomains List.
     * @param varDomains List of MeetingDomains in which index i corresponds to D_i
     * @param constraints Set of DateConstraints specifying how the domains should be constrained.
     * [!] Note, these may be either unary or binary constraints, but this method should only process
     *     the *binary* constraints using the AC-3 algorithm! 
     */
    public static void arcConsistency (List<MeetingDomain> domains, Set<DateConstraint> constraints) 
    {
        Set<Arc> arcs = new HashSet<>();
        for (DateConstraint constraint : constraints) 
        {
            if (constraint.ARITY == 2 ) 
            {
                BinaryDateConstraint binaryDateConstraint = (BinaryDateConstraint) constraint;
                arcs.add(new Arc(binaryDateConstraint.L_VAL, binaryDateConstraint.R_VAL, binaryDateConstraint));
                arcs.add(new Arc(binaryDateConstraint.R_VAL, binaryDateConstraint.L_VAL, binaryDateConstraint.getReverse()));
            }
        }
        boolean changeOccured = false;

        changeOccured = false;
        for (Arc arc : arcs) 
        {
            if (evaluateArcConsistency(domains.get(arc.TAIL), domains.get(arc.HEAD), arc.CONSTRAINT)) 
            {
                    changeOccured = true;
            }
        }
        while (changeOccured)
        {
            changeOccured = false;
            for (Arc arc : arcs) 
            {
                if (evaluateArcConsistency(domains.get(arc.TAIL), domains.get(arc.HEAD), arc.CONSTRAINT)) 
                {
                    changeOccured = true;
                }
            }
        }
    }

    //checks if an arc is consistant
    private static boolean evaluateArcConsistency(MeetingDomain tailDomain,MeetingDomain headDomain,DateConstraint constraint)
    {
        boolean changed = false;
        Iterator<LocalDate> iterator = tailDomain.domainValues.iterator();
        while (iterator.hasNext()) 
        {
            LocalDate tailDate = iterator.next();
            boolean valid = false;
            for (LocalDate headDate : headDomain.domainValues) 
            {
                if (constraint.isSatisfiedBy(tailDate, headDate)) 
                {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }
    
    /**
     * Private helper class organizing Arcs as defined by the AC-3 algorithm, useful for implementing the
     * arcConsistency method.
     * [!] You may modify this class however you'd like, its basis is just a suggestion that will indeed work.
     */
    private static class Arc {
        
        public final DateConstraint CONSTRAINT;
        public final int TAIL, HEAD;
        
        /**
         * Constructs a new Arc (tail -> head) where head and tail are the meeting indexes
         * corresponding with Meeting variables and their associated domains.
         * @param tail Meeting index of the tail
         * @param head Meeting index of the head
         * @param c Constraint represented by this Arc.
         * [!] WARNING: A DateConstraint's isSatisfiedBy method is parameterized as:
         * isSatisfiedBy (LocalDate leftDate, LocalDate rightDate), meaning L_VAL for the first
         * parameter and R_VAL for the second. Be careful with this when creating Arcs that reverse
         * direction. You may find the BinaryDateConstraint's getReverse method useful here.
         */
        public Arc (int tail, int head, DateConstraint c) {
            this.TAIL = tail;
            this.HEAD = head;
            this.CONSTRAINT = c;
        }
        
        @Override
        public boolean equals (Object other) {
            if (this == other) { return true; }
            if (this.getClass() != other.getClass()) { return false; }
            Arc otherArc = (Arc) other;
            return this.TAIL == otherArc.TAIL && this.HEAD == otherArc.HEAD && this.CONSTRAINT.equals(otherArc.CONSTRAINT);
        }
        
        @Override
        public int hashCode () {
            return Objects.hash(this.TAIL, this.HEAD, this.CONSTRAINT);
        }
        
        @Override
        public String toString () {
            return "(" + this.TAIL + " -> " + this.HEAD + ")";
        }
        
    }
    
}
