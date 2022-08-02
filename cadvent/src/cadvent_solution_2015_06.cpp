#define LIGHT_GRID_SIDE 1000

enum command_type
{
    CommandType_Toggle = 0,
    CommandType_TurnOn,
    CommandType_TurnOff,
};

struct command
{
    command_type Type;
    v2i P0;
    v2i P1;
};

command StringGetCommand(string String)
{
    command Result = {};
    
    u32 FirstCoordinateIndex = StringFirstIndexOfNumber(String, 0);
    if (FirstCoordinateIndex == 7) 
    {
        Result.Type = CommandType_Toggle;
    }
    else if (FirstCoordinateIndex == 8)
    {
        Result.Type = CommandType_TurnOn;
    }
    else if (FirstCoordinateIndex == 9)
    {
        Result.Type = CommandType_TurnOff;
    }
    
    u32 LastIndex = StringFirstIndexOf(String, FirstCoordinateIndex, ',');
    Result.P0.X = StringToI32(String, FirstCoordinateIndex, LastIndex);
    u32 LastStartIndex = LastIndex + 1;
    LastIndex = StringFirstIndexOf(String, LastStartIndex, ' ');
    Result.P0.Y = StringToI32(String, LastStartIndex, LastIndex);
    
    LastStartIndex = StringFirstIndexOfNumber(String, LastIndex);
    LastIndex = StringFirstIndexOf(String, LastIndex, ',');
    Result.P1.X = StringToI32(String, LastStartIndex, LastIndex);
    LastStartIndex = LastIndex + 1;
    Result.P1.Y = StringToI32(String, LastStartIndex, String.Size);
    
    return Result;
}

SOLVER(2015, 06)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    
    i32* LightGrid = ArenaPushArray(i32, Arena, LIGHT_GRID_SIDE * LIGHT_GRID_SIDE);
    i32* DimmableLightGrid = ArenaPushArray(i32, Arena, LIGHT_GRID_SIDE * LIGHT_GRID_SIDE);
    for (u32 Row = 0; Row < LIGHT_GRID_SIDE; ++Row)
    {
        for (u32 Col = 0; Col < LIGHT_GRID_SIDE; ++Col)
        {
            u32 LightIndex = Row * LIGHT_GRID_SIDE + Col;
            LightGrid[LightIndex] = 0;
            DimmableLightGrid[LightIndex] = 0;
        }
    }
    
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        command Command = StringGetCommand(Line);
        
        for (i32 Row = Command.P0.Y; Row <= Command.P1.Y; ++Row)
        {
            for (i32 Col = Command.P0.X; Col <= Command.P1.X; ++Col)
            {
                u32 LightIndex = Row * LIGHT_GRID_SIDE + Col;
                if (Command.Type == CommandType_Toggle)
                {
                    LightGrid[LightIndex] = 1 - LightGrid[LightIndex];
                    DimmableLightGrid[LightIndex] += 2;
                }
                else if (Command.Type == CommandType_TurnOn)
                {
                    LightGrid[LightIndex] = 1;
                    DimmableLightGrid[LightIndex] += 1;
                }
                else if (Command.Type == CommandType_TurnOff)
                {
                    LightGrid[LightIndex] = 0;
                    DimmableLightGrid[LightIndex] = MAX(DimmableLightGrid[LightIndex] - 1, 0);
                }
            }
        }
    }
    
    i32 LitCount = 0;
    i32 DimmableLitCount = 0;
    for (u32 Row = 0; Row < LIGHT_GRID_SIDE; ++Row)
    {
        for (u32 Col = 0; Col < LIGHT_GRID_SIDE; ++Col)
        {
            u32 LightIndex = Row * LIGHT_GRID_SIDE + Col;
            LitCount += LightGrid[LightIndex];
            DimmableLitCount += DimmableLightGrid[LightIndex];
        }
    }
    
    solution Solution = { LitCount, DimmableLitCount };
    return Solution;
}
