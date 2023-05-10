package abstraction.eq2Producteur2;

import abstraction.eqXRomu.bourseCacao.IVendeurBourse;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Lot;

public class Producteur2ASPPVendeurBourse extends Producteur2ASProducteurPlanteur implements IVendeurBourse{
	
	//  code écrit par Flavien

	/**
	 * Retourne la quantite en tonnes de feves de type f que le vendeur 
	 * souhaite vendre a cette etape sachant que le cours actuel de 
	 * la feve f est cours
	 * @param f le type de feve
	 * @param cours le cours actuel des feves de type f
	 * @return la quantite en tonnes de feves de type f que this souhaite vendre 
	 */
	
	public double stock_mis_en_bourse(Feve f) {
		if (f==Feve.F_BQ) {
			return this.getStocksTotTheo(Feve.F_BQ, Filiere.LA_FILIERE.getEtape()).get(Filiere.LA_FILIERE.getEtape());//renvoie le stock dispo pour la bourse à l'étape actuelle
		}
		if (f==Feve.F_MQ) {
			return this.getStocksTotTheo(Feve.F_MQ, Filiere.LA_FILIERE.getEtape()).get(Filiere.LA_FILIERE.getEtape());
		}
		if (f==Feve.F_MQ_BE) {
			return this.getStockTotTime(Feve.F_MQ_BE, (int)this.stepsVecuesPourBourseMQ_BE.getValeur());
		}//la fonction getStockTotTime renvoie pour le type de fève mis en premier argument, la quantité de fève dont le step d'âge est au-moins celui mis en 2e argument
		
		return 0;
	}
		
	// Cette fonction en-dessous doit servir à déterminer la quantité de chaque fève mis en bourse en fonction du cours de la bourse et des quantités disponible,
	// attention à bien décrémenter le stock entre les étapes, notamment celui du contrat cadre.
	
	
	public double offre(Feve f, double cours_de_f) {
		if (f==Feve.F_BQ) {
			// Regarder en fonction du cours de F et de la fève  un seuil à partir duquel il est acceptable de vendre, et en quelles quantités
			// cela passe par la création de 2 prix seuil, un à partir duquel on commence à vendre, et un autre à partir duquel on vend tout le stock
			// avec une continuité linéaire de la relation prix/proportion_vendue entre ces 2 points
			// prix_seuil_1=1, prix_seuil_2=10, ces prix sont pour l'instant arbitraires
			float prix_seuil_1=2000;
			float prix_seuil_2=2500;
			if (cours_de_f < prix_seuil_1 || this.Rentabilites(f, cours_de_f)==false) {
				return 0;
			}
			if (this.Rentabilites(f, cours_de_f)==true && cours_de_f < prix_seuil_1) { //si le cours permet de faire au moins 10% de profit, on ne met en vente que les fèves 
				return this.getStockTotTime(Feve.F_BQ, (int)this.stepsVecuesPourBourseBQ.getValeur()); //BQ qui vont bientôt disparaître
			}
			if (cours_de_f >= prix_seuil_1 && cours_de_f <= prix_seuil_2) { // si le prix est suffisemment élevé, on met en vente aussi les fèves de MQ qui ne sont pas proches d'être déclassées
				return (stock_mis_en_bourse(f)-this.getStockTotTime(Feve.F_BQ, (int)this.stepsVecuesPourBourseBQ.getValeur()))*(cours_de_f - prix_seuil_1)/(prix_seuil_2 - prix_seuil_1) + this.getStockTotTime(Feve.F_BQ, (int)this.stepsVecuesPourBourseBQ.getValeur());
			}
			if(cours_de_f >= prix_seuil_2) {
				return stock_mis_en_bourse(f);
			}
		}
		if (f==Feve.F_MQ) {
			float prix_seuil_1=2400;
			float prix_seuil_2=3000;
			if (cours_de_f < prix_seuil_1 || this.Rentabilites(f, cours_de_f)==false) { //si le cours de f est trop bas, où que le cours n'assure pas les 10% minimum de rentabilités on n'en met pas en bourse
				return 0;
			}
			if (this.Rentabilites(f, cours_de_f)==true && cours_de_f < prix_seuil_1) { //si le cours permet de faire au moins 10% de profit, on ne met en vente que les fèves 
				return this.getStockTotTime(Feve.F_MQ, (int)this.stepsVecuesPourBourseMQ.getValeur()); //MQ qui vont bientôt se déclasser en BQ
			}
			if (cours_de_f >= prix_seuil_1 && cours_de_f <= prix_seuil_2) { // si le prix est suffisemment élevé, on met en vente aussi les fèves de MQ qui ne sont pas proches d'être déclassées
				return (stock_mis_en_bourse(f)-this.getStockTotTime(Feve.F_MQ,(int)this.stepsVecuesPourBourseMQ.getValeur()))*(cours_de_f - prix_seuil_1)/(prix_seuil_2 - prix_seuil_1) + this.getStockTotTime(Feve.F_MQ, (int)this.stepsVecuesPourBourseMQ.getValeur());
			}
			if(cours_de_f >= prix_seuil_2) {
				return stock_mis_en_bourse(f);
			}
		}	
		//		           ON NE VEUT VENDRE EN BOURSE QUE DES FEVES BQ ET MQ ou des feves MQ BE proches de la date de péremption 
//					c'est à dire des fèves MQ BE ayant plus de 10 steps d'âge (et des fèves HQ BE ayant plus de 12 steps d'âges
//					car elles ont rétrogradé en fève MQ BE proches de la destruction)
		if (f==Feve.F_MQ_BE ){ 
			if (this.Rentabilites(f, cours_de_f)==false) {
				return 0;
			}
			if (this.Rentabilites(f, cours_de_f)==true) { //si le cours permet de faire au moins 10% de profit, on ne met en vente que les fèves 
				return this.stock_mis_en_bourse(f); //MQ_BE qui vont bientôt se déclasser en BQ ou disparaitre (si elles ont déjà été déclassées depuis HQ_BE)
			}  
		}
		return 0;
	}

