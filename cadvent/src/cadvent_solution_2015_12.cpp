i32 SumJsonNumbers(json_element* Element, b32 SkipRed)
{
    i32 Result = {};
    switch (Element->Type)
    {
        case JsonElementType_Object:
        {
            json_element_object* ElementObject = (json_element_object*) Element->Data;
            b32 NoSkip = true;
            if (SkipRed)
            {
                for (u32 ElementIndex = 0; ElementIndex < ElementObject->Count; ++ElementIndex)
                {
                    json_element_named* N = ElementObject->NamedElements + ElementIndex;
                    if ((N->Element.Type == JsonElementType_String) && 
                        (StringCompare(*(string*)N->Element.Data, "red")))
                    {
                        NoSkip = false;
                    }
                }
            }
            if (NoSkip)
            {
                for (u32 ElementIndex = 0; ElementIndex < ElementObject->Count; ++ElementIndex)
                {
                    json_element_named* N = ElementObject->NamedElements + ElementIndex;
                    i32 ObjectSum = SumJsonNumbers(&N->Element, SkipRed);
                    Result += ObjectSum;
                }
            }
        } break;
        case JsonElementType_Array:
        {
            json_element_array* ElementArray = (json_element_array*) Element->Data;
            for (u32 ElementIndex = 0; ElementIndex < ElementArray->Count; ++ElementIndex)
            {
                json_element* E = ElementArray->Elements + ElementIndex;
                i32 ArraySum = SumJsonNumbers(E, SkipRed);
                Result += ArraySum;
            }
        } break;
        case JsonElementType_String:
        {
        } break;
        case JsonElementType_Number:
        {
            i32* Value = (i32*) Element->Data;
            Result += *Value;
        } break;
        default:
        {
            InvalidCodePath;
        }
    }
    return Result;
}

SOLVER(2015, 12)
{
    json_element Json = ParseJsonElement(Arena, InputBuffer);
    i32 Sum  = SumJsonNumbers(&Json, false);
    i32 NotRedSum = SumJsonNumbers(&Json, true);
    
    solution Result = Solution(Arena, Sum, NotRedSum);
    return Result;
};
