package collaboration.platform.files;

import java.sql.Date;
/**
 * <h1>Room</h1> <br>
 * H κλάση Room περιέχει όλα τα στοιχεία που έχει ένα Room της
 * πλατφόρμας. Δημιουργούνται αντικείμενα της κλάσης από την κλάση Api η οποία
 * τα κωδικοποιεί μέσω της βιβλιοθήκης Xstream σε json.Για να τα επιστρέψει στον
 * χρήστη.
 * 
 * @author Adamos Koumi 949993
 * @version 1.0
 * @since 2015-04-25
 */
public class Room {
	private int roomId;
	private String name;
	private Date dateCreated;
	private String path;

	/**
	 * Κατασκευαστής της κλάσης Room
	 * @param roomId To id του Room
	 * @param name Το όνομα του Room
	 * @param dateCreated  Ημερομηνία που δημιουργήθηκε το Room 
	 * @param path Το url με το οποίο ο χρήστης μπορεί να αιτηθεί το Room
	 */
	public Room(int roomId, String name, Date dateCreated, String path) {
		this.roomId = roomId;
		this.name = name;
		this.dateCreated = dateCreated;
		this.path = path;

	}
}
