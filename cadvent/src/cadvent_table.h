#ifndef CADVENT_TABLE_H
#define CADVENT_TABLE_H

struct hash_table_element
{
    string Value;
    u32 ElementIndex;
    hash_table_element* Next;
};

struct hash_table
{
    u32 MaxSize;
    u32 Count;
    hash_table_element* Elements;
    hash_table_element** HashTable;
};

hash_table CreateHashTable(memory_arena* Arena, u32 Size)
{
    hash_table Result;
    Result.MaxSize = Size;
    Result.Count = 0;
    Result.Elements = ArenaPushArray(hash_table_element, Arena, Size);
    Result.HashTable = ArenaPushArray(hash_table_element*, Arena, Size);
    return Result;
}

u32 HashTableHashValue(hash_table* Table, string Value)
{
    u32 Result = 0;
    for (u32 Index = 0; Index < Value.Size; ++Index)
    {
        u8 C = Value.Data[Index];
        Result = (7 * Result + 3 * C) % Table->MaxSize;
    }
    return Result;
}

hash_table_element* HashTableGetElement(hash_table* Table, string Value)
{
    u32 Hash = HashTableHashValue(Table, Value);
    hash_table_element* Element = Table->HashTable[Hash];
    
    while (Element)
    {
        if (StringCompare(Element->Value, Value))
        {
            return Element;
        }
        Element = Element->Next;
    }
    return 0;
}

hash_table_element* HashTableAddElement(hash_table* Table, string Value)
{
    u32 Hash = HashTableHashValue(Table, Value);
    hash_table_element* NewElement = Table->Elements + Table->Count;
    NewElement->Value = Value;
    NewElement->ElementIndex = Table->Count;
    ++Table->Count;
    
    hash_table_element* Element = Table->HashTable[Hash];
    if (!Element)
    {
        Table->HashTable[Hash] = NewElement;
    }
    else
    {
        while (Element->Next)
        {
            Element = Element->Next;
        }
        Element->Next = NewElement;
    }
    return NewElement;
}

hash_table_element* HashTableAddElementIfAbsent(hash_table* Table, string Value)
{
    hash_table_element* Element = HashTableGetElement(Table, Value);
    if (!Element)
    {
        Element = HashTableAddElement(Table, Value);
    }
    return Element;
}

#endif
