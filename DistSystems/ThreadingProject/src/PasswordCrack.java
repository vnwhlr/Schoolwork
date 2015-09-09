import java.io.File;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The PasswordCrack class encapsulates the logic to read from a database of usernames and hashed passwords and a dictionary of common passwords,
 * then applies SHA-256 100,000 times to generate the password hash. It accomplishes this in a concurrent fashion, spawning threads for each username-hash pair and password hash computation.
 */

public class PasswordCrack {
	private static final int NUM_ITERATIONS = 100000;
	
	/**
	 * Checks for correct parameters, creates the repository, then spawns the Group 2 threads, then the Group 1 threads.
	 * @param command line arguments: dictionary path and database path
	 */
	public static void main(String args[])
	{
		if(args.length!=2)
		{
			System.err.println("Usage: java PasswordCrack <dictionaryFile> <databaseFile>");
			return;
		}
		PasswordCrack inst = new PasswordCrack();
		MonitoredMap<String, String> map = inst.new MonitoredMap<String,String>();
		String dictionaryarg = args[0], dbarg = args[1];
		
		Scanner dbReader = null, dictionaryReader = null;
		try {
			dbReader = new Scanner(new File(dbarg));
			dictionaryReader = new Scanner(new File(dictionaryarg));
		} catch (FileNotFoundException e) {
			System.out.print(e.getMessage());
			return;
		}
		Thread thread, prevthread=null;
		while(dbReader.hasNextLine())
		{
			//spawn group 2 threads		
			String[] tokens = dbReader.nextLine().split("\\s+");
			thread = inst.new Group2Thread(tokens[0], tokens[1], prevthread, map);
			/*
			 * The thread spawned before the current one is passed as an argument, so that a thread can join on the previous one and complete in the order that they are spawned.
			 */
			prevthread = thread;
			map.registerConsumer();
			thread.start();
		}
		dbReader.close();
		while(dictionaryReader.hasNextLine())
		{
			String password = dictionaryReader.nextLine();
			map.registerProducer();
			inst.new Group1Thread(password, map).start();
		}
		map.producersSpawned();
		dictionaryReader.close();

	}
	
	/**
	 * Group 2 Thread takes a username and password and reference to a repository and checks whether the password hash has been placed 
	 * in the repository, then waits for the previous thread to terminate and prints the username-password pair if it is found.
	 * @author Evan
	 */
	class Group2Thread extends Thread
	{
		String username;
		String passwordhash;
		MonitoredMap<String, String> repository;
		Thread prevThread;
		public Group2Thread(String username, String hash, Thread t, MonitoredMap<String, String> currhash)
		{
			this.username = username;
			this.passwordhash = hash;
			this.repository = currhash;
			this.prevThread=t;
		}
		
		
		/**
		 * Performs match() operations until no more entries will be placed in the map or a matching entry is found.
		 */
		@Override
		public void run() {			
			
				String password=null;
				/*Matches while the repository is still waiting for group 1 threads to finish.*/
				do{
					 password = repository.match(passwordhash);
					if(password!=null)
						break;	
				}while(!repository.done());
				if(prevThread!=null) //join on the previous thread, if applicable
				{
					try {
					prevThread.join(); //wait for previous threads to complete
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				if(password!=null) 
					System.out.format("%s %s\n", username, password);
			
		}
		
	}
	
	/**
	 * Group 1 thread computes a password hash and places it in the repository when done.
	 * */
	class Group1Thread extends Thread
	{
		String password;
		MonitoredMap<String, String> repository;
		
		public Group1Thread(String password, MonitoredMap<String, String> currhash)
		{
			this.password=password;
			this.repository=currhash;
		}	
		
		/**
		 * Initiates the password hash computation, then places the result in the repository,
		 * which will wait until all the Group 1 threads match against it.
		 * */
		@Override
		public void run(){
			byte[] digesteddata = null;
			MessageDigest md = null;
			//convert password to byte array
			try {
				digesteddata = password.getBytes ("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}		
			int i = NUM_ITERATIONS;
				while(i>0)
				{
					try {
						md = MessageDigest.getInstance ("SHA-256");
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					md.update (digesteddata);
					digesteddata = md.digest();
					i--;
				}
			StringBuilder sb = new StringBuilder();
			for(byte hex : digesteddata)
			{
				sb.append(String.format("%02x", hex));
			}
			repository.putAndWait(sb.toString(), password);
			return;
		}
		
	}
	
		/**
		 * A "repository" that fundamentally acts as an n-to-m handoff queue.
		 * It waits for results from producer threads that must register with the MonitoredMap,
		 * then waits for the consumer threads to match against the result.
		 */
		class MonitoredMap<K, V> 
		{
			
			HashMap<K, V> repository;
			boolean producersSpawned;
			AtomicInteger producers;
			AtomicInteger consumers;
			Semaphore entrySemaphore, exitSemaphore;
			
			public MonitoredMap()
			{	
				entrySemaphore = new Semaphore(0, true); //fairness ensures each consumer will match only once per putAndWait
				exitSemaphore = new Semaphore(0, false);
                repository = new HashMap<K, V>();
                producers = new AtomicInteger(0);
				consumers = new AtomicInteger(0);
			}
			
			/**
			 * Puts a result into the repository and waits for the consumer threads to check the result. Only one putAndWait() operations
			 * can occur at a time.
			 * @param Key to be matched on 
			 * @param Value associated with key
			 */
			public synchronized void putAndWait(K key, V value)
			{
				repository.put(key, value);
				producers.getAndDecrement();
				int tempconsumers = consumers.get();
				entrySemaphore.release(tempconsumers); //Allow consumers to enter match()
				try {
					//Wait for consumers to exit match()
					exitSemaphore.acquire(tempconsumers);
				} catch (InterruptedException e) {
					//Interrupted during match
					Thread.currentThread().interrupt();				
				}
				
				return;
			}
			
			/**
			 * Group 2 threads call match() to check whether the key (in this case, the password hash) is present in the map.
			 * Allows multiple match() calls simultaneously.
			 * @param Key to match on
			 * @return Value associated with key if present in repository, null otherwise
			 */
			public V match(K key) 
			{
					try {
						//Wait for a call to putAndWait()
						entrySemaphore.acquire();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						// Interrupted before match
					}
					//Consumer will stop matching on positive match
					if(repository.containsKey(key))
						consumers.getAndDecrement();
					//Signal thread has completed match()
					exitSemaphore.release();
					return repository.get(key);
			}
			
			/**
			 * Registers consumer with MonitoredMap, so it knows how many matches to allow.
			 * @return Number of consumers.
			 */
			public synchronized int registerConsumer()
			{
				consumers.incrementAndGet();
				return consumers.get();
			}
			
			/**
			 * Registers producer so the MonitoredMap knows when all producer threads are done.
			 * @return Number of producers.
			 */
			public synchronized int registerProducer()
			{
				producers.incrementAndGet();
				return producers.get();
			}
			
			/**
			 * Signal that all producer threads that put a result into the MonitoredMap have been started.
			 * @return true
			 */
			public boolean producersSpawned()
			{
				producersSpawned=true;
				return producersSpawned;
			}
					
			/**
			 * Signal that no more entries will be put into the monitored map.
			 * @return true if no more putAndWait() operations will occur, false otherwise
			 */
			public boolean done()
			{
				return producersSpawned&&producers.get()==0;
			}
			
		}
		
	}
