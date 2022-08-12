struct cookie_ingredient
{
    i32 Capacity;
    i32 Durability;
    i32 Flavor;
    i32 Texture;
    i32 Calories;
};

cookie_ingredient* ParseCookieIngredients(memory_arena* Arena, string_list* Lines)
{
    cookie_ingredient* Result = ArenaPushArray(cookie_ingredient, Arena, Lines->Count);
    for (u32 LineIndex = 0; LineIndex < Lines->Count; ++LineIndex)
    {
        string Line = Lines->Strings[LineIndex];
        cookie_ingredient CookieIngredient = {};
        
        u32 Index = 0;
        u32 LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        CookieIngredient.Capacity = StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        CookieIngredient.Durability = StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        CookieIngredient.Flavor = StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = StringFirstIndexOfNonNumber(Line, LastIndex);
        CookieIngredient.Texture = StringToI32(Line, LastIndex, Index);
        
        LastIndex = StringFirstIndexOfNumber(Line, Index);
        Index = Line.Size;
        CookieIngredient.Calories = StringToI32(Line, LastIndex, Index);
        
        Result[LineIndex] = CookieIngredient;
    }
    return Result;
}

cookie_ingredient CookieScore(cookie_ingredient* Ingredients, i32* Amounts, u32 Count)
{
    cookie_ingredient Result = {};
    for (u32 IngredientIndex = 0; IngredientIndex < Count; ++IngredientIndex)
    {
        cookie_ingredient* Ingredient = Ingredients + IngredientIndex;
        i32 Amount = Amounts[IngredientIndex];
        
        Result.Capacity += Ingredient->Capacity * Amount;
        Result.Durability += Ingredient->Durability * Amount;
        Result.Flavor += Ingredient->Flavor * Amount;
        Result.Texture += Ingredient->Texture * Amount;
        Result.Calories += Ingredient->Calories * Amount;
    }
    
    Result.Capacity = MAX(0, Result.Capacity);
    Result.Durability = MAX(0, Result.Durability);
    Result.Flavor = MAX(0, Result.Flavor);
    Result.Texture = MAX(0, Result.Texture);
    
    return Result;
}

#define INGREDIENT_SUM 100
#define CALORIC_SUM 500

SOLVER(2015, 15)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    cookie_ingredient* Ingredients = ParseCookieIngredients(Arena, &Lines);
    i32* Amounts = ArenaPushArray(i32, Arena, Lines.Count);
    
    u32 MaxScore = 0;
    u32 CaloricMaxScore = 0;
    b32 NotDone = true;
    while (NotDone)
    {
        u32 Sum = 0;
        for (u32 Index = 0; Index < Lines.Count; ++Index)
        {
            u32 Amount = Amounts[Index];
            Sum += Amount;
        }
        
        if (Sum == INGREDIENT_SUM)
        {
            cookie_ingredient IngredientScore = CookieScore(Ingredients, Amounts, Lines.Count);
            u32 Score = IngredientScore.Capacity * IngredientScore.Durability * IngredientScore.Flavor * IngredientScore.Texture;
            if (MaxScore < Score)
            {
                MaxScore = Score;
            }
            if (IngredientScore.Calories == CALORIC_SUM)
            {
                if (CaloricMaxScore < Score)
                {
                    CaloricMaxScore = Score;
                }
            }
        }
        
        ++Amounts[0];
        for (u32 Index = 0; Index < Lines.Count; ++Index)
        {
            if (Amounts[Index] > 100)
            {
                Amounts[Index] = 0;
                if (Index < Lines.Count - 1)
                {
                    ++Amounts[Index + 1];
                }
                else
                {
                    NotDone = false;
                }
            }
        }
    }
    
    solution Result = Solution(Arena, MaxScore, CaloricMaxScore);
    return Result;
};
