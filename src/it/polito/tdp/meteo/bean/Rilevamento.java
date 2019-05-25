package it.polito.tdp.meteo.bean;

import java.time.LocalDate;

public class Rilevamento {

	private String localita;
	private LocalDate data;
	private double umidita;

	public Rilevamento(String localita, LocalDate data, double umidita) {
		super();
		this.localita = localita;
		this.data = data;
		this.umidita = umidita;
	}

	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public double getUmidita() {
		return umidita;
	}

	public void setUmidita(int umidita) {
		this.umidita = umidita;
	}

	@Override
	public String toString() {
		return String.format("Rilevamento [localita=%s, data=%s, umidita=%s]", localita, data, umidita);
	}

//	@Override
//	public String toString() {
//		return String.valueOf(umidita);
//	}

}
