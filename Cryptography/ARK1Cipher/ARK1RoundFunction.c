#include "ARK1Utils.h"
#include <stdio.h>
#include <string.h>

uint64_t roundFunction(uint64_t input, uint64_t subkey)
{
	uint64_t q, r, s = input ^ subkey, output=0;
	q = AFFINE(s>>32); r=AFFINE(s);
	MIX(q,r);
	output |= r & 255;
	output |= (q & 255) << 8;

	q = AFFINE(s>>40); r=AFFINE(s>>8);
	MIX(q,r);
	output |= (r & 255) << 16;
	output |= (q & 255) << 24;

	q = AFFINE(s>>48); r=AFFINE(s>>16);
	MIX(q,r);
	output |= (r & 255) << 32;
	output |= (q & 255) << 40;

	q = AFFINE(s>>56); r=AFFINE(s>>24);
	MIX(q,r);
	output |= (r & 255) << 48;
	output |= (q & 255) << 56;

	return output;
/*
	//printf("Encrypting %016llx\n", input);
	input, subkey;
	//printf("%016llx ^ %016llx: %016llx\n", input, subkey, output);
	//printf("\nSplitting\n");
	//for(i=0;i<8;i++) printf("%02x ", bytearr[i]); printf("\n");
	//printf("\nApplying affine function\n");
	input[0] = affine(input[0]);
	input[1] = affine(input[1]);
	input[2] = affine(input[2]);
	input[3] = affine(input[3]);
	input[4] = affine(input[4]);
	input[5] = affine(input[5]);
	input[6] = affine(input[6]);
	input[7] = affine(input[7]);
	//for(i=0;i<8;i++) printf("%02x ", bytearr[i]); printf("\n");
	//printf("\nShuffling\n");
	shuffleBytes(input, 8);

	//for(i=0;i<8;i++) printf("%02x ", bytearr[i]); printf("\n");
	//printf("\nMixing\n");
	mix(input + 0, input +1);
	mix(input + 2, input + 3);
	mix(input + 4, input + 5);
	mix(input + 6, input + 7);
	//for(i=0;i<8;i++) printf("%02x", bytearr[i]); printf("\n");
*/
}

/*
int main(void)
{
	uint64_t input = 0x4aa0facb6cb33eddLL;
	uint64_t key0 = 0x0000000000000000LL;
	uint64_t key1 = 0x0000000000000000LL;
	uint64_t subkeys[101];
	generateSubkeys(key0, key1, subkeys);
	input ^= subkeys[100];
	uint64_t result = inverseRound(input, subkey[99]);
	printf("%016llx\n", result);
	uint8_t bytes[8];
	//uint8_t byte = 0x0f;
	split(bytes, input);
	//int i;
	printf("%016llx \n", input);
	uint64_t state = roundFunction(0,0xc4a6c4a6c4a6c4a6LL);
	printf("%016llx \n", state);
	printf("\nAffine\n");
	printf("%02x : %02x", byte, affine(byte));
	printf("\nInverse affine\n");
	printf("%02x : %02x", affine(byte), inverseAffine(affine(byte)));

	printf("\nShuffling\n");
	for(i=0;i<8;i++)
		printf("%02x ", bytes[i]);
	shuffleBytes(bytes,8);
	printf("\n");
	for(i=0;i<8;i++)
		printf("%02x ", bytes[i]);
	printf("\nInverse shuffle:\n");py
	inverseShuffle(bytes,8);
	for(i=0;i<8;i++)
		printf("%02x ", bytes[i]);
	printf("\nMixing\n");
	for(i=0;i<4;i++)
	{
		mix(bytes, 2*i, 2*i+1);
		printf("%02x %02x ", bytes[2*i], bytes[2*i+1]);
	}
	printf("\nInverse mixing\n");
	for(i=0;i<4;i++)
	{
		inverseMix(bytes, 2*i, 2*i+1);
		printf("%02x %02x ", bytes[2*i], bytes[2*i+1]);
	}
	printf("\nJoining\n");
	uint64_t output = join(bytes);

	printf("%016llx\n", output);

	return 0;
}
*/
