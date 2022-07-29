#ifndef CADVENT_TYPES_H
#define CADVENT_TYPES_H

#include <stdint.h>

#define Assert(Condition) if (!(Condition)) {*(int*)0 = 0;}
#define ArrayCount(Array) (sizeof(Array) / sizeof(Array[0]))

typedef int8_t i8;
typedef int16_t i16;
typedef int32_t i32;
typedef int64_t i64;

typedef uint8_t u8;
typedef uint16_t u16;
typedef uint32_t u32;
typedef uint64_t u64;

typedef size_t umm;

typedef i32 b32;

typedef float f32;
typedef double f64;

struct buffer
{
    u32 Size;
    u8* Data;
};
typedef buffer string;

#define Kilobytes(Size) ((Size) * 1024LL)
#define Megabytes(Size) (Kilobytes(Size) * 1024LL)
#define Gigabytes(Size) (Gigabytes(Size) * 1024LL)

struct memory_arena
{
    umm Size;
    umm Used;
    u8* Base;
};

#define ArenaPushStruct(Type, Arena) (Type*) ArenaPush(Arena, sizeof(Type))
#define ArenaPushArray(Type, Arena, Count) (Type*) ArenaPush(Arena, sizeof(Type) * Count)

inline void* ArenaPush(memory_arena* Arena, umm Size)
{
    Assert(Arena->Used + Size < Arena->Size);
    void* Result = Arena->Base + Arena->Used + Size;
    Arena->Used += Size;
    return Result;
}

#define PLATFORM_OPEN_AND_READ_FILE(Name) buffer Name(char* FileName)
typedef PLATFORM_OPEN_AND_READ_FILE(platform_open_and_read_file);

#define PLATFORM_CREATE_AND_WRITE_FILE(Name) void Name(char* FileName, buffer Buffer, u64 Size)
typedef PLATFORM_CREATE_AND_WRITE_FILE(platform_create_and_write_file);

struct platform
{
    platform_open_and_read_file* OpenAndReadFile;
    platform_create_and_write_file* CreateAndWriteFile;
};

struct cadvent_state
{
    memory_arena Arena;
    platform Platform;
};

#endif
