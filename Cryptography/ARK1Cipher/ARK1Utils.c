#include "ARK1Utils.h"
#include <stdint.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>
#include <malloc.h>

void inline byteXOR(uint8_t* a, uint8_t* b)
{
	a[0] ^= b[0];
	a[1] ^= b[1];
	a[2] ^= b[2];
	a[3] ^= b[3];
	a[4] ^= b[4];
	a[5] ^= b[5];
	a[6] ^= b[6];
	a[7] ^= b[7];
}

//Split a 64-bit input into 8 bytes.
void inline split(uint8_t bytes[], uint64_t input)
{
	bytes[0] = (uint8_t) (input >> 56);
	bytes[1] = (uint8_t) (input >> 48);
	bytes[2] = (uint8_t) (input >> 40);
	bytes[3] = (uint8_t) (input >> 32);
	bytes[4] = (uint8_t) (input >> 24);
	bytes[5] = (uint8_t) (input >> 16);
	bytes[6] = (uint8_t) (input >> 8);
	bytes[7] = (uint8_t) (input);
}


uint64_t powq(int a, int b)
{
	if(b)
		return a * powq(a, b-1);
	return 1; 
}

//Perform the knuth shuggle on a series of bytes.
//Optimization: Find way to get around allocating memory?
void inline shuffleBytes(uint8_t bytes[], int numBytes)
{
	uint8_t temp[numBytes];
	int i;
	for(i=0;i<numBytes;i++)
		temp[i] = bytes[(i&1)*(numBytes/2)+(i/2)];  
	memcpy(bytes, temp, numBytes);
}


//Mix bytes.
//Optimization: Don't allocate stack variables
void inline mix(uint64_t x, uint64_t y, uint64_t* a, uint64_t* b)
{
	(*a) = x + y;
	(*b) = *a ^ y; 
}

//Join 8 bytes into a 64-bit integer.
//Optimization: Use union instead(?)
uint64_t inline join(uint8_t bytes[])
{
	uint64_t output = 0;
	output |= (uint64_t) bytes[0] << 56;
	output |= (uint64_t) bytes[1] << 48;
	output |= (uint64_t)bytes[2] << 40;
	output |= (uint64_t)bytes[3] << 32;
	output |= (uint64_t)bytes[4] << 24;
	output |= (uint64_t)bytes[5] << 16;
	output |= (uint64_t)bytes[6] << 8;
	output |= (uint64_t)bytes[7];
	return output;	
}

//Shifts a 128-bit field represented as two integers to the right k (mod 128) positions.
void inline rotr(uint8_t* bytes, int k)
{
	uint8_t tmp[16]; 
	int shift = k & 127; //shift mod 128
	int byteShift = shift/8;
	int bitShift = shift-byteShift*8;
	int i=0;
	for(i=0;i<16;i++)
		tmp[(i+byteShift)&(15)]=bytes[i];
	//printf("After r-shift by 25:\n");
	
	//for(i=0;i<16;i++)
	//	printf("%02x", tmp[i]);
	uint8_t lastSavedBits =0, currSavedBits;
	i=0;
	if(bitShift){
	for(;i<16;i++)
	{
		currSavedBits = tmp[i] & ((1 << bitShift) - 1);
		tmp[i] >>= bitShift;
		tmp[i] = (lastSavedBits << (8-bitShift)) | ((tmp[i]) & ((1<<(8-bitShift))-1));
		lastSavedBits = currSavedBits;

	}
	tmp[0] = (lastSavedBits << (8-bitShift)) 
		| ((tmp[0]) & ((1<<(8-bitShift))-1));
	}
	memcpy(bytes, tmp, 16);
}

//Performs affine transformation.
uint64_t inline affine(uint64_t x)
{
	return ((215*x) + 98); 
}

void hexStringToBytes(uint8_t* bytes, char* str, int num)
{
	int i=0;
	memset(bytes, 0, num/2);
	for(i=0;i<num/2;i++)
	{
			bytes[i] |= hexCharToByte(str[2*i]) << 4;
			bytes[i] |= hexCharToByte(str[2*i+1]);
	}
}

uint64_t toLL(char* str)
{
	int i;
	uint64_t result=0;
	int endIndex=strlen(str);
	for(i=endIndex-1;i>=0;--i)
	{
		result += (str[i]-48) * powq(10, endIndex-i-1);
	}
	return result;
}

int isNumber(char* str)
{
	int i;
	for(i=0;i<strlen(str);i++)
	{
		if(!isdigit(str[i]))
			return 0;
	}
	return 1;
}

int isHexNumber(char* str)
{
	int i;
	for(i=0;i<strlen(str);i++)
	{
		if(!isxdigit(str[i]))
			return 0;
	}
	return 1;
}

uint8_t inline hexCharToByte(char ch)
{
	if(ch>='0'&&ch<='9')
		return ch-0x30;	
	else if(ch>='a'&&ch<='f')
		return ch-0x57;
	else
		return 0xff;
}

/*
//Tests functions.
int main(void)
{
	int i=0;
	printf("int affine[] = {");
	for(i=0;i<255;i++)
		printf("%d, ", AFFINE(i) & 255);
	printf("%d};", AFFINE(i) & 255);
	return 0;
}
	printf("Enter input in hex:\n");
	//char input[33];
	//scanf("%s", input);
	
	uint8_t bytes[] = {0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0xa, 0xb, 0xc, 0xd, 0xe, 0xf};
	uint8_t bytesCpy[16];
	//input[33] = '\0';
	//hexStringToBytes(bytes, input, strlen(input));
	memcpy(bytesCpy, bytes, 16);
	rotr(bytesCpy,25);
	int i;
	printf("After rotation: \n");
	for(i=0;i<16;i++)
		printf("%02x ", bytesCpy[i]);
	memcpy(bytesCpy, bytes, 16);
	printf("\nShuffling\n");
	shuffleBytes(bytesCpy, 16);
	for(i=0;i<8;i++)
		printf("%02x ", bytesCpy[i]);
	memcpy(bytesCpy, bytes, 16);
	printf("\nMixing\n");
	for(i=0;i<4;i++)
	{
		mix(&bytesCpy[2*i], &bytesCpy[2*i+1]);
		printf("%02x %02x ", bytesCpy[2*i], bytesCpy[2*i+1]);
	}
	return 0;
}
*/
