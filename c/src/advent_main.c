#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

typedef struct buffer_
{
    char *Data;
    int Size;
} buffer;

buffer ReadFile(char *FileName)
{
    buffer Result = {};
    int InputFile = open(FileName, O_RDONLY);
    if (InputFile < 0 )
    {
        printf("Cannot open file\n");
        exit(-1);
    }
    Result.Size = lseek(InputFile, 0, SEEK_END);
    Result.Data = (char *)malloc(Result.Size);
    lseek(InputFile, 0, SEEK_SET);
    int ReadBytes = read(InputFile, Result.Data, Result.Size);
    if (ReadBytes < -1)
    {
        printf("Cannot read file\n");
        exit(-2);
    }
    return Result;
}

int SumNumberLines(buffer Buffer)
{
    int NumberFirst = -1;
    int NumberLast = 0;
    int NumberSum = 0;
    for (int DataIndex = 0; DataIndex < Buffer.Size; ++DataIndex)
    {
        char Character = Buffer.Data[DataIndex];
        if (Character == '\n')
        {
            NumberSum += 10 * NumberFirst + NumberLast;
            NumberFirst = -1;
            NumberLast = 0;
        }
        if (Character >= '0' && Character <= '9')
        {
            NumberLast = Character - '0';
            if (NumberFirst == -1)
            {
                NumberFirst = NumberLast;
            }
        }
    }
    return NumberSum;
}

int StringLength(char *String)
{
    int Result = 0;
    if (String)
    {
        while (String[Result])
        {
            ++Result;
        }
    }
    return Result;
}

int StringIndexOf(char *String, int Size, char *Contained, int ContainedSize)
{
    int MatchingStartIndex = -1;
    int ContainedIndex = 0;
    for (int Index = 0; Index < Size; ++Index)
    {
        char Character = String[Index];
        char ContainedCharacter = Contained[ContainedIndex];
        if (Character == ContainedCharacter)
        {
            if (MatchingStartIndex == -1)
            {
                MatchingStartIndex = Index;
            }
            ++ContainedIndex;
            if (ContainedIndex == ContainedSize)
            {
                return MatchingStartIndex;
            }
        }
        else
        {
            MatchingStartIndex = -1;
            ContainedIndex = 0;
        }
    }
    return -1;
}

char *AlphaNumbers[] =
{
    "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
};

#define ArrayCount(Array) ((sizeof(Array)) / (sizeof(Array[0])))

buffer StringClone(buffer String)
{
    buffer Result;
    Result.Data = (char *)malloc(String.Size);
    Result.Size = String.Size;
    for (int Index = 0; Index < String.Size; ++Index)
    {
        Result.Data[Index] = String.Data[Index];
    }
    return Result;
}

buffer ParseAndReplaceAlphaNumbers(buffer Buffer)
{
    buffer Result = StringClone(Buffer);
    int LineStart = 0;
    for (int DataIndex = 0; DataIndex < Buffer.Size; ++DataIndex)
    {
        char Character = Buffer.Data[DataIndex];
        if (Character == '\n')
        {
            for (int AlphaNumberIndex = 0; AlphaNumberIndex < ArrayCount(AlphaNumbers); ++AlphaNumberIndex)
            {
                char *AlphaNumber = AlphaNumbers[AlphaNumberIndex];
                int AlphaNumberSize = StringLength(AlphaNumber);
                for (int LineIndex = LineStart; LineIndex < DataIndex + 1; ++LineIndex)
                {
                    char *Line = Buffer.Data + LineIndex;
                    char *ResultLine = Result.Data + LineIndex;
                    int AlphaNumberLocation = StringIndexOf(Line, DataIndex + 1 - LineIndex, AlphaNumber, AlphaNumberSize);
                    if (AlphaNumberLocation > -1)
                    {
                        ResultLine[AlphaNumberLocation] = '0' + AlphaNumberIndex + 1;
                    }   
                }
            }
            
            LineStart = DataIndex + 1;
        }
    }
    return Result;
}

int main()
{
    buffer Buffer = ReadFile("../data/2023/day1/input.txt");
    int NumberSum = SumNumberLines(Buffer);
    buffer ReplacedBuffer = ParseAndReplaceAlphaNumbers(Buffer);
    int AlphaNumberSum = SumNumberLines(ReplacedBuffer);
    printf("%d %d\n", NumberSum, AlphaNumberSum);
     
    return 0;
}
