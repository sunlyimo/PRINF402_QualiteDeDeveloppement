package com.kerware.simulateur;

/**
 * Simule le calcul de l'impôt sur le revenu en France pour l'année 2024
 * (sur les revenus de l'année 2023), pour des contribuables :
 * célibataires, mariés, divorcés, veufs ou pacsés,
 * avec ou sans enfants à charge, enfants handicapés, ou parent isolé.
 */
public class SimulateurAmeliore implements ICalculateurImpot {

    // --- Tranches du barème progressif de l'impôt 2024 ---
    private static final int[] LIMITES_TRANCHES = {
        0, 11294, 28797, 82341, 177106, Integer.MAX_VALUE
    };

    // --- Taux d'imposition par tranche ---
    private static final double[] TAUX_TRANCHES = {
        0.0, 0.11, 0.30, 0.41, 0.45
    };

    // --- Abattement forfaitaire pour frais professionnels ---
    private static final double TAUX_ABATTEMENT       = 0.10;
    private static final int    ABATTEMENT_MINIMUM    = 495;
    private static final int    ABATTEMENT_MAXIMUM    = 14171;

    // --- Plafonnement du quotient familial ---
    private static final double PLAFOND_DEMI_PART = 1759.0;

    // --- Décote (réduction pour les faibles revenus) ---
    private static final double SEUIL_DECOTE_SEUL   = 1929.0;
    private static final double SEUIL_DECOTE_COUPLE = 3191.0;
    private static final double DECOTE_MAX_SEUL     = 873.0;
    private static final double DECOTE_MAX_COUPLE   = 1444.0;
    private static final double TAUX_DECOTE         = 0.4525;

    // --- Données du contribuable ---
    private int               revenuNet;
    private SituationFamiliale situationFamiliale;
    private int               nbEnfants;
    private int               nbEnfantsHandicapes;
    private boolean           estParentIsole;

    // --- Résultats intermédiaires et finaux ---
    private double revenuFiscalReference;
    private double abattement;
    private double nbPartsDeclarants;
    private double nbPartsFoyer;
    private double impotDeclarants;
    private double impotFoyer;
    private double decote;

    // =========================================================================
    // SETTERS
    // =========================================================================

    @Override
    public void setRevenusNet(int revenuNet) {
        this.revenuNet = revenuNet;
    }

    @Override
    public void setSituationFamiliale(SituationFamiliale sf) {
        this.situationFamiliale = sf;
    }

    @Override
    public void setNbEnfantsACharge(int nb) {
        this.nbEnfants = nb;
    }

    @Override
    public void setNbEnfantsSituationHandicap(int nb) {
        this.nbEnfantsHandicapes = nb;
    }

    @Override
    public void setParentIsole(boolean estParentIsole) {
        this.estParentIsole = estParentIsole;
    }

    // =========================================================================
    // GETTERS
    // =========================================================================

    @Override
    public int getImpotSurRevenuNet() {
        return (int) impotFoyer;
    }

    @Override
    public int getRevenuFiscalReference() {
        return (int) revenuFiscalReference;
    }

    @Override
    public int getAbattement() {
        return (int) abattement;
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        return (int) nbPartsFoyer;
    }

    @Override
    public int getImpotAvantDecote() {
        return (int) (impotFoyer + decote);
    }

    @Override
    public int getDecote() {
        return (int) decote;
    }

    // =========================================================================
    // POINT D'ENTRÉE DU CALCUL
    // =========================================================================

    @Override
    public void calculImpotSurRevenuNet() {
        calculImpot(revenuNet, situationFamiliale, nbEnfants, nbEnfantsHandicapes, estParentIsole);
    }

    /**
     * Calcule l'impôt sur le revenu net et retourne le montant arrondi.
     */
    public long calculImpot(int revNet, SituationFamiliale sitFam,
                            int nbEnf, int nbEnfH, boolean parentIsole) {
        this.revenuNet           = revNet;
        this.nbEnfants           = nbEnf;
        this.nbEnfantsHandicapes = nbEnfH;
        this.estParentIsole      = parentIsole;

        calculerAbattementEtRevenuFiscal();
        calculerNombreDeParts(sitFam);
        calculerImpotDeclarants();
        calculerImpotFoyer();
        appliquerPlafonnementQuotientFamilial();
        appliquerDecote();

        return Math.round(impotFoyer);
    }

    // =========================================================================
    // ÉTAPES DE CALCUL
    // =========================================================================

