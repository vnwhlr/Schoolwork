using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;

/**
 * Author: Evan Wheeler
 * Class handling networking, writing to file.
 */

namespace SatelliteTFTPClient
{
    public class TFTPClient
    {
        public static readonly Encoding ASCII = System.Text.Encoding.ASCII; //I'm too lazy to type out System.Text.Encoding, sue me

        public static readonly bool DEBUG = false;

        public static readonly byte[] REQUESTNOERROR = { 0, 1 };
        public static readonly byte[] REQUESTERROR = { 0, 2 };
        public static readonly byte[] DATA = { 0, 3 };
        public static readonly byte[] ACK = { 0, 4 };
        public static readonly byte[] ERROR = { 0, 5 };
        public static readonly byte[] NACK = { 0, 6 };
        public static readonly byte[] NETASCII = ASCII.GetBytes("netascii");
        public static readonly byte[] OCTET = ASCII.GetBytes("octet");        

        /*Initiates a TFTP file transfer from the server to the local machine.*/
            public bool transfer(IPEndPoint server, String filename, bool error, TransferMode mode)
            {
                if (DEBUG) { Console.WriteLine("Retrieving file " + filename + " from server at " + server.ToString() + (error ? " with errors " : " without errors ") + "using transfer mode " + (mode == TransferMode.NETASCII ? "netascii." : "octet.")); }
                IPEndPoint sender = new IPEndPoint(IPAddress.Any, 0);
                byte[] block;
                byte[] message = new byte[416];
                UInt16 blocknum;
                Int32 msgBytes = 0;
                Int32 index = 0;
                BinaryWriter fileWriter;
                UdpClient client = new UdpClient();
                fileWriter = new BinaryWriter(File.Create(filename, 512));
                requestData(client, server, filename, error, mode);
                do
                {
                    block = recieveData(client, ref sender);
                    if (DEBUG) { Console.WriteLine("Recieved datagram with length " + block.Length + " from " + sender.ToString() + "with op code " + HammingCode.getOpCode(block) + ":\n" + ASCII.GetString(block)); }

                    if (HammingCode.getOpCode(block) == 3)
                    {
                        blocknum = HammingCode.getBlockNum(block);

                        if (DEBUG) { Console.WriteLine("Recieved data packet with block number: " + blocknum); }
                        if (HammingCode.getMessage(block, block.Length, ref message, ref msgBytes))
                        {
                            fileWriter.Write(message, 0, msgBytes);
                            index += msgBytes;
                            acknowledge(client, sender, blocknum);
                        }
                        else
                        {
                            nacknowledge(client, sender, blocknum);
                            continue;
                        }
                    }
                    else if (HammingCode.getOpCode(block) == 5) //error
                    {
                        Console.WriteLine("Error encountered. Terminating file transfer.");  
                        client.Close();
                        fileWriter.Close();
                        File.Delete(filename);
                        return false;
                    }
                    else //wtf?
                    {
                        Console.WriteLine("Recieved packet with unexpected op code. Terminating file transfer.");  
                        client.Close();
                        fileWriter.Close();
                        File.Delete(filename);
                        return false;
                    }
                }
                while (block.Length > 515);           
                fileWriter.Close();
                return true;
            }

                   /*Forms and transmits a request to a server to transfer a file.*/
            private void requestData(UdpClient client, IPEndPoint server, String filename, bool error, TransferMode mode)
            {
                Int32 index = 0;
                Int32 reqLength = ((error ? REQUESTERROR.Length : REQUESTNOERROR.Length) +
                    1 +
                    filename.Length +
                    1 +
                    (mode == TransferMode.NETASCII ? NETASCII.Length : OCTET.Length));
                byte[] request = new byte[reqLength];
                if (error)
                    REQUESTERROR.CopyTo(request, index);
                else
                    REQUESTNOERROR.CopyTo(request, index);
                index = 2;
                ASCII.GetBytes(filename).CopyTo(request, index);
                index += filename.Length;
                request[index++] = (byte)0;
                if (mode == TransferMode.NETASCII)
                {
                    NETASCII.CopyTo(request, index);
                    index += NETASCII.Length;
                }
                else
                {
                    OCTET.CopyTo(request, index);
                    index += OCTET.Length;
                }
                request[index++] = (byte)0;
                if (DEBUG) { Console.WriteLine("Sending request:\n" + ASCII.GetString(request) + "\n"); }
                client.Send(request, index, server);
            }

            /*Waits to recieve a UDP packet.*/
            private byte[] recieveData(UdpClient client, ref IPEndPoint sender)
            {
                if (DEBUG) { Console.WriteLine("Waiting for datagram."); }
                byte[] data = null;
                client.Client.ReceiveTimeout = 30000;
                try
                {
                    data = client.Receive(ref sender);
                    if (DEBUG) 
                      Console.WriteLine("Recieved datagram from " + sender.ToString());
                }
                catch (SocketException) { Console.WriteLine("Request timed out. Closing."); client.Close(); throw; }
                return data;
            }


            /*Sends an ACK.*/
            private void acknowledge(UdpClient client, IPEndPoint sender, UInt16 blocknum)
            {
                if (DEBUG) Console.WriteLine("ACKing block " + blocknum + " from " + sender.ToString());
            
                byte[] ack = new byte[4];
                byte[] blocknumbytes = System.BitConverter.GetBytes(blocknum);
                ACK.CopyTo(ack, 0);
                ack[2] = blocknumbytes[1];
                ack[3] = blocknumbytes[0];
                client.Send(ack, 4, sender);
            }

            /*Sends a NACK.*/
            private void nacknowledge(UdpClient client, IPEndPoint sender, UInt16 blocknum)
            {
                if (DEBUG) Console.WriteLine("NACKing block " + blocknum + " from " + sender.ToString());
            
                byte[] nack = new byte[4];
                byte[] blocknumbytes = System.BitConverter.GetBytes(blocknum);
                NACK.CopyTo(nack, 0);
                nack[2] = blocknumbytes[1];
                nack[3] = blocknumbytes[0];
                client.Send(nack, 4, sender);
            }

	    }
}
