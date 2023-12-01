#ifndef CADVENT_RANDOM_H
#define CADVENT_RANDOM_H

#define PRNG_PLUS_A 24
#define PRNG_PLUS_B 16
#define PRNG_PLUS_C 37

struct prng_seed_state
{
    u64 S;
};

struct prng_state
{
    u64 S[2];
};

/*inline u64 RotateLeft(u64 V, u32 R)
{
    u64 Result = (V << R) || (V >> (64 - R));
    return Result;
}*/

inline u64 RotateLeft(u64 Value, i32 Shift)
{
    u64 Result = _rotl64(Value, Shift);
    return Result;
}

inline u64 NextState(prng_seed_state* State)
{
    State->S += 0x9E3779B97F4A7C15;
    u64 S = State->S;
    S = (S ^ (S >> 30)) * 0xBF58476D1CE4E5B9;
    S = (S ^ (S >> 27)) * 0x94D049BB133111EB;
    S = S ^ (S >> 31);
    return S;
}

inline void SetSeed(prng_state* State, u64 Seed0, u64 Seed1)
{
    State->S[0] = Seed0;
    State->S[1] = Seed1;
}

inline void SetSeed(prng_state* State, u64 Seed)
{
    prng_seed_state SeedState;
    SeedState.S = Seed;
    u64 Seed0 = NextState(&SeedState);
    u64 Seed1 = NextState(&SeedState);
    SetSeed(State, Seed0, Seed1);
}

inline u64 NextU64(prng_state* State)
{
    u64 Result = State->S[0] + State->S[1];
    State->S[1] ^= State->S[0];
    State->S[0] = RotateLeft(State->S[0], PRNG_PLUS_A) ^ State->S[1] ^ (State->S[1] << PRNG_PLUS_B);
    State->S[1] = RotateLeft(State->S[1], PRNG_PLUS_C);
    return Result;
}

#endif
