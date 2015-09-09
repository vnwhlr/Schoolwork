
using System;
using System.Net;
namespace SatelliteTFTPClient {

    /*
     *Author: Evan Wheeler
     * Class of helper functions to extract data and perform parity checks on blocks sent by Hamming TFTP server.
     */

public class HammingCode
{
    public static readonly bool EVEN_PARITY = true;

    public static readonly bool DEBUG = false;

        /*Extract the data from a (512-byte, except for the last one) block*/
        public static bool getMessage(byte[] block, int blockSize, ref byte[] message, ref int numBytes)
        {
            Int32[] blocks = new Int32[128];
            Int32 i = 0, j, data = 0, index = 0;
            byte leftover = 0;
            bool last = false;

            while ((i + 1) * 4 < blockSize)  //Skip the first four bytes (op code and block num)
            {
                if ((i + 2) * 4 < blockSize) //more than 4 bytes available in the block
                    blocks[i] = BitConverter.ToInt32(block, (i + 1) * 4);
                else //get final bytes of block
                {
                    last = true;
                    byte[] fblock = { 0, 0, 0, 0 };
                    for (int k = (i + 1) * 4; k < blockSize; k++)
                        fblock[k - ((i + 1) * 4)] = block[k];
                    blocks[i] = BitConverter.ToInt32(fblock, 0);
                }
                if (!(validateParity(ref blocks[i], EVEN_PARITY))) //return error if parity check fails to correct
                  return false;
                data = extractData(blocks[i]);
                //stick leftover bits on end of extracted data
                data <<= (i * 2) % 8;
                data |= (leftover) >> (8 - (i * 2) % 8) ;
                //invert byte order
                data = IPAddress.NetworkToHostOrder(data); 
                for (j = 3; j >= 1; j--) //write first 3 bytes of extracted data
                    message[index++] = (byte)(data >> j * 8);
                if (i%4==3) //write fourth byte 
                {
                    message[index++] = (byte)data;
                    leftover = 0;
                }
                else //record leftover data
                {
                    leftover = (byte)(data << (8-((i+1) * 2) % 8));
                    if(last) //write if final bytes
                        message[index] = leftover;                  
                }
                i++;
            }           
            numBytes = index;
            return true;
        }

        /*Performs a parity check on a 32-bit block.*/
        public static bool validateParity(ref Int32 block, bool evenParity)
        {
            int errorBit = 0;
            int i, j, k;
            bool skip, isEven;          
            /*Checks p_1 (i=0), p_2(i=1) through p_4(i=16).*/
            for (i = 0; i < 5; i++)
            {
                skip = false; 
                isEven = true;
                k = 1 << i; //Take 2^i bits.
                /*Start taking bits at bit position 2^i*/
                for (j = (1 << i) - 1; j < 31; j++)
                {
                    if (!skip && (((block & (1 << j)) >> j) == 1)) // taken bit at position j is "1"
                        isEven = !isEven;
                    k--;
                    if (k == 0)
                    {
                        skip = !skip;
                        k = (1 << i);
                    }
                }
                if (!isEven) //parity incorrect
                    errorBit += 1 << i;
            }
            if (errorBit > 0)//fix error, if found
                    block ^= 1 << (errorBit-1);           
            isEven = true;
            Int32 blockcopy = block;
            while (blockcopy != 0) //check p_32 by finding overall parity
            {
                isEven = !isEven;
                blockcopy = blockcopy & (blockcopy - 1);
            }
            if (DEBUG)
            {
                Console.WriteLine("Block: {0:X}",block);
                if (errorBit != 0)
                    Console.WriteLine("Error in bit: " + errorBit);
                else
                    Console.WriteLine("No error in first 31 bits.");
                if (isEven)
                    Console.WriteLine("No error in overall parity bit.");
                else
                    Console.WriteLine("Error in overall parity bit.");

            }
            return (evenParity == isEven);
        }

        /*Extracts the data from a 32-bit block.*/
        public static int extractData(int block)
        {
            Int32 data = 0;
            /*The general idea is to create a mask of (2^i)-1 bits, shift it 2^i bits to the right, mask the bits with the block, and move them i+1 bits to the right. It just works, okay?*/
            for (int i = 1; i <= 4; i++)
                data |= (block & (((1 << ((1 << i)-1)) - 1) << (1 << i))) >> (i+1);
            return data;
        }
        
        /*Gets op code.*/
        public static UInt16 getOpCode(byte[] block)
        {
            Int16 a = BitConverter.ToInt16(block, 0);
            Int16 b = IPAddress.NetworkToHostOrder(a);
            UInt16 c = (UInt16)b;
            return c;
        }    

        /*Gets block number.*/
        public static UInt16 getBlockNum(byte[] block)
        {
            return ((UInt16)IPAddress.NetworkToHostOrder(BitConverter.ToInt16(block, 2)));
        }

	}
}
