#ifndef CADVENT_MATH_H
#define CADVENT_MATH_H

#define MIN(A, B) ((A) > (B) ? (B) : (A))
#define MAX(A, B) ((A) > (B) ? (A) : (B))

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

#endif
