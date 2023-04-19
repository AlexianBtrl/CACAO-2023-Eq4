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
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.produits.Lot;

public class DistributeurContratCadreAcheteur extends Distributeur1Acteur implements IAcheteurContratCadre{
	protected List<ExemplaireContratCadre> mesContratEnTantQuAcheteur;
	private List<Object> negociations = new ArrayList<>();
	private double minNego=5;


	
	public DistributeurContratCadreAcheteur() {
		super();
		this.mesContratEnTantQuAcheteur=new LinkedList<ExemplaireContratCadre>();
	}

	
	
	public Echeancier echeancier_strat(int stepDebut, int quantite, int nb_step) {
		Echeancier e = new Echeancier(stepDebut, nb_step, quantite/nb_step);
		return e;
		
	}
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		if (Math.random()<0.1) {
			return contrat.getEcheancier(); // on ne cherche pas a negocier sur le previsionnel de livraison
		} else {//dans 90% des cas on fait une contreproposition pour l'echeancier
			Echeancier e = contrat.getEcheancier();
			e.set(e.getStepDebut(), e.getQuantite(e.getStepDebut())*2.5);// on souhaite livrer 2.5 fois plus lors de la 1ere livraison... un choix arbitraire, juste pour l'exemple...
			return e;
		}
	}

	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		
		if (Math.random()<0.1) {
			return contrat.getPrix(); // on ne cherche pas a negocier dans 10% des cas
		} else {//dans 90% des cas on fait une contreproposition differente
			return contrat.getPrix()*0.95;// 5% de moins.
		}
	}
//	AUTRE VERSION POSSINLE DE CONTREPROPOSITIONPRIXACHETEUR
//
//	public double contrePropositionPrixAcheteur_negociations(ExemplaireContratCadre contrat) {
//	int step_nego = step_nego ( contrat);
//	if (step_nego<minNego) {
//		return contrat.getPrix()*0.95
//	}
	
	
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
     * @author Ghaly sentissi
     */
	public void next() {
		super.next();
		enleve_contrats_obsolete();
		Echeancier echeancier_type = new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, 10.0);

//		journal.ajouter("Recherche d'un vendeur aupres de qui acheter");
		
		
		IProduit produit = Chocolat.C_BQ;
		
//		for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
//
//			if (acteur!=this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(produit)) {
//				getContractForProduct(produit,acteur);
//				}
//			else {
//
//			}
		
		
		// OU proposition d'un contrat a un des vendeurs choisi aleatoirement
		SuperviseurVentesContratCadre superviseurVentesCC = (SuperviseurVentesContratCadre)(Filiere.LA_FILIERE.getActeur("Sup.CCadre")); 

		journal_achat.ajouter("Recherche d'un vendeur aupres de qui acheter");
		List<IVendeurContratCadre> vendeurs = superviseurVentesCC.getVendeurs(produit);
		if (vendeurs.contains(this)) {
			vendeurs.remove(this);
		}
		IVendeurContratCadre vendeur = null;
		if (vendeurs.size()==1) {
			vendeur=vendeurs.get(0);
		} else if (vendeurs.size()>1) {
			vendeur = vendeurs.get((int)( Math.random()*vendeurs.size()));
		}
		if (vendeur!=null) {
			
			getContractForProduct(produit,echeancier_type,vendeur);

		}
		
}
    /**
     * retourne l'étape de négociation en cours
     * @param contrat     
     * @author Ghaly sentissi
     */
	public double step_nego (ExemplaireContratCadre contrat) {
		return contrat.getListePrix().size()/2;
	}	
			
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
	
	
	
	public void receptionner(Lot lot, ExemplaireContratCadre contrat) {
//		stock.ajouter(this, lot.getQuantiteTotale()); // Cet exemple ne gere pas la peremption : il n'utilise pas la mention du step de production du produit
	}

	public boolean achete(IProduit produit) {
		return true;
	}
	public String toString() {
		return this.getNom();
	}

	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 5;
	}
    /**
     * Cette méthode va essayer de lancer un contrat cadre d'un produit avec un acteur donné
     * @param produit le produit qu'on veut vendre
     * @param acteur l'acteur à qui on essaye de vendre
     * @return le contrat s'il existe, sinon null
     * @author Ghaly sentissi
     */
    public ExemplaireContratCadre getContractForProduct(IProduit produit,Echeancier e,  IActeur acteur) {
        // First we need to select a buyer for the product
        this.journal_achat.ajouter(Color.LIGHT_GRAY, Color.BLACK, "Recherche acheteur pour " + produit + "...");

        // Now making the contract
        this.journal_achat.ajouter(Color.LIGHT_GRAY, Color.BLACK, "Tentative de négociation de contrat cadre avec " + acteur.getNom() + " pour " + produit + "...");
        int length = ((int) Math.round(Math.random() * 10)) + 1;
    	SuperviseurVentesContratCadre superviseurVentesCC = (SuperviseurVentesContratCadre)(Filiere.LA_FILIERE.getActeur("Sup.CCadre")); 

		ExemplaireContratCadre cc = superviseurVentesCC.demandeAcheteur((IAcheteurContratCadre)this, ((IVendeurContratCadre)acteur), produit, e, cryptogramme,false);
        if (cc != null) {
            this.journal_achat.ajouter(Color.LIGHT_GRAY, Color.BLACK, "Contrat cadre passé avec " + acteur.getNom() + " pour " + produit + "\nDétails : " + cc + "!");
        } else {
            this.journal_achat.ajouter(Color.LIGHT_GRAY, Color.BLACK, "Echec de la négociation de contrat cadre avec " + acteur.getNom() + " pour " + produit + "...");
        }
        return cc;
    }

	@Override
	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		journal.ajouter("Les negociations avec "+ contrat.getVendeur().getNom()+" ont abouti à un contrat cadre de "+contrat.getProduit().toString());
		
	}






}
