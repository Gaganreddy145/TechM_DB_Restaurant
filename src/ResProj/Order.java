package ResProj;

import java.time.LocalDate;

public class Order {
	Menu m;
	Customer c;
	int quantity = 1;
	LocalDate currentDate = LocalDate.now();

	public Order(Menu m, Customer c) {
		super();
		this.m = m;
		this.c = c;
	}

	public Order(Menu m, Customer c, LocalDate currentDate, int quantity) {
		this(m, c);
		this.currentDate = currentDate;
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		return "Order [m=" + m + ", c=" + c + ", quantity=" + quantity + ", currentDate=" + currentDate + "]";
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Menu getM() {
		return m;
	}

	public void setM(Menu m) {
		this.m = m;
	}

	public Customer getC() {
		return c;
	}

	public void setC(Customer c) {
		this.c = c;
	}

}
