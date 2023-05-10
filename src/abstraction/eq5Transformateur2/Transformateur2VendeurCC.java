package abstraction.eq5Transformateur2;//Fait par Yassine et Wiem

import java.awt.Color;

import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Lot;


public class Transformateur2VendeurCC extends Transformateur2AcheteurCC implements IVendeurContratCadre {
	public static Color COLOR_LLGRAY = new Color(238,238,238);
	protected SuperviseurVentesContratCadre superviseurVentesCC;
	protected LinkedList<ExemplaireContratCadre> contrats;
	private IProduit produit;
	
	//fait par yassine
	public void initialiser() {
        super.initialiser();
        this.superviseurVentesCC = (SuperviseurVentesContratCadre)(Filiere.LA_FILIERE.getActeur("Sup.CCadre"));
        //this.stockChoco.put(Chocolat.C_MQ,2000.);
    }
	
	
	public Transformateur2VendeurCC() {
		super();  
		this.contrats=new LinkedList<ExemplaireContratCadre>();
	}
	 
	//fait par wiem : nous vendons du chocolat sans et avec marque
	public boolean peutVendre(IProduit produit) {
		return ((produit.getType().equals("Chocolat"))||(produit.getType().equals("ChocolatDeMarque")));} 
	
	//fait par wiem : nous vendons du chocolat de moyenne gamme et haute gamme bioéquitable. La vente est possible ssi le stock est supérieur à 100T
	public boolean vend(IProduit produit) {
		if ((stockChocoMarque.containsKey(produit))&&(produit.getType().equals("ChocolatDeMarque"))&&((((ChocolatDeMarque)produit).getGamme()== Gamme.MQ) ||((((ChocolatDeMarque)produit).getGamme()== Gamme.HQ)&&(((ChocolatDeMarque)produit).isBioEquitable())))){
			if (this.stockChocoMarque.get(produit)>100) { 
				this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nous vendons du " + produit.getType() + " " + produit);
				return true;}
			else {this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nous ne vendons pas de " + produit.getType() + " " + produit );
				return false;}}
		else if ((produit.getType().equals("Chocolat"))&&((((Chocolat)produit).getGamme()== Gamme.MQ) ||((((Chocolat)produit).getGamme()== Gamme.HQ)&&(((Chocolat)produit).isBioEquitable())))){
			if (this.stockChoco.get(produit)>100) { 
				this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nous declarons pouvoir vendre du " + produit.getType() + " " + produit);
				return true;
			}
			else {this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nous ne vendons pas de " + produit.getType() + " " + produit );
				return false;}}
		else {this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nous ne vendons pas de " + produit.getType() + " " + produit );
			return false;}}
		

	//fait par yassine : pas de négociations
	public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
			 
		    double prixMin = 1000.0; // Prix minimum acceptable
		    double soldeDisponible = super.getSolde(); // Solde disponible pour le vendeur
		    double quantiteStock = 0 /*super.getStock()*/; // Quantité de produits en stock
		    double prixNegociation = 1.0; // Coefficient de négociation initial
		    Echeancier echeancierPropose = contrat.getEcheancier(); // Echéancier proposé par l'acheteur
		    Echeancier nouvelEcheancier = new Echeancier(echeancierPropose);
		    
		    // Appliquer le coefficient de négociation en fonction de la quantité de produits en stock
		    if (quantiteStock > 6000.0) {
		        prixNegociation = 0.8; // Réduction de 20% si la quantité de stock est supérieure à 1000
		    }

		    double prixPropose = contrat.getPrix() * prixNegociation; // Prix proposé avec le coefficient de négociation
		    
