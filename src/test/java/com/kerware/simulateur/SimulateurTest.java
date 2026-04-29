package com.kerware.simulateur;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimulateurTest {

    @Test
    public void testImpot_Celibataire_Revenu18626_DoitPayer1Euro() {
        // Arrange
        ICalculateurImpot calculateur = new AdaptateurVersCodeHerite();
        calculateur.setRevenusNet(18626); // REMIS À 18626 !
        calculateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        calculateur.setNbEnfantsACharge(0);
        calculateur.setNbEnfantsSituationHandicap(0);
        calculateur.setParentIsole(false);

        // Act
        calculateur.calculImpotSurRevenuNet();
        int resultat = calculateur.getImpotSurRevenuNet();

        // Assert
        assertEquals(1, resultat, "L'impôt d'un célibataire avec 18626€ devrait être de 1€");
    }
    
    @Test
    public void testImpot_Marie_SansEnfant() {
        // Arrange
        ICalculateurImpot calculateur = new AdaptateurVersCodeHerite();
        calculateur.setRevenusNet(65000); 
        calculateur.setSituationFamiliale(SituationFamiliale.MARIE);
        calculateur.setNbEnfantsACharge(0);
        calculateur.setNbEnfantsSituationHandicap(0);
        calculateur.setParentIsole(false);
        
        // Act
        calculateur.calculImpotSurRevenuNet();
        int resultat = calculateur.getImpotSurRevenuNet();

        // Assert (On ne garde QUE la bonne valeur)
        assertEquals(4122, resultat, "L'impôt d'un couple marié avec 65000€ doit être de 4122€");
    }

    @Test
    public void testAbattement_Plafond_RevenuTresEleve() {
        // Arrange
        ICalculateurImpot calculateur = new AdaptateurVersCodeHerite();
        calculateur.setRevenusNet(200000); 
        calculateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        calculateur.setNbEnfantsACharge(0);
        calculateur.setNbEnfantsSituationHandicap(0);
        calculateur.setParentIsole(false);

        // Act
        calculateur.calculImpotSurRevenuNet();
        int resultat = calculateur.getImpotSurRevenuNet();

        // Assert (On ne garde QUE la bonne valeur)
        assertEquals(60768, resultat, "L'impôt d'un célibataire avec 200000€ doit être de 60768€");
    }
}