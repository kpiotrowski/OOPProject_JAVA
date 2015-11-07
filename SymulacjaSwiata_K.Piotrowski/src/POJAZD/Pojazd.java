package POJAZD;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

import DROGA.Droga;
import GUI.MapClickVehicle;
import MAIN.Miasto;
import MAIN.PunktMapy;
import MAIN.Swiat;
import PASAZER.Pasazer;

/**
 * Klasa grupująca wszystkie pojazdy
 * @author Kamil Piotrowski
 *
 */
public abstract class Pojazd extends PunktMapy implements Runnable{
	
	/**
	 * Obecne miejsce, jeśli w trakcje lotu/rejsu = null
	 */
	private PunktMapy obecneMiejsce=null;
	
	/**
	 * Ilosc maksymalnego paliwa
	 */
	private double maxPaliwo;
	
	/**
	 * Ilosc obecnego paliwa
	 */
	private double paliwo;
	
	/**
	 * Maksymalna prędkość pojazdu
	 */
	private int maxSpeed;
	/**
	 * Ilość załogi
	 */
	private int liczbaZalogi;
	/**
	 * Trasa po której porusza się pojazd
	 */
	private List<Droga> trasa = new ArrayList<Droga>();
	/**
	 * Trasa powrotna - kopia trasy w odwrotnej kolejności
	 */
	private List<Droga> trasaPowrotna = new ArrayList<Droga>();
	/**
	 * Przechowuje kopię trasy pierwotnej
	 */
	private List<Droga> trasaTmp = new ArrayList<Droga>();
	/**
	 * Wielkość obrazu danego pojazdu
	 */
	private int size=70;
	/**
	 * Zmienna czy pojazd jest na skrzyżowaniu
	 */
	volatile private boolean czyNaSkrzyzowaniu=false;
	/**
	 * Informuje czy pojazd już zaparkował w mieście
	 */
	volatile private boolean czyZaparkowano = false;
	/**
	 * Miejsce parkingowe
	 */
	private int miejsceParkingowe=-1;
	/**
	 * Czy wykonywana pętla wątka
	 */
	private boolean runnable=true;
	/**
	 * Stan pojazdu
	 * 0 - w mieście startowym, czeka na wylosowanie trasy
	 * 1 - postój w miescie
	 * 2 - w trasie
	 */
	private int stan=0;
	
