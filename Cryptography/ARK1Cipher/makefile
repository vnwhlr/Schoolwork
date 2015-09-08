all : IteratedEncrypt
CC = gcc
CFLAGS = -O3 -Wall


KeyTest: ARK1KeySchedule.c ARK1Utils.c
	$(CC) $(CFLAGS) -o KeyTest ARK1KeySchedule.c ARK1Utils.c

IteratedEncrypt : IteratedEncrypt.c ARK1KeySchedule.c ARK1Utils.c ARK1Cipher.c 
	$(CC) $(CFLAGS) -o IteratedEncrypt IteratedEncrypt.c ARK1KeySchedule.c ARK1Utils.c ARK1Cipher.c

EncryptFile : EncryptFile.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c
	$(CC) $(CFLAGS) -o EncryptFile EncryptFile.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c

DecryptFile: DecryptFile.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c
	$(CC) $(CFLAGS) -o DecryptFile DecryptFile.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c

Encrypt: Encrypt.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c
	$(CC) $(CFLAGS) -o Encrypt Encrypt.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c

Decrypt: Decrypt.o ARK1Cipher.o ARK1KeySchedule.o ARK1RoundFunction.o ARK1Utils.o
	$(CC) $(CFLAGS) -o Decrypt Decrypt.c ARK1Cipher.c ARK1KeySchedule.c ARK1RoundFunction.c ARK1Utils.c

IteratedEncryptS: IteratedEncrypt.c ARK1KeySchedule.c ARK1Utils.c 
	gcc -c -O3 -Wa,-ahl=IteratedEncrypt.s ARK1KeySchedule.c ARK1Utils.c IteratedEncrypt.c

KeySchedule: ARK1Utils.c ARK1KeySchedule.c
	 $(CC) $(CFLAGS) -o ARK1KeySchedule ARK1Utils.c ARK1KeySchedule.c

ARK1Cipher: ARK1Cipher.c ARK1RoundFunction.c ARK1Utils.h ARK1Utils.c
	gcc -c -O3 -fverbose-asm -Wa,-ahl=ARK1Cipher.s ARK1Utils.c ARK1RoundFunction.c ARK1Cipher.c

ARK1UtilsTest: ARK1Utils.c ARK1Utils.h
	gcc -c -g -Wa,-ahl=ARK1Utils.s ARK1Utils.c ARK1Utils.h

ARK1RoundFunction: ARK1RoundFunction.c ARK1Utils.h ARK1Utils.c
	gcc -S -O3 -fverbose-asm -Wa,-ahl=ARK1RoundFunction.s ARK1Utils.c ARK1RoundFunction.c

.PHONY: clean
clean: rm *.o

