enum command_logic_type
{
    CommandLogicType_Assign = 0,
    CommandLogicType_Not,
    CommandLogicType_And,
    CommandLogicType_Or,
    CommandLogicType_LShift,
    CommandLogicType_RShift,
};

enum command_logic_param_type
{
    CommandLogicParamType_None,
    CommandLogicParamType_Value,
    CommandLogicParamType_Signal,
};

struct command_logic_param
{
    command_logic_param_type Type;
    u16 ValueIn;
    u32 SignalIn;
};

struct command_logic
{
    command_logic_type Type;
    command_logic_param Param0;
    command_logic_param Param1;
    u32 SignalOut;
};

struct command_logic_context
{
    b32* SignalExists;
    command_logic** SignalOutToCommand;
    b32* SignalValueSolved;
    u16* SignalValues;
    command_logic* Commands;
};

#define LETTER_COUNT 26

#define LETTER_COUNT_2_MAX ((LETTER_COUNT + 1) * LETTER_COUNT + 1)

u16 SolveSignal(command_logic_context* Context, u32 SignalIndex);

u16 SolveValue(command_logic_context* Context, command_logic_param* Param)
{
    u16 Result = 0;
    if (Param->Type == CommandLogicParamType_Value)
    {
        Result = Param->ValueIn;
    }
    else if (Param->Type == CommandLogicParamType_Signal)
    {
        Result = SolveSignal(Context, Param->SignalIn);
    }
    else
    {
        InvalidCodePath;
    }
    return Result;
}

u16 SolveSignal(command_logic_context* Context, u32 SignalIndex)
{
    Assert(SignalIndex < LETTER_COUNT_2_MAX);
    Assert(Context->SignalExists[SignalIndex]);
    
    if (!Context->SignalValueSolved[SignalIndex])
    {
        command_logic* Command = Context->SignalOutToCommand[SignalIndex];
        u16 Value = 0;
        switch (Command->Type)
        {
            case CommandLogicType_Assign:
            {
                u16 ValueIn0 = SolveValue(Context, &Command->Param0);
                Value = ValueIn0;
            } break;
            case CommandLogicType_Not:
            {
                u16 ValueIn0 = SolveValue(Context, &Command->Param0);
                Value = ~ValueIn0;
            } break;
            case CommandLogicType_And:
            {
                u16 ValueIn0 = SolveValue(Context, &Command->Param0);
                u16 ValueIn1 = SolveValue(Context, &Command->Param1);
                Value = ValueIn0 & ValueIn1;
            } break;
            case CommandLogicType_Or:
            {
                u16 ValueIn0 = SolveValue(Context, &Command->Param0);
                u16 ValueIn1 = SolveValue(Context, &Command->Param1);
                Value = ValueIn0 | ValueIn1;
            } break;
            case CommandLogicType_LShift:
            {
                u16 ValueIn0 = SolveValue(Context, &Command->Param0);
                u16 ValueIn1 = SolveValue(Context, &Command->Param1);
                Value = ValueIn0 << ValueIn1;
            } break;
            case CommandLogicType_RShift:
            {
                u16 ValueIn0 = SolveValue(Context, &Command->Param0);
                u16 ValueIn1 = SolveValue(Context, &Command->Param1);
                Value = ValueIn0 >> ValueIn1;
            } break;
            default:
            {
                InvalidCodePath;
            }
        }
        Context->SignalValues[SignalIndex] = Value;
        Context->SignalValueSolved[SignalIndex] = true;
    }
    
    return Context->SignalValues[SignalIndex];
}

enum command_logic_token
{
    CommandLogicToken_None = 0,
    CommandLogicToken_Value,
    CommandLogicToken_Signal,
    CommandLogicToken_Operand,
    CommandLogicToken_Arrow,
};

command_logic_token CommandToken(u8* Data)
{
    command_logic_token Result = CommandLogicToken_None;
    
    u8 FirstCharacter = Data[0];
    if (FirstCharacter >= '0' && FirstCharacter <= '9')
    {
        Result = CommandLogicToken_Value;
    }
    else if (FirstCharacter >= 'a' && FirstCharacter <= 'z')
    {
        Result = CommandLogicToken_Signal;
    }
    else if (FirstCharacter >= 'A' && FirstCharacter <= 'Z')
    {
        Result = CommandLogicToken_Operand;
    }
    else if (FirstCharacter == '-')
    {
        Result = CommandLogicToken_Arrow;
    }
    else
    {
        InvalidCodePath;
    }
    
    return Result;
}

