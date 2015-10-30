package DROGA;
import MAIN.PunktMapy;

/**
 * 
 * @author Kamil Piotrowski
 * Klasa określająca drogę pomiędzy portami, dziedziczy po Drodze
 *
 */
public class TrasaMorska extends Droga{

	/**
	 * Konstruktor
	 * @param a - punkt mapy A
	 * @param b - lista wszystkich następników punktu A
	 */
	public TrasaMorska(PunktMapy A, PunktMapy B){
		super(A,B);
	}
}
