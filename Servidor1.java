import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.io.PrintWriter;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Servidor1 {

	public final static int SOCKET_PORT = 9999;        
	public final static int FILE_SIZE = 999999999;
	public int num_ejecucion=0;
	public final static Long maxSizeFolder=5000000000L;

	
	private static void tiempo(){
		//System.out.println("Class not found");
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		Date dateobj = new Date();
		System.out.println(df.format(dateobj));
		
	}
	
	public static void enviar(String file, Socket misocket) throws IOException{
		int SOCKET_PORT = 9999; 

		FileInputStream fis = null;
    		BufferedInputStream bis = null;
    		OutputStream os = null;
    		ServerSocket servsock = null;
    		Socket sock = null;
    		try {
      			System.out.println("Waiting...");
       			try {

          			System.out.println("Accepted connection : " + misocket);
				// send file
					System.out.println("file " +file);
				File myFile = new File (file);
				byte [] mybytearray  = new byte [(int)myFile.length()];
				fis = new FileInputStream(myFile);
				bis = new BufferedInputStream(fis);
				bis.read(mybytearray,0,mybytearray.length);
				os = misocket.getOutputStream();
				System.out.println("Sending " + file + "(" + mybytearray.length + " bytes)");
				os.write(mybytearray,0,mybytearray.length);
				os.flush();
				System.out.println("Done.");
			}catch(IOException e1){
        			System.out.println("Error sending the file");
				if (bis != null) bis.close();
				if (os != null) os.close();
        		}finally {
          			if (bis != null) bis.close();
				if (os != null) os.close();
        		}
    		}
		finally {
		}
	}





	public static void recibir(Socket misocket,String name,String size) throws IOException, InterruptedException{


		int bytesRead;
		int current = 0;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;

		try {
			System.out.println("Connecting...");

		      	// receive file
		      	byte [] mybytearray  = new byte [FILE_SIZE];
				misocket.setReceiveBufferSize(1000000000);
				misocket.setSendBufferSize(200000);
		      	InputStream is = misocket.getInputStream();
				Long tamanoT=tamano();
				int aux= comprobarTamano(Long.valueOf(size),tamanoT);
				PrintWriter out2 = new PrintWriter(misocket.getOutputStream(), true);
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(misocket.getInputStream()));
  	        	Scanner scanner2 = new Scanner(System.in);           
                out2.println(aux);
				if(aux==0){
					//el server esta lleno, no se puede almacenar
					System.out.println("el server esta lleno, no se puede almacenar");
					throw new ArrayIndexOutOfBoundsException();
				}				

		      	fos = new FileOutputStream(System.getProperty("user.dir").concat("/").concat(name));
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
		      	System.out.println("File " + name + " downloaded (" + current + " bytes read)");

		}catch(ArrayIndexOutOfBoundsException e1){
        		System.out.println("Error downloading the file");
			if (bos != null) bos.close();
			if (fos != null) fos.close();
		}finally {
			if (fos != null) fos.close();
		      	if (bos != null) bos.close();
		}
	}
	
	//En este metodo comprobamos si el archivo a recibir entra en el server
	public static Long tamano(){
			File folderaa = new File(System.getProperty("user.dir"));
				File[] listOfFilesaa = folderaa.listFiles();
				Long prim= 0L; 
		    	for (int i = 0; i < listOfFilesaa.length; i++) {
		      		if (listOfFilesaa[i].isFile()) {
						prim += (long)listOfFilesaa[i].length();
		      		}
				}
			return prim;
	}
	
	//En este metodo comprobamos si el archivo a recibir entra en el server
	public static int comprobarTamano(Long tamanoFile,Long tamano){
			if((tamanoFile+tamano)<maxSizeFolder){	
				return 1;
			}else{
				//El folder esta lleno
				return 0;
			}
	}

	public static void main(String[] args) {

		int num_ejecucion=0;
		try {

			String hh= null;
			ServerSocket servidor= null;
			while(true){
				num_ejecucion++;				
				if(num_ejecucion==1){					
					SSLServerSocketFactory sslServerSocketFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault(); 
            		servidor = sslServerSocketFactory.createServerSocket(9999);
				}

				System.out.println("\nEscuchando...");
				Socket misocket = servidor.accept();
							
				PrintWriter out = new PrintWriter(misocket.getOutputStream(), false);
           		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(misocket.getInputStream())); 
                String line= null;
				String line2= null;
                while((line = bufferedReader.readLine()) != null){
                    //System.out.println(line);
                    out.println(line);
					break;
                }
								
				DataInputStream flujoIn = new DataInputStream(misocket.getInputStream());
				String tmp=line;
			
			if(tmp.compareTo("1")==0){
				System.out.println("Se va a recibir un archivo");
				
                while((line = bufferedReader.readLine()) != null){
                    //System.out.println(line);
                    out.println(line);
					break;
                }
				while((line2 = bufferedReader.readLine()) != null){
                    //System.out.println(line2);
                    out.println(line2);
					break;
                }
							
				recibir(misocket,line,line2);
				//tiempo();
				
			}else if(tmp.compareTo("2")==0){
				System.out.println("Se va a enviar un archivo");
									
				File folder = new File(System.getProperty("user.dir"));
				File[] listOfFiles = folder.listFiles();
				String concatenado= new String(); 
		    	for (int i = 0; i < listOfFiles.length; i++) {
		      		if (listOfFiles[i].isFile()) {
						concatenado += (i+1)+"-"+listOfFiles[i].getName()+"\n";
		      		}
				}
		    				
				//Enviamos el ls del server al cliente
				PrintWriter out2 = new PrintWriter(misocket.getOutputStream(), true);
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(misocket.getInputStream()));
  	        	Scanner scanner2 = new Scanner(System.in);           
                out2.println(concatenado);
				
				System.out.println("Escuchando...");
				
				PrintWriter out3 = new PrintWriter(misocket.getOutputStream(), false);
           		BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(misocket.getInputStream())); 
                String line3= null;
                while((line3 = bufferedReader3.readLine()) != null){
                    System.out.println(line3);
                    out3.println(line3);
					break;
                }
				out2.flush();
				
				enviar(System.getProperty("user.dir").concat("/").concat(line3),misocket);
			
			}else if(tmp.compareTo("3")==0){	
				//Capacidad del server
				
				Long tamanoT=tamano();
				
				
				//se envia la capacidad al cliente
				PrintWriter out2 = new PrintWriter(misocket.getOutputStream(), true);
				BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(misocket.getInputStream()));
  	        	Scanner scanner2 = new Scanner(System.in);           
                out2.println(tamanoT);
				
			}else if(tmp.compareTo("4")==0){
								
				
			}else if(tmp.compareTo("5")==0){
				//Eliminar un archivo
				while((line = bufferedReader.readLine()) != null){
                    System.out.println(line);
                    out.println(line);
					break;
                }
				System.out.println("El archivo que se va a eliminar es"+line);
				int nombre_size = line.length();
				File folder = new File(System.getProperty("user.dir"));
				File[] listOfFiles = folder.listFiles();
				String concatenado= new String(); 
		    	for (int i = 0; i < listOfFiles.length; i++) {
		      		if (listOfFiles[i].isFile()) {
						if(listOfFiles[i].getName().length()>=nombre_size){
							if(line.compareTo(listOfFiles[i].getName().substring(0,nombre_size))==0){
							System.out.println("se elimina"+listOfFiles[i].getName());
							 listOfFiles[i].delete();
							}
						}
		      		}
				}	
				
				
			}else if(tmp.compareTo("6")==0){
				servidor.close();
				
				
			}else if(tmp.compareTo("10")==0){
				//System.out.println("Entramos en 10");
				
				PrintWriter out3 = new PrintWriter(misocket.getOutputStream(), false);
           		BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(misocket.getInputStream())); 
                String line3= null;
                while((line3 = bufferedReader3.readLine()) != null){
                    System.out.println(line3);
                    out3.println(line3);
					break;
                }
				enviar(System.getProperty("user.dir").concat("/").concat(line3),misocket);
				
			}

			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 


	}

}
