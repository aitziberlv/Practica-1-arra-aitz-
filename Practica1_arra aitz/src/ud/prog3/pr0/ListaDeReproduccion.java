package ud.prog3.pr0;


import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;


/** Clase para crear instancias como listas de reproducción,
 * que permite almacenar listas de ficheros con posición de índice
 * (al estilo de un array / arraylist)
 * con marcas de error en los ficheros y con métodos para cambiar la posición
 * de los elementos en la lista, borrar elementos y añadir nuevos.
 */
public class ListaDeReproduccion implements ListModel<String> {
	ArrayList<File> ficherosLista;     // ficheros de la lista de reproducción
	int ficheroEnCurso = -1;           // Fichero seleccionado (-1 si no hay ninguno seleccionado)

	private static Logger logger = Logger.getLogger( ListaDeReproduccion.class.getName() );

	private static final boolean ANYADIR_A_FIC_LOG = false; // poner true para no sobreescribir
	static {
	try {
	logger.addHandler( new FileHandler(
	 ListaDeReproduccion.class.getName()+".log.xml", ANYADIR_A_FIC_LOG ));
	} catch (Exception e) {
	logger.log( Level.SEVERE, "Error en creación fichero log" );
	}
	}
	
	/** Constructor de lista de reproducción, crea una lista vacía
	 */
	public ListaDeReproduccion() {
		ficherosLista = new ArrayList<>();
	}
	public void intercambio(int pos1,int pos2) {
		
		if (pos1 <= ficherosLista.size() && pos2 <= ficherosLista.size() && pos1>0 && pos2>0){	
			
//				File temp = ficherosLista.get(pos1);
//				ficherosLista.set(pos1, ficherosLista.get(pos2));
//				ficherosLista.set(pos2, temp);
				
				File fich=ficherosLista.get(pos1);
				File dos= ficherosLista.get(pos2);
				ficherosLista.remove(pos2);
				ficherosLista.add(pos2, fich);
				ficherosLista.remove(pos1);
				ficherosLista.add(pos1, dos);			
		}
	}
	public int size() {
		return ficherosLista.size();
		
	}
	public void add(File f) {
		ficherosLista.add(f);
		avisarAnyadido(ficherosLista.size()-1);
	}
	public void removeFic(int pos) {
		ficherosLista.remove(pos);
	}
	
	public void clear() {
		ficherosLista.clear();
	}
	/** Devuelve uno de los ficheros de la lista
	 * @param posi	Posición del fichero en la lista (de 0 a size()-1)
	 * @return	Devuelve el fichero en esa posición
	 * @throws IndexOutOfBoundsException	Si el índice no es válido
	 */
	public File getFic( int posi ) throws IndexOutOfBoundsException {
		return ficherosLista.get( posi );
		
	}	

	/** Añade a la lista de reproducción todos los ficheros que haya en la 
	 * carpeta indicada, que cumplan el filtro indicado.
	 * Si hay cualquier error, la lista de reproducción queda solo con los ficheros
	 * que hayan podido ser cargados de forma correcta.
	 * @param carpetaFicheros	Path de la carpeta donde buscar los ficheros
	 * @param filtroFicheros	Filtro del formato que tienen que tener los nombres de
	 * 							los ficheros para ser cargados.
	 * 							String con cualquier letra o dígito. Si tiene un asterisco
	 * 							hace referencia a cualquier conjunto de letras o dígitos.
	 * 							Por ejemplo p*.* hace referencia a cualquier fichero de nombre
	 * 							que empiece por p y tenga cualquier extensión.
	 * @return	Número de ficheros que han sido añadidos a la lista
	 */
	
	public int add(String carpetaFicheros, String filtroFicheros) {
		// TODO: Codificar este método de acuerdo a la práctica (pasos 3 y sucesivos)
		
		logger.log( Level.INFO, "Añadiendo ficheros con filtro " + filtroFicheros );
		
		filtroFicheros = filtroFicheros.replaceAll( "\\.", "\\\\." );  // Pone el símbolo de la expresión regular \. donde figure un .
		filtroFicheros = filtroFicheros.replaceAll( "\\*", "\\.*" ); 
		
		logger.log( Level.INFO, "Añadidos los ficheros con filtro " + filtroFicheros );
		
		File fInic = new File(carpetaFicheros);
		if (fInic.isDirectory()) {
			for( File f : fInic.listFiles() ) {
				logger.log( Level.FINE, "Procesando fichero " + f.getName() );
		// TODO: Comprobar que f.getName() cumple el patrón y añadirlo a la lista
				if(Pattern.matches(filtroFicheros, f.getName())) {
					this.add(f);
				}
			}
		}
			
		return this.size();
	}
	
	// Métodos de selección
	//
	
	/** Seleciona el primer fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAPrimero() {
		ficheroEnCurso = 0;  // Inicia
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}
	
	/** Seleciona el último fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAUltimo() {
		ficheroEnCurso = ficherosLista.size()-1;  // Inicia al final
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el anterior fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAAnterior(boolean b) {
		if (b){
			return irARandom();
		}
		if (ficheroEnCurso>=0) ficheroEnCurso--;
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el siguiente fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irASiguiente(boolean b ) {
		if (b) {
			return irARandom();
		}
		ficheroEnCurso++;
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Devuelve el fichero seleccionado de la lista
	 * @return	Posición del fichero seleccionado en la lista de reproducción (0 a n-1), -1 si no lo hay
	 */
	public int getFicSeleccionado() {
		return ficheroEnCurso;
	}

	//
	// Métodos de DefaultListModel
	//
	
	@Override
	public int getSize() {
		return ficherosLista.size();
	}

	@Override
	public String getElementAt(int index) {
		return ficherosLista.get(index).getName();
	}

		// Escuchadores de datos de la lista
		ArrayList<ListDataListener> misEscuchadores = new ArrayList<>();
	@Override
	public void addListDataListener(ListDataListener l) {
		misEscuchadores.add( l );
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		misEscuchadores.remove( l );
	}
	
	// Llamar a este método cuando se añada un elemento a la lista
	// (Utilizado para avisar a los escuchadores de cambio de datos de la lista)
	void avisarAnyadido( int posi ) {
		for (ListDataListener ldl : misEscuchadores) {
			ldl.intervalAdded( new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, posi, posi ));
		}
		
		
	
	}
	/** Selecciona un fichero aleatorio de la lista de reproducción.
	* @return true si la selección es correcta, false si hay error y no se puede seleccionar
	*/
	public boolean irARandom() {
		Random aleatorio = new Random();
		File f =ficherosLista.get(aleatorio.nextInt(ficherosLista.size()));
		ficheroEnCurso = 0;  // Inicia
		while  (ficheroEnCurso>=ficherosLista.size()) {
			if(ficherosLista.get(ficheroEnCurso)==f)
				
				return true;
			ficheroEnCurso+= 1;
			
		}
		ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			
		
		return false;
		
	}
	
	
}
