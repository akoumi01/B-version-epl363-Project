package collaboration.platform.files;

import java.util.ArrayList;

/**
 * <h1>Response</h1> <br>
 * H ����� Response �������� ��� ����� ��� documents ,��� ����� ��� Rooms ���
 * ��� ����������� ��� ������ �rror. �������������� ����������� ��� ������ ���
 * ��� ����� Api � ����� �� ����������� ���� ��� ����������� Xstream �� json.���
 * �� �� ���������� ���� ������.
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
	 * * ������������� ��� ������ Response ���� ����������� �� error message ��
	 * none.��� ����� ����������� ��� ������ ��� Rooms,Documents;
	 * 
	 */
	public Response() {
		this.documents = new ArrayList<Document>();
		this.rooms = new ArrayList<Room>();
		error = new Error("none");

	}

	/**
	 * � ������� ��� ��������� ��������� ��� ����� ��� document ���� ����� ���
	 * �� documents
	 * 
	 * @param document
	 *            �� document �� ����� �� �������� ���� ����� ��� �� documents
	 */
	public void addDocumentToList(Document document) {
		documents.add(document);

	}

	/**
	 * � ������� ��� ��������� ��������� ��� ����� ��� Room ���� ����� ��� ��
	 * Rooms
	 * 
	 * @param room
	 *            �� room �� ����� �� �������� ���� ����� ��� �� Rooms
	 */
	public void addRoomToList(Room room) {
		rooms.add(room);

	}

	/**
	 * � ������� ��� ��������� ������� �� ����������� ����� error ��� ������
	 * 
	 * @param error
	 *            �� ����������� ����� error ��� �� �������������� �� �������
	 *            ����������� ����� Error
	 */
	public void addError(Error error) {
		this.error = error;
	}
}
