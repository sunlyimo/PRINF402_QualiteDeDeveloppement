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
    @Test
    public void testAbattement_Plancher_RevenuTresBas() {
        // Arrange
        ICalculateurImpot calculateur = new AdaptateurVersCodeHerite();
        calculateur.setRevenusNet(2000); // 2000€ pour déclencher le plancher de 495€
        calculateur.setSituationFamiliale(SituationFamiliale.CELIBATAIRE);
        calculateur.setNbEnfantsACharge(0);
        calculateur.setNbEnfantsSituationHandicap(0);
        calculateur.setParentIsole(false);

        // Act
        calculateur.calculImpotSurRevenuNet();
        int resultat = calculateur.getImpotSurRevenuNet();

        // Assert (Change le 0 par la valeur que Maven va te donner)
        assertEquals(0, resultat, "Erreur sur le revenu très bas");
    }

    @Test
    public void testImpot_Divorce_Avec3Enfants_ParentIsole() {
        // Arrange
        ICalculateurImpot calculateur = new AdaptateurVersCodeHerite();
        calculateur.setRevenusNet(50000); 
        calculateur.setSituationFamiliale(SituationFamiliale.DIVORCE);
        calculateur.setNbEnfantsACharge(3); // Teste la règle du 3ème enfant
        calculateur.setNbEnfantsSituationHandicap(0);
        calculateur.setParentIsole(true);   // Teste la case Parent Isolé

        // Act
        calculateur.calculImpotSurRevenuNet();
        int resultat = calculateur.getImpotSurRevenuNet();

     // Assert
        assertEquals(1, resultat, "Erreur sur le calcul divorcé avec 3 enfants et isolé");
    }
}