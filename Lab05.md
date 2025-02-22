# Lab05 - wątki

Polecenie:
Podczas laboratorium należy zbudować aplikację, w której dojdzie do synchronizacji wielu wątków. Aplikacja powinna być parametryzowana i pozwalać na uruchamianie wątków oraz obserwowanie ich zachowań i stanów.

Zakładamy, że aplikacja będzie pełnić rolę symulatora studenckiej stołówki, podobnej do stołówki funkcjonującej w SKS na PWr.

Na stołówce zlokalizowano dwa punkty wydawania dań. Do tych punktów ustawiają się kolejki przychodzących klientów. Po otrzymaniu dania Klient przechodzi do jednej z czterech kas, by dokonać opłaty za danie. Po zapłacie Klient kieruje się do stołu, by zająć miejsce. W stołówce istnieją dwa długie stoły, przy których po każdej z dwóch dłuższych stron istnieje n miejsc (czyli przy jednym stole może zasiąść 2*n klientów). Klient próbuje zająć wolne miejsce sekwencyjnie, startując od jakiegoś wybranego punktu, a po zajęciu miejsca konsumuje danie (tj. przez chwilę przy tym stoliku siedzi). Następnie klient opuszcza stołówkę.
Każdy klient jest wątkiem, który cyklicznie udaje się do stołówki, by zamówić i skonsumować danie.

Kolejki przy punktach wydawania są współdzielonym zasobem. Klient powinien ustawiać się w krótszej kolejce wydawania.
Każda kolejka wydawania jest obsługiwana przez jednego Pracownika. Pracownik jest wątkiem, który pobiera Klienta z kolejki i przekazuje mu danie. Danie może być reprezentowane cyfrą. Czas wydania dania powinien być losowy
Kasy są współdzielonym zasobem. Klienci powinni ustawiać się do kas o najkrótszej kolejce. Przy czym może zdarzyć się, że któraś z kas może zostać wyłączona. Wtedy jeśli byli jacyś Klienci przy kasie, zostaną obsłużeni. Jednak żaden nowy Klient do nie może ustawić się do kolejki wyłączonej kasy.

Każda kasa jest obsługiwana przez Kasjera. Kasjer jest wątkiem, który działa podobnie do Pracownika - obsługuje kolejkę Klientów chcących zapłacić za danie. Kasjer sprawdza jedynie, jakie danie wziął Klient. Czas obsługi płatności powinien być losowy.
Miejsca przy stolikach są współdzielonymi zasobami. Można byłoby zaimplementować jakieś algorytmy usadzania przyjaciół blisko siebie - ale takich rzeczy nie trzeba w ramach laboratorium robić. Wystarczy, że Klienci będą próbować zajmować jakieś wolne miejsca.
Wizualizacja stanu wątków chyba najłatwiej zrealizować korzystając z etykiet tekstowych umieszczanych na panelu jak na poniższym schemacie:
.
Klienci   Pracownicy       Kasjerzy    Stoliki
a
.             P1[2]        K1[3]
.         .......b      ......f           |      |   
d                                         |      |
e             P1[1]        K2[3]       h i|      |
.         .......c      ......g           |      |
.                                         |      |
.                          K3[...]        |      |
.                       ......            |      |
.
.
.
Powyższy schemat jest tylko propozycją. Zostanie ona omówiona na zajęciach. Można wymyśleć inny sposób wizualizacji zachowania się wątków.

Należy zadbać o odpowiednią synchronizację wątków. Choć w założeniach podpowiedziano, co traktować jako zasób współdzielony, proszę się zastanowić, czy nie trzeba nałożyć jeszcze jakichś warunków (np. jak pokazać szukanie miejsc przy stolikach).

[Moje rozwiązanie](Lab05)