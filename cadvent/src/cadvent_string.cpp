string String(u32 Size)
{
    string Result;
    Result.Size = Size;
    return Result;
}

string String(char* S)
{
    string Result;
    Result.Size = StringLength(S);
    Result.Data = (u8*)S;
    return Result;
}

string String(string S, u32 Offset, u32 Size)
{
    string Result;
    Result.Size = Size;
    Result.Data = S.Data + Offset;
    return Result;
}

string StringI(string S, u32 FromIndex, u32 ToIndex)
{
    string Result;
    Result.Size = ToIndex - FromIndex;
    Result.Data = S.Data + FromIndex;
    return Result;
}

u32 StringCopy(string Dst, u32 DstOffset, string Src, u32 SrcOffset, u32 SrcCount)
{
    Assert(Dst.Size >= SrcCount + DstOffset - SrcOffset);
    for (u32 Index = 0; Index < SrcCount; ++Index)
    {
        Dst.Data[DstOffset + Index] = Src.Data[SrcOffset + Index];
    }
    return DstOffset + Src.Size;
}

inline u32 StringCopy(string Dst, u32 DstOffset, string Src)
{
    u32 Result = StringCopy(Dst, DstOffset, Src, 0, Src.Size);
    return Result;
}

inline u32 StringCopy(string Dst, string Src)
{
    u32 Result = StringCopy(Dst, 0, Src);
    return Result;
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


inline b32 StringCompare(u8* A, u32 SizeA, char* B)
{
    u32 SizeB = StringLength(B);
    b32 Result = StringCompare(A, SizeA, (u8*)B, SizeB);
    return Result;
}

inline b32 StringCompare(string A, char* B)
{
    b32 Result = StringCompare(A.Data, A.Size, B);
    return Result;
}

inline b32 StringCompare(string A, string B)
{
    b32 Result = StringCompare(A.Data, A.Size, B.Data, B.Size);
    return Result;
}

#define MAX_STRING_LENGTH_I32 11
u32 StringFromI32(string S, u32 Offset, i32 Value, u32 MinLength, b32 LeadingZeros)
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
        S.Data[LastIndex] = Characters[Size - 1 - Index];
    }
    return LastIndex + 1;
}

u32 StringFromI32(string S, u32 Offset, i32 Value)
{
    u32 Result = StringFromI32(S, Offset, Value, 0, false);
    return Result;
}

inline b32 CharacterIsWhitespace(u8 C)
{
    b32 Result = (C == ' ') || (C == '\t') || (C == '\n') || (C == '\r');
    return Result;
}

inline b32 CharacterIsNumber(u8 C)
{
    b32 Result = (C >= '0' && C <= '9') || (C == '-');
    return Result;
}

i32 StringFirstIndexOfNumber(string S, u32 From)
{
    for (u32 Index = From; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        if (CharacterIsNumber(C))
        {
            return Index;
        }
    }
    return -1;
}

i32 StringFirstIndexOfNonNumber(string S, u32 From)
{
    for (u32 Index = From; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        if (!CharacterIsNumber(C))
        {
            return Index;
        }
    }
    return -1;
}

i32 StringFirstIndexOf(string S, u32 Offset, u8 Delimiter)
{
    for (u32 Index = Offset; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        if (C == Delimiter)
        {
            return Index;
        }
    }
    return -1;
}

i32 StringLastIndexOf(string S, u32 Offset, u8 Delimiter)
{
    i32 Result = -1;
    for (u32 Index = Offset; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        if (C == Delimiter)
        {
            Result = Index;
        }
    }
    return Result;
}

i32 StringToI32(string S, i32 From, i32 To)
{
    i32 Result = 0;
    i32 Exponent = 1;
    
    for (i32 StringIndex = To - 1; StringIndex >= From; --StringIndex)
    {
        char Character = S.Data[StringIndex];
        if (Character != '-')
        {
            i32 ShiftedCharacter = (Character - 48) * Exponent;
            Result += ShiftedCharacter;
            Exponent *= 10;
        }
        else
        {
            Assert(StringIndex == From);
            Result = -Result;
        }
    }
    
    return Result;
}

i32 StringToI32(string S)
{
    i32 Result = 0;
    i32 Exponent = 1;
    
    for (i32 StringIndex = S.Size - 1; StringIndex >= 0; --StringIndex)
    {
        char Character = S.Data[StringIndex];
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

string_list StringSplit(memory_arena* Arena, string S, u8 Delimiter)
{
    string_list Result = {};
    
    u32 MatchCount = 0;
    for (u32 Index = 0; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        u32 Match = (C == Delimiter) || (Index == S.Size - 1);
        if (Match)
        {
            ++MatchCount;
        }
    }
    Result.Count = MatchCount;
    Result.Strings = ArenaPushArray(string, Arena, MatchCount);
    
    u32 PrevStartIndex = 0;
    u32 MatchIndex = 0;
    for (u32 Index = 0; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        
        u32 Match = (C == Delimiter) || (Index == S.Size - 1);
        if (Match)
        {
            string NewString;
            NewString.Size = Index - PrevStartIndex + ((C == Delimiter) ? 0 : 1);
            NewString.Data = S.Data + PrevStartIndex;
            Result.Strings[MatchIndex++] = NewString;
            PrevStartIndex = Index + 1;
        }
    }
    
    return Result;
}
