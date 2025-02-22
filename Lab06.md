# Lab06 - gniazda TCP/IP

Polecenia:
Podczas laboratorium należy zbudować aplikację działającą w środowisku rozproszonym, wykorzystującą do komunikacji gniazda TCP/IP obsługiwane za pomocą klas ServerSocket oraz Socket. Dokładniej – należy zaimplementować mały system, w którego skład wejdą podsystemy uruchamiane równolegle (na jednym lub na kilku różnych komputerach). Zakładamy, że system będzie pełnił rolę symulatora interakcji zachodzących podczas wywozu nieczystości między następującymi podsystemami:

Dom (House)
Reprezentuje dom jednorodzinny, który nie jest podłączony do sieci kanalizacyjnej. Zużywana w nim woda i wytwarzane nieczystości gromadzone są w przydomowym szambie. Co jakiś czas przydomowe szambo musi być opróżnione.  
Dom wysyła zamówienie na usługę do Biura, gdy szambo wypełni się do poziomu alarmowego. Realizacją takiej usługi zajmuje się bezpośrednio Cysterna.  
Dom jest parametryzowany własnym portem oraz pojemnością szamba.  
Na własnym porcie wystawia interfejs IHouse z metodą:  
- int getPumpOut(int max) – metoda pozwalająca opróżnić przydomowe szambo, jej parametrem jest dostępna pojemność cysterny, wartością zwracaną zaś objętość rzeczywiście wypompowanych nieczystości;  
Dom korzysta z interfejsu Biura, by wysłać zamówienie na usługę opróżnienia szamba.

Biuro (Office)
Reprezentuje biuro firmy świadczącej usługi wywozu nieczystości. Przyjmuje zamówienie na usługi od Domów, zleca ich wykonanie Cysternom.  
Biuro jest parametryzowane własnym portem oraz hostem i portem oczyszczalni.  
Na własnym porcie wystawia interfejs IOffice z metodami:  
- int register(string host, string port) – metoda pozwalająca Cysternie zarejestrować się w Biurze, jej parametrami są dane hosta i portu, na którym działa Cysterna. Wartością zwracaną jest przyznany numer Cysterny.
- int order(string host, string port) – metoda pozwalająca Domowi zamówić wywóz nieczystości, jej parametrami są dane hosta i portu, na którym działa Dom. Wartością zwracaną jest wartość oznaczająca przyjęcie (1) lub odrzucenie (0) zamówienia na usługę.
- void setReadyToServe(int number) – metoda pozwalająca Cysternie zgłosić gotowość przyjęcia zlecenia.  
Biuro korzysta z interfejsu Cysterny, by wysłać zlecenie wykonania usługi.  
Biuro korzysta z interfejsu Oczyszczalni, by pozyskać informację o sumarycznej objętości przywiezionych tam nieczystości przez poszczególne Cysterny oraz by się rozliczyć za nieczystości przywiezione przez poszczególne Cysterny.

Cysterna (Tanker)
Reprezentuje cysternę, która rejestruje się w Biurze. Przyjmuje zlecenia wykonania usługi od Biura. Wypompowuje nieczystości z szamba przy Domu w ramach wykonywania usługi. Wywozi nieczystości do Oczyszczalni. Zgłasza do Biura gotowość przyjęcia zlecenia;  
Cysterna jest parametryzowana własnym portem, hostem i portem Biura, hostem i portem Oczyszczalni, jak również własną maksymalną pojemnością.  
Na własnym porcie wystawia interfejs ITanker z metodami:  
- void setJob(string host, string port) – metoda, którą Biuro zleca wywóz nieczystości, jej parametrami są dane hosta i portu Domu.  
Cysterna korzysta z interfejsu Biura, by się tam zarejestrować oraz zgłaszać gotowość przyjęcia zlecenia;  
Cysterna korzysta z interfejsu Oczyszczalni, aby wypompować tam przywiezione nieczystości.

Oczyszczalnia (SewagePlant)
Reprezentuje oczyszczalnię, w której Cysterny zostawiają nieczystości;  
Oczyszczalnia jest parametryzowana własnym portem;  
Na własnym porcie wystawia interfejs ISewagePlant z metodami:  
- void setPumpIn(int number, int volume) – metoda, którą Cysterna przepompowuje przywiezione nieczystości, jej parametrami są numer Cysterny oraz objętość nieczystości;  
- int getStatus(int number) – metoda, którą Biuro może pozyskać informację o sumarycznej objętości przywiezionych nieczystości przez daną Cysternę, jej parametrem jest numer Cysterny;  
- void setPayoff(int number) – metoda, którą Biuro rozlicza się za przywiezione nieczystości przez daną Cysternę, jej parametrem jest numer Cysterny, po wykonaniu tej metody zeruje się suma objętości przywiezionych nieczystości przez daną Cysternę.

Komunikacja między elementami systemu ma odbywać się z wykorzystaniem gniazd TCP/IP. Aby odpalić metodę danego interfejsu trzeba wysłać żądanie zakodowane tekstowo w postaci:  
digit excluding zero = "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" ;  
digit = "0" | digit excluding zero ;  
positive integer = digit excluding zero, { digit } ;  
host = positive integer, ".", positive integer, ".", positive integer, ".", positive integer ;  
get method = "gp:", positive integer | "gs:", positive integer ;  
set method = "sr:", positive integer | "sj:", host, "," positive integer | "spi:", positive integer, ",", positive integer | "spo:", positive integer ;  
register method = "r:" host, "," positive integer ;  
order method = "o:" host, "," positive integer ;  
request = get method | set method | register method | order method ;  
response = positive integer;

[Moje rozwiązanie](Lab06)
