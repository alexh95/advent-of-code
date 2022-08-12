struct parsed_line
{
    string FirstName;
    string SecondName;
    i32 Value;
};

parsed_line* ParseLines(memory_arena* Arena, string_list* Lines)
{
    parsed_line* Result = ArenaPushArray(parsed_line, Arena, Lines->Count);
    for (u32 LineIndex = 0; LineIndex < Lines->Count; ++LineIndex)
    {
        string Line = Lines->Strings[LineIndex];
        parsed_line Parsed;
        
        u32 LastIndex = 0;
        u32 Index = StringFirstIndexOf(Line, LastIndex, ' ');
        Parsed.FirstName = String(Line, LastIndex, Index - LastIndex);
        
        LastIndex = Index + 7;
        Index = LastIndex + 4;
        i32 Sign = 1;
        if (StringCompare(Line.Data + LastIndex, 4, "lose"))
        {
            Sign = -1;
        }
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex + 1);
        Parsed.Value = Sign * StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringLastIndexOf(Line, Index, ' ') + 1;
        Index = StringFirstIndexOf(Line, LastIndex, '.');
        Parsed.SecondName = String(Line, LastIndex, Index - LastIndex);
        
        Result[LineIndex] = Parsed;
    }
    return Result;
}

void Swap(u32* A, u32* B)
{
    u32 Temp = *A;
    *A = *B;
    *B = Temp;
}

i32 MaxValuePlacement(memory_arena* Arena, hash_table* HashTable, parsed_line* ParsedLines, u32 ParsedLineCount)
{
    i32* ValueMap = ArenaPushArray(i32, Arena, HashTable->Count * HashTable->Count);
    for (u32 Index = 0; Index < HashTable->Count * HashTable->Count; ++Index)
    {
        ValueMap[Index] = 0;
    }
    
    for (u32 Index = 0; Index < ParsedLineCount; ++Index)
    {
        parsed_line Parsed = ParsedLines[Index];
        
        hash_table_element* FirstElement = HashTableGetElement(HashTable, Parsed.FirstName);
        hash_table_element* SecondElement = HashTableGetElement(HashTable, Parsed.SecondName);
        
        u32 ValueIndex = FirstElement->ElementIndex + HashTable->Count * SecondElement->ElementIndex;
        ValueMap[ValueIndex] = Parsed.Value;
    }
    
    u32* Permutation = ArenaPushArray(u32, Arena, HashTable->Count);
    for (u32 Index = 0; Index < HashTable->Count; ++Index)
    {
        Permutation[Index] = Index;
    }
    u32* PermutationCounter = ArenaPushArray(u32, Arena, HashTable->Count - 1);
    for (u32 Index = 0; Index < HashTable->Count - 1; ++Index)
    {
        PermutationCounter[Index] = 0;
    }
    
    i32 ValueSumMax = MIN_I32;
    u32 CounterSize = HashTable->Count - 1;
    b32 NotDone = true;
    while (NotDone)
    {
        for (i32 CounterIndex = CounterSize - 1; CounterIndex >= 0; --CounterIndex)
        {
            u32 SwapWidth = PermutationCounter[CounterIndex];
            Swap(Permutation + CounterIndex + 1, Permutation + CounterIndex + 1 - SwapWidth);
        }
        
        i32 ValueSum = 0;
        for (u32 Index = 0; Index < HashTable->Count; ++Index)
        {
            u32 LeftIndex = (Index > 0) ? (Index - 1) : (HashTable->Count - 1);
            u32 RightIndex = (Index < HashTable->Count - 1) ? (Index + 1) : 0;
            
            u32 PermutedLeftIndex = Permutation[LeftIndex];
            u32 PermutedIndex = Permutation[Index];
            u32 PermutedRightIndex = Permutation[RightIndex];
            
            u32 LeftValueIndex = PermutedIndex + HashTable->Count * PermutedLeftIndex;
            u32 RightValueIndex = PermutedIndex + HashTable->Count * PermutedRightIndex;
            
            ValueSum += ValueMap[LeftValueIndex] + ValueMap[RightValueIndex];
        }
        if (ValueSum > ValueSumMax)
        {
            ValueSumMax = ValueSum;
        }
        
        for (u32 CounterIndex = 0; CounterIndex < CounterSize; ++CounterIndex)
        {
            u32 SwapWidth = PermutationCounter[CounterIndex];
            Swap(Permutation + CounterIndex + 1, Permutation + CounterIndex + 1 - SwapWidth);
        }
        
        ++PermutationCounter[0];
        for (u32 CounterIndex = 0; CounterIndex < CounterSize; ++CounterIndex)
        {
            if (PermutationCounter[CounterIndex] >= CounterIndex + 2)
            {
                PermutationCounter[CounterIndex] = 0;
                if (CounterIndex < CounterSize - 1)
                {
                    ++PermutationCounter[CounterIndex + 1];
                }
                else
                {
                    NotDone = false;
                }
            }
        }
    }
    
    return ValueSumMax;
}

SOLVER(2015, 13)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    parsed_line* ParsedLines = ParseLines(Arena, &Lines);
    
    hash_table HashTable_ = CreateHashTable(Arena, 64);
    hash_table* HashTable = &HashTable_;
    for (u32 Index = 0; Index < Lines.Count; ++Index)
    {
        parsed_line Parsed = ParsedLines[Index];
        HashTableAddElementIfAbsent(HashTable, Parsed.FirstName);
        HashTableAddElementIfAbsent(HashTable, Parsed.SecondName);
    }
    
    i32 ValueSumMax = MaxValuePlacement(Arena, HashTable, ParsedLines, Lines.Count);
    
    string OneExtra = ArenaPushString(Arena, "me");
    HashTableAddElement(HashTable, OneExtra);
    
    i32 ValueOneExtraSumMax = MaxValuePlacement(Arena, HashTable, ParsedLines, Lines.Count);
    
    solution Result = Solution(Arena, ValueSumMax, ValueOneExtraSumMax);
    return Result;
}
