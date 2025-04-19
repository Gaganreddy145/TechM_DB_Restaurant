package ResProj;

import java.util.*;

public class Customer {
	private int id;
	private String name;

	public Customer(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", name=" + name + "]";
	}

	public Menu addOrder(List<Menu> lm, int id) {
		Menu requestedItem = null;
		for (Menu m : lm) {
			if (m.getId() == id) {
				requestedItem = m;
			}
		}
		if (requestedItem == null)
			return null;
		return requestedItem;
	}

}
