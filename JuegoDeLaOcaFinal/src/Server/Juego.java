package Server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import oca.Jugador;

public class Juego {

	private static HashMap<Integer, Integer> ocas;
	private static HashMap<Integer, Integer> puentes;
	private static HashMap<Integer, Integer> dados;
	private static ArrayList<ClientHandler> clientes = new ArrayList<ClientHandler>();
	private int turno = 0;
	
	public Juego(ArrayList<ClientHandler> clientes) {
		Juego.clientes = clientes;
	}
	
	public void jugar() throws InterruptedException, IOException{
		//System.out.println("Introduce el numero de jugadores: ");
			boolean juegoActivo = true;
			ocas = getOcas();
			puentes = getPuentes();
			dados = getDados();

			ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
			for(ClientHandler c: clientes) {
				jugadores.add(new Jugador(c.getNombre()));
			}

			do {
				turno++;
				enviarMensaje("\nTURNO " + turno);
				enviarMensaje("-----------------------------");
				for(Jugador j: jugadores) {
					if(j.isActivo()) {
						enviarMensaje("Es el turno de " + j.getNombre());
						if(j.getTurnosRestantes()==0) {
							int n = tirarDado();
							enviarMensaje("Ha sacado un " + n);
							int nuevaPosicion = n + j.getCasilla();
							enviarMensaje(j.getNombre() + " ha caido en la casilla " + nuevaPosicion + "\n");
							
							int casilla = calcularCasillaDestino(nuevaPosicion, j);
							if(casilla == -1) {
								juegoActivo = false;
							}else {
								j.setCasilla(casilla);
							}
							Thread.sleep(1000);
						}
						else {
							enviarMensaje(j.getNombre() + " tiene que esperar " + j.getTurnosRestantes() +"\n");
							j.setTurnosRestantes(j.getTurnosRestantes()-1);
						}
					}
				}

			}while(juegoActivo);
	}

	static int calcularCasillaDestino(int nuevaPosicion, Jugador j) throws IOException {
		Integer casillaDestino = ocas.get(nuevaPosicion);
		Integer casillaDestino2 = puentes.get(nuevaPosicion);
		Integer casillaDestino3 = dados.get(nuevaPosicion);

		if(casillaDestino != null) {
			enviarMensaje("De oca a oca y tiro porque me toca. La oca te ha llevado a la casilla " + casillaDestino);
			int n = tirarDado();
			return calcularCasillaDestino(casillaDestino + n, j);
		}else if(nuevaPosicion == 19) {
			enviarMensaje("Has caido en la posada. Vas a pasar aqui otro turno");
			j.setTurnosRestantes(1);
		}else if(nuevaPosicion == 31) {
			enviarMensaje("Has caido en el pozo de bronce, tendrás que esperar 3 turnos");
			j.setTurnosRestantes(3);
		}else if(nuevaPosicion == 52) {
			enviarMensaje("Has caido en la carcel, algo habras hecho. Tendras que pasar aqui 2 turnos");
			j.setTurnosRestantes(2);
		}else if(casillaDestino2 != null ) {
			enviarMensaje("De puente a puente y tiro porque me lleva la corriente. La corriente te ha llevado a la casilla " + casillaDestino2);
			int n = tirarDado();
			return calcularCasillaDestino(casillaDestino2 + n, j);
		}else if(casillaDestino3 != null) {
			enviarMensaje("De dado a dado y tiro porque me ha tocado. El dado de la suerte te ha llevado a la casilla " + casillaDestino3);
			int n = tirarDado();
			return calcularCasillaDestino(casillaDestino3 + n, j);
		}else if(nuevaPosicion == 58) {
			enviarMensaje("El jugador " + j.getNombre() + " ha fallecido");
			j.setActivo(false);
		}else if(nuevaPosicion == 63) {
			enviarMensaje("El jugador " + j.getNombre() + " ha ganado");
			return -1;
		}else if (nuevaPosicion > 63) {
			int diff = nuevaPosicion - 63;
			return calcularCasillaDestino(63 - diff, j); //De esta forma necesitaremos sacar el número exacto para ganar
		}

		return nuevaPosicion;
	}


	//Método que nos construirá un HashMap con las 
	//posiciones de las ocas
	static HashMap<Integer, Integer> getOcas(){
		HashMap<Integer, Integer> ocas = new HashMap<Integer,Integer>();
		ocas.put(5, 9);
		ocas.put(9, 14);
		ocas.put(14, 18);
		ocas.put(18, 23);
		ocas.put(23, 27);
		ocas.put(27, 32);
		ocas.put(32, 36);
		ocas.put(36, 41);
		ocas.put(41, 45);
		ocas.put(45, 50);
		ocas.put(50, 54);
		ocas.put(54, 59);
		return ocas;
	}

	//Método que nos construirá un HashMap con las
	//posciones de los puentes
	static HashMap<Integer, Integer> getPuentes(){
		HashMap<Integer, Integer> puentes = new HashMap<Integer,Integer>();
		puentes.put(6, 12);
		puentes.put(12, 6);
		return puentes;
	}

	//Método que nos construirá un HashMap con las
	//posiciones de las casillas con los dados
	static HashMap<Integer, Integer> getDados(){
		HashMap<Integer, Integer> dados = new HashMap<Integer,Integer>();
		dados.put(26, 53);
		dados.put(53, 26);
		return dados;
	}

	//Método para tirar el dado
	static int tirarDado() {
		Random r = new Random();
		return r.nextInt(6)+1; //Asi nunca sale un 0
	}
	
	public static void enviarMensaje(String mensaje) throws IOException {
		for(ClientHandler c : clientes) {
			DataOutputStream dos = c.getDataOutputStream();
			dos.writeUTF(mensaje);
		}
	}
}
