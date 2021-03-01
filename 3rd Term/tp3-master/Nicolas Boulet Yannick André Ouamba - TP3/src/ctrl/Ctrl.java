package ctrl;

import java.awt.Point;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import modele.RegroupementParticules;
import musique.BoomBox;
import vue.Collision_Vue;

/**
 * Classe de controle du collisionneur Gère les évènements liés aux composantes
 * de la vue et s'occupe de lier les particules aux éléments visuels de lu GUI
 * 
 * @author Nicolas Boulet et Yannick André Ouamba
 *
 */
public class Ctrl {

	/**
	 * Lien entre les valeurs selectionnées dans le GUI et cette classe.
	 */
	private Data data;
	/**
	 * Référence au GUI, permet de gérer les contraintes reliés à ce dernier.
	 */
	private Collision_Vue vue;

	/**
	 * Point représentant le point le plus éloigné de l'origine de la zone
	 * d'affichage des particules.
	 */
	private Point dimensionBoite;
	/**
	 * Objet servant à gérer la musique dans l'application.
	 */
	private BoomBox musique;
	/**
	 * Référence vers l'objet contenant les listes et opérations sur les
	 * particultes. Utile pour lui envoyer l'information du GUI
	 */
	private RegroupementParticules liste;
	private Thread threadDep;

	/**
	 * Constructeur
	 * 
	 * Instantie les deux listes ainsi que le lien vers les données selectionnées
	 * par l'utilisateur. Instancie les attributs non-instanciés.
	 * 
	 * @throws Exception déclaré si le fichier FXML est non-trouvé
	 */
	public Ctrl() throws Exception {
		data = new Data();
		liste = new RegroupementParticules(this);
		vue = new Collision_Vue(this);

		dimensionBoite = new Point();
		dimensionBoite.setLocation(vue.getBoite().getPrefWidth(), vue.getBoite().getPrefHeight());
		dimensionBoite.setLocation(vue.getBoite().getPrefWidth(), vue.getBoite().getPrefHeight());

		genLabelBindings();
		genDataBindings();

		threadDep = new Thread(liste);
		threadDep.setDaemon(true);
		threadDep.start();
//		threadDep.setDaemon(true);
	}

	public boolean quitter() {
		Alert quitter = new Alert(AlertType.WARNING, "Votre musique sera interrompue", ButtonType.YES, ButtonType.NO);
		quitter.setTitle("Confirmation");
		quitter.setHeaderText("Voulez-vous vraiment quitter?");
		if (quitter.showAndWait().get().equals(ButtonType.YES))
			Platform.exit();
		return false;
	}

	/**
	 * @return Les dimensions de la boite en Point. Ce point représente le coin le
	 *         plus éloigné de l'origine.
	 */
	public Point getDimensionBoite() {
		return dimensionBoite;
	}

	/**
	 * Méthode utilisée par le GUI pour modifier la couleur dans data à chaque fois
	 * qu'une nouvelle couleur est selectionnée
	 */
	public void setDataCouleur() {
		data.setCouleur(vue.getCouleur_CP().getValue());
	}

	/**
	 * Crée un objet musique et permet de choisir le dossier de musique voulu. Joue
	 * la musique automatiquement
	 */
	public void gogoMusique() {
		if (musique == null) {
			musique = new BoomBox(data.getVolume().getValue());
//			musique.setVolume(100);
			Thread t = new Thread(musique);
			t.setDaemon(true);
			t.start();
		} else {
			musique.getMediaView().setMediaPlayer(null);
			musique = null;
			gogoMusique();
		}
	}

	/**
	 * Donne la valeur minimale de volume au Slider au à l'objet qui joue la musique
	 * si le bouton est activé. S'il est désactivé le volume est rétabli au volume
	 * précédant.
	 */
	public void clickMute() {
		try {
			musique.mute();
			vue.getMusique_Slr().setValue(musique.getMediaView().getMediaPlayer().getVolume());
		} catch (Exception e) {

		}
	}

	/**
	 * Crée le lien entre le data et chacun des Slider dans le GUI. Utilise aussi un
	 * ChangeListener pour changer le volume de la musique en temps réel
	 */
	private void genDataBindings() {
		data.getAngle().bind(vue.getAngle_Slr().valueProperty());
		data.getRayon().bind(vue.getRayon_Slr().valueProperty());
		data.getVitesse().bind(vue.getVitesse_Slr().valueProperty());
		data.isEstRebond().bind(vue.getRebond_RB().selectedProperty());
		data.getVolume().bind(vue.getMusique_Slr().valueProperty());
		// Changement du volume
		vue.getMusique_Slr().valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (musique != null)
					musique.setVolume(vue.getMusique_Slr().valueProperty().getValue().doubleValue());

			}
		});

	}

	/**
	 * Crée le lien entre les Slider et les Label, permetant ainsi de voir avec
	 * précision la valeur selectionné par ces derniers.
	 */
	private void genLabelBindings() {
		vue.getRayon_L().textProperty().bind(vue.getRayon_Slr().valueProperty().asString("%4.1f"));

		vue.getAngle_L().textProperty().bind(vue.getAngle_Slr().valueProperty().asString("%4.1f"));

		vue.getVitesse_L().textProperty().bind(vue.getVitesse_Slr().valueProperty().asString("%4.1f"));

		vue.getVolume_L().textProperty().bind(vue.getMusique_Slr().valueProperty().multiply(100).asString("%1.1f"));

		vue.getNbrPart_L().textProperty().bind(liste.getNbrePart_Property().asString());
	}

	/**
	 * 
	 * @return L'objet {@link Data} utilisé par le controlleur
	 */
	public Data getData() {
		return data;
	}

	/**
	 * 
	 * @return L'objet {@link RegroupementParticules} utilisé par le controlleur
	 */
	public RegroupementParticules getList() {
		return liste;
	}

	/**
	 * @return la référence à la vue de type Collision_Vue
	 */
	public Collision_Vue getVue() {
		return vue;
	}
}
