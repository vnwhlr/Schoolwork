#include "ARK1Cipher.h"
#include "ARK1Utils.h"
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>




int main(int argc, char* argv[])
{
	if(argc!=4){
		printf("Invalid number of arguments.\n");
		return 1;	
	}//error
	if(strlen(argv[1])<32){
		printf("Key length is invalid.\n");
		return 2;	
	}//error
	char key0str[17], key1str[17]; 
	strncpy(key0str, argv[1], 16);
	strncpy(key1str, argv[1]+16, 16);
	key0str[16]='\0'; key1str[16]='\0';
	uint64_t key0 = toLL(key0str);
	if(errno!=0){
		printf("Invalid key format.\n");
		return 5;
	}
	uint64_t key1 = toLL(key1str);//sscanf(key1str, "%016llx", &key1);
	if(errno!=0){
		printf("Invalid key format.\n");
		return 5;
	}

	//printf("Encrypting %016llx, Key %016llx%016llx\n", plaintext, key0, key1);
	FILE *inp, *outp;
	inp = fopen(argv[2], "r");
	if(inp==NULL){
		printf("Cannot open file for reading.\n");
		return 1;	
	}//error
	outp = fopen(argv[3], "wb");	
	//open files
	uint64_t input;
	int i;
	int padding=0;
	uint64_t output;
	uint8_t inputbytes[8];
	
	while(!feof(inp))
	{
		for(i=0;i<8;i++)
		{
			if(fscanf(inp, "%c", &inputbytes[i])<0)
				break;
		}
		if(i<8)
		{
			padding=1;
			inputbytes[i++]=0x80;
			while(i<8)
				inputbytes[i++]=0x0;
		}
		input = join(inputbytes);
		output = encrypt(input, key0, key1);
		//printf("%016llx\n", output);
		uint8_t outputBytes[8];
		split(outputBytes, output);
		//for(i=0;i<8;i++){printf("%02x", outputBytes[i]);
		fwrite(outputBytes, sizeof(uint8_t), 8, outp);
		
		
		/*
		for(i=0;i<4;i++)
		{
			fwrite(&outputBytes[2*i+1],1,1,outp);
			fwrite(&outputBytes[2*i],1,1, outp);
		}
		*/
		if(padding)
			break;
	}
fclose(inp);
fclose(outp);	
return 1;	
}