u32 CommandSignalIndex(u8* Data, u32 Count)
{
    Assert(Count <= 2);
    u32 Result = 0;
    u32 R = 1;
    for (u32 Index = 0; Index < Count; ++Index)
    {
        u8 C = Data[Index];
        Result += R * (C - 'a' + 1); 
        R *= LETTER_COUNT;
    }
    Assert(Result < LETTER_COUNT_2_MAX);
    return Result;
}

command_logic ParseCommandLogic(string Line)
{
    command_logic Command = {};
    
    i32 StartIndex = 0;
    
    b32 OperandFound = false;
    b32 ArrowFound = false;
    b32 NotDone = true;
    while (NotDone)
    {
        i32 LastIndex = StringFirstIndexOf(Line, StartIndex, ' ');
        if (LastIndex == -1)
        {
            LastIndex = Line.Size;
        }
        command_logic_token Token = CommandToken(Line.Data + StartIndex);
        
        switch (Token)
        {
            case CommandLogicToken_Value:
            {
                u16 Value = (u16)StringToI32(Line, StartIndex, LastIndex);
                if (!OperandFound)
                {
                    Command.Param0.Type = CommandLogicParamType_Value;
                    Command.Param0.ValueIn = Value;
                }
                else
                {
                    if (Command.Type == CommandLogicType_Not)
                    {
                        Command.Param0.Type = CommandLogicParamType_Value;
                        Command.Param0.ValueIn = Value;
                        Command.Param1.Type = CommandLogicParamType_None;
                    }
                    else
                    {
                        Command.Param1.Type = CommandLogicParamType_Value;
                        Command.Param1.ValueIn = Value;
                    }
                }
            } break;
            case CommandLogicToken_Signal:
            {
                u32 SignalIndex = CommandSignalIndex(Line.Data + StartIndex, LastIndex - StartIndex);
                if (ArrowFound)
                {
                    Command.SignalOut = SignalIndex;
                    NotDone = false;
                }
                else
                {
                    if (!OperandFound)
                    {
                        Command.Param0.Type = CommandLogicParamType_Signal;
                        Command.Param0.SignalIn = SignalIndex;
                    }
                    else
                    {
                        if (Command.Type == CommandLogicType_Not)
                        {
                            Command.Param0.Type = CommandLogicParamType_Signal;
                            Command.Param0.SignalIn = SignalIndex;
                            Command.Param1.Type = CommandLogicParamType_None;
                        }
                        else
                        {
                            Command.Param1.Type = CommandLogicParamType_Signal;
                            Command.Param1.SignalIn = SignalIndex;
                        }
                    }
                }
            } break;
            case CommandLogicToken_Operand:
            {
                if (StringCompare(Line.Data + StartIndex, LastIndex - StartIndex, "NOT"))
                {
                    Command.Type = CommandLogicType_Not;
                }
                else if (StringCompare(Line.Data + StartIndex, LastIndex - StartIndex, "AND"))
                {
                    Command.Type = CommandLogicType_And;
                }
                else if (StringCompare(Line.Data + StartIndex, LastIndex - StartIndex, "OR"))
                {
                    Command.Type = CommandLogicType_Or;
                }
                else if (StringCompare(Line.Data + StartIndex, LastIndex - StartIndex, "LSHIFT"))
                {
                    Command.Type = CommandLogicType_LShift;
                }
                else if (StringCompare(Line.Data + StartIndex, LastIndex - StartIndex, "RSHIFT"))
                {
                    Command.Type = CommandLogicType_RShift;
                }
                
                OperandFound = true;
            } break;
            case CommandLogicToken_Arrow:
            {
                ArrowFound = true;
                if (!OperandFound)
                {
                    Command.Type = CommandLogicType_Assign;
                    OperandFound = true;
                }
            } break;
            default:
            {
                InvalidCodePath;
            }
        }
        
        StartIndex = LastIndex + 1;
    }
    
    return Command;
}

