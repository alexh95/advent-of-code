#define LIGHTS_STEPS 100

u32 CountGrid(u32* Grid, i32 Rows, i32 Cols, i32 FromRow, i32 ToRow, i32 FromCol, i32 ToCol)
{
    u32 Count = 0;
    for (i32 Row = MAX(FromRow, 0); Row <= MIN(ToRow, Rows - 1); ++Row)
    {
        for (i32 Col = MAX(FromCol, 0); Col <= MIN(ToCol, Cols - 1); ++Col)
        {
            u32 GridIndex = Row * Cols + Col;
            u32 Value = Grid[GridIndex];
            if (Value & 1)
            {
                ++Count;
            }
        }
    }
    return Count;
}

u32 CountGrid(u32* Grid, i32 Rows, i32 Cols)
{
    u32 Result = CountGrid(Grid, Rows, Cols, 0, Rows -1, 0, Cols - 1);
    return Result;
}


void ConwayStep(u32* Grid, i32 Rows, i32 Cols)
{
    for (i32 Row = 0; Row < Rows; ++Row)
    {
        for (i32 Col = 0; Col < Cols; ++Col)
        {
            u32 GridIndex = Row * Cols + Col;
            u32 Count = CountGrid(Grid, Rows, Cols, Row - 1, Row + 1, Col - 1, Col + 1) - (Grid[GridIndex] & 1);
            if (Grid[GridIndex] & 1)
            {
                if (!(Count == 2 || Count == 3))
                {
                    Grid[GridIndex] |= 2;
                }
            }
            else
            {
                if (Count == 3)
                {
                    Grid[GridIndex] |= 2;
                }
            }
        }
    }
    
    for (i32 Row = 0; Row < Rows; ++Row)
    {
        for (i32 Col = 0; Col < Cols; ++Col)
        {
            u32 GridIndex = Row * Cols + Col;
            if (Grid[GridIndex] & 2)
            {
                Grid[GridIndex] = 1 - (Grid[GridIndex] & 1);
            }
        }
    }
}

void InitGrid(u32* Grid, string_list* Lines)
{
    for (u32 LineIndex = 0; LineIndex < Lines->Count; ++LineIndex)
    {
        string Line = Lines->Strings[LineIndex];
        for (u32 Index = 0; Index < Line.Size; ++Index)
        {
            u32 GridIndex = LineIndex * Line.Size + Index;
            u8 C = Line.Data[Index];
            if (C == '.')
            {
                Grid[GridIndex] = 0;
            }
            else if (C == '#')
            {
                Grid[GridIndex] = 1;
            }
            else
            {
                InvalidCodePath;
            }
        }
    }
}

SOLVER(2015, 18)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    u32 Cols = Lines.Strings[0].Size;
    u32 Rows = Lines.Count;
    u32* Grid = ArenaPushArray(u32, Arena, Cols * Rows);
    
    InitGrid(Grid, &Lines);
    for (u32 StepIndex = 0; StepIndex < LIGHTS_STEPS; ++StepIndex)
    {
        ConwayStep(Grid, Rows, Cols);
    }
    u32 Count = CountGrid(Grid, Rows, Cols);
    
    InitGrid(Grid, &Lines);
    for (u32 StepIndex = 0; StepIndex < LIGHTS_STEPS; ++StepIndex)
    {
        ConwayStep(Grid, Rows, Cols);
        Grid[(       0) * Cols + (       0)] = 1;
        Grid[(       0) * Cols + (Cols - 1)] = 1;
        Grid[(Rows - 1) * Cols + (       0)] = 1;
        Grid[(Rows - 1) * Cols + (Cols - 1)] = 1;
    }
    u32 CornerCount = CountGrid(Grid, Rows, Cols);
    
    solution Result = Solution(Arena, Count, CornerCount);
    return Result;
}
