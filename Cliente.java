//package cliente;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.io.DataInputStream;
import javax.net.ssl.SSLSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.*;
import java.io.*;
import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.io.IOException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.*;
import javax.crypto.spec.SecretKeySpec;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;



public class Cliente implements Serializable{

 public final static int SOCKET_PORT = 9999;      
  public final static String SERVER = "10.0.0.2";    

  public final static int FILE_SIZE = 6022386;
	



		private SecretKeySpec secretKey;
	private Cipher cipher;


	public static void recibir(Socket miSocket, String namefile,Cliente ske) throws IOException, InterruptedException ,BadPaddingException,IllegalBlockSizeException,NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException{

		//System.out.println("Entra en enviar");		

		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Socket sock = null;
		try {
			TimeUnit.SECONDS.sleep(2);
			//sock = new Socket(SERVER, SOCKET_PORT);
			System.out.println("Connecting...");

		      	// receive file
		      	byte [] mybytearray  = new byte [FILE_SIZE];
		      	InputStream is = miSocket.getInputStream();
		      	fos = new FileOutputStream(namefile);
		      	bos = new BufferedOutputStream(fos);
		      	bytesRead = is.read(mybytearray,0,mybytearray.length);

		      	current = bytesRead;

		      	do {
				bytesRead =is.read(mybytearray, current, (mybytearray.length-current));
			 	if(bytesRead >= 0) current += bytesRead;
		      	} 
			while(bytesRead > -1);

		      	bos.write(mybytearray, 0 , current);
		      	bos.flush();
		      	System.out.println("File " + namefile  + " downloaded (" + current + " bytes read)");
				File dir2 = new File(namefile);
				ske.decryptFile(dir2);
				File dir3 = new File("gggggggg");
				dir3.renameTo(dir2);
				//dir2.delete();
		}catch(ArrayIndexOutOfBoundsException e1){
        		System.out.println("Error downloading the file");
			if (bos != null) bos.close();
			if (fos != null) fos.close();
		}finally {
			if (fos != null) fos.close();
		      	if (bos != null) bos.close();
		}
	}


	public static void enviar(String file, Socket miSocket,Cliente ske) throws IOException, InterruptedException,BadPaddingException,IllegalBlockSizeException,NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException{
		int SOCKET_PORT = 9999; 

		FileInputStream fis = null;
    		BufferedInputStream bis = null;
    		OutputStream os = null;
    		ServerSocket servsock = null;
    		Socket sock = null;
    		try {

        			//System.out.println("Waiting...");
        			try {
						
					File myFile2 = new File (file);
					ske.encryptFile(myFile2);
					
					//El archivo gg es el encriptado, y ue luego eliminamos
					File myFile = new File ("gggggggg");
						
					//tiempo();
					byte [] mybytearray  = new byte [(int)myFile.length()];
					try{
						fis = new FileInputStream(myFile);
						bis = new BufferedInputStream(fis);
					}catch(Exception e1){
						System.out.println("No existe el archivo o directorio");
					}
					TimeUnit.SECONDS.sleep(2);
						System.out.println("Accepted connection : " + miSocket);
						miSocket.setReceiveBufferSize(10000000);
						miSocket.setSendBufferSize(200000);
						bis.read(mybytearray,0,mybytearray.length);
						os = miSocket.getOutputStream();
						System.out.println("Sending " + file + "(" + mybytearray.length + " bytes)");
						os.write(mybytearray,0,mybytearray.length);
						os.flush();
						System.out.println("Done.");
						myFile.delete();
				}catch(IOException|NullPointerException e1){
        				System.out.println("Error sending the file");
					if (bis != null) bis.close();
					if (os != null) os.close();
				}finally {
          if (bis != null) bis.close();
					if (os != null) os.close();
					//if (sock!=null) sock.close();
        			}
      			//}
    		}
		finally {
			//if (servsock != null) servsock.close();
		}
		//servsock.close();
	}

  
  
