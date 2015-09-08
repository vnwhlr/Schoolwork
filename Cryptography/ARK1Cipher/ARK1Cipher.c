#include "ARK1Utils.h"
#define AFFINE(a) (affineq[(a) & 255])

//Performs one encryption operation on a 64-bit plaintext.
uint64_t encrypt(uint64_t plaintext, uint64_t numIterations,  uint64_t* subkeys)
{
	uint64_t affineq[] = {98, 57, 16, 231, 190, 149, 108, 67, 26, 241, 200, 159, 118, 77, 36, 251, 210, 169, 128, 87, 46, 5, 220, 179, 138, 97, 56, 15, 230, 189, 148, 107, 66, 25, 240, 199, 158, 117, 76, 35, 250, 209, 168, 127, 86, 45, 4, 219, 178, 137, 96, 55, 14, 229, 188, 147, 106, 65, 24, 239, 198, 157, 116, 75, 34, 249, 208, 167, 126, 85, 44, 3, 218, 177, 136, 95, 54, 13, 228, 187, 146, 105, 64, 23, 238, 197, 156, 115, 74, 33, 248, 207, 166, 125, 84, 43, 2, 217, 176, 135, 94, 53, 12, 227, 186, 145, 104, 63, 22, 237, 196, 155, 114, 73, 32, 247, 206, 165, 124, 83, 42, 1, 216, 175, 134, 93, 52, 11, 226, 185, 144, 103, 62, 21, 236, 195, 154, 113, 72, 31, 246, 205, 164, 123, 82, 41, 0, 215, 174, 133, 92, 51, 10, 225, 184, 143, 102, 61, 20, 235, 194, 153, 112, 71, 30, 245, 204, 163, 122, 81, 40, 255, 214, 173, 132, 91, 50, 9, 224, 183, 142, 101, 60, 19, 234, 193, 152, 111, 70, 29, 244, 203, 162, 121, 80, 39, 254, 213, 172, 131, 90, 49, 8, 223, 182, 141, 100, 59, 18, 233, 192, 151, 110, 69, 28, 243, 202, 161, 120, 79, 38, 253, 212, 171, 130, 89, 48, 7, 222, 181, 140, 99, 58, 17, 232, 191, 150, 109, 68, 27, 242, 201, 160, 119, 78, 37, 252, 211, 170, 129, 88, 47, 6, 221, 180, 139};
	uint64_t i=numIterations, j;
	uint64_t q, r, output=0, state = plaintext;
	for(;i;--i)
	{
		for(j=100;j;j--)
		{
			state ^= subkeys[j]; output =0;
			q = AFFINE(state>>32); r=AFFINE(state);
			MIX(q,r);
			output |= r & 255; 
			output |= (q & 255) << 8;

			q = AFFINE(state>>40); r=AFFINE(state>>8);
			MIX(q,r);
			output |= (r & 255) << 16;
			output |= (q & 255) << 24;

			q = AFFINE(state>>48); r=AFFINE(state>>16);
			MIX(q,r);
			output |= (r & 255) << 32;
			output |= (q & 255) << 40;

			q = AFFINE(state>>56); r=AFFINE(state>>24);
			MIX(q,r);
			output |= (r & 255) << 48;
			output |= (q & 255) << 56;
			state = output;
			
		}
		state ^= subkeys[j];	
	}
	return state;
}


/*
int main(void)
{
	//uint64_t einput = 0x9944e518caec29e8LL;
	uint64_t dinput = 0x1a401f407a93ef25L;
	uint64_t key0 = 0xffffffffffffffffLL;
	uint64_t key1 = 0xffffffffffffffffLL;
	//uint64_t subkeys[101];
	//generateSubkeys(key0, key1, subkeys);
	//roundFunction(einput, subkeys[99]);
	uint64_t result = decrypt(dinput, key0, key1);
	printf("%016llx\n", result);
	return 1;
}
*/
