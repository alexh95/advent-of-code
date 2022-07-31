#ifndef CADVENT_SOLUTION_H
#define CADVENT_SOLUTION_H

#define SOLUTION_YEAR_MIN 2015
#define SOLUTION_DAY_MAX 25

struct solution
{
    i32 FirstPart;
    i32 SecondPart;
};

#define SOLVER_NAME(YEAR, DAY) SolveYear ## YEAR ## Day ## DAY

#define SOLVER(Year, Day) solution SOLVER_NAME(Year, Day)(buffer InputBuffer)

#define SOLVER_(Name) solution Name(buffer InputBuffer)
typedef SOLVER_(solver);

#endif