  public static void enviarVarios(String file, List<Socket> ListSockets,Cliente ske) throws IOException, InterruptedException,BadPaddingException,IllegalBlockSizeException,NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException{
		int SOCKET_PORT = 9999; 
      
    		ServerSocket servsock = null;
    		Socket miSocket = null;
    
        //Calculamos el numero de servidores, para determinar en cuantos trozos tenemos que dividir el archivo
        int num_server=ListSockets.size();
    
        File File2 = new File (file);
    
	  	FileInputStream fis = new FileInputStream(File2); 
	  
	  		List<String> reparto = new ArrayList<String>();
	  		List<Socket> ListSocketsNoLlenos = new ArrayList<Socket>();
	  		List<Integer> division = new ArrayList<Integer>();
			Long sizeFile = File2.length();
			int trozo=(int)(sizeFile/num_server);
	  
	  
	  		for (int h = 0; h < ListSockets.size(); h++) {
								PrintWriter out = new PrintWriter(ListSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ListSockets.get(h).getInputStream()));
							Scanner scanner = new Scanner(System.in);
							out.println(trozo);

							PrintWriter out3 = new PrintWriter(ListSockets.get(h).getOutputStream(), false);
							BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(ListSockets.get(h).getInputStream())); 
							String line3= null;
							while((line3 = bufferedReader3.readLine()) != null){
								if (line3.length()==0){
								break;	
								}
								//System.out.println(line3);
								out3.println(line3);
								break;
							}
							
