v2i UpdatePosition(v2i* VisitedPositions, i32* VisitedCount, v2i Position, v2i Delta)
{
    v2i NewPosition = Position + Delta;
    b32 PositionExists = false;
    for (i32 PositionIndex = 0; PositionIndex < *VisitedCount; ++PositionIndex)
    {
        v2i VisitedPosition = VisitedPositions[PositionIndex];
        if (NewPosition == VisitedPosition)
        {
            PositionExists = true;
            break;
        }
    }
    if (!PositionExists)
    {
        VisitedPositions[*VisitedCount] = NewPosition;
        *VisitedCount += 1;
    }
    return NewPosition;
}

SOLVER(2015, 03)
{
    v2i Position = {};
    v2i VisitedPositions[4096] = {};
    i32 VisitedCount = 0;
    VisitedPositions[VisitedCount++] = Position;
    
    v2i PositionSplit1 = {};
    v2i PositionSplit2 = {};
    v2i VisitedPositionsSplit[4096] = {};
    i32 VisitedCountSplit = 0;
    VisitedPositionsSplit[VisitedCountSplit++] = PositionSplit1;
    
    
    for (u32 Index = 0; Index < InputBuffer.Size; ++Index)
    {
        u8 Character = InputBuffer.Data[Index];
        v2i Delta = {};
        switch (Character)
        {
            case '<':
            {
                Delta.X = -1;
            } break;
            case '>':
            {
                Delta.X = 1;
            } break;
            case '^':
            {
                Delta.Y = -1;
            } break;
            case 'v':
            {
                Delta.Y = 1;
            } break;
        }
        Position = UpdatePosition(VisitedPositions, &VisitedCount, Position, Delta);
        if (Index % 2 == 0)
        {
            PositionSplit1 = UpdatePosition(VisitedPositionsSplit, &VisitedCountSplit, PositionSplit1, Delta);
        }
        else
        {
            PositionSplit2 = UpdatePosition(VisitedPositionsSplit, &VisitedCountSplit, PositionSplit2, Delta);
        }
    }
    
    solution Result = Solution(Arena, VisitedCount, VisitedCountSplit);
    return Result;
}
