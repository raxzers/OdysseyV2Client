package clienteOdysseyV2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TablePage {
	/**
     * Numero de pagina
     */
    public int pageNumber;
    /**
     * Numero de canciones totales
     */
    public int totalSongs;
    /**
     * Numero de paginas totales
     */
    public int pages;
    /**
     * Tamano de paginas
     */
    public int pageSize;
    /**
     * Canciones almacenadas en esta pagina
     */
    public ObservableList<Metadata> songs = FXCollections.observableArrayList();
}
