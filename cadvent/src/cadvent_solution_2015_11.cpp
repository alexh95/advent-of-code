static b32 IsValidPassword(string S)
{
    b32 IncrementSequence = false;
    b32 NotContainingDisallowedLetters = true;
    u32 PairCount = 0;
    
    u8 LL = 0;
    u8 L = 0;
    for (u32 Index = 0; Index < S.Size; ++Index)
    {
        u8 C = S.Data[Index];
        
        if (C == (L + 1) && L == (LL + 1))
        {
            IncrementSequence = true;
        }
        
        if ((C == 'i') || (C == 'o') || (C == 'l'))
        {
            NotContainingDisallowedLetters = false;
        }
        
        if (C == L && C != LL)
        {
            ++PairCount;
        }
        
        LL = L;
        L = C;
    }
    
    b32 Result = IncrementSequence && NotContainingDisallowedLetters && (PairCount >= 2);
    return Result;
}

static void IncrementPassword(string S)
{
    b32 Carry = true;
    for (i32 Index = S.Size - 1; Index >= 0; --Index)
    {
        u8 C = S.Data[Index];
        Assert('a' <= C && C <= 'z');
        if (Carry)
        {
            if (C < 'z')
            {
                ++S.Data[Index];
                Carry = false;
            }
            else
            {
                S.Data[Index] = 'a';
                Carry = true;
            }
        }
    }
}

static void IncrementToValidPassword(string S)
{
    b32 NotDone = true;
    while (NotDone)
    {
        if (IsValidPassword(S))
        {
            NotDone = false;
        }
        else
        {
            IncrementPassword(S);
        }
    }
}

SOLVER(2015, 11)
{
    string Password = ArenaPushString(Arena, InputBuffer);
    
    IncrementToValidPassword(Password);
    string FirstPassword = ArenaPushString(Arena, Password);
    IncrementPassword(Password);
    IncrementToValidPassword(Password);
    
    solution Result = Solution(Arena, FirstPassword, Password);
    return Result;
}
