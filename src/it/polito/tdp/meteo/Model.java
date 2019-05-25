package it.polito.tdp.meteo;

//import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List<Citta> tutteCitta;
//	private List<SimpleCity> tutteCittaSemplici;
	private MeteoDAO dao;

	private List<SimpleCity> bestCitySeq;
	private Double bestScore;

	/**
	 * questo model precarica la lista di citta per singola istanza
	 */
	public Model() {
		dao = new MeteoDAO();
		// precarico la lista delle citta
		tutteCitta = dao.getCitta();
		bestCitySeq = new ArrayList<>();
		bestScore = new Double(Double.MAX_VALUE);

	}

	/**
	 * Dato un mese int tra 1 e 12 restituisce una stringa contenente l'umidità
	 * media per ogni città presente nel DB
	 * 
	 * @param mese il mese che si desidera
	 * @return l'elenco delle umidità medie città per città
	 */
	public String getUmiditaMedia(int mese) {

		List<Rilevamento> res = dao.getAvgRilevamentiMese(mese);

		StringBuilder sb = new StringBuilder();
		for (Rilevamento r : res) {
			sb.append(String.format("Citta: %s, Umidita media: %.2f\n", r.getLocalita(), r.getUmidita()));
		}
		return sb.toString();

	}

	/**
	 * Wrapper della funzione ricorsiva per il calcolo del percorso
	 * 
	 * @param mese
	 * @return
	 */
	public String trovaSequenza(int mese) {

		long start = System.nanoTime();
//		List<SimpleCity> bestSequence = new ArrayList<>();
//		double bestScore = Double.MAX_VALUE;
		magicFunction(new ArrayList<>(), LocalDate.of(0000, mese, 1));
		System.out.println("Ho trovato la sequenza migliore in " + bestCitySeq + " con punteggio " + bestScore);
		long finish = System.nanoTime();
		System.out.format("Tempo impiegato: %.3f secondi", (finish - start) / 1e9);
//		System.out.println("BestScore: " + bestScore);
//		System.out.println("Calcolato: " + punteggioSoluzione(bestSequence));
		return bestCitySeq.toString();
	}

	private void magicFunction(List<SimpleCity> partialCitySeq, LocalDate partialDate) {

//		System.out.format("Dim: %d, Data: %s, valori:%s\n", partialCitySeq.size(), partialDate, partialCitySeq);
//		 try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//		}
		// uscita
		if (partialDate.getDayOfMonth() > NUMERO_GIORNI_TOTALI) {
//			System.out.println("[DEBUG] Troppi giorni totali");
			return;
		}
		// controlli migliore
		else if (controllaParziale(partialCitySeq) && partialDate.getDayOfMonth() == NUMERO_GIORNI_TOTALI) {
			// se la sequenza è valida, quindi ho ngiorni = numerogiornitotale
			Double partialScore = punteggioSoluzione(partialCitySeq);
//			System.out.println("[DEBUG] Seq Valida");

			// controllo se è la sequenza migliore
			if (partialScore < bestScore) {
				// questa che ho trovato è la soluzione migliore
				bestCitySeq.clear();
				bestCitySeq.addAll(partialCitySeq);
				bestScore = partialScore;
//				System.out.println("[DEBUG] Trovata sequenza migliore");
			}
		} else if (controllaParziale(partialCitySeq)) {
			// devo ancora costruire
			for (Citta c : tutteCitta) {
//				System.out.println("[DEBUG] Continuo a costruire");
				int umidita = (int) c.getUmiditaByDate(partialDate).intValue();
//				System.out.format("[DEBUG] [ADD] Citta: %s, Data:%s, Umidita:%d\n", c.getNome(), partialDate, umidita);
				if (umidita == -1) // non ho il valore per quella specifica data, provo il prossimo troncando il
									// ramo
					return;
				SimpleCity sc = new SimpleCity(c.getNome(), umidita);
				List<SimpleCity> newPartial = new ArrayList<>(partialCitySeq);
				newPartial.add(sc);
				magicFunction(newPartial, partialDate.plusDays(1));
			}
		}

	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		// qui do per scontato che la soluzione parziale sia corretta
		// DA ESEGUIRE DOPO CONTROLLAPARZIALE!!!!!!!
		if (soluzioneCandidata.size() == 1)
			// una sola citta considerata
			return new Double(soluzioneCandidata.get(0).getCosto());
		else if (soluzioneCandidata.size() == 0)
			// nessuna citta considerata, non penso possa avvenire
			return -1.0;
		Double score = 0.0;
		for (int i = 1; i < soluzioneCandidata.size(); i++) {
			if (!soluzioneCandidata.get(i - 1).getNome().equals(soluzioneCandidata.get(i).getNome()))
				score += COST;
			score += soluzioneCandidata.get(i).getCosto();
		}

		return score;

	}

	/**
	 * Devo verificare che: stia almeno 3 giorni in quella citta non stia piu di 6
	 * giorni in quella citta
	 * 
	 * @param parziale
	 * @return
	 */
	private boolean controllaParziale(List<SimpleCity> parziale) {
		if (parziale.size() == 0)
			return true;
		// non esegue il controllo sulla durata complessiva
		myEntry inHotel = new myEntry(parziale.get(0).getNome(), 1);
		Map<String, Integer> inCitta = new HashMap<String, Integer>();
		inCitta.put(parziale.get(0).getNome(), 1);

//		System.out.format("Sto valutando la prima citta: %s", parziale.get(0));

		for (int i = 1; i < parziale.size(); i++) {

//			System.out.format("Sto valutando la citta: %s", parziale.get(i));

			try { // OCCHIO CHE NON PARTE DA 1!!
				if (inHotel.getKey().contentEquals(parziale.get(i).getNome())
						&& !parziale.get(i).getNome().contentEquals(parziale.get(i + 1).getNome())) {
					// se il tipo è stato in hotel ed è il suo ultimo giorno
					inHotel.setValue(inHotel.getValue() + 1);
					if (inHotel.getValue() < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
//						System.out.println("[DEBUG] Troppi pochi giorni in hotel");
						return false;
					}
				} else if (inHotel.getKey().contentEquals(parziale.get(i).getNome())) {
					// è rimasto fermo nella stessa città
					inHotel.setValue(inHotel.getValue() + 1);
				} else {
					// vuol dire che si è spostato, l'hotel è nuovo
					inHotel = new myEntry(parziale.get(i).getNome(), 1);
				}
			} catch (IndexOutOfBoundsException e) {
				// nulla da fare, è la prima visita in quella città
			}

			if (inCitta.containsKey(parziale.get(i).getNome())) {
				// la citta è gia presente in quelle visitate
				inCitta.put(parziale.get(i).getNome(), inCitta.get(parziale.get(i).getNome()) + 1);
			} else {
				// la citta non è mai stata visitata
				inCitta.put(parziale.get(i).getNome(), 1);
			}
			if (inCitta.get(parziale.get(i).getNome()) > NUMERO_GIORNI_CITTA_MAX) {
//				System.out.println("[DEBUG] Troppi giorni nella stessa citta (anche distaccati)");
				return false;
			}

		}
		// so far, so good
		return true;
	}

	private class myEntry implements Entry<String, Integer> {

		private String key;
		private Integer value;

		public myEntry(String key, Integer value) {
			super();
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return this.key;
		}

		@Override
		public Integer getValue() {
			return this.value;
		}

		@Override
		public Integer setValue(Integer value) {
			Integer oldValue = this.value;
			this.value = value;
			return oldValue;
		}

	}

	public static void main(String[] args) {
		// test the model
		// questo precarica anche tutte le citta
		Model m = new Model();

//		m.run1(m);
		m.run2(m);
	}

