package ResProj;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class Restaurant {

	private static String DB_USERNAME = "root";
	private static String DB_PASSWORD = "password";

	private static String DB_URL = "jdbc:mysql://127.0.0.1:3306/restaurant";
	private String name;
	List<Customer> lc;
	List<Menu> lm;
	List<Order> lo;

	@Override
	public String toString() {
		return "Restaurant [name=" + name + "]";
	}

	public Restaurant(String name) {
		super();
		this.name = name;
		lc = new ArrayList<>();
		lm = new ArrayList<>();
		lo = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void updateMenuItem(int id, double price,Connection conn) throws SQLException {
		Menu exist = null;
		for (Menu m : lm) {
			if (m.getId() == id) {
				exist = m;
				break;
//				m.setPrice(price);
//				System.out.println("Successfully updated!!!");
//				return;
			}
		}
		if(exist != null) {
			String s = "update menu set price = ? where mid = ?";
			PreparedStatement ps = conn.prepareStatement(s);
			ps.setDouble(1, price);
			ps.setInt(2, id);
			int res = ps.executeUpdate();
			if(res > 0) {
				exist.setPrice(price);
				System.out.println("Successfully updated!!!");
			}else {
				System.out.println("Failed to update");
			}
			return;
		}
		System.out.println("No such item!!!");
	}

	public void removeMenuItem(int id) {
		int found = 0;
		for (Menu m : lm) {
			if (m.getId() == id) {
				lm.remove(m);
				found = 1;
				break;
			}
		}
		if (found == 1) {
			System.out.println("Successfully Removed the Item");
		} else {
			System.out.println("No such item");
		}
	}

	public void createOrder(Menu requestedItem, Customer c, PreparedStatement psCreateOrder, Connection conn)
			throws SQLException {
		Order exist = null;
		for (Order ord : lo) {
			if (ord.getM().getId() == requestedItem.getId() && ord.getC().getId() == c.getId()
					&& (ord.currentDate.compareTo(LocalDate.now()) == 0)) {
				exist = ord;
			}
		}
		if (exist != null) {
			String s = "update orders set quantity = ? where omid = ? and cid = ? and odate = ?";
			psCreateOrder = conn.prepareStatement(s);
			psCreateOrder.setInt(1, exist.getQuantity() + 1);
			psCreateOrder.setInt(2, exist.getM().getId());
			psCreateOrder.setInt(3, exist.getC().getId());
			psCreateOrder.setDate(4, Date.valueOf(exist.currentDate));
			int res = psCreateOrder.executeUpdate();
			if (res > 0) {
				System.out.println("Order updated successfully");
				exist.setQuantity(exist.getQuantity() + 1);
			} else {
				System.out.println("Failed to update the order");
			}
			return;
		}
		String s = "insert into orders(omid,cid,quantity,odate) values(?,?,?,?)";
		psCreateOrder = conn.prepareStatement(s);
		psCreateOrder.setInt(1, requestedItem.getId());
		psCreateOrder.setInt(2, c.getId());
		Order o = new Order(requestedItem, c);
		psCreateOrder.setInt(3, o.getQuantity());
		psCreateOrder.setDate(4, Date.valueOf(o.currentDate));
		int res = psCreateOrder.executeUpdate();
		psCreateOrder.close();
		if (res > 0) {
			System.out.println("Order created!!!");
			lo.add(o);
		} else {
			System.out.println("Order creation failure");
		}

	}

	public double calculateBill(Customer c) {
		double total = 0;
		for (Order ord : lo) {
			if (ord.getC().getId() == c.getId()) {
				total += ord.getM().getPrice() * ord.getQuantity();
			}
		}
		return total;
	}

	public void displayMenu() {
		if (lm.size() == 0) {
			System.out.println("No items in the menu!!!");
			return;
		}
		for (Menu m : lm) {
			System.out.println(m);
		}
	}

	public void displayOrders() {
		if (lo.size() == 0) {
			System.out.println("No orders!!!");
			return;
		}
		for (Order o : lo) {
			System.out.println(o);
		}
	}

	public void displayCustomers() {
		if (lc.size() == 0) {
			System.out.println("No customers!!!");
			return;
		}
		for (Customer c : lc) {
			System.out.println(c);
		}
	}

	public HashMap<String, Integer> generateTotalCountByItemsForToday() {
		HashMap<String, Integer> todayItems = new HashMap<>();
		LocalDate today = LocalDate.now();
		for (Order o : lo) {
			if (o.currentDate.compareTo(today) == 0) {
				if (todayItems.containsKey(o.getM().getName())) {
					todayItems.put(o.getM().getName(), todayItems.get(o.getM().getName()) + o.getQuantity());
				} else {
					todayItems.put(o.getM().getName(), o.getQuantity());
				}
			}
		}
		return todayItems;
	}

	public String todayHighestOrderItem() {
		HashMap<String, Integer> todayItems = generateTotalCountByItemsForToday();
		int max = Integer.MIN_VALUE;
		String item = null;
		for (String key : todayItems.keySet()) {
			if (max < todayItems.get(key)) {
				max = todayItems.get(key);
				item = key;
			}
		}
		return item;
	}

	public Customer selectCustomer(int id) {
		for (Customer c : lc) {
			if (c.getId() == id)
				return c;
		}
		System.out.println("No such customer!!!");
		return null;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Restaurant r = new Restaurant("Satyam");
		Customer currentCustomer = null;
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
			PreparedStatement forQueryMenuItems;
			forQueryMenuItems = conn.prepareStatement("select * from menu");
			ResultSet menus = forQueryMenuItems.executeQuery();
			while (menus.next()) {
				r.lm.add(new Menu(menus.getInt("mid"), menus.getString("name"), menus.getDouble("price")));
			}
			forQueryMenuItems.close();
			PreparedStatement queryCustomers = conn.prepareStatement("select * from customer");
			ResultSet customers = queryCustomers.executeQuery();
			while (customers.next()) {
				r.lc.add(new Customer(customers.getInt(1), customers.getString(2)));
			}
			queryCustomers.close();
			PreparedStatement queryOrders = conn.prepareStatement(
					"select * from orders o join menu m on  o.omid = m.mid join customer c on c.id = o.cid");
			ResultSet orders = queryOrders.executeQuery();
			while (orders.next()) {
				Menu tempMenu = new Menu(orders.getInt("mid"), orders.getString(7), orders.getDouble("price"));
				Customer tempCustomer = new Customer(orders.getInt("id"), orders.getString(10));
				int tempQuantity = orders.getInt("quantity");
				Date sqlDate = orders.getDate("odate");
				LocalDate localDate = sqlDate.toLocalDate();
				r.lo.add(new Order(tempMenu, tempCustomer, localDate, tempQuantity));
			}
			orders.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		int conti = 0;
		do {
			System.out.print(
					"1.Add Menu Item to Restaurant 2.Add Customer 3.Display Menu\n4.Display Customers 5.Select Customer 6.Create Order"
							+ "\n7.Calculate Bill 8.Display Orders 9.Today's Orders:"
							+ "\n10.Today's highest ordered item\n11.Update Menu Item Price:");
			int choice = sc.nextInt();
			switch (choice) {
			case 1:
				System.out.print("Enter the item name: ");
				sc.nextLine();
				String itemName = sc.nextLine();
				System.out.print("Enter the price of the item: ");
				double price = sc.nextDouble();
				String s = "insert into menu(name,price) values(?,?)";
				PreparedStatement ps;
				try {
					ps = conn.prepareStatement(s, Statement.RETURN_GENERATED_KEYS);
					ps.setString(1, itemName);
					ps.setDouble(2, price);
					int res = ps.executeUpdate();
					if (res > 0) {
						System.out.println("Menu Item was added!!!");
						ResultSet rs = ps.getGeneratedKeys();
						if (rs.next()) {
							r.lm.add(new Menu(rs.getInt(1), itemName, price));
						}
					} else {
						System.out.println("Menu Item insertion was failed");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 2:
				System.out.print("Enter the customer name: ");
				sc.nextLine();
				String customerName = sc.nextLine();
				String customerInsertStatement = "insert into customer(name) values(?)";
				try {
					PreparedStatement customerInsert = conn.prepareStatement(customerInsertStatement,
							Statement.RETURN_GENERATED_KEYS);
					customerInsert.setString(1, customerName);
					int res = customerInsert.executeUpdate();
					if (res > 0) {
						System.out.println("Customer was added!!!");
						ResultSet rs = customerInsert.getGeneratedKeys();
						if (rs.next()) {
							r.lc.add(new Customer(rs.getInt(1), customerName));
						}
					} else {
						System.out.println("Customer insertion was failed!!!");
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				break;
			case 3:
				r.displayMenu();
				break;
			case 4:
				r.displayCustomers();
				break;
			case 5:
				System.out.print("Enter the id of the customer to select: ");
				int id = sc.nextInt();
				currentCustomer = r.selectCustomer(id);
				break;
			case 6:
				if (currentCustomer != null) {
					r.displayMenu();
					System.out.print("Enter the id of the item: ");
					int idItem = sc.nextInt();
					Menu requestedItem = currentCustomer.addOrder(r.lm, idItem);
					if (requestedItem != null) {
						PreparedStatement psCreateOrder = null;
						try {
							r.createOrder(requestedItem, currentCustomer, psCreateOrder, conn);
						} catch (SQLException sqe) {
							sqe.printStackTrace();
						}
					} else {
						System.out.println("No such item!!!");
					}
				} else {
					System.out.println("No customer was selected");
				}
				break;
			case 7:
				if (currentCustomer != null)
					System.out.println("Total Bill: " + r.calculateBill(currentCustomer));
				else
					System.out.println("No customer was selected");
				break;
			case 8:
				r.displayOrders();
				break;
			case 9:
				HashMap<String, Integer> todayItems = r.generateTotalCountByItemsForToday();
				for (String food : todayItems.keySet()) {
					System.out.println(food + " : " + todayItems.get(food));
				}
				break;
			case 10:
				String highestOrderedItemToday = r.todayHighestOrderItem();
				if (highestOrderedItemToday == null) {
					System.out.println("No item available");
				} else {
					System.out.println(highestOrderedItemToday);
				}
				break;
			case 11:
				System.out.print("Enter the id of the item to be updated:");
				int itemId = sc.nextInt();
				System.out.print("Enter the price of the item:");
				double priceItem = sc.nextDouble();
				try {
					r.updateMenuItem(itemId, priceItem, conn);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			System.out.print("Do u want to continue[1|0]:");
			conti = sc.nextInt();
		} while (conti == 1);

		System.out.println("Bye Bye");

//		r.lm.add(new Menu(1, "Dosa", 80));
//		r.lm.add(new Menu(2, "Idli", 50));
//		r.lm.add(new Menu(3, "Puri", 100));
//		Customer c1 = new Customer(101, "Mahesh");
//		Customer c2 = new Customer(102, "Beggar");
//		r.lc.add(c1);
//		r.lc.add(c2);
//		char ch = 'n';
//		do {
//			r.displayMenu();
//			System.out.print("Enter the id of the item: ");
//			int id = sc.nextInt();
//			Menu requestedItem = c1.addOrder(r.lm, id);
//			if (requestedItem != null) {
//				r.createOrder(requestedItem, c1);
//			} else {
//				System.out.println("No such item!!!");
//			}
//			System.out.print("Do u want to add more items? ");
//			sc.nextLine();
//			ch = sc.nextLine().charAt(0);
//		} while (ch == 'y' || ch == 'Y');
//		sc.close();
//		System.out.println("Total Bill: " + r.calculateBill(c1));
//		System.out.println(r.lo);
//		HashMap<String, Integer> todayItems = r.generateTotalCountByItemsForToday();
//		for (String food : todayItems.keySet()) {
//			System.out.println(food + " : " + todayItems.get(food));
//		}
//		String highestOrderedItemToday = r.todayHighestOrderItem();
//		if (highestOrderedItemToday == null) {
//			System.out.println("No item available");
//		} else {
//			System.out.println(highestOrderedItemToday);
//		}

//		r.removeMenuItem(1);
//		r.updateMenuItem(1, 60);
//		r.displayMenu();

	}

}
