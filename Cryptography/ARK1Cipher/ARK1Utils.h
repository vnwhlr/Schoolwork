#pragma once
#include <stdio.h>
#include <stdint.h>
#define MIX(a, b) (a += b, b^= a)


uint64_t toLL(char* str);
void inline rotr(uint8_t* bytes, int k);
void inline hexStringToBytes(uint8_t* bytes, char* str, int num);
void inline byteXOR(uint8_t* a, uint8_t* b);
int isNumber(char* str);
int isHexNumber(char* str);
uint8_t inline hexCharToByte(char ch);
void inline split(uint8_t bytes[], uint64_t input);
void shuffleBytes(uint8_t bytes[], int numBytes);
void inline mix(uint64_t x, uint64_t y, uint64_t* a, uint64_t* b);
uint64_t inline join(uint8_t bytes[]);
uint64_t inline affine(uint64_t x);
