package it.polito.tdp.meteo.db;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;

@SuppressWarnings("unused")
public class TestMeteoDAO {

	public static void main(String[] args) {

		MeteoDAO dao = new MeteoDAO();

//		List<Rilevamento> list = dao.getAllRilevamenti();
//
//		// STAMPA: localita, giorno, mese, anno, umidita (%)
//		for (Rilevamento r : list) {
//			System.out.format("%-10s %2td/%2$2tm/%2$4tY %3d%%\n", r.getLocalita(), r.getData(), r.getUmidita());
//		}

//		System.out.println(dao.getAllRilevamentiLocalitaMese(1, "Genova"));
//		System.out.println(dao.getAvgRilevamentiLocalitaMese(1, "Genova"));
//		
//		System.out.println(dao.getAllRilevamentiLocalitaMese(5, "Milano"));
//		System.out.println(dao.getAvgRilevamentiLocalitaMese(5, "Milano"));
//		
//		System.out.println(dao.getAllRilevamentiLocalitaMese(5, "Torino"));
//		System.out.println(dao.getAvgRilevamentiLocalitaMese(5, "Torino"));

		List<Citta> citta = dao.getCitta();
		System.out.println(citta);
		for (Citta c : citta) {
			System.out.println("Ci sono " + c.getRilevamenti().size() + " rilevamenti per " + c.getNome());
			System.out.println(c.getRilevamenti());
		}

		System.out.format("Umidità media a Torino nel mese di febbraio %.2f\n",
				dao.getAvgRilevamentiLocalitaMese(2, "Torino"));
		System.out.format("Umidità media a Genova nel mese di giugnio %.2f\n",
				dao.getAvgRilevamentiLocalitaMese(6, "Genova"));
		System.out.format("Umidità media a Milano nel mese di dicembre %.2f\n",
				dao.getAvgRilevamentiLocalitaMese(12, "Milano"));

		List<Rilevamento> rilevamenti = dao.getAllRilevamentiLocalitaMese(2, "Milano");
		System.out.println("\n\n\nOttengo tutti i rilevamenti per Milano, sono " + rilevamenti.size());
		System.out.println(rilevamenti);
		
		System.out.format("Citta: %s ", citta.get(0).getNome());
		System.out.println(citta.get(0).getRilevamenti());

		LocalDate ld = LocalDate.of(2013, 01, 29);
		// non funziona porca troia
		System.out.println(citta.get(0).getUmiditaByDate(ld));
		System.out.format("La mia data è: %s", ld);
		

	}

}
