#include "ARK1Cipher.h"
#include "ARK1KeySchedule.h"
#include "ARK1Utils.h"
#include <string.h>
#include <stdio.h>

int main(int argc, char* argv[])
{
	if(argc!=5)
	{
		printf("Invalid number of arguments.\n");
		return 1;
	}
	if(strlen(argv[1])!=16){
		printf("Invalid upper key length.\n");
		return 2;
	}
	if(strlen(argv[2])!=16){
		printf("Invalid lower key length.\n");
		return 3;
	}
	if(strlen(argv[3])!=16){
		printf("Invalid plaintext length.\n");
		return 4;
	}
	if(!isHexNumber(argv[1])||!isHexNumber(argv[2])){
		printf("Invalid key format.\n");
		return 5;
	}
	if(!isHexNumber(argv[3]))
	{
		printf("Invalid plaintext format.\n");
		return 6;
	}
	if(!isNumber(argv[4]))
	{
		printf("Invalid input for number of iterations.");
		return 7;
	}
	uint8_t keyBytes[16];
	uint64_t subkeys[101];
	uint8_t plaintextBytes[8];
	hexStringToBytes(keyBytes, argv[1], strlen(argv[1]));
	hexStringToBytes(keyBytes+8, argv[2], strlen(argv[2]));
	hexStringToBytes(plaintextBytes, argv[3], strlen(argv[3]));
	
	uint64_t numIterations = toLL(argv[4]);
	generateSubkeys(keyBytes, subkeys);
	printf("%016llx\n", encrypt(join(plaintextBytes), numIterations, subkeys));
	return 0;
}


