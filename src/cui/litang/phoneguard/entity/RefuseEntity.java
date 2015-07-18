package cui.litang.phoneguard.entity;

/**
 * 黑名单实体
 * @author Cuilitang
 * @Date 2015年7月18日
 */
public class RefuseEntity {
	
	private String id;
	private String number;
	private String mode;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
}
