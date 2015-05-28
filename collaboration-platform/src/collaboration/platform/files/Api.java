package collaboration.platform.files;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

/**
 * <h1>Collaboration platform rest Api</h1> <br>
 * Η κλάση τα Api υλοποιεί ένα restApi με την χρήση των βιβλιοθηκών
 * <ul>
 * <li>Java (Jersey / JAX-RS)</li>
 * <li>Java Database Connectivity (JDBC)</li>
 * <li>Xstream Java library</li>
 * </ul>
 * <br>
 * Το RestApi έχει της εξής λειτουργίες:
 * <ul>
 * 
 * <li>Δημιουργία ομάδας (Room).</li>
 * <li>Διαγραφή ομάδας (Room).</li>
 * <li>Δημιουργία καινούριου εγγράφου (Documents).</li>
 * <li>Ενημέρωση εγγράφου (Documents).</li>
 * <li>Παρουσίαση αλλαγών στα έγραφα των ROOMS που είναι μέλος ο χρήστης</li>
 * <li>Επιστροφή περιεχόμενον της πλατφόρμας είτε Room είτε Document</li>
 * </ul>
 * 
 * Για να καλέσουμε οποιαδήποτε fuction του api πρέπει να γράψουμε
 * RestApi/request/το path που εχεί η ανάλογη συνάρτηση.
 * 
 *
 * @author Adamos Koumi 949993
 * @version 1.0
 * @since 2015-04-25
 */
@Path("/request=")
public class Api {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	String dataBaseUrl = "127.0.0.1/collaboration_platform";
	String dBUsername = "root";
	String dBPassword = "";

