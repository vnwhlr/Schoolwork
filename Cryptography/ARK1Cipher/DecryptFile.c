#include "ARK1Cipher.h"
#include "ARK1Utils.h"
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

int removePadding(uint8_t bytes[]);
int main(int argc, char* argv[])
{
	if(argc!=4)
	{
		printf("Invalid number of arguments.\n");
		return 1;
	}
	if(strlen(argv[1])!=32)
	{
		printf("Invalid key length.\n");
		return 1;
	}
	char key0str[17], key1str[17]; 
	strncpy(key0str, argv[1], 16);
	strncpy(key1str, argv[1]+16, 16);
	key0str[16]='\0'; key1str[16]='\0';
	uint64_t key0 = toLL(key0str);
	if(errno!=0){
		printf("Invalid key format.\n");
		return 5;
	}
	uint64_t key1 = toLL(key1str);
	if(errno!=0){
		printf("Invalid key format.\n");
		return 5;
	}
	//printf("Encrypting %016llx, Key %016llx%016llx\n", plaintext, key0, key1);
	FILE *inp, *outp;
	inp = fopen(argv[2], "rb");
	if(inp==NULL){

		printf("Cannot open file for reading.\n");
		return 1;	
	}
	outp = fopen(argv[3], "wb");
	//open files
	uint64_t input;
	int count =0, removed=0;
	uint64_t output;
	int flag =1;
	uint8_t inputbytes[8], pastBytes[8];
	while(!feof(inp))
	{
		count += fread(&inputbytes,1,8,inp);
		if(count==0)
		{
			removed=removePadding(pastBytes);
			fwrite(pastBytes, 1, 8-removed, outp);
			break;
		}
		if(count<8)
		{
			printf("File size is not a multiple of 8: %d", count);
			fclose(inp);
			fclose(outp);
			return 7;
		}
		input = join(inputbytes);
		output = decrypt(input, key0, key1);
		split(inputbytes, output);
		if(!flag) //write the previous 8 bytes if not the first
			fwrite(pastBytes, 1, 8, outp);
		memcpy(pastBytes, inputbytes, 8);
		flag=0;
		count=0;
	}
fclose(inp);
fclose(outp);
return 0;	
	
}

int removePadding(uint8_t bytes[])
{
	int i=7;
	int removedCount=0;
	while(i>0)
	{
		//printf("%02x", bytes[i]);
		if(bytes[i]==0x80)
		{
			bytes[i]=-1;
			removedCount++;
			break;
		}
		bytes[i]=0;
		removedCount++;
		i--;
	}
	bytes[i]=-1;
	return removedCount;
}




