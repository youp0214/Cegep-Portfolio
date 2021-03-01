package modele;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.function.Consumer;

import ctrl.Ctrl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class RegroupementParticules extends Task<Void> {

	/**
	 * Constante pour le maximum de particules à générer à la fois en utilisant la
	 * fonctionnalité de génération aléatoire.
	 */
	public static final int NBR_PART_MAX = 300;

	/**
	 * Hashtable qui gère le lien entre les particules et leurs représentations
	 * graphiques.
	 */
	private Hashtable<Particule, Shape> lienParticules;
	/**
	 * ArrayList qui contient toutes les particules affichées. Se modifie au même
	 * rythme que lienParticules
	 */
	private ArrayList<Particule> listParticules;
	/**
	 * Compteur du nombre de particules affichées dans le GUI.
	 */
	private SimpleIntegerProperty nbrePart_Property;
	/**
	 * Attribut faisant le lien entre cet objet et son appelant
	 */
	private Ctrl ctrlRef;
	private static final int NBR_RAFRAICHISSEMENTS = 5;

	/**
	 * Constructeur
	 * 
	 * @param ctrl L'appelant
	 */
	public RegroupementParticules(Ctrl ctrl) {
		ctrlRef = ctrl;
		listParticules = new ArrayList<>();
		lienParticules = new Hashtable<>();
		nbrePart_Property = new SimpleIntegerProperty(0);
	}

	/**
	 * 
	 * @return Le lien entre les particules et leurs représentation graphiques
	 */
	public Hashtable<Particule, Shape> getLienParticules() {
		return lienParticules;
	}

	/**
	 * 
	 * @return La liste des particules seules. Utilisé pour instancier la
	 *         représentation graphique de chacune
	 */
	public ArrayList<Particule> getListParticules() {
		return listParticules;
	}

	/**
	 * 
	 * @return La propriété représentant le nombre de particules
	 */
	public SimpleIntegerProperty getNbrePart_Property() {
		return nbrePart_Property;
	}

	/**
	 * Change l'effet sur les balles selon si la case est cochée ou non.
	 * 
	 * @param estCoche Détermine si la case est cochée ou non.
	 */
	public void setEffet(boolean estCoche) {
		for (Shape s : lienParticules.values()) {
			if (estCoche)
				s.getStyleClass().add("effet");
			else
				s.getStyleClass().remove("effet");
		}
	}

	/**
	 * Modifie la DoubleProperty nbrePart_Property pour qu'elle concorde avec le
	 * nombre de particules dans la liste de particules.
	 */
	private void updateNbrPart() {
		nbrePart_Property.set(lienParticules.size());
	}

	/**
	 * Méthode appelé lors d'un click dans la région d'affichage des particules.
	 * Crée une nouvelle particules selon les données selectionnées dans le GUI en
	 * passant par le Data et l'ajoute aux listes en utilisant la méthode
	 * ajouterPart
	 * 
	 * @param position Position du click dans la zone d'affichage
	 */
	public void genPart(Point position) {
		try {
			if (ctrlRef.getData().isEstRebond().getValue())
				ajouterPart(new Rebondissante(position, ctrlRef.getData().getVitesse().getValue(),
						ctrlRef.getData().getAngle().getValue(), ctrlRef.getData().getRayon().getValue(),
						ctrlRef.getData().getCouleur(), ctrlRef.getDimensionBoite(), this));
			else
				ajouterPart(new Mangeuse(position, ctrlRef.getData().getVitesse().getValue(),
						ctrlRef.getData().getAngle().getValue(), ctrlRef.getData().getRayon().getValue(),
						ctrlRef.getData().getCouleur(), ctrlRef.getDimensionBoite(), this));
			updateNbrPart();
		} catch (NumberFormatException ex) {
			System.err.println(ex.getMessage());
		}
	}

	/**
	 * Vide la liste de particules ainsi que les liens entre les particules et leurs
	 * représentations graphiques. Enlève aussi l'affichage des particules dans le
	 * GUI
	 */
	public void reiniPart() {
		lienParticules.clear();
		listParticules.clear();
		ctrlRef.getVue().getBoite().getChildren().clear();
		updateNbrPart();
	}

	/**
	 * Ajoute une particule à la liste et crée le lien entre elle et sa
	 * représentation graphique
	 * 
	 * @param pPart La particule à ajouter à la liste
	 */
	public void ajouterPart(Particule pPart) {
		listParticules.add(0, pPart);
		Point pointTemp = new Point();
		pointTemp.setLocation(pPart.getPosX().getValue(), pPart.getPosY().getValue());
		lienParticules.put(pPart, ctrlRef.getVue().addShape());
	}

	/**
	 * Retire une particule de la liste, détruit son lien avec sa représentation
	 * graphique et arrête son affichage
	 * 
	 * @param pPart La particule à enlever
	 */
	public void enleverPart(Particule pPart) {
//		pPart.getMouvement().die();
		ctrlRef.getVue().getBoite().getChildren().remove(lienParticules.get(pPart));
		lienParticules.remove(pPart);
		listParticules.remove(pPart);
	}

	public void rebond(Particule pPart1, Particule pPart2) {
		if (pPart1.getRayon().getValue() >= pPart2.getRayon().getValue()) {
			double[][] vec1et2 = pPart1.calculerVitesse(pPart2);
			pPart1.setVecVitesse(vec1et2[0]);
			pPart2.setVecVitesse(vec1et2[1]);
		}
	}

	private boolean[] verifierMur(Particule particule) {
		boolean[] estBonDep = { true, true };
		Point pointTemp = new Point();

		pointTemp.setLocation(particule.getPosX().getValue() + particule.getVitesse()[0],
				particule.getPosY().getValue() - particule.getVitesse()[1]);
		if (pointTemp.getX() + 2 >= particule.getZoneDep().getX() - particule.getRayon().getValue()
				|| pointTemp.getX() - particule.getRayon().getValue() <= 0)
			estBonDep[0] = false;

		if (pointTemp.getY() + 2 >= particule.getZoneDep().getY() - particule.getRayon().getValue()
				|| pointTemp.getY() - particule.getRayon().getValue() <= 0)
			estBonDep[1] = false;

		return estBonDep;
	}

	private Particule verifierAutrePart(Particule particule) {
		Particule temp = null;
		if (getListParticules().size() > 0)
			for (Particule part : getListParticules()) {
				if (!part.equals(particule)) {
					double minDistance = (particule.getRayon().getValue() + part.getRayon().getValue());
					Point2D temp2Dthis = new Point2D(particule.getPosition().getX() + particule.getVitesse()[0],
							particule.getPosition().getY() - particule.getVitesse()[1]);
					Point2D temp2Dthat = new Point2D(part.getPosition().getX() + part.getVitesse()[0],
							part.getPosition().getY() - part.getVitesse()[1]);

					if (temp2Dthis.distance(temp2Dthat) < minDistance + 2) {
						temp = part;
						break;
					}
				}
			}

		return temp;
	}

	private void deplacer(Particule particule) {
		Point pointTemp = new Point();

		pointTemp.setLocation(particule.getPosX().getValue() + particule.getVitesse()[0],
				particule.getPosY().getValue() - particule.getVitesse()[1]);

		boolean[] pasMur = verifierMur(particule);

		Particule colliPart = verifierAutrePart(particule);

		double[] newVitesse = { particule.getVitesse()[0], particule.getVitesse()[1] };
		if (!pasMur[0] || !pasMur[1]) {
			particule.collisionMur(pasMur, newVitesse);
		} else if (colliPart != null) {
			particule.handleCollision(colliPart);
		} else {
			getListParticules().get(getListParticules().indexOf(particule)).setPosition(pointTemp);
		}
	}

	/**
	 * Génére un nombre de particules avec des propriétés aléatoires. La seule
	 * propriété non-aléatoire est l'état rebondissant ou mangeant. On ne peut en
	 * créer plus de 300 à la fois.
	 * 
	 * @param nbr       Le nombre de particules à générer. 300 maximum
	 * @param estRebond true si Rebondissante est cochée. Change le type de
	 *                  particule à générer
	 */
	public void genererRandPart(int nbr, boolean estRebond) {
		double vit, ang, ray;
		Point pos;
		Color col;
		if (ctrlRef.getVue().getNbrRand() <= NBR_PART_MAX)
			for (int i = 0; i < nbr; i++) {
				try {
					vit = getRandDouble(ctrlRef.getVue().getVitesse_Slr().getMin(),
							ctrlRef.getVue().getVitesse_Slr().getMax());
					ang = getRandDouble(ctrlRef.getVue().getAngle_Slr().getMin(),
							ctrlRef.getVue().getAngle_Slr().getMax());
					ray = getRandDouble(ctrlRef.getVue().getRayon_Slr().getMin(),
							ctrlRef.getVue().getRayon_Slr().getMax());
					col = new Color(getRandDouble(0, 1), getRandDouble(0, 1), getRandDouble(0, 1), 1);

					pos = new Point();
					pos.setLocation(getRandDouble(ray, ctrlRef.getDimensionBoite().getX() - ray),
							getRandDouble(ray, ctrlRef.getDimensionBoite().getY() - ray));
					if (estRebond)
						ajouterPart(new Rebondissante(pos, vit, ang, ray, col, ctrlRef.getDimensionBoite(), this));
					else
						ajouterPart(new Mangeuse(pos, vit, ang, ray, col, ctrlRef.getDimensionBoite(), this));
					updateNbrPart();
				} catch (NumberFormatException ex) {
					System.err.println(ex.getMessage());
				}
			}
	}

	/**
	 * Méthode utilitaire servant à générer un double aléatoire entre valeurs
	 * 
	 * @param min Borne inférieure
	 * @param max Borne supérieure
	 * @return Un double aléatoire entre les bornes
	 */
	public static double getRandDouble(double min, double max) {
		return (Math.random() * (max - min)) + min;
	}

	@Override
	protected Void call() throws Exception {
		final Timeline loop = new Timeline(
				new KeyFrame(Duration.millis(NBR_RAFRAICHISSEMENTS), new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						for (Particule part : listParticules)
							deplacer(part);

					}

				}));
		loop.setCycleCount(Timeline.INDEFINITE);
		loop.play();
		return null;
	}

}