	/**
	 * Η μέθοδος εξυπηρετεί http put request του τύπου
	 * {userId}/createRoom/home/{Name}" Δημιουργεί Room μόνο σε επίπεδο
	 * RootRoom. To userId αντιπροσωπεύει Id του χρήστη που αιτείται την
	 * δημιουργία του καινούριου Room και Name αντιπροσωπεύει το όνομα του
	 * καινούριου Room.
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του PATH Name αντιπροσωπεύει
	 *            το όνομα του καινούριου Room.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του PATH userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται την δημιουργία του καινούριου Room
	 * @return Εάν έχει δημιουργηθεί με επιτυχία το ROOM Επιστρέφει http status
	 *         200 και ένα αντικείμενο τύπου response κωδικοποιημένο σε json με
	 *         τιμή για την Εrror message none.Εάν δεν είχε δημιουργηθεί με
	 *         επιτυχία το ROOM επιστρέφει http status 404 και ένα αντικείμενο
	 *         τύπου response κωδικοποιημένο σε json με τιμή για το Εrror
	 *         message το πρόβλημα που υπήρξε.
	 */
	@PUT
	@Path("{userId}/createRoom/home/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRootRoom(@PathParam("Name") String name,
			@PathParam("userId") int userId) {
		Error error = new Error("None");

		String filePath = "home/" + name;
		int httpStatus = 200;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;

			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.users where id= ? ; ");
			preparedStatement.setInt(1, userId);

			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				try {
					preparedStatement = connect
							.prepareStatement(
									"INSERT INTO collaboration_platform.room (name,path) VALUES (?,?) ; ",
									Statement.RETURN_GENERATED_KEYS);
					preparedStatement.setString(1, name);
					preparedStatement.setString(2, filePath);
					preparedStatement.executeUpdate();
					resultSet = preparedStatement.getGeneratedKeys();
					resultSet.next();
					int groupId = resultSet.getInt(1);
					preparedStatement = connect
							.prepareStatement("INSERT INTO collaboration_platform.roomusers (groupId,userId,userState) VALUES (?,?,?) ; ");
					preparedStatement.setInt(1, groupId);
					preparedStatement.setInt(2, userId);
					preparedStatement.setString(3, "ADMIN");

					preparedStatement.executeUpdate();

				} catch (Exception e) {
					error = new Error("Error the room already exists");
					httpStatus = 200;
				}

			} else {
				error = new Error("Error Unauthorized user");
				httpStatus = 404;

			}

		} catch (Exception e) {
			httpStatus = 404;
			error = new Error("Error While Creating the Room");
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				httpStatus = 404;
				error = new Error("Error While Creating the Room");

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * Η μέθοδος εξυπηρετεί http put request του τύπου
	 * {userId}/createRoom/home/{FolderPath:.+}/{Name}" . Δημιουργεί καινούριο
	 * Room στο συγκεκριμένο path που ορίζει η παράμετρος FolderPath To userId
	 * αντιπροσωπεύει Id του χρήστη που αιτείται την δημιουργία του καινούριου
	 * Room , το Name αντιπροσωπεύει το όνομα του καινούριου Room.
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name αντιπροσωπεύει
	 *            το όνομα του καινούριου Room.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται την δημιουργία του καινούριου Room
	 * @param pathPar
	 *            Παίρνει την τιμή της παραμέτρου του path FolderPath Όπου
	 *            αντιπροσωπεύει το path του Room που θα δημιουργηθεί μέσα του
	 *            το καινούριο Room
	 * @return Εάν έχει δημιουργηθεί με επιτυχία το ROOM Επιστρέφει http status
	 *         200 και ένα αντικείμενο τύπου response κωδικοποιημένο σε json με
	 *         τιμή για την Εrror message none.Εάν δεν είχε δημιουργηθεί με
	 *         επιτυχία το ROOM επιστρέφει http status 404 και ένα αντικείμενο
	 *         τύπου response κωδικοποιημένο σε json με τιμή για το Εrror
	 *         message το πρόβλημα που υπήρξε.
	 */
	@PUT
	@Path("{userId}/createRoom/home/{FolderPath:.+}/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createRoom(@PathParam("Name") String name,
			@PathParam("userId") int userId,
			@PathParam("FolderPath") List<PathSegment> pathPar

	) {
		Error error = new Error("None");
		int httpStatus = 200;

		String filePath = "home/";
		for (int i = 0; i < pathPar.size(); i++) {

			filePath = filePath + pathPar.get(i).getPath().toString() + "/";
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;
			filePath = filePath.substring(0, filePath.length() - 1);
			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);
			resultSet = preparedStatement.executeQuery();
			boolean groupExist = resultSet.next();
			int groupId;
			if (groupExist) {

				groupId = resultSet.getInt("id");
				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers where groupId= ? AND userId=? ; ");
				preparedStatement.setInt(1, groupId);
				preparedStatement.setInt(2, userId);

				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					try {
						preparedStatement = connect
								.prepareStatement("INSERT INTO collaboration_platform.room (name,path) VALUES (?,?) ; ");
						filePath = filePath + "/" + name;
						preparedStatement.setString(1, name);
						preparedStatement.setString(2, filePath);
						preparedStatement.executeUpdate();

						resultSet = preparedStatement.getGeneratedKeys();
						resultSet.next();
						int newGroupId = resultSet.getInt(1);
						preparedStatement = connect
								.prepareStatement("INSERT INTO collaboration_platform.roomusers (groupId,userId,userState) VALUES (?,?,?) ; ");
						preparedStatement.setInt(1, newGroupId);
						preparedStatement.setInt(2, userId);
						preparedStatement.setString(3, "ADMIN");
						preparedStatement.executeUpdate();

					} catch (Exception e) {
						error = new Error("Error the room already exists");
						httpStatus = 200;
					}

				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;
				}

			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;
			}
		} catch (Exception e) {
			error = new Error("Error While Creating the Room");
			httpStatus = 404;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While Creating the Room");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * Η μέθοδος εξυπηρετεί http delete request του τύπου
	 * {userId}/deleteRoom/home/{Name} Διαγράφη ένα Room βρίσκετε κάτω από το
	 * home με όνομα ίσο με την παράμετρο του path Name μετά από αίτηση του
	 * χρήστη με id ίσο με την παράμετρο του path userId
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name που
	 *            αντιπροσωπεύει το όνομα του ROOM που θα διαγραφή.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            το Id του χρήστη που αιτείται την διαγραφή του Room
	 * @return Εάν έχει διαγραφή με επιτυχία το ROOM Επιστρέφει http status 200
	 *         και ένα αντικείμενο τύπου response κωδικοποιημένο σε json με τιμή
	 *         για την Εrror message none.Εάν δεν είχε διαγραφή με επιτυχία το
	 *         ROOM επιστρέφει http status 404 και ένα αντικείμενο τύπου
	 *         response κωδικοποιημένο σε json με τιμή για το Εrror message το
	 *         πρόβλημα που υπήρξε.
	 */
	@DELETE
	@Path("{userId}/deleteRoom/home/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteRootRoom(@PathParam("Name") String name,
			@PathParam("userId") int userId

	) {
		Error error = new Error("None");
		int httpStatus = 200;

		String filePath = "home/" + name;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;
			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int groupId = resultSet.getInt("id");

				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers WHERE userId= ? AND groupId= ? ; ");
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, groupId);

				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					try {

						filePath = filePath + "%";
						preparedStatement = connect
								.prepareStatement("DELETE FROM collaboration_platform.room WHERE path LIKE ? ;");
						preparedStatement.setString(1, filePath);
						preparedStatement.execute();

					} catch (Exception e) {
						error = new Error("Error will deleting room");
						httpStatus = 404;
					}

				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;
				}
			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;

			}

		} catch (Exception e) {
			error = new Error("Error While deleting the Room");
			httpStatus = 404;

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While deleting the Room");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		response.addError(error);
		xstream.alias("response", Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * Η μέθοδος εξυπηρετεί http delete request του τύπου
	 * "{userId}/deleteRoom/home/{FolderPath:.+}/{Name}" Διαγράφη ένα Room
	 * βρίσκετε στο συγκεκριμένο path που ορίζει η παράμετρος FolderPath με
	 * όνομα ίσο με την παράμετρο του path Name μετά από αίτηση του χρήστη με id
	 * ίσο με την παράμετρο του path userId
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name που
	 *            αντιπροσωπεύει το όνομα του ROOM που θα διαγραφή.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            το Id του χρήστη που αιτείται την διαγραφή του Room
	 * @param pathPar
	 *            Παίρνει την τιμή της παραμέτρου του path FolderPath που
	 *            αντιπροσωπεύει το path του Room που θα διαγραφή.
	 * @return Εάν έχει διαγραφή με επιτυχία το ROOM Επιστρέφει http status 200
	 *         και ένα αντικείμενο τύπου response κωδικοποιημένο σε json με τιμή
	 *         για την Εrror message none.Εάν δεν είχε διαγραφή με επιτυχία το
	 *         ROOM επιστρέφει http status 404 και ένα αντικείμενο τύπου
	 *         response κωδικοποιημένο σε json με τιμή για το Εrror message το
	 *         πρόβλημα που υπήρξε.
	 */
	@DELETE
	@Path("{userId}/deleteRoom/home/{FolderPath:.+}/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("Name") String name,
			@PathParam("userId") int userId,
			@PathParam("FolderPath") List<PathSegment> pathPar

	) {
		Error error = new Error("None");
		int httpStatus = 200;

		String filePath = "home/";
		for (int i = 0; i < pathPar.size(); i++) {

			filePath = filePath + pathPar.get(i).getPath().toString() + "/";

		}
		filePath = filePath + name;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;
			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int groupId = resultSet.getInt("id");

				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers where userId= ? AND groupId= ? ; ");
				preparedStatement.setInt(1, userId);
				preparedStatement.setInt(2, groupId);

				resultSet = preparedStatement.executeQuery();
				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.users where id= ? ; ");
				preparedStatement.setInt(1, userId);

				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					try {

						filePath = filePath + "%";
						preparedStatement = connect
								.prepareStatement("DELETE FROM collaboration_platform.room WHERE path LIKE ? ;");
						preparedStatement.setString(1, filePath);
						preparedStatement.execute();

					} catch (Exception e) {

						error = new Error("Error will deleting Room");
						httpStatus = 404;
					}

				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;
				}
			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;

			}

		} catch (Exception e) {
			error = new Error("Error While deleting the Room");
			httpStatus = 404;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While deleting the Room");
				httpStatus = 404;
			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response)).build();
	}

	/**
	 * Η μέθοδος εξυπηρετεί http post request του τύπου
	 * "{userId}/uploadFile/home/{FolderPath:.+}" Δημιουργεί καινούριο Document
	 * που βρίσκεται στο συγκεκριμένο path που ορίζει η παράμετρος FolderPath To
	 * userId αντιπροσωπεύει Id του χρήστη που αιτείται την δημιουργία του
	 * καινούριου Document.
	 * 
	 * @param fileInputStream
	 *            Ροή των δεδομένων από του αρχείου το οποίο θα αποθήκευση στην
	 *            βάση δεδομένων.
	 * 
	 * 
	 * @param contentDispositionHeader
	 *            Περιέχει στοιχεία για το αρχείο το οποίο θα δημιουργηθεί και
	 *            ποιο συγκεκριμένα παίρνουμε το όνομα του αρχείου.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται την δημιουργία του καινούριου
	 *            Document
	 * @param pathPar
	 *            Παίρνει την τιμή της παραμέτρου του path FolderPath που
	 *            αντιπροσωπεύει το path του καινούριου Document που θα
	 *            δημιουργηθεί
	 * @return Εάν έχει δημιουργηθεί με επιτυχία το Document Επιστρέφει http
	 *         status 200 και ένα αντικείμενο τύπου response κωδικοποιημένο σε
	 *         json με τιμή για την Εrror message none.Εάν δεν είχε δημιουργηθεί
	 *         με επιτυχία το Document επιστρέφει http status 404 και ένα
	 *         αντικείμενο τύπου response κωδικοποιημένο σε json με τιμή για το
	 *         Εrror message το πρόβλημα που υπήρξε.
	 */
	@POST
	@Path("{userId}/uploadFile/home/{FolderPath:.+}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
			@PathParam("userId") int userId,
			@PathParam("FolderPath") List<PathSegment> pathPar

	) {
		String name = contentDispositionHeader.getFileName();
		Error error = new Error("None");
		int httpStatus = 200;

		String filePath = "home/";
		for (int i = 0; i < pathPar.size(); i++) {

			filePath = filePath + pathPar.get(i).getPath().toString() + "/";
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;
			filePath = filePath.substring(0, filePath.length() - 1);
			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);

			resultSet = preparedStatement.executeQuery();
			boolean groupExist = resultSet.next();
			int groupId;
			if (groupExist) {

				groupId = resultSet.getInt("id");
				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers where groupId= ? AND userId=? ; ");
				preparedStatement.setInt(1, groupId);
				preparedStatement.setInt(2, userId);

				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					try {
						filePath = filePath + "/" + name;
						preparedStatement = connect
								.prepareStatement("INSERT INTO collaboration_platform.documents (name,groupId,path,author,fileData) VALUES (?,?,?,?,?) ; ");
						preparedStatement.setString(1, name);
						preparedStatement.setInt(2, groupId);
						preparedStatement.setString(3, filePath);
						preparedStatement.setInt(4, userId);
						preparedStatement.setBlob(5, fileInputStream);
						preparedStatement.executeUpdate();
					} catch (Exception e) {
						error = new Error("Error While uploading the file");
						httpStatus = 404;
					}

				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;
				}

			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;
			}
		} catch (Exception e) {

			error = new Error("Error While uploading the file");
			httpStatus = 404;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While uploading the file");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * * Η μέθοδος εξυπηρετεί http post request του τύπου
	 * "{userId}/updateFile/home/{FolderPath:.+}/{Name}" Γίνεται ενημέρωση ενός
	 * υπάρχοντος Document στο συγκεκριμένο path που ορίζει η παράμετρος
	 * FolderPath και έχει όνομα ίσο με την παράμετρο του path Name. To userId
	 * αντιπροσωπεύει Id του χρήστη που αιτείται την δημιουργία του καινούριου
	 * Document.
	 * 
	 * @param fileInputStream
	 *            Ροή των δεδομένων από του αρχείου το οποίο θα ενημερωθεί στην
	 *            βάση δεδομένων.
	 * @param contentDispositionHeader
	 *            Περιέχει στοιχεία για το αρχείο το οποίο θα ενημερωθεί και
	 *            ποιο συγκεκριμένα παίρνουμε το όνομα του αρχείου.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται την ενημέρωση του Document
	 * @param pathPar
	 *            Παίρνει την τιμή της παραμέτρου του path FolderPath που
	 *            αντιπροσωπεύει το path του Document που θα ενημερωθεί
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name που
	 *            αντιπροσωπεύει το όνομα του Document που θα ενημερωθεί.
	 * @return Εάν έχει ενημερωθεί με επιτυχία το Document Επιστρέφει http
	 *         status 200 και ένα αντικείμενο τύπου response κωδικοποιημένο σε
	 *         json με τιμή για την Εrror message none.Εάν δεν είχε ενημερωθεί
	 *         με επιτυχία το Document επιστρέφει http status 404 και ένα
	 *         αντικείμενο τύπου response κωδικοποιημένο σε json με τιμή για το
	 *         Εrror message το πρόβλημα που υπήρξε.
	 */
	@POST
	@Path("{userId}/updateFile/home/{FolderPath:.+}/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response updateFile(
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
			@PathParam("userId") int userId,
			@PathParam("FolderPath") List<PathSegment> pathPar,
			@PathParam("Name") String name

	) {
		Error error = new Error("None");
		int httpStatus = 200;

		String filePath = "home/";
		for (int i = 0; i < pathPar.size(); i++) {

			filePath = filePath + pathPar.get(i).getPath().toString() + "/";
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;
			filePath = filePath.substring(0, filePath.length() - 1);
			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);

			resultSet = preparedStatement.executeQuery();
			boolean groupExist = resultSet.next();
			int groupId;
			if (groupExist) {

				groupId = resultSet.getInt("id");
				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers where groupId= ? AND userId=? ; ");
				preparedStatement.setInt(1, groupId);
				preparedStatement.setInt(2, userId);

				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					try {

						filePath = filePath + "/" + name;
						preparedStatement = connect
								.prepareStatement("UPDATE  collaboration_platform.documents SET fileData= ? WHERE path LIKE ? ; ");
						preparedStatement.setBlob(1, fileInputStream);

						preparedStatement.setString(2, filePath);

						preparedStatement.executeUpdate();
					} catch (Exception e) {
						error = new Error("Error While uploading the file");
						httpStatus = 404;
					}

				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;
				}

			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;

			}
		} catch (Exception e) {

			error = new Error("Error While uploading the file");
			httpStatus = 404;

		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While uploading the file");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * Η μέθοδος εξυπηρετεί http get request του τύπου
	 * {userId}/home/{FolderPath:.+}/{Name} . Δέχεται αίτησης για να επιστρέψει
	 * ένα αντικείμενο της πλατφόρμας στον χρήστη. To userId αντιπροσωπεύει Id
	 * του χρήστη που αιτείται να πάρει το αντικείμενο. Το FolderPath
	 * αντιπροσωπεύει το path του αντικειμένου της πλατφόρμας που θα επιστραφεί.
	 * Το Νame αντιπροσωπεύει το όνομα του αντικειμένου της πλατφόρμας που θα
	 * επιστραφεί. Εάν το αντικείμενό που ζήτα ο χρήστης είναι Room επιστρέφει
	 * τα περιεχόμενα του Room. Εάν είναι document επιστρέφει το document.
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name που
	 *            αντιπροσωπεύει το όνομα του αντικειμένου της πλατφόρμας που θα
	 *            επιστραφεί.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται να πάρει το αντικείμενο της
	 *            πλατφόρμας
	 * @param pathPar
	 *            Παίρνει την τιμή της παραμέτρου του path FolderPath που
	 *            αντιπροσωπεύει το path του αντικειμένου της πλατφόρμας που θα
	 *            επιστραφεί.
	 * @return Εάν το αντικείμενο υπάρχει και είναι Room επιστρέφει http status
	 *         200 και ένα αντικείμενο τύπου response κωδικοποιημένο σε json. Το
	 *         οποίο περιέχει μια λίστα με τα documents και μια λίστα με τα Room
	 *         . Ακόμα περιέχει το πεδίο Εrror με τιμή για το message none. Εάν
	 *         το αντικείμενο υπάρχει και είναι Document Επιστρέφει http status
	 *         200 και το Document. Εάν δεν είχε υπάρχει το αντικείμενο
	 *         επιστρέφει http status 404 και ένα αντικείμενο τύπου response
	 *         κωδικοποιημένο σε json με τιμή για το Εrror message το πρόβλημα
	 *         που υπήρξε.
	 */
	@GET
	@Path("{userId}/home/{FolderPath:.+}/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFiles(@PathParam("Name") String name,
			@PathParam("userId") int userId,
			@PathParam("FolderPath") List<PathSegment> pathPar

	) {
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		int httpStatus = 200;

		Error error = new Error("None");

		String filePath = "home/";
		for (int i = 0; i < pathPar.size(); i++) {

			filePath = filePath + pathPar.get(i).getPath().toString() + "/";
		}

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;

			filePath = filePath.substring(0, filePath.length() - 1);
			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);

			resultSet = preparedStatement.executeQuery();
			filePath = filePath + "/" + name;
			boolean groupExist = resultSet.next();
			int groupId;
			boolean authorizeUser = false;
			if (groupExist) {

				groupId = resultSet.getInt("id");
				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers where groupId= ? AND userId=? ; ");
				preparedStatement.setInt(1, groupId);
				preparedStatement.setInt(2, userId);

				resultSet = preparedStatement.executeQuery();

				authorizeUser = resultSet.next();
				if (authorizeUser) {
					preparedStatement = connect
							.prepareStatement("select * from collaboration_platform.documents where path LIKE ? ; ");
					preparedStatement.setString(1, filePath);
					resultSet = preparedStatement.executeQuery();
					boolean isDocument = resultSet.next();

					if (isDocument) {
						InputStream file = resultSet.getBlob("fileData")
								.getBinaryStream();
						String fileName = resultSet.getString("name");

						return Response
								.ok(file, MediaType.APPLICATION_OCTET_STREAM)
								.header("Content-Disposition",
										"attachment; filename=\"" + fileName
												+ "\"").build();

					} else {

						String path = filePath + "/%";
						String limitPath = filePath + "/%/%";
						preparedStatement = connect
								.prepareStatement("SELECT * FROM collaboration_platform.room JOIN collaboration_platform.roomusers ON roomusers.groupId = room.id WHERE roomusers.userId = ? AND room.path LIKE ? AND room.path NOT LIKE ? ;");

						preparedStatement.setInt(1, userId);
						preparedStatement.setString(2, path);
						preparedStatement.setString(3, limitPath);

						resultSet = preparedStatement.executeQuery();
						int i = 0;
						while (resultSet.next()) {
							i++;
							Room room = new Room(resultSet.getInt("id"),
									resultSet.getString("name"),
									resultSet.getDate("dateCreated"),
									resultSet.getString("path"));

							response.addRoomToList(room);

						}
						preparedStatement = connect
								.prepareStatement("SELECT * FROM collaboration_platform.documents JOIN collaboration_platform.users ON documents.author = users.id WHERE documents.path LIKE ? AND documents.path NOT LIKE ? ;");

						preparedStatement.setString(1, path);
						preparedStatement.setString(2, limitPath);
						resultSet = preparedStatement.executeQuery();
						PreparedStatement preparedStatement2;
						ResultSet resultSet2;
						while (resultSet.next()) {
							preparedStatement2 = connect
									.prepareStatement("SELECT * FROM collaboration_platform.room WHERE id= ? ;");
							preparedStatement2.setInt(1,
									resultSet.getInt("groupId"));
							resultSet2 = preparedStatement2.executeQuery();
							resultSet2.next();
							i++;
							Document document = new Document(
									resultSet.getInt("documents.id"),
									resultSet.getString("documents.name"),
									resultSet.getDate("documents.lastEdit"),
									resultSet.getString("documents.path"),
									resultSet.getString("users.nickName"),
									resultSet2.getString("name"));
							response.addDocumentToList(document);
						}
						if (i == 0) {
							error = new Error(
									"The room does not have any files");
							httpStatus = 200;

						}

					}

				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;

				}
			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;

			}

		} catch (Exception e) {
			httpStatus = 404;

			error = new Error("Error While conecting to server");
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While uploading the file");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * Η μέθοδος εξυπηρετεί http get request του τύπου {userId}/home/{Name} .
	 * Δέχεται αίτησης για να επιστρέψει ένα αντικείμενο της πλατφόρμας στον
	 * χρήστη. To userId αντιπροσωπεύει Id του χρήστη που αιτείται να πάρει το
	 * αντικείμενο. Το Νame αντιπροσωπεύει το όνομα του αντικειμένου της
	 * πλατφόρμας που θα επιστραφεί. Εάν το αντικείμενό που ζήτα ο χρήστης είναι
	 * Room επιστρέφει τα περιεχόμενα του Room. Εάν είναι document επιστρέφει το
	 * document.
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name που
	 *            αντιπροσωπεύει το όνομα του αντικειμένου της πλατφόρμας που θα
	 *            επιστραφεί.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται να πάρει το αντικείμενο της
	 *            πλατφόρμας
	 * @return Εάν το αντικείμενο υπάρχει και είναι Room επιστρέφει http status
	 *         200 και ένα αντικείμενο τύπου response κωδικοποιημένο σε json. Το
	 *         οποίο περιέχει μια λίστα με τα documents και μια λίστα με τα Room
	 *         . Ακόμα περιέχει το πεδίο Εrror με τιμή για το message none. Εάν
	 *         το αντικείμενο υπάρχει και είναι Document Επιστρέφει http status
	 *         200 και το Document. Εάν δεν είχε υπάρχει το αντικείμενο
	 *         επιστρέφει http status 404 και ένα αντικείμενο τύπου response
	 *         κωδικοποιημένο σε json με τιμή για το Εrror message το πρόβλημα
	 *         που υπήρξε.
	 */
	@GET
	@Path("{userId}/home/{Name}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getHomefile(@PathParam("Name") String name,
			@PathParam("userId") int userId

	) {
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		int httpStatus = 200;

		Error error = new Error("None");

		String filePath = "home/" + name;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;

			preparedStatement = connect
					.prepareStatement("select * from collaboration_platform.room where path LIKE ? ; ");
			preparedStatement.setString(1, filePath);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				int groupId = resultSet.getInt("id");
				preparedStatement = connect
						.prepareStatement("select * from collaboration_platform.roomusers where groupId= ? AND userId=? ; ");
				preparedStatement.setInt(1, groupId);
				preparedStatement.setInt(2, userId);

				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {

					String path = filePath + "/%";
					String limitPath = filePath + "/%/%";
					preparedStatement = connect
							.prepareStatement("SELECT * FROM collaboration_platform.room JOIN collaboration_platform.roomusers ON roomusers.groupId = room.id WHERE roomusers.userId = ? AND room.path LIKE ? AND room.path NOT LIKE ? ;");

					preparedStatement.setInt(1, userId);
					preparedStatement.setString(2, path);
					preparedStatement.setString(3, limitPath);

					resultSet = preparedStatement.executeQuery();
					int i = 0;
					while (resultSet.next()) {
						i++;
						Room room = new Room(resultSet.getInt("id"),
								resultSet.getString("name"),
								resultSet.getDate("dateCreated"),
								resultSet.getString("path"));

						response.addRoomToList(room);

					}
					preparedStatement = connect
							.prepareStatement("SELECT * FROM collaboration_platform.documents JOIN collaboration_platform.users ON documents.author = users.id WHERE documents.path LIKE ? AND documents.path NOT LIKE ? ;");

					preparedStatement.setString(1, path);
					preparedStatement.setString(2, limitPath);
					resultSet = preparedStatement.executeQuery();

					PreparedStatement preparedStatement2;
					ResultSet resultSet2;
					while (resultSet.next()) {
						preparedStatement2 = connect
								.prepareStatement("SELECT * FROM collaboration_platform.room WHERE id= ? ;");
						preparedStatement2.setInt(1,
								resultSet.getInt("groupId"));
						resultSet2 = preparedStatement2.executeQuery();
						resultSet2.next();
						i++;
						Document document = new Document(
								resultSet.getInt("documents.id"),
								resultSet.getString("documents.name"),
								resultSet.getDate("documents.lastEdit"),
								resultSet.getString("documents.path"),
								resultSet.getString("users.nickName"),
								resultSet2.getString("name"));
						response.addDocumentToList(document);
					}
					if (i == 0) {
						error = new Error("The room does not have any files");
						httpStatus = 200;

					}
				} else {
					error = new Error("Error Unauthorized user");
					httpStatus = 404;
				}

			} else {
				error = new Error("Error Invalid path");
				httpStatus = 404;

			}

		} catch (Exception e) {
			httpStatus = 404;

			error = new Error("Error While conecting to server");
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While uploading the file");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * * Η μέθοδος εξυπηρετεί http get request του τύπου {userId}/home . Δέχεται
	 * αίτησης για να επιστρέψει Τα rooms του RootRoom στον χρήστη. To userId
	 * αντιπροσωπεύει Id του χρήστη που αιτείται να πάρει τα rooms του RootRoom
	 * . Επιστρέφει τα περιεχόμενα του Room.
	 * 
	 * @param name
	 *            Παίρνει την τιμή της παραμέτρου του path Name που
	 *            αντιπροσωπεύει το όνομα του αντικειμένου της πλατφόρμας που θα
	 *            επιστραφεί.
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται να πάρει το αντικείμενο της
	 *            πλατφόρμας
	 * @return Eπιστρέφει http status 200 και ένα αντικείμενο τύπου response
	 *         κωδικοποιημένο σε json. Το οποίο περιέχει μια λίστα με τα Room
	 *         του RootRoom. Ακόμα περιέχει το πεδίο Εrror με τιμή για το
	 *         message none. Εάν υπάρξει οποιοδήποτε πρόβλημά επιστρέφει http
	 *         status 404 και ένα αντικείμενο τύπου response κωδικοποιημένο σε
	 *         json με τιμή για το Εrror message το πρόβλημα που υπήρξε.
	 */
	@GET
	@Path("{userId}/home")
	@Produces(MediaType.APPLICATION_JSON)
	public Response gethome(@PathParam("Name") String name,
			@PathParam("userId") int userId

	) throws Exception {
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		int httpStatus = 200;

		Error error = new Error("None");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;

			String path = "home" + "/%";
			String limitPath = "home" + "/%/%";
			preparedStatement = connect

					.prepareStatement("SELECT * FROM collaboration_platform.room JOIN collaboration_platform.roomusers ON roomusers.groupId = room.id WHERE roomusers.userId = ? AND room.path LIKE ? AND room.path NOT LIKE ? ;");

			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, path);
			preparedStatement.setString(3, limitPath);

			resultSet = preparedStatement.executeQuery();
			int i = 0;
			while (resultSet.next()) {
				i++;
				Room room = new Room(resultSet.getInt("id"),
						resultSet.getString("name"),
						resultSet.getDate("dateCreated"),
						resultSet.getString("path"));

				response.addRoomToList(room);

			}
			if (i == 0) {
				httpStatus = 200;
				error = new Error("You are not member of any room");
			}

		} catch (Exception e) {
			httpStatus = 404;

			error = new Error("Error While conecting to server");
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While uploading the file");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias("response", collaboration.platform.files.Response.class);
		response.addError(error);
		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

	/**
	 * Η μέθοδος εξυπηρετεί http get request του τύπου
	 * "{userId}/getNotifications" Επιστρέφει τες αλλαγές που έγιναν στα
	 * Documents των Rooms από την τελευταία φορά που έκανε log in ο χρήστης στο
	 * σύστημα. To userId αντιπροσωπεύει Id του χρήστη που αιτείται τες αλλαγές
	 * που έγιναν στα Documents των Rooms από την τελευταία φορά που έκανε log
	 * in.
	 * 
	 * @param userId
	 *            Παίρνει την τιμή της παραμέτρου του path userId αντιπροσωπεύει
	 *            Id του χρήστη που αιτείται τες αλλαγές που έγιναν στα
	 *            Documents των Rooms από την τελευταία φορά που έκανε log in.
	 * @return Eπιστρέφει http status 200 και ένα αντικείμενο τύπου response
	 *         κωδικοποιημένο σε json. Το οποίο περιέχει μια λίστα με τα
	 *         Documents που έχουν αλλάξει από την τελευταία φορά που έκανε Log
	 *         In ο χρήστης. Ακόμα περιέχει το πεδίο Εrror με τιμή για το
	 *         message none. Εάν υπάρξει οποιοδήποτε πρόβλημά επιστρέφει http
	 *         status 404 και ένα αντικείμενο τύπου response κωδικοποιημένο σε
	 *         json με τιμή για το Εrror message το πρόβλημα που υπήρξε.
	 */
	@GET
	@Path("{userId}/getNotifications")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNotifications(@PathParam("userId") int userId

	) throws Exception {
		collaboration.platform.files.Response response = new collaboration.platform.files.Response();
		int httpStatus = 200;
		Error error = new Error("None");

		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection("jdbc:mysql://" + dataBaseUrl
					+ "?" + "user=" + dBUsername + "&password=" + dBPassword);
			statement = connect.createStatement();
			PreparedStatement preparedStatement;
			preparedStatement = connect
					.prepareStatement("select lastLogIn from collaboration_platform.users where id= ? ; ");
			preparedStatement.setInt(1, userId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				Date lastLogIn = resultSet.getDate("lastLogIn");

				preparedStatement = connect
						.prepareStatement("SELECT * FROM collaboration_platform.documents JOIN collaboration_platform.roomusers ON documents.groupId = roomusers.groupId WHERE roomusers.userId= ? AND documents.lastEdit BETWEEN  documents.lastEdit AND  ? ;");

				preparedStatement.setInt(1, userId);
				preparedStatement.setDate(2, lastLogIn);
				resultSet = preparedStatement.executeQuery();
				PreparedStatement preparedStatement2;
				ResultSet resultSet2;
				PreparedStatement preparedStatement3;
				ResultSet resultSet3;
				int i = 0;
				while (resultSet.next()) {
					preparedStatement3 = connect
							.prepareStatement("SELECT * FROM collaboration_platform.room WHERE id= ? ;");
					preparedStatement3.setInt(1, resultSet.getInt("groupId"));
					resultSet3 = preparedStatement3.executeQuery();
					resultSet3.next();
					i++;
					preparedStatement2 = connect
							.prepareStatement("SELECT * FROM collaboration_platform.users WHERE id= ? ;");
					preparedStatement2.setInt(1, resultSet.getInt("author"));
					resultSet2 = preparedStatement2.executeQuery();
					resultSet2.next();
					Document document = new Document(
							resultSet.getInt("documents.id"),
							resultSet.getString("documents.name"),
							resultSet.getDate("documents.lastEdit"),
							resultSet.getString("documents.path"),
							resultSet2.getString("nickName"),
							resultSet3.getString("name"));
					response.addDocumentToList(document);
					if (i >= 10) {
						break;
					}
				}
				if (i == 0) {
					error = new Error(
							"No new documents were added since your last login");
					httpStatus = 200;

				}

			} else {
				error = new Error("Error Unauthorized user");
				httpStatus = 404;

			}

		} catch (Exception e) {
			httpStatus = 404;

			error = new Error("Error While conecting to server");
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}

				if (statement != null) {
					statement.close();
				}

				if (connect != null) {
					connect.close();
				}
			} catch (Exception e) {
				error = new Error("Error While uploading the file");
				httpStatus = 404;

			}

		}

		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		xstream.setMode(XStream.NO_REFERENCES);
		response.addError(error);
		xstream.alias("response", collaboration.platform.files.Response.class);

		return Response.status(httpStatus).entity(xstream.toXML(response))
				.build();

	}

}
