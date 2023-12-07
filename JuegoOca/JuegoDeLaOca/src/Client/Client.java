package Client;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {
	
	private final Socket clientSocket;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public Client(String host, int port) throws IOException {
		this.clientSocket = new Socket(host, port);
		try{
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream());
		} catch(IOException e){
			e.getCause();
		}
	}
	
	@Override 
	public void run() {

		try{
			System.out.println("Introduce tu nombre: ");
			Scanner sc = new Scanner(System.in);
			String name = sc.nextLine();
			while(name == ""){
				System.out.println("Porfavor, introduce tu nombre");
				sc.nextLine();
			}
			System.out.println("¡Hola " + name + "!");
			dos.writeUTF(name);
			
			while(true) {
				;
			}
			

		} catch (IOException e) {
			e.getCause();
			System.out.println("Ha ocurrido un error al contactar con el servidor");
		}
	}
	
	public static void main(String args[]) {
		Client c = null;
		try {
			c = new Client("localhost", 8080);
			c.run();
		} catch (IOException e) {
			System.out.print("Error al crear el cliente");
		} 
	}
}