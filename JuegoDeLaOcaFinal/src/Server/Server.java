package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import Client.Client;

public class Server {
    private static int PORT = 8080;
    private static int NUM_MAX_JUGADORES = 4; // Máximo de jugadores
    private static ArrayList<String> jugadores = new ArrayList<String>();
    private static ArrayList<ClientHandler> clientes = new ArrayList<ClientHandler>();
    
    public static void main(String args[]) {
        
        final CountDownLatch counter = new CountDownLatch(4);
        ExecutorService es = Executors.newFixedThreadPool(NUM_MAX_JUGADORES);
        final CyclicBarrier starter = new CyclicBarrier(4);

        try (ServerSocket s = new ServerSocket(PORT)) {

            System.out.println("[SERVIDOR] Esperando a los jugadores...");
                                               
            while (counter.getCount() - 1  > 0) { // Porque he tenido que poner este menos uno
            
                try {
            
                    Socket c = s.accept();
                    
                    ClientHandler cHandler = new ClientHandler(c, counter, starter);
                    clientes.add(cHandler);
                    es.submit(cHandler);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            counter.await();
            System.out.println("Número de clientes alcanzado. ¡Empieza la partida!.");
            Juego juego = new Juego(clientes);
            juego.jugar();
            s.close();
        }catch(IOException e){
            e.getCause();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (es != null) {
                es.shutdown();
            }
        }
    } 
    
    public static void addJugador(String n) {
    	Server.jugadores.add(n);
    }
}


/*class ClientHandler implements Runnable {
    private DataInput dis;
    private DataOutputStream dos;
    private Socket s;
    private CountDownLatch cdl;
    private CyclicBarrier cb;

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
    
}*/