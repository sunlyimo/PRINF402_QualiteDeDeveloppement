package com.kerware.simulateur;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimulateurTest {

    @Test
    public void testImpot_Celibataire_Revenu18626_DoitPayer1Euro() {
        // 1. Arrange (Préparation)
    	ICalculateurImpot calculateur = new AdaptateurVersCodeHerite();
        calculateur.setRevenusNet(200000); 
        calculateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        calculateur.setNbEnfantsACharge(0);
        calculateur.setNbEnfantsSituationHandicap(0);
        calculateur.setParentIsole(false);

        // 2. Act (Exécution)
        calculateur.calculImpotSurRevenuNet();
        int resultat = calculateur.getImpotSurRevenuNet();

        // 3. Assert (Vérification)
        // On s'attend à 1 euro
        assertEquals(1, resultat, "L'impôt d'un célibataire avec 18626€ devrait être de 1€");
    }
}