using System;
using System.Net;
using System.Text;

/*
 * Class handling command-line input.
 */
namespace SatelliteTFTPClient
{
    public enum TransferMode { NETASCII, OCTET };

    class HammingTFTP
    {
        public static readonly bool DEBUG = false;
        public static readonly Int32 REQUEST_PORT = 7000;
        static void Main(string[] args)
        {
                if (args.Length != 3)
                {
                    Console.Write("Usage: |mono| HammingTFTP.exe |error|noerror| tftp-host file");
                }
                else
                {
                    bool error = false;
                    if (args[0].Equals("error", StringComparison.CurrentCultureIgnoreCase)) error = true;
                    else if (args[0].Equals("noerror", StringComparison.CurrentCultureIgnoreCase)) error = false;
                    else
                    { Console.Write("Usage: |mono| HammingTFTP.exe |error|noerror| tftp-host file"); return; }
                    IPAddress ipaddr = Dns.GetHostAddresses(args[1])[0];
                    IPEndPoint ipe = new IPEndPoint(ipaddr, REQUEST_PORT);
                    TFTPClient tftpc = new TFTPClient();
                    Console.WriteLine("Attempting transfer of file " + args[2] + " from " + ipaddr.ToString() + ".");
                    Console.WriteLine(tftpc.transfer(ipe, args[2], error, TransferMode.OCTET) ? "Transfer successful." : "Transfer failed.");
                    Console.WriteLine("Press any key to exit.");
                    Console.ReadKey();
                }
            }

      
        

 
    }
}
