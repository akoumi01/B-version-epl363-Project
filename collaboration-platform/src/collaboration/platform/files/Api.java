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
 * � ����� �� Api �������� ��� restApi �� ��� ����� ��� �����������
 * <ul>
 * <li>Java (Jersey / JAX-RS)</li>
 * <li>Java Database Connectivity (JDBC)</li>
 * <li>Xstream Java library</li>
 * </ul>
 * <br>
 * �� RestApi ���� ��� ���� �����������:
 * <ul>
 * 
 * <li>���������� ������ (Room).</li>
 * <li>�������� ������ (Room).</li>
 * <li>���������� ���������� �������� (Documents).</li>
 * <li>��������� �������� (Documents).</li>
 * <li>���������� ������� ��� ������ ��� ROOMS ��� ����� ����� � �������</li>
 * <li>��������� ������������ ��� ���������� ���� Room ���� Document</li>
 * </ul>
 * 
 * ��� �� ��������� ����������� fuction ��� api ������ �� ��������
 * RestApi/request/�� path ��� ���� � ������� ���������.
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
	 * � ������� ���������� http put request ��� �����
	 * {userId}/createRoom/home/{Name}" ���������� Room ���� �� �������
	 * RootRoom. To userId �������������� Id ��� ������ ��� �������� ���
	 * ���������� ��� ���������� Room ��� Name �������������� �� ����� ���
	 * ���������� Room.
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� PATH Name ��������������
	 *            �� ����� ��� ���������� Room.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� PATH userId ��������������
	 *            Id ��� ������ ��� �������� ��� ���������� ��� ���������� Room
	 * @return ��� ���� ������������ �� �������� �� ROOM ���������� http status
	 *         200 ��� ��� ����������� ����� response �������������� �� json ��
	 *         ���� ��� ��� �rror message none.��� ��� ���� ������������ ��
	 *         �������� �� ROOM ���������� http status 404 ��� ��� �����������
	 *         ����� response �������������� �� json �� ���� ��� �� �rror
	 *         message �� �������� ��� ������.
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
	 * � ������� ���������� http put request ��� �����
	 * {userId}/createRoom/home/{FolderPath:.+}/{Name}" . ���������� ���������
	 * Room ��� ������������ path ��� ������ � ���������� FolderPath To userId
	 * �������������� Id ��� ������ ��� �������� ��� ���������� ��� ����������
	 * Room , �� Name �������������� �� ����� ��� ���������� Room.
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ��������������
	 *            �� ����� ��� ���������� Room.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� ��� ���������� ��� ���������� Room
	 * @param pathPar
	 *            ������� ��� ���� ��� ���������� ��� path FolderPath ����
	 *            �������������� �� path ��� Room ��� �� ������������ ���� ���
	 *            �� ��������� Room
	 * @return ��� ���� ������������ �� �������� �� ROOM ���������� http status
	 *         200 ��� ��� ����������� ����� response �������������� �� json ��
	 *         ���� ��� ��� �rror message none.��� ��� ���� ������������ ��
	 *         �������� �� ROOM ���������� http status 404 ��� ��� �����������
	 *         ����� response �������������� �� json �� ���� ��� �� �rror
	 *         message �� �������� ��� ������.
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
	 * � ������� ���������� http delete request ��� �����
	 * {userId}/deleteRoom/home/{Name} �������� ��� Room �������� ���� ��� ��
	 * home �� ����� ��� �� ��� ��������� ��� path Name ���� ��� ������ ���
	 * ������ �� id ��� �� ��� ��������� ��� path userId
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ���
	 *            �������������� �� ����� ��� ROOM ��� �� ��������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            �� Id ��� ������ ��� �������� ��� �������� ��� Room
	 * @return ��� ���� �������� �� �������� �� ROOM ���������� http status 200
	 *         ��� ��� ����������� ����� response �������������� �� json �� ����
	 *         ��� ��� �rror message none.��� ��� ���� �������� �� �������� ��
	 *         ROOM ���������� http status 404 ��� ��� ����������� �����
	 *         response �������������� �� json �� ���� ��� �� �rror message ��
	 *         �������� ��� ������.
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
	 * � ������� ���������� http delete request ��� �����
	 * "{userId}/deleteRoom/home/{FolderPath:.+}/{Name}" �������� ��� Room
	 * �������� ��� ������������ path ��� ������ � ���������� FolderPath ��
	 * ����� ��� �� ��� ��������� ��� path Name ���� ��� ������ ��� ������ �� id
	 * ��� �� ��� ��������� ��� path userId
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ���
	 *            �������������� �� ����� ��� ROOM ��� �� ��������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            �� Id ��� ������ ��� �������� ��� �������� ��� Room
	 * @param pathPar
	 *            ������� ��� ���� ��� ���������� ��� path FolderPath ���
	 *            �������������� �� path ��� Room ��� �� ��������.
	 * @return ��� ���� �������� �� �������� �� ROOM ���������� http status 200
	 *         ��� ��� ����������� ����� response �������������� �� json �� ����
	 *         ��� ��� �rror message none.��� ��� ���� �������� �� �������� ��
	 *         ROOM ���������� http status 404 ��� ��� ����������� �����
	 *         response �������������� �� json �� ���� ��� �� �rror message ��
	 *         �������� ��� ������.
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
	 * � ������� ���������� http post request ��� �����
	 * "{userId}/uploadFile/home/{FolderPath:.+}" ���������� ��������� Document
	 * ��� ��������� ��� ������������ path ��� ������ � ���������� FolderPath To
	 * userId �������������� Id ��� ������ ��� �������� ��� ���������� ���
	 * ���������� Document.
	 * 
	 * @param fileInputStream
	 *            ��� ��� ��������� ��� ��� ������� �� ����� �� ���������� ����
	 *            ���� ���������.
	 * 
	 * 
	 * @param contentDispositionHeader
	 *            �������� �������� ��� �� ������ �� ����� �� ������������ ���
	 *            ���� ������������ ��������� �� ����� ��� �������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� ��� ���������� ��� ����������
	 *            Document
	 * @param pathPar
	 *            ������� ��� ���� ��� ���������� ��� path FolderPath ���
	 *            �������������� �� path ��� ���������� Document ��� ��
	 *            ������������
	 * @return ��� ���� ������������ �� �������� �� Document ���������� http
	 *         status 200 ��� ��� ����������� ����� response �������������� ��
	 *         json �� ���� ��� ��� �rror message none.��� ��� ���� ������������
	 *         �� �������� �� Document ���������� http status 404 ��� ���
	 *         ����������� ����� response �������������� �� json �� ���� ��� ��
	 *         �rror message �� �������� ��� ������.
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
	 * * � ������� ���������� http post request ��� �����
	 * "{userId}/updateFile/home/{FolderPath:.+}/{Name}" ������� ��������� ����
	 * ���������� Document ��� ������������ path ��� ������ � ����������
	 * FolderPath ��� ���� ����� ��� �� ��� ��������� ��� path Name. To userId
	 * �������������� Id ��� ������ ��� �������� ��� ���������� ��� ����������
	 * Document.
	 * 
	 * @param fileInputStream
	 *            ��� ��� ��������� ��� ��� ������� �� ����� �� ���������� ����
	 *            ���� ���������.
	 * @param contentDispositionHeader
	 *            �������� �������� ��� �� ������ �� ����� �� ���������� ���
	 *            ���� ������������ ��������� �� ����� ��� �������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� ��� ��������� ��� Document
	 * @param pathPar
	 *            ������� ��� ���� ��� ���������� ��� path FolderPath ���
	 *            �������������� �� path ��� Document ��� �� ����������
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ���
	 *            �������������� �� ����� ��� Document ��� �� ����������.
	 * @return ��� ���� ���������� �� �������� �� Document ���������� http
	 *         status 200 ��� ��� ����������� ����� response �������������� ��
	 *         json �� ���� ��� ��� �rror message none.��� ��� ���� ����������
	 *         �� �������� �� Document ���������� http status 404 ��� ���
	 *         ����������� ����� response �������������� �� json �� ���� ��� ��
	 *         �rror message �� �������� ��� ������.
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
	 * � ������� ���������� http get request ��� �����
	 * {userId}/home/{FolderPath:.+}/{Name} . ������� ������� ��� �� ����������
	 * ��� ����������� ��� ���������� ���� ������. To userId �������������� Id
	 * ��� ������ ��� �������� �� ����� �� �����������. �� FolderPath
	 * �������������� �� path ��� ������������ ��� ���������� ��� �� ����������.
	 * �� �ame �������������� �� ����� ��� ������������ ��� ���������� ��� ��
	 * ����������. ��� �� ����������� ��� ���� � ������� ����� Room ����������
	 * �� ����������� ��� Room. ��� ����� document ���������� �� document.
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ���
	 *            �������������� �� ����� ��� ������������ ��� ���������� ��� ��
	 *            ����������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� �� ����� �� ����������� ���
	 *            ����������
	 * @param pathPar
	 *            ������� ��� ���� ��� ���������� ��� path FolderPath ���
	 *            �������������� �� path ��� ������������ ��� ���������� ��� ��
	 *            ����������.
	 * @return ��� �� ����������� ������� ��� ����� Room ���������� http status
	 *         200 ��� ��� ����������� ����� response �������������� �� json. ��
	 *         ����� �������� ��� ����� �� �� documents ��� ��� ����� �� �� Room
	 *         . ����� �������� �� ����� �rror �� ���� ��� �� message none. ���
	 *         �� ����������� ������� ��� ����� Document ���������� http status
	 *         200 ��� �� Document. ��� ��� ���� ������� �� �����������
	 *         ���������� http status 404 ��� ��� ����������� ����� response
	 *         �������������� �� json �� ���� ��� �� �rror message �� ��������
	 *         ��� ������.
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
	 * � ������� ���������� http get request ��� ����� {userId}/home/{Name} .
	 * ������� ������� ��� �� ���������� ��� ����������� ��� ���������� ����
	 * ������. To userId �������������� Id ��� ������ ��� �������� �� ����� ��
	 * �����������. �� �ame �������������� �� ����� ��� ������������ ���
	 * ���������� ��� �� ����������. ��� �� ����������� ��� ���� � ������� �����
	 * Room ���������� �� ����������� ��� Room. ��� ����� document ���������� ��
	 * document.
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ���
	 *            �������������� �� ����� ��� ������������ ��� ���������� ��� ��
	 *            ����������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� �� ����� �� ����������� ���
	 *            ����������
	 * @return ��� �� ����������� ������� ��� ����� Room ���������� http status
	 *         200 ��� ��� ����������� ����� response �������������� �� json. ��
	 *         ����� �������� ��� ����� �� �� documents ��� ��� ����� �� �� Room
	 *         . ����� �������� �� ����� �rror �� ���� ��� �� message none. ���
	 *         �� ����������� ������� ��� ����� Document ���������� http status
	 *         200 ��� �� Document. ��� ��� ���� ������� �� �����������
	 *         ���������� http status 404 ��� ��� ����������� ����� response
	 *         �������������� �� json �� ���� ��� �� �rror message �� ��������
	 *         ��� ������.
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
	 * * � ������� ���������� http get request ��� ����� {userId}/home . �������
	 * ������� ��� �� ���������� �� rooms ��� RootRoom ���� ������. To userId
	 * �������������� Id ��� ������ ��� �������� �� ����� �� rooms ��� RootRoom
	 * . ���������� �� ����������� ��� Room.
	 * 
	 * @param name
	 *            ������� ��� ���� ��� ���������� ��� path Name ���
	 *            �������������� �� ����� ��� ������������ ��� ���������� ��� ��
	 *            ����������.
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� �� ����� �� ����������� ���
	 *            ����������
	 * @return E��������� http status 200 ��� ��� ����������� ����� response
	 *         �������������� �� json. �� ����� �������� ��� ����� �� �� Room
	 *         ��� RootRoom. ����� �������� �� ����� �rror �� ���� ��� ��
	 *         message none. ��� ������� ����������� �������� ���������� http
	 *         status 404 ��� ��� ����������� ����� response �������������� ��
	 *         json �� ���� ��� �� �rror message �� �������� ��� ������.
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
	 * � ������� ���������� http get request ��� �����
	 * "{userId}/getNotifications" ���������� ��� ������� ��� ������ ���
	 * Documents ��� Rooms ��� ��� ��������� ���� ��� ����� log in � ������� ���
	 * �������. To userId �������������� Id ��� ������ ��� �������� ��� �������
	 * ��� ������ ��� Documents ��� Rooms ��� ��� ��������� ���� ��� ����� log
	 * in.
	 * 
	 * @param userId
	 *            ������� ��� ���� ��� ���������� ��� path userId ��������������
	 *            Id ��� ������ ��� �������� ��� ������� ��� ������ ���
	 *            Documents ��� Rooms ��� ��� ��������� ���� ��� ����� log in.
	 * @return E��������� http status 200 ��� ��� ����������� ����� response
	 *         �������������� �� json. �� ����� �������� ��� ����� �� ��
	 *         Documents ��� ����� ������� ��� ��� ��������� ���� ��� ����� Log
	 *         In � �������. ����� �������� �� ����� �rror �� ���� ��� ��
	 *         message none. ��� ������� ����������� �������� ���������� http
	 *         status 404 ��� ��� ����������� ����� response �������������� ��
	 *         json �� ���� ��� �� �rror message �� �������� ��� ������.
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
