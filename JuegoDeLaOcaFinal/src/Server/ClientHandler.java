package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import oca.Jugador;

class ClientHandler implements Runnable {
    private DataInput dis;
    private DataOutputStream dos;
    private Socket s;
    private CountDownLatch cdl;
    private CyclicBarrier cb;
    private String nombre;

    public ClientHandler(Socket s, CountDownLatch c, CyclicBarrier cb){
        this.s= s;
        this.cdl = c;
        this.cb = cb;
        try{
            this.dis = new DataInputStream(this.s.getInputStream());
            this.dos = new DataOutputStream(this.s.getOutputStream());
        } catch(IOException e) {
            e.getCause();
        }
        
    }

    @Override
    public void run() {
        String name;
        try {
            name = dis.readUTF();
            this.nombre = name;
            Server.addJugador(name);
            System.out.println("[SERVER] Se ha conectado: " + name + ", ¡Hola!");
            
            // Incrementar el contador de clientes
            this.cdl.countDown();
            // Imprimir el número de clientes que aún se esperan
            System.out.println("Clientes restantes: " + this.cdl.getCount());
        
            dos.writeUTF("Clientes restantes: " + this.cdl.getCount());
            
            this.cb.await();    // Esperamos a que todos los clientes lleguen aqui

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        
    }
    
    public String getNombre() {
    	return this.nombre;
    }
    
    public DataOutputStream getDataOutputStream() {
		return this.dos;
    	
    }
}