#include "cadvent_solution.h"
#include "cadvent_solution_2015_01.cpp"
#include "cadvent_solution_2015_02.cpp"
#include "cadvent_solution_2015_03.cpp"
#include "cadvent_solution_2015_04.cpp"
#include "cadvent_solution_2015_05.cpp"
#include "cadvent_solution_2015_06.cpp"
#include "cadvent_solution_2015_07.cpp"
#include "cadvent_solution_2015_08.cpp"
#include "cadvent_solution_2015_09.cpp"

solver* Solvers[] =
{
    SolveYear2015Day01, SolveYear2015Day02, SolveYear2015Day03, SolveYear2015Day04, SolveYear2015Day05,
    SolveYear2015Day06, SolveYear2015Day07, SolveYear2015Day08, SolveYear2015Day09,
};

buffer ReadInputFile(cadvent_state* State, u32 Year, u32 Day)
{
    string InputFilePath = ArenaPushString(&State->Arena, "..\\data\\yearYYYY\\dayDD\\input.txt ");
    StringFromI32(InputFilePath, 12, Year);
    StringFromI32(InputFilePath, 20, Day, 2, true);
    InputFilePath.Data[InputFilePath.Size - 1] = 0;
    
    buffer Result = State->Platform.OpenAndReadFile((char*)InputFilePath.Data);
    return Result;
}

solver* GetSolver(cadvent_state* State, u32 Year, u32 Day)
{
    Assert(SOLUTION_YEAR_MIN <= Year && Year <= 2022);
    Assert(1 <= Day && Day <= 25);
    
    u32 SolverIndex = (SOLUTION_YEAR_MIN - Year) * SOLUTION_DAY_MAX + (Day - 1);
    solver* Result = Solvers[SolverIndex];
    return Result;
}

void WriteOutputFile(cadvent_state* State, u32 Year, u32 Day, solution Solution)
{
    string OutputFilePath = ArenaPushString(&State->Arena, "..\\data\\yearYYYY\\dayDD\\output.txt ");
    StringFromI32(OutputFilePath, 12, Year);
    StringFromI32(OutputFilePath, 20, Day, 2, true);
    OutputFilePath.Data[OutputFilePath.Size - 1] = 0;
    
    string SolutionString = ArenaPushString(&State->Arena, 50);
    u32 SolutionIndex = StringFromI32(SolutionString, 0, Solution.FirstPart);
    SolutionString.Data[SolutionIndex++] = '\n';
    SolutionIndex = StringFromI32(SolutionString, SolutionIndex, Solution.SecondPart);
    
    State->Platform.CreateAndWriteFile((char*)OutputFilePath.Data, SolutionString, SolutionIndex);
}

void Solve(cadvent_state* State, u32 Year, u32 Day)
{
    buffer InputFileBuffer = ReadInputFile(State, Year, Day);
    solver* Solver = GetSolver(State, Year, Day);
    solution Solution = Solver(&State->Arena, InputFileBuffer);
    WriteOutputFile(State, Year, Day, Solution);
}