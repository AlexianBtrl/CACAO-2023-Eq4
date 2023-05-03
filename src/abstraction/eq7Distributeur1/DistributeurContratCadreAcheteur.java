package abstraction.eq7Distributeur1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Lot;

public class DistributeurContratCadreAcheteur extends Distributeur1Acteur implements IAcheteurContratCadre{
	protected List<ExemplaireContratCadre> mesContratEnTantQuAcheteur;
	protected List<ExemplaireContratCadre> historique_de_mes_contrats;
	protected SuperviseurVentesContratCadre superviseurVentesCC;
	private List<Object> negociations = new ArrayList<>();
	private double minNego=5;


	public void initialiser() {
		super.initialiser();
		this.superviseurVentesCC = (SuperviseurVentesContratCadre)(Filiere.LA_FILIERE.getActeur("Sup.CCadre"));
	}
	
	public DistributeurContratCadreAcheteur() {
		super();
		this.mesContratEnTantQuAcheteur=new LinkedList<ExemplaireContratCadre>();
	}
	
	/**
	 * @author Theo
	 * @return echeancier sur 1 an, base sur les previsions de ventes
	 */
	public Echeancier echeancier_strat(int stepDebut, ChocolatDeMarque marque) {
		Echeancier e = new Echeancier(stepDebut);
		for (int etape = stepDebut+1; etape<stepDebut+25; etape++) {
			int etapemod = etape%24;
			e.ajouter(previsionsperso.get(etapemod).get(marque)*1.5);
		}
		return e;
	}
	
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		if (Math.random()<0.3) {
			return contrat.getEcheancier(); // on ne cherche pas a negocier sur le previsionnel de livraison
		} else {//dans 70% des cas on fait une contreproposition pour l'echeancier
			Echeancier e = contrat.getEcheancier();
			int stepdebut = e.getStepDebut();
			for (int step = stepdebut; step < e.getStepFin()+1; step++) {
				e.set(step, e.getQuantite(step)*0.9);
			}
			return e;
		}
	}

	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		
		if (Math.random()<0.3) {
			return contrat.getPrix(); // on ne cherche pas a negocier dans 30% des cas
		} else {//dans 70% des cas on fait une contreproposition differente
			return contrat.getPrix()*0.95;// 5% de moins.
		}
	}
	
	
    /**
     * 	enleve les contrats obsolete (nous pourrions vouloir les conserver pour "archive"...)
     * @author Ghaly sentissi
     */

	public void enleve_contrats_obsolete() {
		List<ExemplaireContratCadre> contratsObsoletes=new LinkedList<ExemplaireContratCadre>();
		for (ExemplaireContratCadre contrat : this.mesContratEnTantQuAcheteur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
			}
		}
		this.mesContratEnTantQuAcheteur.removeAll(contratsObsoletes);
	}

	/**   
	 * proposition d'un contrat a un des vendeurs choisi aleatoirement
	 * @param produit le produit qu'on veut vendre
	 * @return le contrat s'il existe, sinon null
     * @author Ghaly sentissi
     */
	public ExemplaireContratCadre getContrat(IProduit produit,Echeancier e) {

		this.journal_achat.ajouter("Recherche acheteur pour " + produit + "...");
		List<IVendeurContratCadre> vendeurs = superviseurVentesCC.getVendeurs(produit);
		ExemplaireContratCadre cc = null;
		
		//On parcourt tous les vendeurs aleatoirement
		while (!vendeurs.isEmpty() && cc == null) {
			IVendeurContratCadre vendeur = null;
			if (vendeurs.size()==1) {
				vendeur=vendeurs.get(0);
			} else if (vendeurs.size()>1) {
				vendeur = vendeurs.get((int)( Math.random()*vendeurs.size()));
			}
			vendeurs.remove(vendeur);
			if (vendeur!=null) {
				//On lance la procedure
				this.journal_achat.ajouter("Tentative de négociation de contrat cadre avec "+vendeur.getNom()+" pour "+produit+"...");
				cc = superviseurVentesCC.demandeAcheteur((IAcheteurContratCadre)this, ((IVendeurContratCadre)vendeur), produit, e, cryptogramme,false);
			
				if (cc != null) { //Si la procedure aboutit
					this.journal_achat.ajouter("Contrat cadre passé avec "+vendeur.getNom()+" pour "+produit+"\nDétails : "+cc+"!");
		        	mesContratEnTantQuAcheteur.add(cc);
		        	
		        	//Actualisation du cout
		        	double nvcout = getCout((ChocolatDeMarque) cc.getProduit());
		        	nvcout = (nvcout + (cc.getPrix()/cc.getQuantiteTotale()))/2;
		        	couts((ChocolatDeMarque) cc.getProduit(), nvcout);
		        }
				else {
		            this.journal_achat.ajouter("Echec de la négociation de contrat cadre avec "+vendeur.getNom()+" pour "+produit);
		        }
			}
		}
		return cc;
	}
	
	/**
	 * @author Theo
	 * @return la qte d'un produit devant se faire livrer dans l'annee prochaine (en supposant que la duree d'un CC <= 1 an
	 */
	public double getLivraison(IProduit produit) {
		double somme = 0;
		for (ExemplaireContratCadre contrat : mesContratEnTantQuAcheteur) {
			if (contrat.getProduit() == produit) {
				somme += contrat.getQuantiteRestantALivrer();
			}
		}
		return somme;
	}
	
	public double getLivraisonEtape(IProduit produit) {
		double somme = 0;
		for (ExemplaireContratCadre contrat : mesContratEnTantQuAcheteur) {
			if (contrat.getProduit() == produit) {
				somme += contrat.getQuantiteALivrerAuStep();
			}
		}
		return somme;
	}
	
	/**
	 * @author Ghaly & Theo
	 */
	public void next() {
		super.next();
		enleve_contrats_obsolete();
		
		//On va regarder si on a besoin d'un nouveau contrat cadre pour chaque marque
		if (this.superviseurVentesCC!=null) {
			int etape = Filiere.LA_FILIERE.getEtape();
			for (ChocolatDeMarque marque : Filiere.LA_FILIERE.getChocolatsProduits()) {
				//V2 : ENLEVER LES MARQUES DISTRIBUTEUR
				double previsionannee = 0;
				for (int numetape = etape+1; numetape < etape+25 ; numetape++ ) {
					previsionannee += previsionsperso.get(numetape%24).get(marque);
				}
				if (previsionannee > stockChocoMarque.get(marque)+getLivraison(marque)) { //On lance un CC seulement si notre stock n'est pas suffisant sur l'annee qui suit
					Echeancier echeancier = echeancier_strat(etape+1,marque);
					journal_achat.ajouter("Recherche d'un vendeur aupres de qui acheter "+ marque.getNom());

					ExemplaireContratCadre cc = getContrat(marque,echeancier);
					}
				}
			}						
		}
		
	@Override
	/**
	 * @author Theo
	 */
	// A COMPLETER SI ASSEZ DE STOCK (appele si cc initie par vendeur)
	public boolean achete(IProduit produit) {
		if (produit instanceof ChocolatDeMarque) {
			return true;
		}
		return false;
	}
    /**
     * retourne l'étape de négociation en cours
     * @param contrat     
     * @author Ghaly sentissi
     */
	public double step_nego (ExemplaireContratCadre contrat) {
		return contrat.getListePrix().size()/2;
	}
	
