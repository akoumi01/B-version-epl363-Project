package collaboration.platform.files;

import java.sql.Date;

/**
 * <h1>Document</h1> <br>
 * H κλάση document περιέχει όλα τα στοιχεία που έχει ένα document της
 * πλατφόρμας. Δημιουργούνται αντικείμενα της κλάσης από την κλάση Api η οποία
 * τα κωδικοποιεί μέσω της βιβλιοθήκης Xstream σε json.Για να τα επιστρέψει στον
 * χρήστη.
 * 
 * @author Adamos Koumi 949993
 * @version 1.0
 * @since 2015-04-25
 */
public class Document {
	private int id;
	private String name;
	private Date lastEdit;
	private String type;
	private String path;
	private String author;
	private String parentRoomName;

	/**
	 * Κατασκευαστής της κλάσης Document 
	 * @param id To id του Document
	 * @param name Το όνομα του Document
	 * @param lastEdit Ημερομηνία τελευταίας φορά που ενημερώθηκε το Document  
	 * @param path Το url με το οποίο ο χρήστης μπορεί να αιτηθεί το Document
	 * @param author Το nickname του δημιουργού του Document
	 * @param parentRoomName Το id του Room στο οποίο ανήκει το Document
	 */
	public Document(int id, String name, Date lastEdit, String path,
			String author, String parentRoomName) {
		this.id = id;
		this.name = name;
		this.lastEdit = lastEdit;
		this.path = path;
		this.author = author;
		this.parentRoomName = parentRoomName;
	}

}