		    if (prixPropose < prixMin ) {
		        // Si le prix proposé est inférieur au prix minimum, annuler les négociations
		        nouvelEcheancier.vider();
		        return nouvelEcheancier;
		    }
		    
		
		this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : j'accepte l'echeancier "+contrat.getEcheancier());
		return contrat.getEcheancier(); } 


	//fait par wiem : prix sans reel sens, juste indicatifs
	public double propositionPrix(ExemplaireContratCadre contrat) {
		double prix = 0;
		Chocolat cp=null ;
		if ((contrat.getProduit() instanceof Chocolat)) {
			cp=(Chocolat)contrat.getProduit();
		} else {
			cp = ((ChocolatDeMarque)contrat.getProduit()).getChocolat();
		}
		if (cp == Chocolat.C_MQ ) {
			prix = 70.0; }
		if ( cp 
				== Chocolat.C_HQ_BE ) {
		prix = 110.0 ; }
		return prix; }
		

	
	//fait par yassine 
	public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
		double dernierPrix = contrat.getPrix();
		double soldeDisponible = super.getSolde();
		double proposition = 0;

		// Si c'est la première offre, propose un prix supérieur de 15%
		if (contrat.getListePrix().size() == 1) {
		    proposition = dernierPrix * 1.15;
		    {this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nous vous proposons tant " +contrat.getPrix() + "" +proposition);
		}} 
		// Sinon, calcule la proposition en fonction des deux derniers prix
		else {
		    double avantDernierPrix = contrat.getListePrix().get(contrat.getListePrix().size() - 2);

		    // Si le dernier prix est inférieur ou égal à 15% plus élevé que le précédent, accepte le prix
		    if (dernierPrix <= avantDernierPrix * 1.15) {
		        proposition = dernierPrix;
		    } 
		    // Sinon, propose un prix entre les deux derniers prix avec une augmentation de 50%
		    else {
		        proposition = avantDernierPrix + (dernierPrix - avantDernierPrix) * 1.50;
		    }
		}

		// Si la proposition est supérieure à ce que l'acheteur peut payer, propose le maximum possible
		if (proposition > soldeDisponible) {
		    proposition = soldeDisponible;
		}

		// Retourne la proposition de prix
		return proposition;
		/*return contrat.getPrix(); */
	}
	
	

	//fait par yassine : renvoie la quantité livrée, met à jour les stocks.
	public Lot livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
		double stock=0.0;
		double livre=0.0;
		Lot lot = null;
		if (produit instanceof ChocolatDeMarque) {
			if (this.stockChocoMarque.keySet().contains(produit)) {
				stock= this.stockChocoMarque.get(produit);
				livre = Math.min(stock, quantite);
				if (livre==0) {
					this.stockChocoMarque.put((ChocolatDeMarque)produit, this.stockChocoMarque.get(produit)-livre);
				}
				lot=new Lot((ChocolatDeMarque)produit);
			}
		} else if (produit instanceof Chocolat) {
			if (this.stockChoco.keySet().contains(produit)) {
				stock= this.stockChoco.get(produit);
				livre = Math.min(stock, quantite);
				if (livre==0) {
					this.stockChoco.put((Chocolat)produit, this.stockChoco.get(produit)-livre);
				}
				lot=new Lot((Chocolat)produit);
			}}
		this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : doit livrer "+quantite+" de "+produit+" --> livre "+livre);
		lot.ajouter(Filiere.LA_FILIERE.getEtape(), livre);
		return lot;
	}
	
	
	//fait par yassine  : ajout au journal des propositions de contrats cadres
	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "  CCV : nouveau cc conclu "+contrat);
	}


	//fait par wiem  : on cherche un acheteur potentiel et on établit un contrat avec 
	public ExemplaireContratCadre getContrat(Chocolat produit) {
    	this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "Recherche acheteur pour " + produit);
    	List<IAcheteurContratCadre> acheteurs = superviseurVentesCC.getAcheteurs(produit);
    	IAcheteurContratCadre acheteur = acheteurs.get((int)(Math.random() * acheteurs.size())); 
    	
    	this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "Tentative de négociation de contrat cadre avec " + acheteur.getNom() + " pour " + produit);
        ExemplaireContratCadre cc = superviseurVentesCC.demandeVendeur(acheteur, this, produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, (SuperviseurVentesContratCadre.QUANTITE_MIN_ECHEANCIER+10.0)/10), cryptogramme,false);
        if (cc != null) {   
        		this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "Contrat cadre passé avec " + acheteur.getNom() + " pour " + produit + "CC : " + cc);
        	} else {
        		this.journalVentes.ajouter(COLOR_LLGRAY, Color.BLUE, "Echec de la négociation de contrat cadre avec " + acheteur.getNom() + " pour " + produit);
        	}
        	return cc; 
    	}
    
   //fait par wiem 
	public void next() {
	super.next();
	this.getContrat(Chocolat.C_MQ);
	this.getContrat(Chocolat.C_HQ_BE);

    }
	

}
