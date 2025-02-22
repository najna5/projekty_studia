# Lab03 - CRUD

Polecenie:
Podczas laboratorium należy zbudować "mały system", pozwalający na interakcje z użytkownikami (z poziomu konsoli w wersji minimum, z poziomu okienek w wersji rozszerzonej), umożliwiający wykonywanie operacji CRUD (od ang. create, read, update and delete; pol. utwórz, odczytaj, aktualizuj i usuń) na przetwarzanych danych. Dane powinny być w jakiś sposób utrwalane. Mogą być zapisywane w plikach lub bazie danych zapisywanej do pliku (h2 czy sqlite).

Wymagane jest, by logika biznesowa systemu była oddzielona od interfejsu użytkownika. Ponadto należy obsłużyć własne wyjątki (oprócz wyjątków generowanych przez Java API).
Budowany system powinien wspierać obsługę procesu rozliczania kosztów ogrzewania lokali mieszkaniowych, w których zamontowano podzielniki ciepła. Oczywiście system ten będzie jedynie "przybliżeniem" rzeczywistości. Aby dało się go zaimplementować przyjmujemy znaczące uproszczenia.

Zakładamy, że w procesie biorą udział następujący aktorzy: Najemca, Kontroler, Zarządca.
Wymienieni aktorzy uzyskują dostęp do systemu za pośrednictwem osobnych aplikacji: NajemcaApp (oferującej interfejs najemcy - osoby, do której przypisany jest lokal), KontrolerApp (oferującej interfejs kontrolera - osoby dokonującej pomiary stanów podzielników), ZarządcaApp (oferującej interfejs zarządcy - osoby, która zarządza wieloma nieruchomościami).
Najemca: posiada konto w systemie, ma dostęp do informacji o wymaganych opłatach za ogrzewanie oraz historii rozliczeń, zobowiązany jest do wnoszenia zryczałtowanych opłat za ogrzewanie (za pośrednictwem NajemcaApp).

Kontroler: posiada konto w systemie, ma dostęp do informacji o zleceniach wykonania odczytu liczników, wprowadza wyniki odczytów (za pośrednictwem KontrolerApp).
Zarządca: posiada konto w systemie, definiuje koszty zryczałtowanych opłat za ogrzewanie dla poszczególnych lokali, zleca zadania wykonania odczytu podzielników ciepła, uruchamia rozliczenia kosztów ogrzewania z uwzględnieniem stanu licznika głównego oraz zużycia ciepła na części wspólne (za pośrednictwem ZarządcaApp).

Zakres gromadzonych danych w systemie może być minimalny. Poniżej przedstawiono wstępny zarys modelu danych. Model ten należy zmodyfikować odpowiednio do potrzeb.
lokale: numer, lista z rodzajem grzejników z przypisanymi im podzielnikami ciepła
budynki: adres, licznik główny, lista lokali
najemcy: nazwa, lista podległych lokali
zadania: lista lokali, planowana data wykonania, wykonawca
odczyty: lokal, lista stanów liczników, rzeczywista data wykonania, wykonawca
Aby przetestować działanie systemu powinno dać się uruchamiać osobno: przynajmniej dwie instancje NajemcaApp, przynajmniej jedną instancję KontrolerApp, przynajmniej jedną instancję ZarządcaApp.

Synchronizacja pomiędzy uruchomionymi instancjami wymienionych aplikacji powinna odbywać się poprzez współdzielenie utrwalanych gdzieś danych. W przypadku zapisywania danych w systemie plików może pojawić się kłopot - system operacyjny może zablokować możliwość zapisu do danego pliku, jeśli aktualnie jest on otwarty w innej aplikacji. Wtedy może przydać się właśnie obsługa wyjątków. Generalnie - implementacja wielodostępu to bardzo trudny temat. Na potrzeby laboratorium mocno go upraszczamy (nie ma potrzeby budowania tytaj jakichś bardzo złożonych mechanizmów). W przypadku posługiwania się bazami danych zapisanymi w pliku też ten problem może wystąpić.

[Moje rozwiązanie](Lab03)