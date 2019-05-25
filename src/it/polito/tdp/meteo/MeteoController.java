package it.polito.tdp.meteo;

import java.net.URL;
//import java.sql.Time;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
import java.time.Month;
//import java.util.Date;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

public class MeteoController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ChoiceBox<String> boxMese;

	@FXML
	private Button btnCalcola;

	@FXML
	private Button btnUmidita;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCalcolaSequenza(ActionEvent event) {
		String monthString = boxMese.getSelectionModel().getSelectedItem();
		int month = Month.valueOf(monthString).getValue();
		txtResult.setText("Inizio il calcolo, questo puo richiedere fino a 30 secondi");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		String sequence = model.trovaSequenza(month);
		txtResult.setText("Migliore sequenza trovata:\n");
		txtResult.appendText(sequence);
	}

	@FXML
	void doCalcolaUmidita(ActionEvent event) {
		String monthString = boxMese.getSelectionModel().getSelectedItem();
		int month = Month.valueOf(monthString).getValue();
		String resultString = model.getUmiditaMedia(month);
		txtResult.setText("L'umidità nelle citta salvate è:\n");
		txtResult.appendText(resultString);

	}

	@FXML
	void initialize() {
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnCalcola != null : "fx:id=\"btnCalcola\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert btnUmidita != null : "fx:id=\"btnUmidita\" was not injected: check your FXML file 'Meteo.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Meteo.fxml'.";

		for (Month m : Month.values())
			boxMese.getItems().add(m.name());
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