    /** Étape 1 : abattement forfaitaire de 10 % plafonné, puis revenu fiscal de référence. */
    private void calculerAbattementEtRevenuFiscal() {
        abattement = revenuNet * TAUX_ABATTEMENT;
        abattement = Math.min(abattement, ABATTEMENT_MAXIMUM);
        abattement = Math.max(abattement, ABATTEMENT_MINIMUM);

        revenuFiscalReference = revenuNet - abattement;
    }

    /** Étape 2 : nombre de parts fiscales selon la situation familiale et les enfants. */
    private void calculerNombreDeParts(SituationFamiliale sitFam) {
        nbPartsDeclarants = partsSelonSituationFamiliale(sitFam);
        nbPartsFoyer      = nbPartsDeclarants + partsEnfants() + partsSupplementaires();
    }

    private double partsSelonSituationFamiliale(SituationFamiliale sitFam) {
        return switch (sitFam) {
            case MARIE  -> 2.0;
            case VEUF   -> 1.0; // Le veuf avec enfant bénéficie d'une part supplémentaire via partsEnfants()
            default     -> 1.0; // CELIBATAIRE, DIVORCE
        };
    }

    private double partsEnfants() {
        if (nbEnfants <= 2) {
            return nbEnfants * 0.5;
        } else {
            // À partir du 3e enfant : 1 part entière par enfant supplémentaire
            return 1.0 + (nbEnfants - 2);
        }
    }

    private double partsSupplementaires() {
        double parts = 0;
        if (estParentIsole && nbEnfants > 0) {
            parts += 0.5;
        }
        parts += nbEnfantsHandicapes * 0.5;
        return parts;
    }

    /** Étape 3 : impôt brut calculé uniquement pour les déclarants (sans enfants). */
    private void calculerImpotDeclarants() {
        double revenuParPartDeclarant = revenuFiscalReference / nbPartsDeclarants;
        impotDeclarants = Math.round(appliquerBareme(revenuParPartDeclarant) * nbPartsDeclarants);
    }

    /** Étape 4 : impôt brut calculé avec toutes les parts du foyer (enfants inclus). */
    private void calculerImpotFoyer() {
        double revenuParPartFoyer = revenuFiscalReference / nbPartsFoyer;
        impotFoyer = Math.round(appliquerBareme(revenuParPartFoyer) * nbPartsFoyer);
    }

    /**
     * Applique le barème progressif à un revenu par part et retourne l'impôt par part.
     */
    private double appliquerBareme(double revenuParPart) {
        double impot = 0;
        for (int i = 0; i < TAUX_TRANCHES.length; i++) {
            if (revenuParPart < LIMITES_TRANCHES[i + 1]) {
                impot += (revenuParPart - LIMITES_TRANCHES[i]) * TAUX_TRANCHES[i];
                break;
            } else {
                impot += (LIMITES_TRANCHES[i + 1] - LIMITES_TRANCHES[i]) * TAUX_TRANCHES[i];
            }
        }
        return impot;
    }

    /**
     * Étape 5 : le gain lié aux enfants ne peut pas dépasser un plafond
     * de 1 759 € par demi-part supplémentaire.
     */
    private void appliquerPlafonnementQuotientFamilial() {
        double gainBrutEnfants    = impotDeclarants - impotFoyer;
        double nbDemiPartsEnfants = nbPartsFoyer - nbPartsDeclarants;
        double plafond            = (nbDemiPartsEnfants / 0.5) * PLAFOND_DEMI_PART;

        if (gainBrutEnfants >= plafond) {
            impotFoyer = impotDeclarants - plafond;
        }
    }

    /**
     * Étape 6 : réduction d'impôt pour les contribuables modestes (décote).
     */
    private void appliquerDecote() {
        decote = 0;

        boolean estSeul   = (nbPartsDeclarants == 1);
        boolean estCouple = (nbPartsDeclarants == 2);

        if (estSeul && impotFoyer < SEUIL_DECOTE_SEUL) {
            decote = DECOTE_MAX_SEUL - impotFoyer * TAUX_DECOTE;
        } else if (estCouple && impotFoyer < SEUIL_DECOTE_COUPLE) {
            decote = DECOTE_MAX_COUPLE - impotFoyer * TAUX_DECOTE;
        }

        decote = Math.round(decote);

        // La décote ne peut pas dépasser l'impôt lui-même (pas de remboursement)
        if (impotFoyer <= decote) {
            decote = impotFoyer;
        }

        impotFoyer -= decote;
    }
}