	/**
	 * Kostruktor
	 * @param d - położenie X Pojazdu
	 * @param e - położenie Y Pojazdu
	 * @param name - nazwa pojazdu
	 * @param id - id pojazdu
	 * @param miasto - miasto w którym zaczyna pojazd
	 * Losuje także paliwo z przedziału 1000 - 1500
	 * Ustawia obecne miejsce
	 */
	public Pojazd(double d,double e, String name, int id, Miasto miasto){
		super(d, e, name, id);
		Random generator = new Random();
		this.maxPaliwo=generator.nextInt(5000)+10000;
		this.liczbaZalogi=generator.nextInt(10)+10;
		this.paliwo=maxPaliwo;
		this.obecneMiejsce=miasto;
	}
	/**
	 * Usuwa pojazd oraz wszystko co z nim związane
	 */
	public synchronized void removePojazd(){
		//Usuwanie z parkingu / miasta
		if((this.isCzyZaparkowano()) && (this.obecneMiejsce instanceof Miasto)){
			((Miasto)this.getObecneMiejsce()).getParking()[this.getMiejsceParkingowe()]=0;
			((Miasto)this.getObecneMiejsce()).getListaPojazdow().remove(this);
			((Miasto)this.getObecneMiejsce()).zwiekszPojemnosc();
		}
		//Usuwanie z trasy
		if(!this.getTrasa().isEmpty()) this.getTrasa().get(0).getPojazdyNaDrodze().remove(this);
		//Czyszczenie pasażerów
		if(this instanceof PojazdPasazerski){
			System.out.println("Własnie zabiłeś: "+((PojazdPasazerski)this).getListaPasazerow().size() + " pasażerow");
			for(Pasazer pas : ((PojazdPasazerski)this).getListaPasazerow() ){
				pas.stop();
				Swiat.getListaPasazerow().remove(pas);
				pas=null;
			}
			((PojazdPasazerski)this).getListaPasazerow().clear();
		}
		//Usuwanie z Listy głównej
		Swiat.getListaPojazdow().remove(this);
		//Zatrzymywanie wątku
		this.stop();
		//Usuwanie z mapy
		this.setKoorX(-1000);
		this.setKoorY(-1000);
		
		
		
		
	
	
	
	}
	/**
	 * Zwraca button z pojazdem do wyswietlenia na mapie
	 * @param zoom	- zoom mapy
	 * @param koorX	- wspołrzędna X
	 * @param koorY	- wspólrzędna Y
	 * @return button
	 */
	public MapClickVehicle rysuj(double zoom, int koorX, int koorY){
		int size=(int)(this.getSize()/zoom);		
		ImageIcon ikona = this.returnIcon(zoom);
		MapClickVehicle button= new MapClickVehicle(koorX,koorY, size, size, this);
		button.setIcon(ikona);
		return button;
	};
	/**
	 * Zwraca obruconą, przeskalowaną ikonę pojazdu
	 * @param zoom - zomm na mapie
	 * @return
	 */
	public ImageIcon returnIcon(double zoom){
		int size=(int)(this.getSize()/zoom);
		int katObrotu = 0;
		if( !this.getTrasa().isEmpty() ) katObrotu=(int) this.getTrasa().get(0).getKatProstej();
		katObrotu*=-1;
		
		BufferedImage gr = new BufferedImage(size,size,BufferedImage.TYPE_INT_ARGB);
		Graphics2D bg = gr.createGraphics();
		
		bg.setColor(null);
		bg.setBackground(null);
		bg.rotate(Math.toRadians(katObrotu), size/2, size/2);
		bg.drawImage(this.getImage(),0,0,size, size,0,0,this.getSize(), this.getSize(), null);
	    bg.dispose();
		ImageIcon ikona = new ImageIcon(gr);
		
		return ikona;
	};
	/**
	 * Zwraca obraz danepo pojazdy
	 * @return obraz danego pojazdu
	 */
	public abstract BufferedImage getImage();
	/**
	 * Przesuwa pojazd o odpowiednie odległości
	 * x - mnożnik przesunięcia
	 */
	public void move(int x){
		double diffX=this.getTrasa().get(0).getB().getKoorX()-this.getKoorX();
		double diffY=this.getTrasa().get(0).getB().getKoorY()-this.getKoorY();
		double alfa=Math.atan(diffY/diffX);
		double moveX= Math.cos(alfa)*this.getMaxSpeed()/3*x;
		double moveY= Math.sin(alfa)*this.getMaxSpeed()/3*x;
		double newKoorX=0;
		double newKoorY=0;
		if( diffX>0 ){
			newKoorX=this.getKoorX()+moveX;
			newKoorY=this.getKoorY()+moveY;
		}
		if( diffX<0 ){
			newKoorX=this.getKoorX()-moveX;
			newKoorY=this.getKoorY()-moveY;
		}
		while((this.isRunnable()) && (!this.canMove(newKoorX, newKoorY)) ) 	try { Thread.sleep(10); } 
			catch (InterruptedException e) { e.printStackTrace(); }
		this.setKoorX(newKoorX);
		this.setKoorY(newKoorY);
		this.setPaliwo(this.getPaliwo()-1);
	}
	public void moveToPoint(double koorX,double koorY){
		while((this.isRunnable()) && (!this.canMove(koorX, koorY)) ) 	try { Thread.sleep(10); } 
			catch (InterruptedException e) { e.printStackTrace(); }
		this.setKoorX(koorX);
		this.setKoorY(koorY);
	}
	public boolean canMove(double X, double Y ){
		for(Pojazd pojazd : this.getTrasa().get(0).getPojazdyNaDrodze()){
			if(pojazd.getid()!=this.getid()){
				double diffX=0;
				double diffY=0;
				diffX=pojazd.getKoorX()-X;
				diffY=pojazd.getKoorY()-Y;
				diffX=Math.abs(diffX);
				diffY=Math.abs(diffY);
				double diffP=Math.pow(Math.pow(diffX, 2)+Math.pow(diffY, 2) , 0.5);
				if( diffP < 65 && !pojazd.isCzyZaparkowano() ) return false;
			}
			
		}
		return true;
	}
	/**
	 * Parkuje pojazd
	 */
	public void zaparkuj(){
		if(this.getObecneMiejsce() instanceof Miasto ){
			Miasto miasto = (Miasto) this.getObecneMiejsce();
			for(int i=0;i<4;i++ ){
				if(miasto.getParking()[i] == 0){
					switch(i){
					case 0:
						this.setKoorX(this.getKoorX()+38);
						this.setKoorY(this.getKoorY()+38);
						break;
					case 1:
						this.setKoorX(this.getKoorX()+38);
						this.setKoorY(this.getKoorY()-38);
						break;
					case 2:
						this.setKoorX(this.getKoorX()-38);
						this.setKoorY(this.getKoorY()-38);
						break;
					case 3:
						this.setKoorX(this.getKoorX()-38);
						this.setKoorY(this.getKoorY()+38);
						break;
					}
					miasto.getParking()[i]=1;
					this.setMiejsceParkingowe(i);
					break;
				}
			}
			
		}
		this.setCzyZaparkowano(true);
		
	}
	/**
	 * Wyparkowuje pojazd
	 */
	public void wyparkuj(){
		synchronized (this.getObecneMiejsce().getVeronica() ){
			double newKoorX=this.getTrasa().get(0).getA().getKoorX();
			double newKoorY=this.getTrasa().get(0).getA().getKoorY();
			this.moveToPoint(newKoorX, newKoorY);
			this.move(120);
			//Zwiększanie parkingu w mieście
			if(this.getObecneMiejsce() instanceof Miasto ){
				Miasto miasto = (Miasto) this.getObecneMiejsce();
				if(this.getMiejsceParkingowe()>-1)
					miasto.getParking()[this.getMiejsceParkingowe()]=0;
				this.setMiejsceParkingowe(-1);
			}
			//Zwiększanie pojemności miasta oraz usuwanie pojazdu z miasta
			if(this.getObecneMiejsce() instanceof Miasto ){
				Miasto miasto = (Miasto) this.getObecneMiejsce();
				miasto.zwiekszPojemnosc();
				miasto.getListaPojazdow().remove(this);
				this.setObecneMiejsce(null);
			}
			this.setCzyZaparkowano(false);
		}
	}
	public boolean czyPunktPostoju(){
		double diffX=this.getTrasa().get(0).getB().getKoorX()-this.getKoorX();
		double diffY=this.getTrasa().get(0).getB().getKoorY()-this.getKoorY();
		diffX=Math.abs(diffX);
		diffY=Math.abs(diffY);
		double diffP=Math.pow(Math.pow(diffX, 2)+Math.pow(diffY, 2) , 0.5);
		if( diffP < 110) return true;
		return false;
	}
	public double returnDifferenceThisB(){
		double diffX=this.getTrasa().get(0).getB().getKoorX()-this.getKoorX();
		double diffY=this.getTrasa().get(0).getB().getKoorY()-this.getKoorY();
		diffX=Math.abs(diffX);
		diffY=Math.abs(diffY);
		double diffP=Math.pow(Math.pow(diffX, 2)+Math.pow(diffY, 2) , 0.5);
		return diffP;
	}
	public double returnDifferenceThisA(){
		double diffX=this.getTrasa().get(0).getA().getKoorX()-this.getKoorX();
		double diffY=this.getTrasa().get(0).getA().getKoorY()-this.getKoorY();
		diffX=Math.abs(diffX);
		diffY=Math.abs(diffY);
		double diffP=Math.pow(Math.pow(diffX, 2)+Math.pow(diffY, 2) , 0.5);
		return diffP;
	}
	public void wejdzNaSkrzyzowanie(){
		boolean out=false;
		while(this.isRunnable()){
			try {
				move(1);
				if(out==false && this.returnDifferenceThisB()<2){
					this.getTrasa().get(0).getPojazdyNaDrodze().remove(this);
					this.getTrasa().remove(0);
					this.setKoorX(this.getTrasa().get(0).getA().getKoorX());
					this.setKoorY(this.getTrasa().get(0).getA().getKoorY());
					this.getTrasa().get(0).getPojazdyNaDrodze().add(this);
					out=true;
				}
				if(out == true){
					if(this.returnDifferenceThisA() > 100) break;
				}
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * @throws InterruptedException
	 */
	public void wejdzDoMiasta() throws InterruptedException{
		if(this.getTrasa().get(0).getB() instanceof Miasto){
			Miasto miasto = (Miasto) this.getTrasa().get(0).getB();
			while( miasto.getPojemosc()<=0 ){
				Thread.sleep(20);
			}
			miasto.zmniejszPojemnosc();
			miasto.getListaPojazdow().add(this);
			this.setObecneMiejsce(miasto);
			this.setKoorX(miasto.getKoorX());
			this.setKoorY(miasto.getKoorY());
			this.zaparkuj();
			this.getTrasa().get(0).getPojazdyNaDrodze().remove(this);
			this.getTrasa().remove(0);
			if(!this.getTrasa().isEmpty())
				this.getTrasa().get(0).getPojazdyNaDrodze().add(this);
			this.setStan(1);
			this.setPaliwo(this.getMaxPaliwo());
		}
	}
	/**
	 * Przepisuje prasię porwotną do zmienne trasy (ten na podstawie której podróżujemy i którą zmniejszamy)
	 * @param statek  - referencja do pojazdu na którym działamy
	 */
	public void przepiszTrase(Pojazd statek){
		statek.getTrasa().clear();
		if(statek.getObecneMiejsce().getid() == statek.getTrasaPowrotna().get(0).getA().getid() )
			for(Droga trasa : statek.getTrasaPowrotna() ){
				statek.getTrasa().add(trasa);
			}
		else
			for(Droga trasa : statek.getTrasaTmp() ){
				statek.getTrasa().add(trasa);
			}
		statek.getTrasa().get(0).getPojazdyNaDrodze().add(statek);
	}
	
	
	
	
	public List<Droga> getTrasa() {
		return trasa;
	}
	public void setTrasa(List<Droga> trasa) {
		this.trasa = trasa;
	}
	public List<Droga> getTrasaPowrotna() {
		return trasaPowrotna;
	}
	public void setTrasaPowrotna(List<Droga> trasaPowrotna) {
		this.trasaPowrotna = trasaPowrotna;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public PunktMapy getObecneMiejsce() {
		return obecneMiejsce;
	}
	public void setObecneMiejsce(PunktMapy obecneMiejsce) {
		this.obecneMiejsce = obecneMiejsce;
	}
	public double getMaxPaliwo() {
		return maxPaliwo;
	}
	public void setMaxPaliwo(double maxPaliwo) {
		this.maxPaliwo = maxPaliwo;
	}
	public double getPaliwo() {
		return paliwo;
	}
	public void setPaliwo(double paliwo) {
		this.paliwo = paliwo;
	}
	public int getLiczbaZalogi() {
		return liczbaZalogi;
	}
	public void setLiczbaZalogi(int liczbaZalogi) {
		this.liczbaZalogi = liczbaZalogi;
	}
	public int getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public synchronized boolean isCzyNaSkrzyzowaniu() {
		return czyNaSkrzyzowaniu;
	}
	public synchronized void setCzyNaSkrzyzowaniu(boolean czyNaSkrzyzowaniu) {
		this.czyNaSkrzyzowaniu = czyNaSkrzyzowaniu;
	}
	public int getStan() {
		return stan;
	}
	public void setStan(int stan) {
		this.stan = stan;
	}
	public boolean isCzyZaparkowano() {
		return czyZaparkowano;
	}
	public void setCzyZaparkowano(boolean czyZaparkowano) {
		this.czyZaparkowano = czyZaparkowano;
	}
	public int getMiejsceParkingowe() {
		return miejsceParkingowe;
	}
	public void setMiejsceParkingowe(int miejsceParkingowe) {
		this.miejsceParkingowe = miejsceParkingowe;
	}
	public List<Droga> getTrasaTmp() {
		return trasaTmp;
	}
	public void setTrasaTmp(List<Droga> trasaTmp) {
		this.trasaTmp = trasaTmp;
	}
	public boolean isRunnable() {
		return runnable;
	}
	public void setRunnable(boolean runnable) {
		this.runnable = runnable;
	}
	public void stop(){
		this.setRunnable(false);
	}

}
