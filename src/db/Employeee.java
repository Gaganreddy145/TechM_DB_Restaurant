package db;

public class Employeee {
	private int eid;
	private String name,email;
	@Override
	public String toString() {
		return "Employeee [eid=" + eid + ", name=" + name + ", email=" + email + "]";
	}
	public Employeee(int eid, String name, String email) {
		super();
		this.eid = eid;
		this.name = name;
		this.email = email;
	}
	public int getEid() {
		return eid;
	}
	public void setEid(int eid) {
		this.eid = eid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
