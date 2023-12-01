struct compound_replacement
{
    u32 FromIndex;
    u32 ToIndex;
};

struct compound_match
{
    u32 CompoundReplacementIndex;
    u32 MoleculeIndex;
};

void ReplaceCompound(string NewMolecule, string Molecule, u32 MoleculeIndex, string FromCompound, string ToCompound)
{
    u32 UnchangedMoleculeLeftSize = MoleculeIndex - FromCompound.Size + 1;
    StringCopy(NewMolecule, StringI(Molecule, 0, UnchangedMoleculeLeftSize));
    StringCopy(NewMolecule, UnchangedMoleculeLeftSize, ToCompound);
    StringCopy(NewMolecule, MoleculeIndex + ToCompound.Size - FromCompound.Size + 1, StringI(Molecule, MoleculeIndex + 1, Molecule.Size));
}

void FindCompoundMatches(memory_arena* Arena,
                         string Molecule,
                         hash_table* CompoundHashTable,
                         compound_replacement* CompoundReplacements,
                         u32 CompoundReplacementCount,
                         compound_match** Matches,
                         u32* MatchCount,
                         b32 ForwardReplacement)
{
    for (u32 CompoundReplacementIndex = 0; CompoundReplacementIndex < CompoundReplacementCount; ++CompoundReplacementIndex)
    {
        compound_replacement CompoundReplacement = CompoundReplacements[CompoundReplacementIndex];
        string Compound;
        if (ForwardReplacement)
        {
            Compound = CompoundHashTable->Elements[CompoundReplacement.FromIndex].Value;
        }
        else
        {
            Compound = CompoundHashTable->Elements[CompoundReplacement.ToIndex].Value;
        }
        
        u32 MatchIndex = 0;
        for (u32 MoleculeIndex = 0; MoleculeIndex < Molecule.Size; ++MoleculeIndex)
        {
            u8 C = Molecule.Data[MoleculeIndex];
            
            if (C == Compound.Data[MatchIndex])
            {
                ++MatchIndex;
            }
            else
            {
                MatchIndex = 0;
            }
            if (MatchIndex == Compound.Size)
            {
                MatchIndex = 0;
                ++*MatchCount;
            }
        }
    }
    
    *Matches = ArenaPushArray(compound_match, Arena, *MatchCount);
    u32 Index = 0;
    for (u32 CompoundReplacementIndex = 0; CompoundReplacementIndex < CompoundReplacementCount; ++CompoundReplacementIndex)
    {
        compound_replacement CompoundReplacement = CompoundReplacements[CompoundReplacementIndex];
        string Compound;
        if (ForwardReplacement)
        {
            Compound = CompoundHashTable->Elements[CompoundReplacement.FromIndex].Value;
        }
        else
        {
            Compound = CompoundHashTable->Elements[CompoundReplacement.ToIndex].Value;
        }
        
        u32 MatchIndex = 0;
        for (u32 MoleculeIndex = 0; MoleculeIndex < Molecule.Size; ++MoleculeIndex)
        {
            u8 C = Molecule.Data[MoleculeIndex];
            
            if (C == Compound.Data[MatchIndex])
            {
                ++MatchIndex;
            }
            else
            {
                MatchIndex = 0;
            }
            if (MatchIndex == Compound.Size)
            {
                MatchIndex = 0;
                compound_match CompoundMatch;
                CompoundMatch.CompoundReplacementIndex = CompoundReplacementIndex;
                CompoundMatch.MoleculeIndex = MoleculeIndex;
                (*Matches)[Index++] = CompoundMatch;
            }
        }
    }
}

i32 CountNewMolecules(memory_arena* Arena,
                      hash_table* CompoundHashTable,
                      compound_replacement* CompoundReplacements,
                      u32 CompoundReplacementCount,
                      string Molecule,
                      compound_match* Matches,
                      u32 MatchCount)
{
    hash_table MoleculeHashTable = CreateHashTable(Arena, 1024);
    for (u32 Index = 0; Index < MatchCount; ++Index)
    {
        compound_match Match = Matches[Index];
        compound_replacement CompoundReplacement = CompoundReplacements[Match.CompoundReplacementIndex];
        string FromCompound = CompoundHashTable->Elements[CompoundReplacement.FromIndex].Value;
        string ToCompound = CompoundHashTable->Elements[CompoundReplacement.ToIndex].Value;
        
        u32 NewMoleculeSize = Molecule.Size + ToCompound.Size - FromCompound.Size;
        string NewMolecule = ArenaPushString(Arena, NewMoleculeSize);
        ReplaceCompound(NewMolecule, Molecule, Match.MoleculeIndex, FromCompound, ToCompound);
        HashTableAddElementIfAbsent(&MoleculeHashTable, NewMolecule);
    }
    
    return MoleculeHashTable.Count;
}

