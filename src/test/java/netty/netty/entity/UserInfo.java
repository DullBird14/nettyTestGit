package netty.netty.entity;

public class UserInfo {
	private Integer age;
	private String name;
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "UserInfo [age=" + age + ", name=" + name + "]";
	}
	
}
