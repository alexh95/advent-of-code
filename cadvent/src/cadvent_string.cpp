string String(u32 Size)
{
    string Result;
    Result.Size = Size;
    return Result;
}

string String(string S, u32 Offset, u32 Size)
{
    string Result;
    Result.Size = Size;
    Result.Data = S.Data + Offset;
    return Result;
}

u32 StringCopy(string Dst, string Src)
{
    Assert(Dst.Size >= Src.Size);
    for (u32 Index = 0; Index < Src.Size; ++Index)
    {
        Dst.Data[Index] = Src.Data[Index];
    }
    return Src.Size;
}

b32 StringCompare(u8* A, u32 SizeA, u8* B, u32 SizeB)
{
    if (SizeA != SizeB)
    {
        return false;
    }
    
    for (u32 Index = 0; Index < SizeA; ++Index)
    {
        if (A[Index] != B[Index])
        {
            return false;
        }
    }
    
    return true;
}

b32 StringCompare(u8* A, u32 SizeA, char* B)
{
    u32 SizeB = StringLength(B);
    b32 Result = StringCompare(A, SizeA, (u8*)B, SizeB);
    return Result;
}

b32 StringCompare(string A, string B)
{
    b32 Result = StringCompare(A.Data, A.Size, B.Data, B.Size);
    return Result;
}

#define MAX_STRING_LENGTH_I32 11
u32 StringFromI32(string String, u32 Offset, i32 Value, u32 MinLength, b32 LeadingZeros)
{
    u8 Characters[MAX_STRING_LENGTH_I32] = {};
    u32 Size = 0;
    b32 Negative = false;
    if (Value < 0)
    {
        Negative = true;
        Value = -Value;
    }
    if (Value == 0)
    {
        Characters[Size++] = '0';
    }
    while (Value > 0)
    {
        u8 Character = 48 + Value % 10;
        Characters[Size] = Character;
        ++Size;
        Value /= 10;
    }
    if (Negative)
    {
        --MinLength;
    }
    for (u32 Index = Size; Index < MinLength; ++Index)
    {
        u8 Filler = LeadingZeros ? '0' : ' ';
        Characters[Index] = Filler;
        ++Size;
    }
    if (Negative)
    {
        Characters[Size++] = '-';
    }
    u32 LastIndex = 0;
    for (u32 Index = 0; Index < Size; ++Index)
    {
        LastIndex = Offset + Index;
        String.Data[LastIndex] = Characters[Size - 1 - Index];
    }
    return LastIndex + 1;
}

u32 StringFromI32(string String, u32 Offset, i32 Value)
{
    return StringFromI32(String, Offset, Value, 0, false);
}


i32 StringFirstIndexOfNumber(string String, u32 Offset)
{
    for (u32 Index = Offset; Index < String.Size; ++Index)
    {
        u8 C = String.Data[Index];
        if (C >= '0' && C <= '9')
        {
            return Index;
        }
    }
    return -1;
}

i32 StringFirstIndexOf(string String, u32 Offset, u8 Delimiter)
{
    for (u32 Index = Offset; Index < String.Size; ++Index)
    {
        u8 C = String.Data[Index];
        if (C == Delimiter)
        {
            return Index;
        }
    }
    return -1;
}

i32 StringToI32(string String, i32 Offset, i32 Size)
{
    i32 Result = 0;
    i32 Exponent = 1;
    
    for (i32 StringIndex = Size - 1; StringIndex >= Offset; --StringIndex)
    {
        char Character = String.Data[StringIndex];
        i32 ShiftedCharacter = (Character - 48) * Exponent;
        Result += ShiftedCharacter;
        Exponent *= 10;
    }
    
    return Result;
}

i32 StringToI32(string String)
{
    i32 Result = 0;
    i32 Exponent = 1;
    
    for (i32 StringIndex = String.Size - 1; StringIndex >= 0; --StringIndex)
    {
        char Character = String.Data[StringIndex];
        i32 ShiftedCharacter = (Character - 48) * Exponent;
        Result += ShiftedCharacter;
        Exponent *= 10;
    }
    
    return Result;
}

struct string_list
{
    u32 Count;
    string* Strings;
};

string_list StringSplit(memory_arena* Arena, string String, u8 Delimiter)
{
    string_list Result = {};
    
    u32 MatchCount = 0;
    for (u32 Index = 0; Index < String.Size; ++Index)
    {
        u8 Character = String.Data[Index];
        u32 Match = (Character == Delimiter) || (Index == String.Size - 1);
        if (Match)
        {
            ++MatchCount;
        }
    }
    Result.Count = MatchCount;
    Result.Strings = ArenaPushArray(string, Arena, MatchCount);
    
    u32 PrevStartIndex = 0;
    u8 PrevCharacter = 0;
    u32 MatchIndex = 0;
    for (u32 Index = 0; Index < String.Size; ++Index)
    {
        u8 Character = String.Data[Index];
        
        u32 Match = (Character == Delimiter) || (Index == String.Size - 1);
        if (Match)
        {
            string NewString;
            NewString.Size = Index - PrevStartIndex;
            NewString.Data = String.Data + PrevStartIndex;
            Result.Strings[MatchIndex++] = NewString;
        }
        else
        {
            if (PrevCharacter == Delimiter)
            {
                PrevStartIndex = Index;
            }
        }
        
        PrevCharacter = Character;
    }
    
    return Result;
}
