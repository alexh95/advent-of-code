struct u128
{
    union
    {
        u32 Dword[4];
        u8 Byte[16];
    };
};

struct md5_context
{
    u64 Size;
    u128 Hash;
    u8 Input[64];
};

static u32 ShiftTable[] = 
{
    7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
    5,  9, 14, 20, 5,  9, 14, 20, 5,  9, 14, 20, 5,  9, 14, 20,
    4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
    6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21,
};
static u32 SineTable[] =
{
    0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501,
    0x698098d8, 0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e, 0x49b40821,
    
    0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
    0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a,
    
    0xfffa3942, 0x8771f681, 0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
    0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665,
    
    0xf4292244, 0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d, 0x85845dd1,
    0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1, 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391,
};

static u8 MD5Padding[64] = { 0x80 };

inline u32 MD5Function0(u32 A, u32 B, u32 C)
{
    u32 Result = (A & B) | (~A & C);
    return Result;
}

inline u32 MD5Function1(u32 A, u32 B, u32 C)
{
    u32 Result = (A & C) | (B & ~C);
    return Result;
}

inline u32 MD5Function2(u32 A, u32 B, u32 C)
{
    u32 Result = A ^ B ^ C;
    return Result;
}

inline u32 MD5Function3(u32 A, u32 B, u32 C)
{
    u32 Result = B ^ (A | ~C);
    return Result;
}

inline u32 MD5RotateLeft(u32 Value, u32 Shift)
{
    u32 Result = (Value << Shift) | (Value >> (32 - Shift));
    return Result;
}

md5_context MD5InitContext()
{
    md5_context Context = {};
    Context.Hash.Dword[0] = 0x67452301;
    Context.Hash.Dword[1] = 0xefcdab89;
    Context.Hash.Dword[2] = 0x98badcfe;
    Context.Hash.Dword[3] = 0x10325476;
    return Context;
}

void MD5Step(u128* Hash, u32* Input)
{
    u32 H0 = Hash->Dword[0];
    u32 H1 = Hash->Dword[1];
    u32 H2 = Hash->Dword[2];
    u32 H3 = Hash->Dword[3];
    
    for (u32 ByteIndex = 0; ByteIndex < 64; ++ByteIndex)
    {
        u32 FunctionResult;
        u32 Index;
        if (ByteIndex < 16)
        {
            FunctionResult = MD5Function0(H1, H2, H3);
            Index = ByteIndex;
        }
        else if (ByteIndex < 32)
        {
            FunctionResult = MD5Function1(H1, H2, H3);
            Index = (5 * ByteIndex + 1) % 16;
        }
        else if (ByteIndex < 48)
        {
            FunctionResult = MD5Function2(H1, H2, H3);
            Index = (3 * ByteIndex + 5) % 16;
        }
        else
        {
            FunctionResult = MD5Function3(H1, H2, H3);
            Index = (7 * ByteIndex) % 16;
        }
        
        u32 Temp = H3;
        H3 = H2;
        H2 = H1;
        H1 += MD5RotateLeft(H0 + FunctionResult + SineTable[ByteIndex] + Input[Index], ShiftTable[ByteIndex]);
        H0 = Temp;
    }
    
    Hash->Dword[0] += H0;
    Hash->Dword[1] += H1;
    Hash->Dword[2] += H2;
    Hash->Dword[3] += H3;
}

void MD5Update(md5_context* Context, u8* InputBytes, u32 InputByteCount)
{
    u32 Offset = Context->Size % 64;
    Context->Size += InputByteCount;
    u32 Input[16];
    for (u32 ByteIndex = 0; ByteIndex < InputByteCount; ++ByteIndex)
    {
        Context->Input[Offset++] = InputBytes[ByteIndex];
        if (Offset % 64 == 0)
        {
            for (u32 Index = 0; Index < 16; ++Index)
            {
                Input[Index] = (u32)(Context->Input[4 * Index + 0] << 0) 
                    | (u32)(Context->Input[4 * Index + 1] << 8) 
                    | (u32)(Context->Input[4 * Index + 2] << 16) 
                    | (u32)(Context->Input[4 * Index + 3] << 24);
            }
            MD5Step(&Context->Hash, Input);
        }
    }
}

void MD5Finalize(md5_context* Context)
{
    u32 Offset = Context->Size % 64;
    u32 PaddingLength = (Offset < 56) ? (56 - Offset) : (56 + 64) - Offset;
    
    MD5Update(Context, MD5Padding, PaddingLength);
    Context->Size -= PaddingLength;
    
    u32 Input[16];
    for (u32 Index = 0; Index < 14; ++Index)
    {
        Input[Index] = (u32)(Context->Input[4 * Index + 0] << 0) 
            | (u32)(Context->Input[4 * Index + 1] << 8) 
            | (u32)(Context->Input[4 * Index + 2] << 16) 
            | (u32)(Context->Input[4 * Index + 3] << 24);
    }
    Input[14] = (u32)((8 * Context->Size) >>  0);
    Input[15] = (u32)((8 * Context->Size) >> 32);
    
    MD5Step(&Context->Hash, Input);
}

u128 MD5(u8* Data, u32 Size)
{
    md5_context Context = MD5InitContext();
    MD5Update(&Context, Data, Size);
    MD5Finalize(&Context);
    
    u128 Result = Context.Hash;
    return Result;
}
