package collaboration.platform.files;

/**
 * <h1>Error</h1> <br>
 * H κλάση Error περιέχει περιέχει το μήνυμα λάθους που θα αποσταλεί στον χρήστη
 * εάν ύπαρξη οποιοδήποτε πρόβλημά στης λειτουργίες του Αpi. Δημιουργούνται
 * αντικείμενα της κλάσης από την κλάση Api η οποία τα κωδικοποιεί μέσω της
 * βιβλιοθήκης Xstream σε json.Για να τα επιστρέψει στον χρήστη σε περίπτωση
 * σφάλματος.
 * 
 * @author Adamos Koumi 949993
 * @version 1.0
 * @since 2015-04-25
 */
public class Error {
	private String message;

	/**
	 * * Κατασκευαστής της κλάσης Error
	 * 
	 * @param message
	 *            Tο μήνυμα λάθους για το πρόβλημα που δημιουργήθηκε
	 */
	public Error(String message) {
		this.message = message;
	}

	/**
	 * Η μέθοδος αλλάζει το error message
	 * @param message Το νέο μήνυμα λάθους 
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
