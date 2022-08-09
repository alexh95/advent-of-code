b32 IsVowel(u8 C)
{
    b32 Result = (C == 'a' ||
                  C == 'e' ||
                  C == 'i' ||
                  C == 'o' ||
                  C == 'u');
    return Result;
}

b32 IsDissalowedPhrase(u8 P, u8 C)
{
    if (P == 'a' && C == 'b')
    {
        return true;
    }
    else if (P == 'c' && C == 'd')
    {
        return true;
    }
    else if (P == 'p' && C == 'q')
    {
        return true;
    }
    else if (P == 'x' && C == 'y')
    {
        return true;
    }
    return false;
}

b32 IsNice(string Word)
{
    u32 VowelCount = 0;
    b32 DoubleCharacter = false;
    b32 NotContainingDissalowedPhrase = true;
    
    u8 PrevCharacter = 0;
    for (u32 CharacterIndex = 0; CharacterIndex < Word.Size; ++CharacterIndex)
    {
        u8 Character = Word.Data[CharacterIndex];
        if (IsVowel(Character))
        {
            ++VowelCount;
        }
        if (Character == PrevCharacter)
        {
            DoubleCharacter = true;
        }
        if (IsDissalowedPhrase(PrevCharacter, Character))
        {
            NotContainingDissalowedPhrase = false;
        }
        
        PrevCharacter = Character;
    }
    
    b32 Result = (VowelCount >= 3) && DoubleCharacter && NotContainingDissalowedPhrase;
    return Result;
};

#define LETTER_COUNT 26
b32 IsActuallyNice(string Word)
{
    b32 RepeatingPair = false;
    b32 RepeatingSpaced = false;
    
    u32 PairCount[LETTER_COUNT * LETTER_COUNT] = {};
    
    u8 PrevPrevCharacter = 0;
    u8 PrevCharacter = 0;
    for (u32 CharacterIndex = 0; CharacterIndex < Word.Size; ++CharacterIndex)
    {
        u8 Character = Word.Data[CharacterIndex];
        if (Character == PrevPrevCharacter)
        {
            RepeatingSpaced = true;
        }
        if ((Character != PrevCharacter || Character != PrevPrevCharacter) && PrevCharacter)
        {
            u32 PairIndex = (Character - 'a') * LETTER_COUNT + (PrevCharacter - 'a');
            ++PairCount[PairIndex];
            if (PairCount[PairIndex] > 1)
            {
                RepeatingPair = true;
            }
        }
        
        PrevPrevCharacter = PrevCharacter;
        PrevCharacter = Character;
    }
    
    b32 Result = RepeatingPair && RepeatingSpaced;
    return Result;
}

SOLVER(2015, 05)
{
    i32 NiceCount = 0;
    i32 ActuallyNiceCount = 0;
    
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        
        if (IsNice(Line))
        {
            ++NiceCount;
        }
        if (IsActuallyNice(Line))
        {
            ++ActuallyNiceCount;
        }
    }
    
    solution Result = Solution(Arena, NiceCount, ActuallyNiceCount);
    return Result;
}
