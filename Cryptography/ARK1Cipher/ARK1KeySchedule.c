#include "ARK1Utils.h"
#include <malloc.h>
#include <string.h>
#define ROTATER 25

/*
* ARK1 Key Schedule:
* 1: rotate key state 25 bits to right
* 2: divide into 16 bytes
* 3: permute eight most significant bytes by perfect shuffle
* 4: mix each pair of eight most significant byts together by mix function
* 5: add (mod 256) round number to LSB
* 6: rejoin bytes into 128-bit key
* 7: 64 MSB are subkey
*/

uint64_t generateNextSubkey(uint8_t* bytes, int round);

void generateSubkeys(uint8_t* key, uint64_t* subkeys)
{
	int i=0;
	for(;i<101;i++)
		subkeys[100-i] = generateNextSubkey(key,i+1);
}

uint64_t generateNextSubkey(uint8_t* bytes, int round)
{
	int i;
	rotr(bytes, ROTATER);
	for(i=0;i<8;i++)
		bytes[i] = affine(bytes[i]);
	shuffleBytes(bytes, 8);
	for(i=0;i<4;i++)
		MIX(bytes[2*i], bytes[2*i+1]);
	bytes[15] = (round + bytes[15]);
	return join(bytes);
}

/*
int main(void)
{
	printf("TESTING SUBKEY GENERATION\n");
	int i;
	
	uint64_t subkeys[101];
	uint8_t inputKey[] = {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0, 0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0};
	generateSubkeys(inputKey, subkeys);
	for(i=100;i--;)
	{
		printf("Subkey %d: ", i+1); 
		printf("%016llx\n", subkeys[i]);
	}
	return 0;
}
*/
