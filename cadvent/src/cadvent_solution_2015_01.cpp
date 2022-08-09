SOLVER(2015, 01)
{
    i32 Floor = 0;
    i32 BasementIndex = 0;
    for (u32 Index = 0; Index < InputBuffer.Size; ++Index)
    {
        u8 Character = InputBuffer.Data[Index];
        if (Character == '(')
        {
            ++Floor;
        }
        else if (Character == ')')
        {
            --Floor;
        }
        if (Floor == -1 && BasementIndex == 0)
        {
            BasementIndex = Index + 1;
        }
    }
    
    solution Result = Solution(Arena, Floor, BasementIndex);
    return Result;
}
