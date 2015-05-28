package collaboration.platform.files;

import java.util.ArrayList;

/**
 * <h1>Response</h1> <br>
 * H κλάση Response περιέχει μια λίστα από documents ,μια λίστα από Rooms και
 * ένα αντικείμενό της κλάσης Εrror. Δημιουργούνται αντικείμενα της κλάσης από
 * την κλάση Api η οποία τα κωδικοποιεί μέσω της βιβλιοθήκης Xstream σε json.Για
 * να τα επιστρέψει στον χρήστη.
 * 
 * @author Adamos Koumi 949993
 * @version 1.0
 * @since 2015-04-25
 */

public class Response {
	ArrayList<Document> documents;
	ArrayList<Room> rooms;
	Error error;

	/**
	 * * Κατασκευαστής της κλάσης Response όπου αρχικοποιεί το error message σε
	 * none.Και ακόμα αρχικοποιεί της λίστες τον Rooms,Documents;
	 * 
	 */
	public Response() {
		this.documents = new ArrayList<Document>();
		this.rooms = new ArrayList<Room>();
		error = new Error("none");

	}

	/**
	 * Η μέθοδος που ακολουθεί προσθέτει ένα ακόμα ένα document στην λίστα από
	 * τα documents
	 * 
	 * @param document
	 *            Το document το οποίο θα πρόσθεση στην λίστα από τα documents
	 */
	public void addDocumentToList(Document document) {
		documents.add(document);

	}

	/**
	 * Η μέθοδος που ακολουθεί προσθέτει ένα ακόμα ένα Room στην λίστα από τα
	 * Rooms
	 * 
	 * @param room
	 *            Το room το οποίο θα πρόσθεση στην λίστα από τα Rooms
	 */
	public void addRoomToList(Room room) {
		rooms.add(room);

	}

	/**
	 * Η μέθοδος που ακολουθεί αλλάζει Το αντικείμενο τύπου error της κλάσης
	 * 
	 * @param error
	 *            Το αντικείμενο τύπου error που θα αντικαταστήσει το υπάρχον
	 *            αντικείμενο τύπου Error
	 */
	public void addError(Error error) {
		this.error = error;
	}
}
