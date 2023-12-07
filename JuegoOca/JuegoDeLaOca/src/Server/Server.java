package Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private static int PORT = 8080;
    private static int NUM_MAX_JUGADORES = 4; // Máximo de jugadores
    private static CountDownLatch counter = new CountDownLatch(4);
    ExecutorService es = Executors.newFixedThreadPool(NUM_MAX_JUGADORES);
    public static void main(String args[]) {
        
        try (ServerSocket s = new ServerSocket(PORT)) {

            System.out.println("[SERVIDOR] Esperando a los jugadores...");

            Thread esperaClientesThread = new Thread(() -> { // Esto se puede poner con el Executor(tengo que ver como exactamente) Pero se podria hacer una clase que implemente runnable para esto
                                                           
                while (counter.getCount() > 0) { // Basicamente seria la lógica de un clientHandler 1 (clienthandler 2 seria la logica de pasarle toda la partida)
                    try {
                        Socket c = s.accept();

                        DataInputStream dis = new DataInputStream(c.getInputStream());
                        String name = dis.readUTF();
                        System.out.println("[SERVER] Se ha conectado: " + name + ", ¡Hola!");

                        // Incrementar el contador de clientes
                        counter.countDown();

                        // Imprimir el número de clientes que aún se esperan
                        System.out.println("Clientes restantes: " + counter.getCount());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

           
            esperaClientesThread.start();  // Iniciar el hilo de espera de clientes
            
            counter.await(); // Esperar a que se conecten todos los clientes

            // Realiza acciones adicionales una vez que se alcanza el número de clientes esperados

            System.out.println("Número de clientes alcanzado. ¡Empieza la partida!.");

            
            esperaClientesThread.interrupt(); // Detener el hilo de espera de clientes 
            s.close(); // Cerrar el ServerSocket

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


