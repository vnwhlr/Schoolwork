using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace FTP
{
    /* Author: Evan Wheeler
     * Represents the "U_PI" entity that executes user commands entered into UI.
     */
    class UserProtocolInterface
    {
        FTPConnection conn;
        bool Debug;

        /*Constructor also initiates control connection.*/
        public UserProtocolInterface(String host)
        {
            conn = new FTPConnection(host);
            Debug = FTPClientInterface.DEFAULT_DEBUG;
        }

        /*Toggles whether to print debug messages and passes onto connection.*/
        public void toggleDebug()
        {
            Debug=!Debug;
            conn.toggleDebug();
            Console.WriteLine("Debug mode : " + (Debug ? "on" : "off"));
        }

        /*Issues the CWD command code corresponding to the "cd" client command.*/
        public void cd(String dir)
        {
            if (conn.ControlConnected)
            {
                conn.issueCommand("CWD " + dir, true);
                pwd();
            }
            else
            {
                Console.Write("Not connected.");
            }
        }

        /*Issues the CDUP command corresponding to the "cd" client command.*/
        public void cdUp()
        {
            if (conn.ControlConnected)
            {
                conn.issueCommand("CDUP", true);
                pwd();
            }
            else
            {
                Console.Write("Not connected.");
            }
            
        }

        /*Issues the LIST command corresponding to the "dir" client command.
         * Opens a new data connection. */
        public void dir()
        {
            if (conn.ControlConnected)
            {              
                conn.issueCommand("LIST", false); //issueCommand will open the data connection of conn.
                Stream dataReader = conn.getDataStream();
                String reply = conn.getReply();
                String str;
                try
                {
                    StreamReader sr = new StreamReader(conn.getDataStream());
                    str = sr.ReadLine();
                    while (str != null)
                    {
                        Console.WriteLine(str);
                        str = sr.ReadLine();
                    }
                }
                catch (IOException) { Console.WriteLine("Transfer terminated by timeout."); }
                finally
                {
                    dataReader.Close();
                    reply = conn.getReply();                  
                }
            }
            else
            {
                Console.WriteLine("Not connected.");
            }
            
        }

        /*Toggles passive/active mode.*/
        public void passive()
        {
            if (conn.ControlConnected)
                conn.setMode(!(conn.isActive()));
            else
                Console.WriteLine("Not connected.");
        }
        
        /*Issues RETR command, opens data stream and transfers a copy of the file specified by the filepath to the local machine.*/
        public void get(String filepath)
        {
            if (conn.ControlConnected)
            {
                StreamReader replyStream = conn.getReplyStream();
                byte[] buf = new byte[1024];
                String reply;
                int read;               
                conn.issueCommand("RETR " + filepath, false);
                Stream data = conn.getDataStream();
                reply = conn.getReply();
                /*Writes data to file.*/
                FileStream fileStream = File.Create(filepath);
                BinaryWriter fileWriter = new BinaryWriter(fileStream);
                try
                {
                    read = data.Read(buf,0,1024);
                    while (read!=0)
                    {
                       fileWriter.Write(buf,0,read);
                       read = data.Read(buf, 0, 1024);
                    }
                }
                catch (IOException) { Console.WriteLine("Transfer terminated by timeout."); }
                finally {
                    data.Close();
                    reply = conn.getReply();
                    if (reply.StartsWith("2"))
                        Console.WriteLine("Transfer of file " + filepath + "successful.");
                    else
                        Console.WriteLine("Transfer failed.");
                    fileStream.Close(); }         
            }
            else
            {
                Console.WriteLine("Not connected.");
            }
        }

        public void pwd()
        {
            String str = conn.issueCommand("PWD", true);
            Console.WriteLine(str.Substring(str.IndexOf('"'), str.Length - str.IndexOf('"')));
        }

        public void setASCII(bool ascii)
        {
            if (conn.ControlConnected)
            {
                String reply;
                if (ascii)
                    reply = conn.issueCommand("TYPE A", true);
                else
                    reply = conn.issueCommand("TYPE I", true);
                if(reply.StartsWith("200")){
                    if(ascii)
                        Console.WriteLine("Data type set to ASCII.");
                    else
                        Console.WriteLine("Data type set to binary.");
                    }
                else
                    Console.WriteLine("Command failed.");
            }
            else Console.WriteLine("Not connected.");
        }

        public void closeConnection()
        {
            conn.close();
        }

        public bool changeUser(String username)
        {
            return conn.changeUser(username);
        }        
    }
}
