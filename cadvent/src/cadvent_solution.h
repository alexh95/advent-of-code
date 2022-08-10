#ifndef CADVENT_SOLUTION_H
#define CADVENT_SOLUTION_H

#define SOLUTION_YEAR_MIN 2015
#define SOLUTION_DAY_MAX 25

enum solution_type
{
    SolutionType_I32 = 0,
    SolutionType_String2,
};

struct solution
{
    solution_type Type;
    void* Value;
};

struct solution_i32
{
    i32 FirstPart;
    i32 SecondPart;
};

struct solution_string2
{
    string FirstPart;
    string SecondPart;
};

#define SOLVER_NAME(YEAR, DAY) SolveYear ## YEAR ## Day ## DAY

#define SOLVER(Year, Day) static solution SOLVER_NAME(Year, Day)(memory_arena* Arena, buffer InputBuffer)

#define SOLVER_(Name) solution Name(memory_arena* Arena, buffer InputBuffer)
typedef SOLVER_(solver);

static solution Solution(memory_arena* Arena, i32 FirstPart, i32 SecondPart)
{
    solution Solution;
    Solution.Type = SolutionType_I32;
    solution_i32* Value =  ArenaPushStruct(solution_i32, Arena);
    Value->FirstPart = FirstPart;
    Value->SecondPart = SecondPart;
    Solution.Value = Value;
    return Solution;
}

static solution Solution(memory_arena* Arena, string FirstPart, string SecondPart)
{
    solution Solution;
    Solution.Type = SolutionType_String2;
    solution_string2* Value =  ArenaPushStruct(solution_string2, Arena);
    Value->FirstPart = FirstPart;
    Value->SecondPart = SecondPart;
    Solution.Value = Value;
    return Solution;
}

#endif
