#ifndef CADVENT_SOLUTION_H
#define CADVENT_SOLUTION_H

#define SOLUTION_YEAR_MIN 2015
#define SOLUTION_DAY_MAX 25

struct solution
{
    u32 FirstPart;
    u32 SecondPart;
};

#define SOLVER(Name) solution Name(buffer Buffer)
typedef SOLVER(solver);

#endif
