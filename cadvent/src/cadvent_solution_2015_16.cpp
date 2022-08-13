struct detected_component_list
{
    union
    {
        struct
        {
            i32 Children;
            i32 Cats;
            i32 Samoyeds;
            i32 Pomeranians;
            i32 Akitas;
            i32 Vizslas;
            i32 Goldfish;
            i32 Trees;
            i32 Cars;
            i32 Perfumes;
        };
        i32 Components[10];
    };
};


SOLVER(2015, 16)
{
    hash_table HashTable_ = CreateHashTable(Arena, 64);
    hash_table* HashTable = &HashTable_;
    
    string ComponentNames[] =
    {
        String("children"),
        String("cats"),
        String("samoyeds"),
        String("pomeranians"),
        String("akitas"),
        String("vizslas"),
        String("goldfish"),
        String("trees"),
        String("cars"),
        String("perfumes")
    };
    for (u32 ComponentIndex = 0; ComponentIndex < ArrayCount(ComponentNames); ++ComponentIndex)
    {
        string ComponentName = ComponentNames[ComponentIndex];
        HashTableAddElement(HashTable, ComponentName);
    }
    
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    detected_component_list* ComponentLists = ArenaPushArray(detected_component_list, Arena, Lines.Count);
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        detected_component_list* ComponentList = ComponentLists + LineIndex;
        for (u32 ComponentIndex = 0; ComponentIndex < ArrayCount(ComponentNames); ++ComponentIndex)
        {
            ComponentList->Components[ComponentIndex] = -1;
        }
        
        u32 LastIndex = StringFirstIndexOf(Line, 0, ':') + 2;
        u32 Index = StringFirstIndexOf(Line, LastIndex, ':');
        string ComponentName = String(Line, LastIndex, Index - LastIndex);
        u32 ComponentIndex = HashTableGetElement(HashTable, ComponentName)->ElementIndex;
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        i32 Value = StringToI32(Line, LastIndex, Index);
        ComponentList->Components[ComponentIndex] = Value;
        
        LastIndex = Index + 2;
        Index = StringFirstIndexOf(Line, LastIndex, ':');
        ComponentName = String(Line, LastIndex, Index - LastIndex);
        ComponentIndex = HashTableGetElement(HashTable, ComponentName)->ElementIndex;
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        Value = StringToI32(Line, LastIndex, Index);
        ComponentList->Components[ComponentIndex] = Value;
        
        LastIndex = Index + 2;
        Index = StringFirstIndexOf(Line, LastIndex, ':');
        ComponentName = String(Line, LastIndex, Index - LastIndex);
        ComponentIndex = HashTableGetElement(HashTable, ComponentName)->ElementIndex;
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = Line.Size;
        Value = StringToI32(Line, LastIndex, Index);
        ComponentList->Components[ComponentIndex] = Value;
    }
    
    detected_component_list ExpectedComponentList = { 3, 7, 2, 3, 0, 0, 5, 3, 2, 1 };
    u32 MatchedIndex = 0;
    u32 RetroEncabulatedIndex = 0;
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        detected_component_list* ComponentList = ComponentLists + LineIndex;
        b32 Matched = true;
        b32 RetroEncabulated = true;
        for (u32 ComponentIndex = 0; ComponentIndex < ArrayCount(ComponentNames); ++ComponentIndex)
        {
            i32 ExpectedValue = ExpectedComponentList.Components[ComponentIndex];
            i32 Value = ComponentList->Components[ComponentIndex];
            if (Value != -1)
            {
                if (ExpectedValue != Value)
                {
                    Matched = false;
                }
                
                if (ComponentIndex == 1 || ComponentIndex == 7)
                {
                    if (ExpectedValue > Value)
                    {
                        RetroEncabulated = false;
                    }
                }
                else if (ComponentIndex == 3 || ComponentIndex == 6)
                {
                    if (ExpectedValue < Value)
                    {
                        RetroEncabulated = false;
                    }
                }
                else
                {
                    if (ExpectedValue != Value)
                    {
                        RetroEncabulated = false;
                    }
                }
            }
        }
        if (Matched)
        {
            MatchedIndex = LineIndex + 1;
        }
        if (RetroEncabulated)
        {
            RetroEncabulatedIndex = LineIndex + 1;
        }
    }
    
    solution Result = Solution(Arena, MatchedIndex, RetroEncabulatedIndex);
    return Result;
}
