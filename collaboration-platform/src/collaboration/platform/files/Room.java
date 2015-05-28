package collaboration.platform.files;

import java.sql.Date;
/**
 * <h1>Room</h1> <br>
 * H ����� Room �������� ��� �� �������� ��� ���� ��� Room ���
 * ����������. �������������� ����������� ��� ������ ��� ��� ����� Api � �����
 * �� ����������� ���� ��� ����������� Xstream �� json.��� �� �� ���������� ����
 * ������.
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
	 * ������������� ��� ������ Room
	 * @param roomId To id ��� Room
	 * @param name �� ����� ��� Room
	 * @param dateCreated  ���������� ��� ������������� �� Room 
	 * @param path �� url �� �� ����� � ������� ������ �� ������� �� Room
	 */
	public Room(int roomId, String name, Date dateCreated, String path) {
		this.roomId = roomId;
		this.name = name;
		this.dateCreated = dateCreated;
		this.path = path;

	}
}
