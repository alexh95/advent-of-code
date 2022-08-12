struct node_edge
{
    string NodeFrom;
    string NodeTo;
    u32 Cost;
};

node_edge ParseNodeEdge(string S)
{
    node_edge Result = {};
    
    u32 StartIndex = 0;
    u32 LastIndex = StringFirstIndexOf(S, StartIndex, ' ');
    Result.NodeFrom = String(S, StartIndex, LastIndex - StartIndex);
    
    StartIndex = LastIndex + 4;
    LastIndex = StringFirstIndexOf(S, StartIndex, ' ');
    Result.NodeTo = String(S, StartIndex, LastIndex - StartIndex);
    
    StartIndex = LastIndex + 3;
    Result.Cost = StringToI32(S, StartIndex, S.Size);
    
    return Result;
};

SOLVER(2015, 09)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    node_edge* Edges = ArenaPushArray(node_edge, Arena, Lines.Count);
    
    hash_table HashTable_ = CreateHashTable(Arena, 128);
    hash_table* HashTable = &HashTable_;
    
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        node_edge Edge = ParseNodeEdge(Line);
        Edges[LineIndex] = Edge;
        HashTableAddElementIfAbsent(HashTable, Edge.NodeFrom);
        HashTableAddElementIfAbsent(HashTable, Edge.NodeTo);
    }
    
    u32* Cost = ArenaPushArray(u32, Arena, HashTable->Count * HashTable->Count);
    for (u32 EdgeIndex = 0; EdgeIndex < Lines.Count; ++EdgeIndex)
    {
        node_edge Edge = Edges[EdgeIndex];
        hash_table_element* ElementFrom = HashTableGetElement(HashTable, Edge.NodeFrom);
        hash_table_element* ElementTo = HashTableGetElement(HashTable, Edge.NodeTo);
        
        u32 CostIndex0 = HashTable->Count * ElementFrom->ElementIndex + ElementTo->ElementIndex;
        u32 CostIndex1 = HashTable->Count * ElementTo->ElementIndex + ElementFrom->ElementIndex;
        
        Cost[CostIndex0] = Edge.Cost;
        Cost[CostIndex1] = Edge.Cost;
    }
    
    b32* NodeVisited = ArenaPushArray(b32, Arena, HashTable->Count);
    u32 MinCostOverall = MAX_U32;
    u32 MaxCostOverall = MIN_U32;
    
    for (u32 StartingIndex = 0; StartingIndex < HashTable->Count; ++StartingIndex)
    {
        for (u32 Index = 0; Index < HashTable->Count; ++Index)
        {
            NodeVisited[Index] = false;
        }
        NodeVisited[StartingIndex] = true;
        u32 VisitedCount = 1;
        u32 MinCostSum = 0;
        
        u32 LastToIndex = StartingIndex;
        
        while (VisitedCount < HashTable->Count)
        {
            u32 MinCost = MAX_U32;
            u32 MinCostIndex = MAX_U32;
            u32 MinToIndex = MAX_U32;
            
            u32 FromIndex = LastToIndex;
            
            for (u32 ToIndex = 0; ToIndex < HashTable->Count; ++ToIndex)
            {
                if ((FromIndex != ToIndex) && !NodeVisited[ToIndex])
                {
                    u32 CostIndex = HashTable->Count * FromIndex + ToIndex;
                    u32 EdgeCost = Cost[CostIndex];
                    if (MinCost > EdgeCost)
                    {
                        MinCost = EdgeCost;
                        MinCostIndex = CostIndex;
                        MinToIndex = ToIndex;
                    }
                }
                
            }
            
            MinCostSum += MinCost;
            NodeVisited[MinToIndex] = true;
            ++VisitedCount;
            LastToIndex = MinToIndex;
        }
        
        if (MinCostOverall > MinCostSum)
        {
            MinCostOverall = MinCostSum;
        }
        
        for (u32 Index = 0; Index < HashTable->Count; ++Index)
        {
            NodeVisited[Index] = false;
        }
        NodeVisited[StartingIndex] = true;
        VisitedCount = 1;
        u32 MaxCostSum = 0;
        
        LastToIndex = StartingIndex;
        
        while (VisitedCount < HashTable->Count)
        {
            u32 MaxCost = MIN_U32;
            u32 MaxCostIndex = MIN_U32;
            u32 MaxToIndex = MIN_U32;
            
            u32 FromIndex = LastToIndex;
            
            for (u32 ToIndex = 0; ToIndex < HashTable->Count; ++ToIndex)
            {
                if ((FromIndex != ToIndex) && !NodeVisited[ToIndex])
                {
                    u32 CostIndex = HashTable->Count * FromIndex + ToIndex;
                    u32 EdgeCost = Cost[CostIndex];
                    if (MaxCost < EdgeCost)
                    {
                        MaxCost = EdgeCost;
                        MaxCostIndex = CostIndex;
                        MaxToIndex = ToIndex;
                    }
                }
                
            }
            
            MaxCostSum += MaxCost;
            NodeVisited[MaxToIndex] = true;
            ++VisitedCount;
            LastToIndex = MaxToIndex;
        }
        
        if (MaxCostOverall < MaxCostSum)
        {
            MaxCostOverall = MaxCostSum;
        }
    }
    
    solution Result = Solution(Arena, (i32)MinCostOverall, (i32)MaxCostOverall);
    return Result;
}
