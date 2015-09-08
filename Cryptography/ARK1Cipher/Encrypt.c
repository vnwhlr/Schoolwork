#include "ARK1Cipher.h"
#include <errno.h>
#include <string.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

int main(int argc, char* argv[])
{
	if(argc!=3)
	{
		printf("Invalid number of arguments.\n");
		return 1;
	}
	if(strlen(argv[1])!=32){
		printf("Invalid key length.\n");
		return 2;
	}

	if(strlen(argv[2])!=16){
		printf("Invalid ciphertext length.\n");
		return 3;
	}
	errno=0;
	uint64_t plaintext = toLL(argv[2]);
	if(errno!=0){
		printf("Invalid plaintext format.\n");
		return 4;
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
	uint64_t key1 = toLL(key1str);//sscanf(key1str, "%016llx", &key1);
	if(errno!=0){
		printf("Invalid key format.\n");
		return 5;
	}
	uint64_t ciphertext = encrypt(plaintext, key0, key1);
	printf("%016llx\n", ciphertext);
	return 0;
}
