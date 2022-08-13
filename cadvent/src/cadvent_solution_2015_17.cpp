#define MAX_CAPACITY 150

void CountPartitions(u32* Containers, b32* Used, u32 Count, u32 Start, u32 CurrentSum, u32 UsedCount, u32* Result)
{
    for (u32 Index = Start; Index < Count; ++Index)
    {
        if (!Used[Index])
        {
            u32 Container = Containers[Index];
            u32 NewSum = CurrentSum + Container;
            if (NewSum < MAX_CAPACITY)
            {
                Used[Index] = true;
                CountPartitions(Containers, Used, Count, Index + 1, NewSum, UsedCount + 1, Result);
                Used[Index] = false;
            }
            else if (NewSum == MAX_CAPACITY)
            {
                ++Result[UsedCount];
            }
        }
    }
}

SOLVER(2015, 17)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    u32* Containers = ArenaPushArray(u32, Arena, Lines.Count);
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        i32 ContainerValue = StringToI32(Line, 0, Line.Size);
        Containers[LineIndex] = ContainerValue;
    }
    b32* Used = ArenaPushArray(b32, Arena, Lines.Count);
    
    u32* CombinationsCounts = ArenaPushArray(u32, Arena, Lines.Count);
    CountPartitions(Containers, Used, Lines.Count, 0, 0, 0, CombinationsCounts);
    
    u32 MinCombinationsCount = 0;
    u32 CombinationCount = 0;
    for (u32 Index = 0; Index < Lines.Count; ++Index)
    {
        u32 Count = CombinationsCounts[Index];
        CombinationCount += Count;
        if (MinCombinationsCount == 0)
        {
            MinCombinationsCount = Count;
        }
    }
    
    solution Result = Solution(Arena, CombinationCount, MinCombinationsCount);
    return Result;
}
