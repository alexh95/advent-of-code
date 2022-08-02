string String(u32 Size)
{
    string Result;
    Result.Size = Size;
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
