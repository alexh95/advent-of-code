#include <Windows.h>
#include "cadvent_types.h"
#include "cadvent_math.h"
#include "cadvent_string.cpp"
#include "cadvent_table.h"
#include "md5.cpp"
#include "cadvent_json.h"
#include "cadvent_solution.cpp"

PLATFORM_OPEN_AND_READ_FILE(WinOpenAndReadFile)
{
    HANDLE FileHandle = CreateFileA(FileName, GENERIC_READ, 0, 0, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0);
    if (FileHandle == INVALID_HANDLE_VALUE)
    {
        Assert(0);
    }
    BY_HANDLE_FILE_INFORMATION FileInformation = {};
    if (GetFileInformationByHandle(FileHandle, &FileInformation) == 0)
    {
        Assert(0);
    }
    u32 FileSize = (u32)FileInformation.nFileSizeLow;
    u8* Memory = (u8*)VirtualAlloc(0, FileSize, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    ReadFile(FileHandle, Memory, FileSize, 0, 0);
    
    CloseHandle(FileHandle);
    
    buffer Result;
    Result.Size = FileSize;
    
    Result.Data = Memory;
    return Result;
}

PLATFORM_CREATE_AND_WRITE_FILE(WinCreateAndWriteFile)
{
    HANDLE FileHandle = CreateFileA(FileName, GENERIC_WRITE, 0, 0, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, 0);
    if (FileHandle == INVALID_HANDLE_VALUE)
    {
        Assert(0);
    }
    
    WriteFile(FileHandle, Buffer.Data, (u32)Size, 0, 0);
    CloseHandle(FileHandle);
}

int WinMain(HINSTANCE Instance, HINSTANCE PrevInstance, LPSTR CmdLine, int ShowCmd)
{
    cadvent_state State = {};
    
    State.Arena.Size = Megabytes(512);
    State.Arena.Base = (u8*)VirtualAlloc(0, State.Arena.Size, MEM_COMMIT | MEM_RESERVE, PAGE_READWRITE);
    
    State.Platform.OpenAndReadFile = WinOpenAndReadFile;
    State.Platform.CreateAndWriteFile = WinCreateAndWriteFile;
    
    u32 CurrentDay = 16;
    Solve(&State, 2015, CurrentDay); 
    
    /*for (u32 Day = 1; Day <= CurrentDay; ++Day)
    {
        Solve(&State, 2015, Day);
    }*/
    
    return 0;
}
