#ifndef CADVENT_JSON_H
#define CADVENT_JSON_H

enum json_element_type
{
    JsonElementType_None = 0,
    JsonElementType_Object,
    JsonElementType_Array,
    JsonElementType_Number,
    JsonElementType_String,
};

struct json_element
{
    json_element_type Type;
    void* Data;
};

struct json_element_named
{
    string Name;
    json_element Element;
};

struct json_element_object
{
    u32 Count;
    json_element_named* NamedElements;
};

struct json_element_array
{
    u32 Count;
    json_element* Elements;
};

u32 ParseJsonSkipElement(string S, u32 Index, json_element_type Type)
{
    while (CharacterIsWhitespace(S.Data[Index]) && Index < S.Size)
    {
        ++Index;
    }
    
    while (Index < S.Size)
    {
        u8 C = S.Data[Index];
        if (Type == JsonElementType_Object && C == '}')
        {
            return Index + 1;
        }
        else if (Type == JsonElementType_Array && C == ']')
        {
            return Index + 1;
        }
        else if (Type == JsonElementType_String && C == '\"')
        {
            return Index + 1;
        }
        else if (Type == JsonElementType_Number && !CharacterIsNumber(C))
        {
            return Index;
        }
        
        if (C == '{')
        {
            Index = ParseJsonSkipElement(S, Index + 1, JsonElementType_Object);
        }
        else if (C == '[')
        {
            Index = ParseJsonSkipElement(S, Index + 1, JsonElementType_Array);
        }
        else if (C == '\"')
        {
            Index = ParseJsonSkipElement(S, Index + 1, JsonElementType_String);
        }
        else if (CharacterIsNumber(C))
        {
            Index = ParseJsonSkipElement(S, Index + 1, JsonElementType_Number);
        }
        else
        {
            ++Index;
        }
        
        if (Type == JsonElementType_None)
        {
            return Index;
        }
    }
    return Index;
}

inline u32 ParseJsonSkipElement(string S, u32 Index)
{
    u32 Result = ParseJsonSkipElement(S, Index, JsonElementType_None);
    return Result;
}

json_element ParseJsonElement(memory_arena* Arena, string S, u32* Offset);

json_element_named ParseJsonElementNamed(memory_arena* Arena, string S, u32* Offset);

json_element_object* ParseJsonObject(memory_arena* Arena, string S, u32* Offset)
{
    Assert(S.Data[*Offset] == '{');
    json_element_object* Result = ArenaPushStruct(json_element_object, Arena);
    Result->Count = 0;
    
    b32 NotDone = true;
    u32 Index = *Offset + 1;
    while(NotDone && (Index < S.Size))
    {
        u8 C = S.Data[Index];
        if (C == '}')
        {
            NotDone = false;
        }
        else if (!CharacterIsWhitespace(C) && C != ',')
        {
            while (S.Data[Index++] != ':');
            Index = ParseJsonSkipElement(S, Index);
            ++Result->Count;
        }
        else
        {
            ++Index;
        }
    }
    
    Result->NamedElements = ArenaPushArray(json_element_named, Arena, Result->Count);
    NotDone = true;
    u32 ElementIndex = 0;
    ++*Offset;
    while (NotDone && (*Offset < S.Size))
    {
        u8 C = S.Data[*Offset];
        if (C == '}')
        {
            NotDone = false;
            ++*Offset;
        }
        else if (!CharacterIsWhitespace(C) && C != ',')
        {
            Result->NamedElements[ElementIndex++] = ParseJsonElementNamed(Arena, S, Offset);
        }
        else
        {
            ++*Offset;
        }
    }
    
    return Result;
}

