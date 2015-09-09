using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace FTP
{
     class FTPConnection
        {
            public static readonly Int32 RECIEVE_TIMEOUT = 1000;
            public static readonly Int32 SEND_TIMEOUT = 1000;
            public static readonly Int32 S_CMD_PORT = 21; //default FTP command port
            public static bool DEBUG_DEFAULT = true;

            private TcpClient cmdConn;
            private TcpClient dataConn;
            public bool ControlConnected; //indicates whether the control connection is active.
            private bool active; //flag indicating whether connection is active or passive.
            private String host; //stored for convenience
            
            public bool Debug; 

            /*Constructor initiates control connection.*/
            public FTPConnection(String host)
            {
                ControlConnected = false;
                this.host = host;
                active = false;
                Debug = FTPClientInterface.DEFAULT_DEBUG;
                Console.WriteLine("Initiating FTP connection to "+host);
                initiateConnection();
            }

            /*Returns the stream of the data connection.*/
            public Stream getDataStream()
            {
               return dataConn.GetStream();
            }

            /*Returns a (read-only) stream of the control connection.*/
            public StreamReader getReplyStream()
            {
                return new StreamReader(cmdConn.GetStream());
            }

            /*Returns whether data connection is set to be active or passive.*/
            public bool isActive()
            {
                return active;
            }

            /*Toggles between active and passive modes.*/
            public void setMode(bool setToActive){
                if(active!=setToActive)
                {
                    active = setToActive;
                    if(setToActive)
                        setupActiveConnection();
                    else
                        setupPassiveConnection();
                }
            }

            /*Toggles debug mode.*/
            public void toggleDebug()
            {
                Debug=!Debug;
            }

            /*Closes both data and control connections.*/
            public void close()
            {
                if (Debug) { Console.WriteLine("Closing connection."); }
                issueCommand("QUIT", false);
                cmdConn.Close();
                dataConn.Close();
            }

            /*Sets the timeouts for reading and recieving to defaults.*/
            private void setTimeouts(TcpClient client)
            {
                client.ReceiveTimeout = RECIEVE_TIMEOUT;
                client.SendTimeout = SEND_TIMEOUT;
            }

            /*Retrieves reply over the control connection, or waits until timeout.*/
            public String getReply()
            {
                StreamReader sr = new StreamReader(cmdConn.GetStream());
                StringBuilder reply = new StringBuilder();
                
                byte[] buf = new byte[1024];
                String str="";
                try
                {
                    str = sr.ReadLine(); while (str != null) { reply.Append(str + '\n'); str = sr.ReadLine(); }
                }
                catch (IOException) { /*timeout occured*/ }
                if (Debug) Console.Write("Reply:" + reply.ToString());
                return reply.ToString();
            }

            /*Issues a command. If the command is one that requires a data connection, opens it.*/
            public String issueCommand(String cmd, bool isNotDataTransfer){
                StreamWriter sw = new StreamWriter(cmdConn.GetStream());
                
                //Creates a data connection if needed.

                if (!isNotDataTransfer)
                    if (!createDataConnection(active))
                        return "";
                
                if (Debug) { Console.WriteLine("Issuing command: " + cmd); }              
                
                /*Issues command.*/
                sw.WriteLine(cmd);
                sw.Flush();

                /*Commands requiring a data connection will want to use the replies to know when to stop listening.*/
                if (isNotDataTransfer)
                    return getReply();
                else 
                    return "";
            }

            /*Initiates a control connection.*/
            private void initiateConnection()
            {
                 if (Debug) { Console.WriteLine("Setting up command connection."); }
                 try { cmdConn = new TcpClient(host, S_CMD_PORT); }
                 catch(SocketException) {Console.Write("Failed to establish command connection."); throw;}
                 setTimeouts(cmdConn);
                 StreamReader sr = new StreamReader(cmdConn.GetStream());
                 Console.WriteLine(getReply()); 
                 /*If login is successful, permit commands to be sent.*/
                 if(changeUser(null))
                    ControlConnected=true;
                 else
                    ControlConnected=false;
                 if (Debug) { Console.WriteLine("Control connection successfully established."); }
            }

            /*Creates a data connection, closing if it already exists.*/
            public bool createDataConnection(bool active)
            {
                if(dataConn!=null) dataConn.Close();
                try
                {
                    if (active)
                        setupActiveConnection();
                    else
                        setupPassiveConnection();
                }
                catch (Exception) { Console.WriteLine("Could not establish data connection."); return false; }
                return true;
            }

                /*Creates active connection.*/
                private void setupActiveConnection()
                {
                    if (Debug) { Console.WriteLine("Setting up data connection in active mode."); }
                    //Gets an open TCP port.
                    int port=0;
                    port = getOpenTCPPort();
          
                    //Begin listening on the open TCP port.
                    TcpListener responselistener = new TcpListener(getOwnIP(),port);
                    responselistener.Start();

                    //Issue a PORT command to get the server to issue a connection request to the port.
                    String cmdstr = "PORT " + ipAddressToOctets(getOwnIP()) + "," + portToOctets(port);
                    if (Debug) { Console.WriteLine("Instructing server to connect to " + getOwnIP().ToString() + " on port " + port + "."); }
                    String reply = issueCommand(cmdstr, true);
                    
                    //Wait (Block) for connection. This, um, doesn't work because according to Wireshark I'm not getting any requests from the server.
                    dataConn = responselistener.AcceptTcpClient();
                    responselistener.Stop();
                    setTimeouts(dataConn);
                }

                /*Sets up a passive data connection.*/
                public void setupPassiveConnection()
                {
                    //Issue PASV command and get information about host, port to connect to.
                    if (Debug) { Console.WriteLine("Setting up data connection in passive mode."); }
                    String replystr = issueCommand("PASV", true);
                    if (replystr.Equals(""))
                    {
                        //Can't create connection if no information from PASV reply.
                        Console.WriteLine("Error establishing outgoing passive connection.");
                        throw new IOException("No response to PASV");
                    }

                    //Extract information from PASV reply.
                    String info = Regex.Match(replystr, @"\((.*?)\)").Groups[1].Value;
                    String[] addrportinfo = Regex.Split(info, ",");        
                    //String ipAddr = addrportinfo[0] + ',' + addrportinfo[1] + ',' + addrportinfo[2] + ',' addrportinfo[3];                  
                    Int32 serverDataPort = Int32.Parse(addrportinfo[4]) * 256 + Int32.Parse(addrportinfo[5]);

                    //Initiate connection to opened port of server.
                    if (Debug) { Console.WriteLine("Sending connection request on port " + serverDataPort + "."); }
                    try { dataConn = new TcpClient(host, serverDataPort); }
                    catch (SocketException) { Console.WriteLine("Error establishing outgoing passive connection."); throw; }
                }


                
                /*Prompt for input.*/
                public bool changeUser(String un)
                {
                    if (Debug) { Console.WriteLine("Logging in."); }
                    String username, reply;
                    if (un == null)
                    { //username provided
                        Console.Write("Enter username:");
                        username = Console.ReadLine();
                    }
                    else { username = un; }
                    reply = issueCommand("USER " + username, true);
                    //if (!(reply.StartsWith("2") || reply.StartsWith("3")))
                    //   return false;
                    Console.Write("Enter password:");
                    String password = Console.ReadLine();
                    reply = issueCommand("PASS " + password, true);
                    Console.Write(reply);
                    if (reply.StartsWith("2")) //successful login
                        return true;
                    else
                        return false;
                }       

            /*Code that polls for an open TCP port.*/
            static int getOpenTCPPort()
            {
              TcpListener l = new TcpListener(IPAddress.Loopback, 0);
              l.Start();
              int port = ((IPEndPoint)l.LocalEndpoint).Port;
              l.Stop();
              return port;
            }

            /*Code converting a port number to the appropriate form for a PORT command. Didn't feel like dealing with the bits.*/
            static String portToOctets(int port)
            {
                return ((port/256).ToString() + "," + (port%256).ToString());
            }

            /*Code converting an IPAddress to a form appropriate for a PORT command.*/
            static String ipAddressToOctets(IPAddress addr){
                return addr.ToString().Replace(".",",");
            }

            /* Code that searches for the IP of this machine: copied from
             * http://stackoverflow.com/questions/1069103/how-to-get-my-own-ip-address-in-c */
            static IPAddress getOwnIP()
            {
                IPHostEntry host;
                IPAddress localIP =null;
                host = Dns.GetHostEntry(Dns.GetHostName());
                foreach (IPAddress ip in host.AddressList)
                {
                    if (ip.AddressFamily == AddressFamily.InterNetwork)
                    {
                        localIP = ip;
                    }
                }
                return localIP;
            }
    }
}