//	public double contrePropositionPrixAcheteur_negociations(ExemplaireContratCadre contrat) {
//		int step_nego = step_nego ( contrat);
//		if (step_nego<minNego) {
//			return contrat.getPrix()*0.95
//		}
			
			
    /**
  
     * @author Ghaly sentissi
     */
	public void best_prix(IProduit produit, ExemplaireContratCadre contrat) {
		
		List<Object> list = new ArrayList<>();
		double prix_au_min_nego = contrat.getListePrix().get(2*(int)(minNego)-1);
		if (list.isEmpty()) {
			list.add(contrat.getVendeur());
			list.add(prix_au_min_nego);
			list.add(minNego);			
		}
		else {
			
//			if (Double.compare((double)(list.get(1)), prix_au_min_nego)) {
//				list.removeAll(list);
//				list.add(contrat.getVendeur());
//				list.add(prix_au_min_nego);
//				list.add(step_nego(contrat));		
//			}
	
		}
	}
//	public String meilleur_prix(Echeancier e,IProduit produit) {
//		HashMap<IActeur, Double> res= new HashMap<>();
//		int minNego=5;
//		
//		for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
//			if (acteur!=this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(produit)) {
//				ExemplaireContratCadre cc = superviseurVentesCC.demandeAcheteur((IAcheteurContratCadre)this, (IVendeurContratCadre) acteur, produit,e, cryptogramme,false);
//			}}
//		
//		return "ok";
//	}
	
//	public static Integer obtenirValeurMinimale(HashMap<String, Integer> hashMap) {
//        Integer valeurMinimale = null;
//        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
//            Integer valeur = entry.getValue();
//            if (valeurMinimale == null || valeur < valeurMinimale) {
//                valeurMinimale = valeur;
//            }
//        }
//        return valeurMinimale;	}
	
	
	@Override
	/**
	 * @author Theo
	 */
	public void receptionner(Lot lot, ExemplaireContratCadre contrat) {
		IProduit produit= lot.getProduit();
		double quantite = lot.getQuantiteTotale();
		if (produit instanceof ChocolatDeMarque) {
			if (this.stockChocoMarque.keySet().contains(produit)) {
				this.stockChocoMarque.put((ChocolatDeMarque)produit, this.stockChocoMarque.get(produit)+quantite);
			} else {
				this.stockChocoMarque.put((ChocolatDeMarque)produit, quantite);
			}
			this.totalStocks.ajouter(this, quantite, this.cryptogramme);
			this.journal.ajouter("Reception de "+quantite+" T de "+produit+". Stock->  "+this.stockChocoMarque.get(produit));
		}
	}

	public String toString() {
		return this.getNom();
	}

	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 5;
	}

	@Override
	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		journal.ajouter("Les negociations avec "+ contrat.getVendeur().getNom()+" ont abouti à un contrat cadre de "+contrat.getProduit().toString());
		
	}






}
