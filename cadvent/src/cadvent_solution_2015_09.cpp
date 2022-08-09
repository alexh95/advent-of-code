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

struct node_table_element
{
    string Node;
    u32 ElementIndex;
    node_table_element* Next;
};

struct node_table
{
    u32 MaxSize;
    u32 Count;
    node_table_element* Elements;
    node_table_element** HashTable;
};

node_table CreateNodeTable(memory_arena* Arena, u32 Size)
{
    node_table Result;
    Result.MaxSize = Size;
    Result.Count = 0;
    Result.Elements = ArenaPushArray(node_table_element, Arena, Size);
    Result.HashTable = ArenaPushArray(node_table_element*, Arena, Size);
    return Result;
}

u32 NodeTableHash(node_table* Table, string Node)
{
    u32 Result = 0;
    for (u32 Index = 0; Index < Node.Size; ++Index)
    {
        u8 C = Node.Data[Index];
        Result = (7 * Result + 3 * C) % Table->MaxSize;
    }
    return Result;
}

node_table_element* NodeTableGetElement(node_table* Table, string Node)
{
    u32 Hash = NodeTableHash(Table, Node);
    node_table_element* Element = Table->HashTable[Hash];
    
    while (Element)
    {
        if (StringCompare(Element->Node, Node))
        {
            return Element;
        }
        Element = Element->Next;
    }
    return 0;
}

void NodeTableAddElement(node_table* Table, string Node)
{
    u32 Hash = NodeTableHash(Table, Node);
    node_table_element* NewElement = Table->Elements + Table->Count;
    NewElement->Node = Node;
    NewElement->ElementIndex = Table->Count;
    ++Table->Count;
    
    node_table_element* Element = Table->HashTable[Hash];
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
}

void NodeTableAddElementIfAbsent(node_table* Table, string Node)
{
    if (!NodeTableGetElement(Table, Node))
    {
        NodeTableAddElement(Table, Node);
    }
}

SOLVER(2015, 09)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    node_edge* Edges = ArenaPushArray(node_edge, Arena, Lines.Count);
    
    node_table NodeTable = CreateNodeTable(Arena, 128);
    
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        node_edge Edge = ParseNodeEdge(Line);
        Edges[LineIndex] = Edge;
        NodeTableAddElementIfAbsent(&NodeTable, Edge.NodeFrom);
        NodeTableAddElementIfAbsent(&NodeTable, Edge.NodeTo);
    }
    
    u32* Cost = ArenaPushArray(u32, Arena, NodeTable.Count * NodeTable.Count);
    for (u32 EdgeIndex = 0; EdgeIndex < Lines.Count; ++EdgeIndex)
    {
        node_edge Edge = Edges[EdgeIndex];
        node_table_element* NodeFrom = NodeTableGetElement(&NodeTable, Edge.NodeFrom);
        node_table_element* NodeTo = NodeTableGetElement(&NodeTable, Edge.NodeTo);
        
        u32 CostIndex0 = NodeTable.Count * NodeFrom->ElementIndex + NodeTo->ElementIndex;
        u32 CostIndex1 = NodeTable.Count * NodeTo->ElementIndex + NodeFrom->ElementIndex;
        
        Cost[CostIndex0] = Edge.Cost;
        Cost[CostIndex1] = Edge.Cost;
    }
    
    b32* NodeVisited = ArenaPushArray(b32, Arena, NodeTable.Count);
    u32 MinCostOverall = MAX_U32;
    u32 MaxCostOverall = MIN_U32;
    
    for (u32 StartingIndex = 0; StartingIndex < NodeTable.Count; ++StartingIndex)
    {
        for (u32 Index = 0; Index < NodeTable.Count; ++Index)
        {
            NodeVisited[Index] = false;
        }
        NodeVisited[StartingIndex] = true;
        u32 VisitedCount = 1;
        u32 MinCostSum = 0;
        
        u32 LastToIndex = StartingIndex;
        
        while (VisitedCount < NodeTable.Count)
        {
            u32 MinCost = MAX_U32;
            u32 MinCostIndex = MAX_U32;
            u32 MinToIndex = MAX_U32;
            
            u32 FromIndex = LastToIndex;
            
            for (u32 ToIndex = 0; ToIndex < NodeTable.Count; ++ToIndex)
            {
                if ((FromIndex != ToIndex) && !NodeVisited[ToIndex])
                {
                    u32 CostIndex = NodeTable.Count * FromIndex + ToIndex;
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
        
        for (u32 Index = 0; Index < NodeTable.Count; ++Index)
        {
            NodeVisited[Index] = false;
        }
        NodeVisited[StartingIndex] = true;
        VisitedCount = 1;
        u32 MaxCostSum = 0;
        
        LastToIndex = StartingIndex;
        
        while (VisitedCount < NodeTable.Count)
        {
            u32 MaxCost = MIN_U32;
            u32 MaxCostIndex = MIN_U32;
            u32 MaxToIndex = MIN_U32;
            
            u32 FromIndex = LastToIndex;
            
            for (u32 ToIndex = 0; ToIndex < NodeTable.Count; ++ToIndex)
            {
                if ((FromIndex != ToIndex) && !NodeVisited[ToIndex])
                {
                    u32 CostIndex = NodeTable.Count * FromIndex + ToIndex;
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
