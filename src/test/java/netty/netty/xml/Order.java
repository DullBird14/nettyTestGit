package netty.netty.xml;

public class Order {
	private long orderNumber;
	private Customer customer;
	
	private Address billTo;
	
	private Shapping shipping;
	
	private Address shipTo;
	
	private Float total;

	public long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Address getBillTo() {
		return billTo;
	}

	public void setBillTo(Address billTo) {
		this.billTo = billTo;
	}

	public Shapping getShipping() {
		return shipping;
	}

	public void setShipping(Shapping shipping) {
		this.shipping = shipping;
	}

	public Address getShipTo() {
		return shipTo;
	}

	public void setShipTo(Address shipTo) {
		this.shipTo = shipTo;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}
	
	
}
