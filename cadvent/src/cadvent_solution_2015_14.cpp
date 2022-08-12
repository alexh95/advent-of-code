struct reindeer
{
    u32 Speed;
    u32 MovingDuration;
    u32 RestingDuration;
    
    u32 Distance;
    u32 TimeSinceLastState;
    b32 IsMoving;
    u32 Points;
};

reindeer* ParseLinesReindeer(memory_arena* Arena, string_list* Lines)
{
    reindeer* Result = ArenaPushArray(reindeer, Arena, Lines->Count);
    for (u32 LineIndex = 0; LineIndex < Lines->Count; ++LineIndex)
    {
        string Line = Lines->Strings[LineIndex];
        reindeer Reindeer = {};
        
        u32 LastIndex = StringFirstIndexOfNumber(Line, 0);
        u32 Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        Reindeer.Speed = StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        Reindeer.MovingDuration = StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        Reindeer.RestingDuration = StringToI32(Line, LastIndex, Index);
        
        Reindeer.IsMoving = true;
        
        Result[LineIndex] = Reindeer;
    }
    return Result;
}

#define MAX_RACE_TIME 2503

SOLVER(2015, 14)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    reindeer* Reindeers = ParseLinesReindeer(Arena, &Lines);
    
    for (u32 Second = 0; Second < MAX_RACE_TIME; ++Second)
    {
        for (u32 Index = 0; Index < Lines.Count; ++Index)
        {
            reindeer* Reindeer = Reindeers + Index;
            
            ++Reindeer->TimeSinceLastState;
            if (Reindeer->IsMoving)
            {
                Reindeer->Distance += Reindeer->Speed;
                if (Reindeer->TimeSinceLastState >= Reindeer->MovingDuration)
                {
                    Reindeer->IsMoving = false;
                    Reindeer->TimeSinceLastState = 0;
                }
            }
            else
            {
                if (Reindeer->TimeSinceLastState >= Reindeer->RestingDuration)
                {
                    Reindeer->IsMoving = true;
                    Reindeer->TimeSinceLastState = 0;
                }
            }
        }
        
        reindeer* LeadingReindeer = Reindeers;
        u32 MaxDistance = LeadingReindeer->Distance;
        for (u32 Index = 1; Index < Lines.Count; ++Index)
        {
            reindeer* Reindeer = Reindeers + Index;
            if (MaxDistance < Reindeer->Distance)
            {
                MaxDistance = Reindeer->Distance;
                LeadingReindeer = Reindeer;
            }
        }
        ++LeadingReindeer->Points;
    }
    
    u32 MaxDistance = 0;
    u32 MaxPoints = 0;
    for (u32 Index = 0; Index < Lines.Count; ++Index)
    {
        reindeer* Reindeer = Reindeers + Index;
        if (MaxDistance < Reindeer->Distance)
        {
            MaxDistance = Reindeer->Distance;
        }
        if (MaxPoints < Reindeer->Points)
        {
            MaxPoints = Reindeer->Points;
        }
    }
    
    solution Result = Solution(Arena, MaxDistance, MaxPoints);
    return Result;
}