json_element_array* ParseJsonArray(memory_arena* Arena, string S, u32* Offset)
{
    Assert(S.Data[*Offset] == '[');
    json_element_array* Result = ArenaPushStruct(json_element_array, Arena);
    Result->Count = 0;
    
    b32 NotDone = true;
    u32 Index = *Offset + 1;
    while (NotDone && (Index < S.Size))
    {
        u8 C = S.Data[Index];
        if (C == ']')
        {
            NotDone = false;
        }
        else if (!CharacterIsWhitespace(C) && C != ',')
        {
            Index = ParseJsonSkipElement(S, Index);
            ++Result->Count;
        }
        else
        {
            ++Index;
        }
    }
    
    Result->Elements = ArenaPushArray(json_element, Arena, Result->Count);
    NotDone = true;
    u32 ElementIndex = 0;
    ++*Offset;
    while (NotDone && (*Offset < S.Size))
    {
        u8 C = S.Data[*Offset];
        if (C == ']')
        {
            NotDone = false;
            ++*Offset;
        }
        else if (!CharacterIsWhitespace(C) && C != ',')
        {
            Result->Elements[ElementIndex++] = ParseJsonElement(Arena, S, Offset);
        }
        else
        {
            ++*Offset;
        }
    }
    
    return Result;
}

string ParseJsonElementStringName(memory_arena* Arena, string S, u32* Offset)
{
    Assert(S.Data[*Offset] == '\"');
    string Result;
    u32 IndexOfQuote = StringFirstIndexOf(S, *Offset + 1, '\"');
    Assert(*Offset < IndexOfQuote);
    Result.Size = IndexOfQuote - *Offset - 1;
    Result.Data = ArenaPushArray(u8, Arena, Result.Size);
    StringCopy(Result, 0, S, *Offset + 1, Result.Size);
    *Offset = IndexOfQuote + 1;
    return Result;
}

string* ParseJsonString(memory_arena* Arena, string S, u32* Offset)
{
    Assert(S.Data[*Offset] == '\"');
    string* Result = ArenaPushStruct(string, Arena);
    u32 IndexOfQuote = StringFirstIndexOf(S, *Offset + 1, '\"');
    Assert(*Offset < IndexOfQuote);
    Result->Size = IndexOfQuote - *Offset - 1;
    Result->Data = ArenaPushArray(u8, Arena, Result->Size);
    StringCopy(*Result, 0, S, *Offset + 1, Result->Size);
    *Offset = IndexOfQuote + 1;
    return Result;
}

i32* ParseJsonNumber(memory_arena* Arena, string S, u32* Offset)
{
    Assert(CharacterIsNumber(S.Data[*Offset]));
    i32* Result = ArenaPushStruct(i32, Arena);
    u32 IndexOfNonNumber = StringFirstIndexOfNonNumber(S, *Offset);
    Assert(IndexOfNonNumber > *Offset);
    *Result = StringToI32(S, *Offset, IndexOfNonNumber);
    *Offset = IndexOfNonNumber;
    return Result;
}

json_element ParseJsonElement(memory_arena* Arena, string S, u32* Offset)
{
    json_element Result = {};
    b32 NotDone = true;
    while (NotDone && (*Offset < S.Size))
    {
        u8 C = S.Data[*Offset];
        
        if (C == '{')
        {
            Result.Type = JsonElementType_Object;
            Result.Data = ParseJsonObject(Arena, S, Offset);
            NotDone = false;
        }
        else if (C == '[')
        {
            Result.Type = JsonElementType_Array;
            Result.Data = ParseJsonArray(Arena, S, Offset);
            NotDone = false;
        }
        else if (C == '\"')
        {
            Result.Type = JsonElementType_String;
            Result.Data = ParseJsonString(Arena, S, Offset);
            NotDone = false;
        }
        else if (CharacterIsNumber(C))
        {
            Result.Type = JsonElementType_Number;
            Result.Data = ParseJsonNumber(Arena, S, Offset);
            NotDone = false;
        }
        else
        {
            ++*Offset;
        }
    }
    
    return Result;
}

json_element_named ParseJsonElementNamed(memory_arena* Arena, string S, u32* Offset)
{
    json_element_named Result;
    Result.Name = ParseJsonElementStringName(Arena, S, Offset);
    while (S.Data[*Offset] != ':')
    {
        ++*Offset;
    }
    Result.Element = ParseJsonElement(Arena, S, Offset);
    return Result;
}

json_element ParseJsonElement(memory_arena* Arena, string S)
{
    u32 Offset = 0;
    json_element Result = ParseJsonElement(Arena, S, &Offset);
    return Result;
}

#endif
