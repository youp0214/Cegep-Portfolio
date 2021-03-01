package modele;

import java.awt.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Mangeuse extends Particule {

	// Angles du cercle trigo
	private final double[] ANGLES_MINI_BALLES = { 0, 45, 90, 135, 180, 225, 270, 315 };
	private final int NBR_BALLES_EXPLOSION = ANGLES_MINI_BALLES.length;
	private final int RAYON_MINI_BALLES = 10;
	private final double VITESSE_MINI_BALLES = 1;
	private final double GROSSEUR_MAX = 40;

	public Mangeuse(Point pPos, double pVitesse, double pAngleDep, double pRayon, Color pCouleur, Point pZone,
			RegroupementParticules environementPart) {
		super(pPos, pVitesse, pAngleDep, pRayon, pCouleur, pZone, environementPart);
	}

	@Override
	public void handleCollision(Particule pPart) {
		if (pPart instanceof Rebondissante && getRayon().getValue() > pPart.getRayon().getValue()) {
			manger(pPart);
		} else
			environementPart.rebond(this, pPart);
	}

	private void manger(Particule pPart) {
		System.out.println(getRayon().getValue());
		setRayon(getRayon().getValue() + 1);
		((Circle) environementPart.getLienParticules().get(this)).setRadius(getRayon().getValue());
		if (getRayon().getValue() > GROSSEUR_MAX)
			explose();
		System.out.println(getRayon().getValue());
		environementPart.enleverPart(pPart);
	}

	private void explose() {
		Color newCouleur = getCouleur();
		Point newPosition = new Point();
		environementPart.enleverPart(this);
		for (int i = 0; i < NBR_BALLES_EXPLOSION; i++) {
			newPosition.setLocation(
					getPosition().getX() + ((5 * RAYON_MINI_BALLES)
							* Math.cos(Math.toRadians(ANGLES_MINI_BALLES[NBR_BALLES_EXPLOSION - 1 - i]))),
					getPosition().getY() + ((5 * RAYON_MINI_BALLES)
							* Math.sin(Math.toRadians(ANGLES_MINI_BALLES[NBR_BALLES_EXPLOSION - 1 - i]))));
			try {
				environementPart.ajouterPart(new Rebondissante(newPosition, VITESSE_MINI_BALLES, ANGLES_MINI_BALLES[i],
						RAYON_MINI_BALLES, newCouleur, getZoneDep(), environementPart));
			} catch (NumberFormatException ex) {
				System.err.println(ex.getMessage());
			}
		}

	}

}
