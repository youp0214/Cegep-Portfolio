package ctrl;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;

public class Data {

	/**
	 * Constante représentant la couleur défaut à afficher sur le ColorPicker
	 */
	public static final Color COULEUR_DEFAUT = Color.BLACK;
	/**
	 * Attributs qui représentent les valeurs des différents Slider, leurs nom
	 * s'expliqent d'eux même
	 */
	private DoubleProperty angle, vitesse, rayon, volume;
	/**
	 * Attribut représentant la couleur selectionnée
	 */
	private Color couleur;
	/**
	 * Attribut représentant si la case Ronbondissante est cochée(true). Si la case
	 * mangeuse est cochée cette propriété est false.
	 */
	private BooleanProperty estRebond;

	/**
	 * Constructeur
	 * 
	 * Initialisation des attributs
	 */
	public Data() {
		angle = new SimpleDoubleProperty();
		angle.set(0);
		vitesse = new SimpleDoubleProperty();
		vitesse.set(0);
		rayon = new SimpleDoubleProperty();
		rayon.set(4);
		volume = new SimpleDoubleProperty();
		volume.set(0);
		couleur = COULEUR_DEFAUT;
		estRebond = new SimpleBooleanProperty();
	}

	/**
	 * 
	 * @return L'angle selectionné par l'utilisateur
	 */
	public DoubleProperty getAngle() {
		return angle;
	}

	/**
	 * 
	 * @return La vitesse selectionnée par l'utilisateur
	 */
	public DoubleProperty getVitesse() {
		return vitesse;
	}

	/**
	 * 
	 * @return Le rayon selectionné par l'utilisateur
	 */
	public DoubleProperty getRayon() {
		return rayon;
	}

	/**
	 * @return Le volume selectionné par l'utilisateur
	 */
	public DoubleProperty getVolume() {
		return volume;
	}

	/**
	 * 
	 * @return La couleur selectionnée par l'utilisateur
	 */
	public Color getCouleur() {
		return couleur;
	}

	/**
	 * Change le paramètre de couleur
	 * 
	 * @param pCouleur La nouvelle couleur à entreposer
	 */
	public void setCouleur(Color pCouleur) {
		couleur = pCouleur;
	}

	/**
	 * 
	 * @return La valeur de la case "Rebondissante"
	 */
	public BooleanProperty isEstRebond() {
		return estRebond;
	}

}
