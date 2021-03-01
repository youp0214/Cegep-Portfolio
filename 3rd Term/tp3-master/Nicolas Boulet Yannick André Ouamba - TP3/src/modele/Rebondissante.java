package modele;

import java.awt.Point;
import javafx.scene.paint.Color;

public class Rebondissante extends Particule {

	public Rebondissante(Point pPos, double pVitesse, double pAngleDep, double pRayon, Color pCouleur, Point pZone,
			RegroupementParticules environementPart) {
		super(pPos, pVitesse, pAngleDep, pRayon, pCouleur, pZone, environementPart);
	}

	@Override
	public void handleCollision(Particule pPart) {
		if (!(pPart instanceof Mangeuse))
			environementPart.rebond(this, pPart);
	}

}
