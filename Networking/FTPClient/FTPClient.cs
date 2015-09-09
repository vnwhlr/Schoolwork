using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text.RegularExpressions;

/*
 * Author: Evan Wheeler
 * Code modified from Sample FTP Class by Jeremy S. Brown
 */

namespace FTP
{

    /*
    * User interface to client; handles console input.
    */
    class FTPClientInterface
    {
        public static readonly bool DEFAULT_DEBUG = false;
        // The prompt
        public const string PROMPT = "FTP> ";

        // Information to parse commands
        public static readonly string[] COMMANDS = { "ascii",
					      "binary",
					      "cd",
					      "cdup",
					      "debug",
					      "dir",
					      "get",
					      "help",
					      "passive",
                          "put",
                          "pwd",
                          "quit",
                          "user" };

        public const int ASCII = 0;
        public const int BINARY = 1;
        public const int CD = 2;
        public const int CDUP = 3;
        public const int DEBUG = 4;
        public const int DIR = 5;
        public const int GET = 6;
        public const int HELP = 7;
        public const int PASSIVE = 8;
        public const int PUT = 9;
        public const int PWD = 10;
        public const int QUIT = 11;
        public const int USER = 12;
        // Help message

        public static bool DEBUG_DEFAULT = true;

        public static readonly String[] HELP_MESSAGE = {
	"ascii      --> Set ASCII transfer type",
	"binary     --> Set binary transfer type",
	"cd <path>  --> Change the remote working directory",
	"cdup       --> Change the remote working directory to the",
        "               parent directory (i.e., cd ..)",
	"debug      --> Toggle debug mode",
	"dir        --> List the contents of the remote directory",
	"get path   --> Get a remote file",
	"help       --> Displays this text",
	"passive    --> Toggle passive/active mode",
    "put path   --> Transfer the specified file to the server",
	"pwd        --> Print the working directory on the server",
    "quit       --> Close the connection to the server and terminate",
	"user login --> Specify the user name (will prompt for password" };





        static void Main(string[] args)
        {
            //Scanner in = new Scanner( System.in );
            bool eof = false;
            String input = null;

            // Handle the command line stuff

            if (args.Length != 1)
            {
                Console.Error.WriteLine("Usage: [mono] Ftp server");
                Environment.Exit(1);
            }

            //Creates the "U_PI" entity; takes console inputs and performs connections.
            UserProtocolInterface u_pi = new UserProtocolInterface(args[0]);

            // Command line is done - accept commands
            do
            {
                try
                {
                    Console.Write(PROMPT);
                    input = Console.ReadLine();
                }
                catch (Exception)
                {
                    eof = true;
                }

                // Keep going if we have not hit end of file
                if (!eof && input.Length > 0)
                {
                    int cmd = -1;
                    string[] argv = Regex.Split(input, "\\s+");

                    // What command was entered?
                    for (int i = 0; i < COMMANDS.Length && cmd == -1; i++)
                    {
                        if (COMMANDS[i].Equals(argv[0], StringComparison.CurrentCultureIgnoreCase))
                        {
                            cmd = i;
                        }
                    }

                    // Execute the command
                    switch (cmd)
                    {
                        case ASCII:
                            u_pi.setASCII(true);
                            break;

                        case BINARY:
                            u_pi.setASCII(false);
                            break;

                        case CD:
                            String path= argv[1];
                            u_pi.cd(path);
                            break;

                        case CDUP:
                            u_pi.cdUp();
                            break;

                        case DEBUG:
                            u_pi.toggleDebug();
                            break;

                        case DIR:
                            u_pi.dir();
                            break;

                        case GET:
                            String filepath = argv[1];
                            u_pi.get(filepath);
                            break;

                        case HELP:
                            for (int i = 0; i < HELP_MESSAGE.Length; i++)
                            {
                                Console.WriteLine(HELP_MESSAGE[i]);
                            }
                            break;

                        case PASSIVE:
                            u_pi.passive();
                            break;

                        case PUT:
                            break;

                        case PWD:
                            u_pi.pwd();
                            break;

                        case QUIT:
                            eof = true;
                            u_pi.closeConnection();
                            break;

                        case USER:
                            Console.Write("Enter username: ");
                            u_pi.changeUser(Console.ReadLine());
                            break;

                        default:
                            Console.WriteLine("Invalid command");
                            break;
                    }
                }
            } while (!eof);
        }
    }

}
