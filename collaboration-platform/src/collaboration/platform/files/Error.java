package collaboration.platform.files;

/**
 * <h1>Error</h1> <br>
 * H ����� Error �������� �������� �� ������ ������ ��� �� ��������� ���� ������
 * ��� ������ ����������� �������� ���� ����������� ��� �pi. ��������������
 * ����������� ��� ������ ��� ��� ����� Api � ����� �� ����������� ���� ���
 * ����������� Xstream �� json.��� �� �� ���������� ���� ������ �� ���������
 * ���������.
 * 
 * @author Adamos Koumi 949993
 * @version 1.0
 * @since 2015-04-25
 */
public class Error {
	private String message;

	/**
	 * * ������������� ��� ������ Error
	 * 
	 * @param message
	 *            T� ������ ������ ��� �� �������� ��� �������������
	 */
	public Error(String message) {
		this.message = message;
	}

	/**
	 * � ������� ������� �� error message
	 * @param message �� ��� ������ ������ 
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