// TODO(alex): investigate the grammar solution
i32 MinDepthToMolecule(memory_arena* Arena,
                       hash_table* CompoundHashTable,
                       compound_replacement* CompoundReplacements,
                       u32 CompoundReplacementCount,
                       string Molecule)
{
    u32 Result = 0;
    prng_state PrngState = {};
    SetSeed(&PrngState, 1337LL);
    
    string NewMolecule = ArenaPushString(Arena, Molecule);
    string CurrentMolecule = ArenaPushString(Arena, Molecule);
    
    umm PrevArenaUsed = Arena->Used;
    b32 NotDone = true;
    while (NotDone)
    {
        compound_match* CompoundBackwardMatches = 0;
        u32 CompoundBackwardMatchCount = 0;
        FindCompoundMatches(Arena, CurrentMolecule,
                            CompoundHashTable, CompoundReplacements, CompoundReplacementCount,
                            &CompoundBackwardMatches, &CompoundBackwardMatchCount, false);
        
        if (CompoundBackwardMatchCount > 0)
        {
            u32 NewMatchIndex = (u32)(NextU64(&PrngState) % CompoundBackwardMatchCount);
            compound_match Match = CompoundBackwardMatches[NewMatchIndex];
            
            compound_replacement CompoundReplacement = CompoundReplacements[Match.CompoundReplacementIndex];
            string FromCompound = CompoundHashTable->Elements[CompoundReplacement.ToIndex].Value;
            string ToCompound = CompoundHashTable->Elements[CompoundReplacement.FromIndex].Value;
            
            u32 NewMoleculeSize = CurrentMolecule.Size + ToCompound.Size - FromCompound.Size;
            NewMolecule.Size = NewMoleculeSize;
            ReplaceCompound(NewMolecule, CurrentMolecule, Match.MoleculeIndex, FromCompound, ToCompound);
            CurrentMolecule.Size = NewMoleculeSize;
            StringCopy(CurrentMolecule, NewMolecule);
            
            if (StringCompare(CurrentMolecule, "e"))
            {
                NotDone = false;
            }
            
            ++Result;
        }
        else
        {
            CurrentMolecule.Size = Molecule.Size;
            StringCopy(CurrentMolecule, Molecule);
            Result = 0;
            Arena->Used = PrevArenaUsed;
            SetSeed(&PrngState, NextU64(&PrngState));
        }
    }
    
    return Result;
}

SOLVER(2015, 19)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    u32 CompoundReplacementCount = Lines.Count - 2;
    compound_replacement* CompoundReplacements = ArenaPushArray(compound_replacement, Arena, CompoundReplacementCount);
    hash_table CompoundHashTable = CreateHashTable(Arena, 64);
    string Molecule = {};
    
    for (u32 LineIndex = 0; LineIndex < Lines.Count; ++LineIndex)
    {
        string Line = Lines.Strings[LineIndex];
        
        if (LineIndex < CompoundReplacementCount)
        {
            compound_replacement CompoundReplacement;
            
            u32 Index = StringFirstIndexOf(Line, 0, ' ');
            string FirstCompound = String(Line, 0, Index);
            CompoundReplacement.FromIndex = HashTableAddElementIfAbsent(&CompoundHashTable, FirstCompound)->ElementIndex;
            
            Index = StringFirstIndexOf(Line, Index + 1, ' ') + 1;
            string SecondCompound = String(Line, Index, Line.Size - Index);
            CompoundReplacement.ToIndex = HashTableAddElementIfAbsent(&CompoundHashTable, SecondCompound)->ElementIndex;
            
            CompoundReplacements[LineIndex] = CompoundReplacement;
            Assert(FirstCompound.Size <= SecondCompound.Size);
        }
        else if (LineIndex == Lines.Count - 1)
        {
            Molecule = Line;
        }
    }
    
    compound_match* CompoundForwardMatches = 0;
    u32 CompoundForwardMatchCount = 0;
    FindCompoundMatches(Arena, Molecule, &CompoundHashTable, CompoundReplacements, CompoundReplacementCount,
                        &CompoundForwardMatches, &CompoundForwardMatchCount, true);
    i32 NewMoleculeCount = CountNewMolecules(Arena, &CompoundHashTable, 
                                             CompoundReplacements, CompoundReplacementCount, Molecule,
                                             CompoundForwardMatches, CompoundForwardMatchCount);
    
    i32 MinDepth = MinDepthToMolecule(Arena, &CompoundHashTable,
                                      CompoundReplacements, CompoundReplacementCount, Molecule);
    solution Result = Solution(Arena, NewMoleculeCount, MinDepth);
    return Result;
}
