public class Admins {
    public String name;
    public String email;
    public String phone;
    public String address;
    public String password;

    // Default constructor
    public Admins() {
        // Default values for the fields
        this.name = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.password = "";
    }

    // Parameterized constructor
    public Admins(String name, String email, String phone, String address, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admins{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}