	/**
	 * Methode appelee par la bourse pour avertir le vendeur qu'il est parvenu
	 * a vendre quantiteEnT tonnes de feve f au prix de coursEnEuroParT euros par tonne.
	 * L'acteur this doit retourner un Lot de feves F de quantite totale>=quantiteEnT et 
	 * retirer ces feves livrees de son stock de feves .
	 * Lorsque cette methode est appelee la transaction bancaire a eu lieu 
	 * (vendeurs et acheteurs n'ont pas a s'occuper du virement)
	 * On vend en bourse les fèves BQ et MQ mais aussi les fèves HQ_BE déclassées en MQ_BE, et les fèves MQ_BE déclassées en BQ.
	 * 
	 */
	public Lot notificationVente(Feve f, double quantiteEnT, double coursEnEuroParT) {
		double quantiteLivre = 0.;
		if (f == Feve.F_BQ) {
			quantiteLivre = Math.min(quantiteEnT, this.getStockTot(f).getValeur());//on renvoie le min entre ce qu'on a et ce qu'on a promis
			this.BQquantiteVendueBourse.setValeur(this, quantiteLivre, this.cryptogramme);
		}
		if (f == Feve.F_MQ) {
			if (this.getStockTotTime(Feve.F_MQ_BE, (int)this.stepsVecuesPourBourseMQ_BE.getValeur()) > 0) {
				double quantiteBEDeclassee = Math.min(quantiteEnT, this.getStockTotTime(Feve.F_MQ_BE,(int)this.stepsVecuesPourBourseMQ_BE.getValeur()));
				this.convertStockMQ_BE(quantiteBEDeclassee);
			}
			quantiteLivre = Math.min(quantiteEnT, this.getStockTot(f).getValeur());//on renvoie le min entre ce qu'on a et ce qu'on a promis
			this.MQquantiteVendueBourse.setValeur(this, quantiteLivre, this.cryptogramme);
		}
		if (f == Feve.F_BQ)
            this.argentVenteBQ.ajouter(this, quantiteLivre * coursEnEuroParT, this.cryptogramme);
        else
            this.argentVenteMQ.ajouter(this, quantiteLivre * coursEnEuroParT, this.cryptogramme);
		this.journalBourse.ajouter("Nous avons vendu une quantité " + quantiteLivre + "T de fèves de type " + f);
		return this.retirerStock(f, quantiteLivre);
	}

	/**
	 * Methode appelee par la bourse pour avertir le vendeur qu'il vient 
	 * d'etre ajoute a la black list : l'acteur a precise une quantite qu'il desirait mettre en vente
	 * qu'il n'a pas pu honorer (le lot qu'il a retourne n'etait pas du bon 
	 * type de feves ou de quantite insuffisante)
	 * this ne pourra pas vendre en bourse pendant la duree precisee en 
	 * parametre 
	 */
	public void notificationBlackList(int dureeEnStep) {
		this.journal.ajouter("Aie... blackliste pendant 6 steps");
	}

	public void next() {
		super.next();
		this.BQquantiteVendueBourse.setValeur(this, 0, this.cryptogramme);
		this.MQquantiteVendueBourse.setValeur(this, 0, this.cryptogramme);
	}
	

}
