#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

int main()
{
    int InputFile = open("../data/2023/day1/input.txt", O_RDONLY);
    if (InputFile < 0 )
    {
        printf("Cannot open file\n");
        exit(-1);
    }
    int InputFileSize = lseek(InputFile, 0, SEEK_END);
    char *InputData = (char *)malloc(InputFileSize);
    lseek(InputFile, 0, SEEK_SET);
    int ReadBytes = read(InputFile, InputData, InputFileSize);
    if (ReadBytes < -1)
    {
        printf("Cannot read file\n");
        exit(-2);
    }
    int NumberFirst = -1;
    int NumberLast = 0;
    int NumberSum = 0;
    int LineStart = 0;
    int AlphaNumFirst = -1;
    int AlphaNumLast = 0;
    int AlphaNumSum = 0;
    for (int DataIndex = 0; DataIndex < InputFileSize; ++DataIndex)
    {
        char Character = InputData[DataIndex];
        if (Character == '\n')
        {
            NumberSum += 10 * NumberFirst + NumberLast;
            NumberFirst = -1;
            NumberLast = 0;

            for (int Index = LineStart; Index <= DataIndex; ++Index)
            {
                
            }
            LineStart = DataIndex + 1;
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
    printf("%d", NumberSum);
     
    return 0;
}
