package com.kerware.simulateur;

public class AdaptateurVersCodeHerite implements ICalculateurImpot {
// 1. On crée l'instance du vieux code "Legacy"
   

    // 2. On déclare les variables pour mémoriser les entrées de l'utilisateur
    private int revenusNet;
    private SituationFamiliale situation;
    private int nbEnfants;
    private int nbEnfantsHandicap;
    private boolean parentIsole;

    // 3. On déclare la variable pour stocker le résultat final du calcul
    private int impotFinal;

    // --- IMPLEMENTATION DES SETTERS ---

    @Override
    public void setRevenusNet(int rn) {
        this.revenusNet = rn;
    }

    @Override
    public void setSituationFamiliale(SituationFamiliale sf) {
        this.situation = sf;
    }

    @Override
    public void setNbEnfantsACharge(int nbe) {
        this.nbEnfants = nbe;
    }

    @Override
    public void setNbEnfantsSituationHandicap(int nbesh) {
        this.nbEnfantsHandicap = nbesh;
    }

    @Override
    public void setParentIsole(boolean pi) {
        this.parentIsole = pi;
    }

    // --- LE MOTEUR DU CALCUL ---

    
    @Override
    public void calculImpotSurRevenuNet() {
        // ON CRÉE UN SIMULATEUR TOUT NEUF À CHAQUE CALCUL !
        Simulateur simulateurFrais = new Simulateur();
        
        long resultat = simulateurFrais.calculImpot(
            revenusNet, 
            situation, 
            nbEnfants, 
            nbEnfantsHandicap, 
            parentIsole
        );
        
        this.impotFinal = (int) resultat;
    }

    // --- IMPLEMENTATION DES GETTERS ---

    @Override
    public int getImpotSurRevenuNet() {
        return this.impotFinal;
    }

    // --- METHODES NON DISPONIBLES SUR LE VIEUX CODE ---
    // Le vieux simulateur est une "boîte noire" qui ne donne que le résultat final.
    // On ne peut pas facilement extraire l'abattement ou la décote sans casser le code.
    // On renvoie donc 0 par défaut en attendant de construire ton "SimulateurAmeliore".

    @Override
    public int getRevenuFiscalReference() {
        return 0;
    }

    @Override
    public int getAbattement() {
        return 0;
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        return 0; 
    }

    @Override
    public int getImpotAvantDecote() {
        return 0;
    }

    @Override
    public int getDecote() {
        return 0;
    }
}