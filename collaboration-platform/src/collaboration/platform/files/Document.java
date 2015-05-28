package collaboration.platform.files;

import java.sql.Date;

/**
 * <h1>Document</h1> <br>
 * H ����� document �������� ��� �� �������� ��� ���� ��� document ���
 * ����������. �������������� ����������� ��� ������ ��� ��� ����� Api � �����
 * �� ����������� ���� ��� ����������� Xstream �� json.��� �� �� ���������� ����
 * ������.
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
	 * ������������� ��� ������ Document 
	 * @param id To id ��� Document
	 * @param name �� ����� ��� Document
	 * @param lastEdit ���������� ���������� ���� ��� ����������� �� Document  
	 * @param path �� url �� �� ����� � ������� ������ �� ������� �� Document
	 * @param author �� nickname ��� ���������� ��� Document
	 * @param parentRoomName �� id ��� Room ��� ����� ������ �� Document
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
