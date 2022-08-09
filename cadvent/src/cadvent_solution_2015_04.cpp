SOLVER(2015, 04)
{
    string TestString = ArenaPushString(Arena, 50);
    StringCopy(TestString, InputBuffer);
    
    i32 Index = 0;
    i32 FiveZeroNumber = 0;
    i32 SixZeroNumber = 0;
    b32 NotFound = true;
    while (NotFound)
    {
        u32 LastIndex = StringFromI32(TestString, InputBuffer.Size, Index);
        u128 Hash = MD5(TestString.Data, LastIndex);
        
        b32 FiveZeroHash = Hash.Byte[0] == 0x0 && Hash.Byte[1] == 0x0 && Hash.Byte[2] < 0x10;
        if (FiveZeroNumber == 0 && FiveZeroHash)
        {
            FiveZeroNumber = Index;
        }
        b32 SixZeroHash = Hash.Byte[0] == 0x0 && Hash.Byte[1] == 0x0 && Hash.Byte[2] == 0x0;
        if (SixZeroNumber == 0 && SixZeroHash)
        {
            SixZeroNumber = Index;
        }
        if (FiveZeroHash && SixZeroHash)
        {
            NotFound = false;
        }
        ++Index;
    }
    
    solution Result = Solution(Arena, FiveZeroNumber, SixZeroNumber);
    return Result;
}