void CommandContextInit(memory_arena* Arena, command_logic_context* Context)
{
    Context->SignalExists = ArenaPushArray(b32, Arena, LETTER_COUNT_2_MAX);
    Context->SignalOutToCommand = ArenaPushArray(command_logic*, Arena, LETTER_COUNT_2_MAX);
    Context->SignalValueSolved = ArenaPushArray(b32, Arena, LETTER_COUNT_2_MAX);
    Context->SignalValues = ArenaPushArray(u16, Arena, LETTER_COUNT_2_MAX);
    for (u32 Letter0 = 1; Letter0 <= LETTER_COUNT; ++Letter0)
    {
        for (u32 Letter1 = 0; Letter1 <= LETTER_COUNT; ++Letter1)
        {
            u32 SignalIndex = LETTER_COUNT * Letter1 + Letter0;
            Context->SignalExists[SignalIndex] = false;
            Context->SignalOutToCommand[SignalIndex] = 0;
            Context->SignalValueSolved[SignalIndex] = false;
            Context->SignalValues[SignalIndex] = 0;
        }
    }
    
}

void CommandParseLines(memory_arena* Arena, command_logic_context* Context, string_list* Lines)
{
    Context->Commands = ArenaPushArray(command_logic, Arena, Lines->Count);
    for (u32 LineIndex = 0; LineIndex < Lines->Count; ++LineIndex)
    {
        string Line = Lines->Strings[LineIndex];
        command_logic Command = ParseCommandLogic(Line);
        Context->Commands[LineIndex] = Command;
        
        if (Command.Param0.Type == CommandLogicParamType_Signal)
        {
            Context->SignalExists[Command.Param0.SignalIn] = true;
        }
        if (Command.Param1.Type == CommandLogicParamType_Signal)
        {
            Context->SignalExists[Command.Param1.SignalIn] = true;
        }
        Context->SignalExists[Command.SignalOut] = true;
        Context->SignalOutToCommand[Command.SignalOut] = Context->Commands + LineIndex;
    }
}

void CommandSolveSignals(command_logic_context* Context)
{
    for (u32 Letter0 = 1; Letter0 <= LETTER_COUNT; ++Letter0)
    {
        for (u32 Letter1 = 0; Letter1 <= LETTER_COUNT; ++Letter1)
        {
            u32 SignalIndex = LETTER_COUNT * Letter1 + Letter0;
            if (Context->SignalExists[SignalIndex])
            {
                SolveSignal(Context, SignalIndex);
            }
        }
    }
    
}

SOLVER(2015, 07)
{
    string_list Lines = StringSplit(Arena, InputBuffer, '\n');
    
    command_logic_context Context = {};
    CommandContextInit(Arena, &Context);
    CommandParseLines(Arena, &Context, &Lines);
    CommandSolveSignals(&Context);
    
    u32 SignalAIndex = CommandSignalIndex((u8*)"a", 1);
    i32 SignalAValue = Context.SignalValues[SignalAIndex];
    
    
    command_logic_context ContextOverridden = {};
    CommandContextInit(Arena, &ContextOverridden);
    CommandParseLines(Arena, &ContextOverridden, &Lines);
    
    u32 SignalBIndex = CommandSignalIndex((u8*)"b", 1);
    
    command_logic* CommandOverridden = ContextOverridden.SignalOutToCommand[SignalBIndex];
    CommandOverridden->Type = CommandLogicType_Assign;
    CommandOverridden->Param0.Type = CommandLogicParamType_Value;
    CommandOverridden->Param0.ValueIn = (u16)SignalAValue;
    CommandOverridden->Param0.SignalIn = 0;
    CommandOverridden->Param1.Type = CommandLogicParamType_None;
    CommandOverridden->Param1.ValueIn = 0;
    CommandOverridden->Param1.SignalIn = 0;
    CommandOverridden->SignalOut = SignalBIndex;
    
    CommandSolveSignals(&ContextOverridden);
    
    i32 SignalOverriddenAValue = ContextOverridden.SignalValues[SignalAIndex];
    
    solution Result = Solution(Arena, SignalAValue, SignalOverriddenAValue);
    return Result;
}
