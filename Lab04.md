# lab04 - HTTP client

Polecenie:
Podczas laboratorium należy zbudować aplikację o przyjaznym, graficznym interfejsie użytkownika, pozwalającą na przeglądanie danych udostępnionych w Internecie poprzez otwarte API. Aplikacja ma być zbudowana z wykorzystaniem klas SWING bądź JavaFX.
Dane powinny pochodzić z portalu NFZ, na którym udostępniono parę publicznych API. Opis wystawionych interfejsów można znaleźć pod adresami: https://api.nfz.gov.pl/app-itl-api/#intro, https://api.nfz.gov.pl/app-stat-api-jgp/#intro
Proszę przeglądnąć opisy API i wybrać coś interesującego do zaprezentowania. Może to być np. informacja o dostępnych terminach wizyt, którą można pozyskać (do pozyskania z enpointu queues (proszę zajrzeć na stronę ze swaggerowym API https://api.nfz.gov.pl/app-itl-api/#resources, przykładowe zapytanie: https://api.nfz.gov.pl/app-itl-api/queues?page=1&limit=10&format=json&case=1&province=01&benefitForChildren=true&api-version=1.3)). Mogą to być jakieś dane statystyczne, np. zestawienia świadczeń wykonanych w wybranych ośrodkach.
Proszę zastanowić się nad odpowiednią wizualizacją. Należy przemyśleć, jak będzie wyglądał interfejs użytkownika (czy użyć tabel, czy też zwykłych pól tekstowych; czy użyć okna dialogowe, czy też zakładki; itp.).

Aplikacja ma być modułowa (tj. ma powstać z wykorzystaniem JPMS (ang. Java Platform Module System)), a więc powinna posiadać module-info.java z odpowiednimi wpisami. Ponadto należy zadbać o właściwe zmodyfikowanie ścieżek modułów oraz komendy uruchomieniowej. Stosunkowo prosto do aplikacji modułowych podłącza się klasy SWING. Trudniej jest z JavaFX - to osobny runtime, wymagający osobnej instalacji, a później, odpowiedniej parametryzacji wywołania wirtualnej maszyny (z modyfikacją ścieżki modułów oraz wskazaniem wykorzystanych modułów: --module-path "\path to javafx\lib" --add-modules javafx.controls,javafx.fxml).
Podczas implementacji będzie trzeba zająć się pozyskiwaniem danych poprzez API oraz ich parsowaniem.

Pozyskanie danych można zaimplementować na różne sposoby, korzystając z różnych klas dostępnych w JDK oraz w zewnętrznych bibliotekach. Na stronie: https://www.wiremock.io/post/java-http-client-comparison przedstawiono porównanie popularnych klientów, zaś na stronie: https://www.baeldung.com/java-9-http-client opisano, jak użyć HTTPClient (dostępny w JDK).
Parsowanie danych może odbywać się z wykorzystaniem bibliotek do przetwarzania danych w formacie JSON. Krótki tutorial dotyczący tego tematu znajduje się pod adresem: https://www.baeldung.com/java-json.

Implementując aplikację proszę rozdzielić ją na dwie części, budowane do osobnych plików jar:
lab04_client - ta część odpowiadać ma za logikę biznesową (wysyłanie zapytań, parsowanie odpowiedzi),
lab04_gui - ta część odpowiadać ma za graficzny interfejs użytkownika (wizualizuje dane, korzysta z lab04_client).

Proszę zwrócić uwagę na regulamin serwisów. Zwykle pojawiają się w nim jakieś ograniczenia co do liczby wysyłanych zapytań. Jeśli te ograniczenia zostaną złamane, dostarczyciel serwisu może "zbanować" klienta, który wysłał zapytania. Tak więc proszę zachować umiar przy wykonywaniu testów połączeń.

[Moje rozwiązanie](Lab04)