import POJO.Booking;
import com.google.gson.Gson;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class johnnyRestfulBookerAPITest {
    String url = "https://restful-booker.herokuapp.com";
    String autorizacaoEndpoint="/auth";
    String bookingEndpoint="/booking";
    String pingEndpoint="/ping";
    String bookingId ;
    String token;
    Booking booking = new Booking();

   public String verificaId(String nome, String sobrenome) {
       return given()
               .baseUri(url)
               .when()
               .log().all()
               .get(bookingEndpoint + "?" + "firstname=" + nome + "&" + "lastname=" + sobrenome)
               .then()
               .statusCode(200)
               .log().all()
               .extract()
               .jsonPath()
               .getString("[0].bookingid")
               ;
   }

    @BeforeClass
    public void setUp() {
        booking.setFirstname("Jim");
        booking.setLastname("Brown");
        booking.setTotalprice(111);
        booking.setDepositpaid(true);
        Booking.BookingDates bookingDates = new Booking.BookingDates();
        bookingDates.setCheckin("2018-01-01");
        bookingDates.setCheckout("2019-01-01");
        booking.setBookingdates(bookingDates);
        booking.setAdditionalneeds("Breakfast");

        bookingId = verificaId(booking.getFirstname(),booking.getLastname());

        // TODO pegar o token de autorizacao

        token = given()
                .baseUri(url)
                .contentType("application/json")
                .body("{\n" +
                        "    \"username\" : \"admin\",\n" +
                        "    \"password\" : \"password123\"\n" +
                        "}")
                .when()
                .post(autorizacaoEndpoint)
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        System.out.println("O token e " +token);
    }

    @Test(priority = 0, description="Faz um ping para checar se está funcionando")
    public void ping() {
        // TODO faz um ping para verificar se está funcionando
        given()
                .baseUri(url)
                .when()
                .log().all()
                .get(pingEndpoint)
                .then()
                .statusCode(201);

    }

    @Test(priority=1, description="Cria uma conta no resful-booker")
    public void createBooking() {
        // TODO cria um booking
        String gsonBooking = new Gson().toJson(booking);
        System.out.println(gsonBooking);

        given()
                .log().all()
                .baseUri(url)
                .contentType("application/json")
                .body(gsonBooking)
                .when()
                .log().all()
                .post(bookingEndpoint)
                .then()
                .statusCode(200)
                .body("booking.firstname", equalTo(booking.getFirstname()))
                .body("booking.lastname", equalTo(booking.getLastname()))
                .body("booking.totalprice", equalTo(booking.getTotalprice()))
                .body("booking.depositpaid", equalTo(booking.isDepositpaid()))
                .body("booking.bookingdates.checkin", equalTo(booking.getBookingdates().getCheckin()))
                .body("booking.bookingdates.checkout", equalTo(booking.getBookingdates().getCheckout()))
                .body("booking.additionalneeds", equalTo(booking.getAdditionalneeds()));
        ;
    }

       @Test(priority=2, description="Verifica o id do booking criado")
            public void getBooking() {
                // TODO verifica o booking criado
                given()
                        .baseUri(url)
                        .when()
                        .log().all()
                        .get(bookingEndpoint + "/" + bookingId)
                        .then()
                        .log().all()
                        .statusCode(200)
                        .body("firstname", equalTo("Jim"))
                        .body("lastname", equalTo("Brown"))
                        .body("totalprice", equalTo(111))
                        .body("depositpaid", equalTo(true))
                        .body("bookingdates.checkin", equalTo("2018-01-01"))
                        .body("bookingdates.checkout", equalTo("2019-01-01"))
                        .body("additionalneeds", equalTo("Breakfast"));
            }

            @Test(priority=3,description="Atualiza o booking criado")
    public void updateBooking() {
        // TODO atualiza o booking criado
        Booking.BookingDates bookingDates = new Booking.BookingDates();
        bookingDates.setCheckin("2023-01-01");
        bookingDates.setCheckout("2023-01-01");
        booking.setBookingdates(bookingDates);

        String gsonBooking = new Gson().toJson(booking);

        given()
                .log().all()
                .baseUri(url)
                .contentType("application/json")
                .header("Cookie", "token=" + token)
                .body(gsonBooking)
                .when()
                .log().all()
                .put(bookingEndpoint + "/" + bookingId)
                .then()
                .statusCode(200)
                .body("firstname", equalTo(booking.getFirstname()))
                .body("lastname", equalTo(booking.getLastname()))
                .body("totalprice", equalTo(booking.getTotalprice()))
                .body("depositpaid", equalTo(booking.isDepositpaid()))
                .body("bookingdates.checkin", equalTo(booking.getBookingdates().getCheckin()))
                .body("bookingdates.checkout", equalTo(booking.getBookingdates().getCheckout()))
                .body("additionalneeds", equalTo(booking.getAdditionalneeds()));
        ;
    }

    @Test(priority=4, description  ="Deleta o booking criado")
    public void deleteBooking() {
        // TODO deleta o booking criado
        given()
                .baseUri(url)
                .header("Cookie", "token=" + token)
                .when()
                .log().all()
                .delete(bookingEndpoint + "/" + bookingId)
                .then()
                .statusCode(201);
    }
}
