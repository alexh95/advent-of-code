u32 StringParsedSize(string S)
{
    u32 Result = 0;
    
    b32 EscapeSequence = false;
    u32 RemainingHex = 0;
    for (u32 Index = 0; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        
        if (C == '\\')
        {
            if (EscapeSequence)
            {
                EscapeSequence = false;
                ++Result;
            }
            else
            {
                EscapeSequence = true;
            }
        }
        else
        {
            if (EscapeSequence)
            {
                if (C == '\"')
                {
                    EscapeSequence = false;
                    ++Result;
                }
                else if (C == 'x')
                {
                    RemainingHex = 2;
                }
                else
                {
                    --RemainingHex;
                    if (RemainingHex == 0)
                    {
                        EscapeSequence = false;
                        ++Result;
                    }
                }
            }
            else
            {
                if (C != '\"')
                {
                    ++Result;
                }
            }
        }
    }
    
    return Result;
}

u32 StringEncodedSize(string S)
{
    u32 Result = 2;
    
    for (u32 Index = 0; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        
        if (C == '\\' || C == '\"')
        {
            ++Result;
        }
        ++Result;
    }
    
    return Result;
}

SOLVER(2015, 08)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    
    u32 StringSizeTotal = 0;
    u32 ParsedSizeTotal = 0;
    u32 EncodedSizeTotal = 0;
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        StringSizeTotal += Line.Size;
        u32 ParsedSize = StringParsedSize(Line);
        ParsedSizeTotal += ParsedSize;
        u32 EncodedSize = StringEncodedSize(Line);
        EncodedSizeTotal += EncodedSize;
    }
    i32 ParsedDelta =  StringSizeTotal - ParsedSizeTotal;
    i32 EncodedDelta = EncodedSizeTotal - StringSizeTotal;
    
    solution Result = Solution(Arena, ParsedDelta, EncodedDelta);
    return Result;
}
