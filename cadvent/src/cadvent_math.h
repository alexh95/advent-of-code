#ifndef CADVENT_MATH_H
#define CADVENT_MATH_H

#define MIN_U32 0
#define MAX_U32 ((u32)-1)

#define MIN_I32 ((i32)0x80000000)
#define MAX_I32 ((i32)0X7FFFFFFF)

#define MIN(A, B) ((A) > (B) ? (B) : (A))
#define MAX(A, B) ((A) > (B) ? (A) : (B))

struct v2i
{
    union
    {
        struct
        {
            i32 X, Y;
        };
        i32 E[2];
    };
};

struct v3i
{
    union
    {
        struct
        {
            i32 X, Y, Z;
        };
        i32 E[3];
    };
};

inline v2i operator+(v2i A, v2i B)
{
    v2i Result =
    {
        A.X + B.X,
        A.Y + B.Y
    };
    return Result;
}

inline b32 operator==(v2i A, v2i B)
{
    b32 Result = (A.X == B.X) && (A.Y == B.Y);
    return Result;
}

#endif