//	@SuppressWarnings("unused")
	private void run2(Model m) {
		m.trovaSequenza(2);
	}

	@SuppressWarnings("unused")
	private void run1(Model m) {
		// sbagliata
		List<SimpleCity> simpleCityList = new ArrayList<>();
		simpleCityList.add(new SimpleCity("genova", 34));
		simpleCityList.add(new SimpleCity("genova", 35));
		simpleCityList.add(new SimpleCity("torino", 36));
		simpleCityList.add(new SimpleCity("milano", 37));
//				if (m.controllaParziale(simpleCityList))
//					System.out.println("parziale corretto");
//				else
//					System.out.println("parziale errato");
//				System.out.println("deve dare errato");
//				System.out.format("Punteggio: %.2f\n", m.punteggioSoluzione(simpleCityList));

		// giusta
		simpleCityList.clear();
		simpleCityList.add(new SimpleCity("genova", 34));
		simpleCityList.add(new SimpleCity("genova", 35));
		simpleCityList.add(new SimpleCity("genova", 36));
		simpleCityList.add(new SimpleCity("torino", 34));
		simpleCityList.add(new SimpleCity("torino", 23));
		simpleCityList.add(new SimpleCity("torino", 65));
		simpleCityList.add(new SimpleCity("torino", 43));
		if (m.controllaParziale(simpleCityList))
			System.out.println("parziale corretto");
		else
			System.out.println("parziale errato");
		System.out.println("deve dare corretto");
		System.out.format("Punteggio: %.2f\n", m.punteggioSoluzione(simpleCityList));

		// sbagliata
		simpleCityList.clear();
		simpleCityList.add(new SimpleCity("genova", 34));
		simpleCityList.add(new SimpleCity("genova", 35));
		simpleCityList.add(new SimpleCity("genova", 36));
		simpleCityList.add(new SimpleCity("torino", 34));
		simpleCityList.add(new SimpleCity("torino", 23));
		simpleCityList.add(new SimpleCity("torino", 65));
		simpleCityList.add(new SimpleCity("torino", 43));
		simpleCityList.add(new SimpleCity("milano", 53));
		simpleCityList.add(new SimpleCity("milano", 63));
		simpleCityList.add(new SimpleCity("milano", 73));
		simpleCityList.add(new SimpleCity("milano", 83));
		simpleCityList.add(new SimpleCity("milano", 93));
		simpleCityList.add(new SimpleCity("milano", 13));
		simpleCityList.add(new SimpleCity("milano", 23));
		if (m.controllaParziale(simpleCityList))
			System.out.println("parziale corretto");
		else
			System.out.println("parziale errato");
		System.out.println("deve dare errato, troppi giorni in una citta sola");
		System.out.format("Punteggio: %.2f\n", m.punteggioSoluzione(simpleCityList));
	}
}
