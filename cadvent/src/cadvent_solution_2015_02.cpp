SOLVER(2015, 02)
{
    char Line[10] = {};
    u32 LineIndex = 0;
    string DimensionString = {};
    DimensionString.Data = (u8*)Line;
    u32 DimensionIndex = 0;
    v3i Dimensions = {};
    i32 FullAreaSum = 0;
    i32 FullPerimeterAndVolumeSum = 0;
    for (u32 InputIndex = 0; InputIndex < InputBuffer.Size; ++InputIndex)
    {
        char Character = InputBuffer.Data[InputIndex];
        b32 EndOfLine = Character == '\n';
        b32 X = Character == 'x';
        if (EndOfLine || X)
        {
            DimensionString.Size = LineIndex;
            Dimensions.E[DimensionIndex] = StringToI32(DimensionString);
            LineIndex = 0;
            ++DimensionIndex;
        }
        if (EndOfLine)
        {
            LineIndex = 0;
            DimensionIndex = 0;
            
            u32 SideAreaXY = Dimensions.X * Dimensions.Y;
            u32 SideAreaYZ = Dimensions.Y * Dimensions.Z;
            u32 SideAreaXZ = Dimensions.X * Dimensions.Z;
            u32 MinSideArea = MIN(SideAreaXY, MIN(SideAreaXZ, SideAreaYZ));
            
            u32 SideHalfPerimeterXY = Dimensions.X + Dimensions.Y;
            u32 SideHalfPerimeterYZ = Dimensions.Y + Dimensions.Z;
            u32 SideHalfPerimeterXZ = Dimensions.X + Dimensions.Z;
            u32 MinSideHalfPerimeter = MIN(SideHalfPerimeterXY, MIN(SideHalfPerimeterXZ, SideHalfPerimeterYZ));
            
            u32 Area = 2 * SideAreaXY + 2 * SideAreaYZ + 2 * SideAreaXZ + MinSideArea;
            FullAreaSum += Area;
            u32 Volume = Dimensions.X * Dimensions.Y * Dimensions.Z;
            FullPerimeterAndVolumeSum += 2 * MinSideHalfPerimeter + Volume;
        }
        if (!EndOfLine && !X)
        {
            Line[LineIndex++] = Character;
        }
    }
    
    solution Solution = { FullAreaSum, FullPerimeterAndVolumeSum };
    return Solution;
}
