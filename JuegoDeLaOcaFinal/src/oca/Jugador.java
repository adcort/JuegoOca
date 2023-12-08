package oca;

public class Jugador {
	private String nombre;
	private int casilla;
	private int turnosRestantes;
	private boolean activo;
	
	public Jugador(String n) {
		activo = true;
		this.nombre = n;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCasilla() {
		return casilla;
	}
	public void setCasilla(int casilla) {
		this.casilla = casilla;
	}
	public int getTurnosRestantes() {
		return turnosRestantes;
	}
	public void setTurnosRestantes(int turnosRestantes) {
		this.turnosRestantes = turnosRestantes;
	}
	public boolean isActivo() {
		return activo;
	}
	public void setActivo(boolean activo) {
		this.activo = activo;
	}
	
	

}
