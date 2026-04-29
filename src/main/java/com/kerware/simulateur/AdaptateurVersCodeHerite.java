package com.kerware.simulateur;

public class AdaptateurVersCodeHerite implements ICalculateurImpot {
// 1. On crée l'instance du vieux code "Legacy"
    private Simulateur vieuxSimulateur = new Simulateur();

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
        // C'est ici que la magie de l'adaptateur opère. 
        // On prend nos variables bien propres et on les injecte dans la grosse fonction illisible du vieux code.
        long resultat = vieuxSimulateur.calculImpot(
            revenusNet, 
            situation, 
            nbEnfants, 
            nbEnfantsHandicap, 
            parentIsole
        );
        
        // On convertit le résultat 'long' en 'int' pour respecter le contrat de l'interface
        this.impotFinal = (int) resultat;
    }

    // --- IMPLEMENTATION DES GETTERS ---

    @Override
    public int getImpotSurRevenuNet() {
        return this.impotFinal;
    }

    @Override
    public int getRevenuFiscalReference() {
        return (int) vieuxSimulateur.getrFRef();
    }

    @Override
    public int getAbattement() {
        return (int) vieuxSimulateur.getAbt();
    }

    @Override
    public int getNbPartsFoyerFiscal() {
        return (int) vieuxSimulateur.getNbPts();
    }

    @Override
    public int getImpotAvantDecote() {
        return (int) (vieuxSimulateur.getmImp() + vieuxSimulateur.getDecote());
    }

    @Override
    public int getDecote() {
        return (int) vieuxSimulateur.getDecote();
    }
}