				ListSocketsNoLlenos.add(ListSockets.get(h));
			} 
	    
	  		num_server = ListSocketsNoLlenos.size();
			trozo=(int)(sizeFile/num_server);
	  			
			int acumulado=0;
			for (int i = 0; i < num_server; i++) {
				if(i==num_server-1) {
					division.add((int)(sizeFile - acumulado));
				}else {
					acumulado=acumulado+trozo;
					division.add(trozo );
				}
			}  

	  		byte buffer[] = new byte[division.get(division.size()-1)];
		    int count = 1;
		    while (true) {
				if(count==division.size()+1){
					break;	
				}
		      int i = fis.read(buffer, 0, division.get(count-1));
		      if (i == -1){
		        break;
			  }
		      String filename=File2.getName()+".part"+count;
				reparto.add(filename);
		      FileOutputStream fos = new FileOutputStream(filename);
		      fos.write(buffer, 0, i);
		      fos.flush();
		      fos.close();
		      count++;
		    }
	  		
	  for (int jj = 0; jj < num_server; jj++){
		  String fullpath= new String (System.getProperty("user.dir").concat("/").concat(reparto.get(jj)));
		  enviar(fullpath, ListSocketsNoLlenos.get(jj),ske);
	  }
	  
	 for (int jj = 0; jj < num_server; jj++){
		  File borra = new File(reparto.get(jj));
			borra.delete();
	  }
	  
	}

	
	public static String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
	
	public Cliente (String secret, int length, String algorithm)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] key = new byte[length];
		key = fixSecret(secret, length);
		this.secretKey = new SecretKeySpec(key, algorithm);
		this.cipher = Cipher.getInstance(algorithm);
	}
	
	private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
		if (s.length() < length) {
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) {
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");
	}

	public void encryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		//System.out.println("Encrypting file: " + f.getName());
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		this.writeToFileSend(f);
	}

	public void decryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		//System.out.println("Decrypting file: " + f.getName());
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		this.writeToFileReceive(f);
	}

	public void writeToFileSend(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream("gggggggg");
		byte[] output = this.cipher.doFinal(input);
		out.write(output);

		out.flush();
		out.close();
		in.close();
	}
	
	public void writeToFileReceive(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream("gggggggg");
		//System.out.println("valor del input: "+ input);
		byte[] output = this.cipher.doFinal(input);
		//System.out.println("despues del dofinal: "+ output);
		out.write(output);

		out.flush();
		out.close();
		in.close();
	}

	
	private static void tiempo(){
		//System.out.println("Class not found");
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		Date dateobj = new Date();
		System.out.println(df.format(dateobj));
		
	}
	
	//Funciones para generar el blockChain
	
	public static ArrayList<Cliente> blockchain = new ArrayList<Cliente>(); 
	
	public String hash;
	public String previousHash;
	private ArrayList data; //our data will be a simple message.
	private long timeStamp; //as number of milliseconds since 1/1/1970.
	
	public Cliente (ArrayList data,String previousHash ) {
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); //Making sure we do this after we set the other values.
	}
	
	public static String applySha256(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");	        
			//Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String calculateHash() {
		String calculatedhash = applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				data 
				);
		return calculatedhash;
	}
	
	
	
	public static void main(String[] args) throws InterruptedException,NoSuchAlgorithmException ,BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,InvalidKeyException {
		try {

			ArrayList<Cliente> blockchain = new ArrayList<Cliente>(); 
			
			Cliente ske;
			ske = new Cliente("!@#$MySecr3tPassw0rd", 16, "AES");

			File bb = new File("blockchain");
			
			Socket miSocket=null;
			String opcionMenu;
			//String opcionMenu= args[0];	
			List<Socket> listaSockets= new ArrayList<Socket>();
			int num_ejecucion=0;
			List<Address> listaAddr = new ArrayList<Address>();
			Address ad1=  new Address("10.0.0.2",9999);
			Address ad2=  new Address("10.0.0.3",9999);
			Address ad3=  new Address("10.0.0.4",9999);
			listaAddr.add(ad1);
			listaAddr.add(ad2);
			listaAddr.add(ad3);
			String serverToSend=null;

			
			while(true){
				DataOutputStream flujo_salida= null;
				BufferedReader bufferRead=null;
				num_ejecucion++;
				System.out.println("\n******Menu cliente******");
				System.out.println("1-Enviar un archivo al servidor");
				System.out.println("2-Recibir un archivo del servidor");
				System.out.println("3-Servidores disponibles");
				System.out.println("4-Listar los archivos compartidos");
				System.out.println("5-Eliminar un archivo");
				System.out.println("6-Salir");	
				
				if(serverToSend != null){
					if(Integer.parseInt(serverToSend)==(listaSockets.size()+1) 
					  || Integer.parseInt(serverToSend)==(listaSockets.size()+2) ){
						listaSockets.clear();
						for (int i = 0; i < listaAddr.size(); i++) {
							SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            				miSocket= sslSocketFactory.createSocket(listaAddr.get(i).getIp(),listaAddr.get(i).getPort());
							listaSockets.add(miSocket);
						}
					}else{
						listaSockets.remove(Integer.parseInt(serverToSend)-1);
						SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            			miSocket= sslSocketFactory.createSocket(listaAddr.get(Integer.parseInt(serverToSend)-1).getIp(),listaAddr.get(Integer.parseInt(serverToSend)-1).getPort());
						listaSockets.add(Integer.parseInt(serverToSend)-1,miSocket);
					}
				}else{
					for (int i = 0; i < listaAddr.size(); i++) {						
						SSLSocketFactory sslSocketFactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
            			miSocket= sslSocketFactory.createSocket(listaAddr.get(i).getIp(),listaAddr.get(i).getPort());
												
						listaSockets.add(miSocket);	
					}								
				}
				
				System.out.println("Seleccione una opcion");
				bufferRead = new BufferedReader(new InputStreamReader(System.in));
				opcionMenu = bufferRead.readLine();	

				if(opcionMenu.compareTo("1")==0){
					//System.out.println("Se va a enviar un archivo");

					//Listamos los servidores a los que se puede enviar					
					System.out.println("Seleccione a donde desea enviarlo");
					for (int h = 0; h < listaSockets.size(); h++) {
						System.out.println((h+1)+"-Enviarlo a"+listaSockets.get(h));
					}
					System.out.println(listaSockets.size()+1+"-Dividir");
					System.out.println(listaSockets.size()+2+"-Enviar a todos");
					
					bufferRead = new BufferedReader(new InputStreamReader(System.in));
					//serverToSend=args[1];
					serverToSend = bufferRead.readLine();
					
					if(Integer.parseInt(serverToSend)==(listaSockets.size()+1)){
						//Se divide entre todos los servidores
						for (int h = 0; h < listaSockets.size(); h++) {
							
							PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               				Scanner scanner = new Scanner(System.in);
                    		out.println(opcionMenu);
                    		
						} 

					}else if(Integer.parseInt(serverToSend)==(listaSockets.size()+2)){
						//Se envia a todos los server
						for (int h = 0; h < listaSockets.size(); h++) {
							PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               				Scanner scanner = new Scanner(System.in);
                    		out.println(opcionMenu);
						} 
					}else{
						//Se envia a un en concreto
						PrintWriter out = new PrintWriter(listaSockets.get(Integer.parseInt(serverToSend)-1).getOutputStream(), true);						
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(Integer.parseInt(serverToSend)-1).getInputStream()));
               			Scanner scanner = new Scanner(System.in);
                    	out.println(opcionMenu);
					}

					//Listamos los files en el folder que estamos
					File folder = new File(System.getProperty("user.dir"));
					File[] listOfFiles = folder.listFiles();

    				for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							System.out.println((i+1)+"-" + listOfFiles[i].getName());
						} 
    				}

					//Seleccionamos el archivo a enviar
					System.out.println("Escriba el archivo a enviar");
					BufferedReader bufferRead2 = new BufferedReader(new InputStreamReader(System.in));
					String nameFileToSend = bufferRead2.readLine();
					//String nameFileToSend=args[2];
					//Buscamos el full path
					
					//Concatenamos la ruta con el nombre del fichero a enviar
					String fullpath= new String (System.getProperty("user.dir").concat("/").concat(nameFileToSend));
					
					//Listamos los servidores a los que se puede enviar					
					
					if(Integer.parseInt(serverToSend)==(listaSockets.size()+1)){
						//Se divide entre todos los servidores
						ArrayList<Share> listaShareFile = new ArrayList<Share>();
						Share item1 = new Share("0",nameFileToSend,nameFileToSend);
						listaShareFile.add(item1);
						for (int h = 0; h < listaSockets.size(); h++) {

							
							PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               				Scanner scanner = new Scanner(System.in);
							out.println(nameFileToSend + ".part" + (h+1));
							
							
							//Lo añadimos a la lista de listaShareFile
							Share item2 = new Share(listaSockets.get(h).getInetAddress().toString(),nameFileToSend,nameFileToSend.concat(".part")+(h+1));
							listaShareFile.add(item2);
						}
						if(blockchain.size()==0){
							blockchain.add(new Cliente(listaShareFile,"0"));
						}else{
							blockchain.add(new Cliente(listaShareFile,blockchain.get(blockchain.size()-1).hash));
						}
						//tiempo();
            			enviarVarios(fullpath, listaSockets,ske);
												
						
						try{
							FileOutputStream  fos2 = new FileOutputStream("blockchain");
							ObjectOutputStream oos2= new ObjectOutputStream(fos2);
							oos2.writeObject(blockchain);
							oos2.close();
							fos2.close();
						}catch(IOException ioe){
            				ioe.printStackTrace();
             				return;
          				}
												

					}else if(Integer.parseInt(serverToSend)==(listaSockets.size()+2)){
						//Se envia a todos los server
						for (int h = 0; h < listaSockets.size(); h++) {
								PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
							Scanner scanner = new Scanner(System.in);
							out.println(nameFileToSend);
							File file2= new File(nameFileToSend);
							out.println(file2.length());

							PrintWriter out3 = new PrintWriter(listaSockets.get(h).getOutputStream(), false);
							BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream())); 
							String line3= null;
							while((line3 = bufferedReader3.readLine()) != null){
								if (line3.length()==0){
								break;	
								}
								//System.out.println(line3);
								out3.println(line3);
								break;
							}
							if((line3.compareTo("0"))==0){
								//el server esta lleno, no se envia
								System.out.println("El server esta lleno");
							}else{
								//tiempo();
								enviar(fullpath, listaSockets.get(h),ske);
								//tiempo();
							}
						} 
					}else{
						//Se envia a un en concreto
						flujo_salida = new DataOutputStream(listaSockets.get(Integer.parseInt(serverToSend)-1).getOutputStream());

						PrintWriter out = new PrintWriter(listaSockets.get(Integer.parseInt(serverToSend)-1).getOutputStream(), true);						
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(Integer.parseInt(serverToSend)-1).getInputStream()));
               			Scanner scanner = new Scanner(System.in);
						out.println(nameFileToSend);
						File file2= new File(nameFileToSend);
						out.println(file2.length());
						
						PrintWriter out3 = new PrintWriter(listaSockets.get(Integer.parseInt(serverToSend)-1).getOutputStream(), false);
           				BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(listaSockets.get(Integer.parseInt(serverToSend)-1).getInputStream())); 
                		String line3= null;
                		while((line3 = bufferedReader3.readLine()) != null){
							if (line3.length()==0){
							break;	
							}
                    		//System.out.println(line3);
                    		//out3.println(line3);
							break;
						}
						if((line3.compareTo("0"))==0){
							//el server esta lleno, no se envia
							System.out.println("El server esta lleno");
						}else{
							//tiempo();
							enviar(fullpath, listaSockets.get(Integer.parseInt(serverToSend)-1),ske);
							//tiempo();
						}
						
					}
					
				}
				else if(opcionMenu.compareTo("2")==0){
					System.out.println("Se va a recibir un archivo del server");

					System.out.println("Seleccione desde donde desea recivir");
					for (int h = 0; h < listaSockets.size(); h++) {
						System.out.println(h+1+"-Recibir desde "+listaSockets.get(h));
					}
					System.out.println(listaSockets.size()+1+"-Archivo compartido");
					
          			bufferRead = new BufferedReader(new InputStreamReader(System.in));
					String serverToRcvd = bufferRead.readLine();
          
         			//Enviamos el 2 de optionMenu de recibir a los servidores elegidos
					if(Integer.parseInt(serverToRcvd)==(listaSockets.size()+1)){
						System.out.println("Elige el archivo a descargar ");

						
						for (int h = 0; h < blockchain.size(); h++) {
							ArrayList<Share> aux = new ArrayList<Share>();
							aux = blockchain.get(h).data;
								System.out.println("Archivo "+aux.get(0).getFileName());
						}
						
						//Leemos el archivo a descargar desde los server
						bufferRead = new BufferedReader(new InputStreamReader(System.in));
						String fileNameDownload = bufferRead.readLine();
						
						//Lista para luego unir los trozos
						List<String> filesToJoin = new ArrayList<String>();
						
						//Buscamos en que server y partes esta el archivo a descargar
						
						System.out.println("el blocksize"+blockchain.size());
						for (int kk = 0; kk < blockchain.size(); kk++) {
							ArrayList<Share> aux = new ArrayList<Share>();
							aux = blockchain.get(kk).data;
							System.out.println("el block"+aux.get(0).getFileName());
							System.out.println("el name"+fileNameDownload);
							if(fileNameDownload.compareTo(aux.get(0).getFileName())==0){
								System.out.println("fileNameDownload.compareTo(aux.get(0).getFileName())==0");
								for (int nn = 1; nn <= listaSockets.size(); nn++) {
									System.out.println("for (int nn = 0; nn < listaSockets.size(); nn++) {");
									System.out.println("aux.get(nn).getServer()"+aux.get(nn).getServer());
									System.out.println("listaSockets.get(nn).getInetAddress().toString()"+listaSockets.get(nn-1).getInetAddress().toString());
									if(aux.get(nn).getServer().compareTo(listaSockets.get(nn-1).getInetAddress().toString())==0){
										
										PrintWriter out = new PrintWriter(listaSockets.get(nn-1).getOutputStream(), true);
										BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(nn-1).getInputStream()));
  	        							// Scanner scanner = new Scanner(System.in);           
                    					out.println("10");
                    					//System.out.println("10");
															
										PrintWriter out2 = new PrintWriter(listaSockets.get(nn-1).getOutputStream(), true);
										BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(listaSockets.get(nn-1).getInputStream()));
  	        							// Scanner scanner = new Scanner(System.in);           
                    					out2.println(aux.get(nn).getFileNamePart());
                    					System.out.println(aux.get(nn).getFileNamePart());
										
										filesToJoin.add(aux.get(nn).getFileNamePart());
										recibir(listaSockets.get(nn-1),aux.get(nn).getFileNamePart(),ske);	
										//System.out.println("el priemr lo hace bien");
									}
								}							
							}
						}
						
						// unimos los trozos despues de recibirlos de los distintos server
						OutputStream out = new FileOutputStream(fileNameDownload);
	    				byte[] buf = new byte[1024];
	    				for (String file : filesToJoin) {
	        				InputStream in = new FileInputStream(file);
	        				int b = 0;
	        				while ( (b = in.read(buf)) >= 0) {
	            				out.write(buf, 0, b);
	            				out.flush();
	        				}
	    				}	
	    				out.close();
						
						for (int kk = 0; kk < filesToJoin.size(); kk++) {	
							File borra = new File(filesToJoin.get(kk));
							borra.delete();
	    				}
						
						
					
					}else{
						//Se recibe uno archivo concreto de un solo server
						
						PrintWriter out = new PrintWriter(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getOutputStream(), true);
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getInputStream()));		       
                    	out.println(opcionMenu);

						
						//Recibimos el listado de archivos en el server
						PrintWriter out3 = new PrintWriter(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getOutputStream(), false);
           				BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getInputStream())); 
                		String line3= null;
                		while((line3 = bufferedReader3.readLine()) != null){
							if (line3.length()==0){
							break;	
							}
                    		System.out.println(line3);
                    		out3.println(line3);
							//break;
                		}
						
						System.out.println("Elige el archivo que quieres descargar \n"+line3);

						//Enviamos el nombre del archivo que queremos recibir
						//DataOutputStream flujo_salida2 = new DataOutputStream(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getOutputStream());
						System.out.println("Escriba el archivo a recibir del server");
						BufferedReader bufferRead2 = new BufferedReader(new InputStreamReader(System.in));
						String fileToRcvd = bufferRead2.readLine();
						
						PrintWriter out4 = new PrintWriter(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getOutputStream(), true);
						BufferedReader bufferedReader4 = new BufferedReader(new InputStreamReader(listaSockets.get(Integer.parseInt(serverToRcvd)-1).getInputStream()));
  	        			Scanner scanner4 = new Scanner(System.in);           
                    	out4.println(fileToRcvd);
					
						recibir(listaSockets.get(Integer.parseInt(serverToRcvd)-1),fileToRcvd,ske);
					}		
					serverToSend=serverToRcvd;
				}
				
				
				else if(opcionMenu.compareTo("3")==0){
					List<String> capacidadList = new ArrayList<String>();
					//Se envia a todos los server
					for (int h = 0; h < listaSockets.size(); h++) {							
							PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               				Scanner scanner = new Scanner(System.in);
                    		out.println(opcionMenu);
                    		//System.out.println(opcionMenu);	
												
						PrintWriter out3 = new PrintWriter(listaSockets.get(h).getOutputStream(), false);
           				BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream())); 
                		String line3= null;
                		while((line3 = bufferedReader3.readLine()) != null){
							if (line3.length()==0){
								break;	
							}
                    		//System.out.println(line3);
							String q = new String();
							String capacityString = new String();
							if(line3.length()<3){

							}else if(line3.length()<7 && line3.length()>=3){
								double capacity = (double)Double.parseDouble(line3)/1000;
								q=String.valueOf((double)capacity);
								capacityString= q.substring(0,  (q.indexOf(".")+2)).concat("kB");
							}else if(line3.length()<10 && line3.length()>=7){
								double capacity = (double)Double.parseDouble(line3)/1000000;
								q=String.valueOf((double)capacity);
								capacityString= q.substring(0,  (q.indexOf(".")+2)).concat("MB");
							}else{
								double capacity = (double)Double.parseDouble(line3)/1000000000;
								q=String.valueOf((double)capacity);
								capacityString= q.substring(0,  (q.indexOf(".")+2)).concat("GB");
							}
							capacidadList.add(capacityString);
                    		out3.println(line3);
							break;
                		}
					} 
										
					System.out.println("Servidores disponibles ");
					for (int h = 0; h < listaSockets.size(); h++) {
						System.out.println(h+1+"-"+listaSockets.get(h) +" con "+capacidadList.get(h)+" ocupados");
					}
					serverToSend=String.valueOf(listaSockets.size()+1);
					
				}else if(opcionMenu.compareTo("4")==0){
					System.out.println("Listamos los archivos compartidos");
					//List<String> listString= new ArrayList<String>();
				
					for (int h = 0; h < listaSockets.size(); h++) {							
							PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
							BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               				Scanner scanner = new Scanner(System.in);
                    		out.println(opcionMenu);
					}
					
					
					//System.out.println("el primer caracter del block es"+blockchain.size());
					for (int h = 0; h < blockchain.size(); h++) {
						ArrayList<Share> aux = new ArrayList<Share>();
						aux = blockchain.get(h).data;
						for (int ii = 1; ii < aux.size(); ii++) {
							System.out.println("Archivo "+aux.get(ii).getFileName()+" la parte "+aux.get(ii).getFileNamePart()+" esta en " +aux.get(ii).getServer());
						}
					}
									
				}else if(opcionMenu.compareTo("5")==0){
					System.out.println("Se va a eliminar un archivo...");
					
					for (int h = 0; h < listaSockets.size(); h++) {
						PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               			Scanner scanner = new Scanner(System.in);
                    	out.println(opcionMenu);
					}
					
					//Listamos los files en el folder que estamos
					File folder = new File(System.getProperty("user.dir"));
					File[] listOfFiles = folder.listFiles();

    				for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							System.out.println((i+1)+"-" + listOfFiles[i].getName());
						} 
    				}

					//Seleccionamos el archivo a enviar
					System.out.println("Escriba el archivo que quiere eliminar");
					BufferedReader bufferRead2 = new BufferedReader(new InputStreamReader(System.in));
					String nameFileToSend = bufferRead2.readLine();

					//Buscamos el full path
					
					//Concatenamos la ruta con el nombre del fichero a enviar
					String fullpath= new String (System.getProperty("user.dir").concat("/").concat(nameFileToSend));
					
					System.out.println("Se lo enviamos a todos "+nameFileToSend);
					for (int h = 0; h < listaSockets.size(); h++) {							
						PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               			Scanner scanner = new Scanner(System.in);
						out.println(nameFileToSend);
					} 
					
					//Borramos el archivo del local
					File qwe = new File(nameFileToSend);
					qwe.delete();
					
					//serverToSend=String.valueOf(listaSockets.size()+1);
					
				}else if(opcionMenu.compareTo("6")==0){
					System.out.println("Saliendo...");
					
					for (int h = 0; h < listaSockets.size(); h++) {
						PrintWriter out = new PrintWriter(listaSockets.get(h).getOutputStream(), true);						
						BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(listaSockets.get(h).getInputStream()));
               			Scanner scanner = new Scanner(System.in);
                    	out.println(opcionMenu);
							listaSockets.get(h).close();
					}
					
				}
			}
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
//				e1.printStackTrace();
			System.out.println(e1.getMessage());
		}
	}
}

class Address {
  String ip;
  int port;
	public Address(String string, int i) {
		// TODO Auto-generated constructor stub
		ip=string;
		port=i;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}

class Share implements Serializable{
  String server;
  String fileName;
  String fileNamePart;
	public Share(String string1, String string2, String string3 ) {
		// TODO Auto-generated constructor stub
		server=string1;
		fileName=string2;
		fileNamePart= string3;
	}
	public Share(String string2, String string3 ) {
		// TODO Auto-generated constructor stub
		fileName=string2;
		fileNamePart= string3;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileNamePart() {
		return fileNamePart;
	}
	public void setFileNamePart(String fileNamePart) {
		this.fileNamePart = fileNamePart;
	}
}

 

