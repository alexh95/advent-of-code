SOLVER(2015, 10)
{
    string NewSequence = ArenaPushString(Arena, Megabytes(16));
    string OldSequence = ArenaPushString(Arena, Megabytes(16));
    
    StringCopy(OldSequence, InputBuffer);
    u32 OldSequenceSize = InputBuffer.Size;
    
    i32 LengthAt40 = 0;
    
    for (u32 Iteration = 0; Iteration < 50; ++Iteration)
    {
        u8 PrevC = OldSequence.Data[0];
        u32 SameCount = 0;
        u32 NewSequenceIndex = 0;
        for (u32 Index = 0; Index <= OldSequenceSize; ++Index)
        {
            u8 C = 0;
            if (Index < OldSequenceSize)
            {
                C = OldSequence.Data[Index];
            }
            
            if (C != PrevC)
            {
                NewSequenceIndex = StringFromI32(NewSequence, NewSequenceIndex, SameCount);
                NewSequence.Data[NewSequenceIndex++] = PrevC;
                SameCount = 1;
            }
            else
            {
                ++SameCount;
            }
            
            PrevC = C;
        }
        
        if (Iteration == 39)
        {
            LengthAt40 = NewSequenceIndex;
        }
        
        OldSequenceSize = NewSequenceIndex;
        string Temp = OldSequence;
        OldSequence = NewSequence;
        NewSequence = Temp;
    }
    
    solution Result = Solution(Arena, LengthAt40, (i32)OldSequenceSize);
    return Result;
}
